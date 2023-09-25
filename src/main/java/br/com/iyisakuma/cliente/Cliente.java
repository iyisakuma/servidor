package br.com.iyisakuma.cliente;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {

    static final Logger logger = LogManager.getLogger(Cliente.class);
    private final String nome;
    private final int porta;
    private final String url;

    public Cliente(String nome, String url, int porta) {
        this.nome = nome;
        this.porta = porta;
        this.url = url;
    }

    public void connect() {
        try {
            Socket socket = new Socket(url, porta);

            Thread threadEnviaMensagem = new Thread(() -> enviaMensage(socket));
            Thread threadRecebeMensagem = new Thread(() -> recebeMensagem(socket));

            threadEnviaMensagem.start();
            threadRecebeMensagem.start();

            try {
                threadEnviaMensagem.join();
            }catch (InterruptedException ex){
                logger.error(ex);
            }

            socket.close();
        } catch (IOException ex) {
            logger.error("Não foi possível estabelecer uma conexão.", ex);
        }


    }


    private void enviaMensage(Socket socket) {
        try {
            var teclado = new Scanner(System.in);
            logger.info("{} estabeleceu conexão com o servidor na porta {}", nome, porta);
            PrintStream saida = new PrintStream(socket.getOutputStream());
            while (teclado.hasNext()) {
                var mensagem = teclado.nextLine();
                if (mensagem.trim().equals("exit")) {
                    break;
                }

                saida.printf("(%s):%s\n", nome, mensagem);
                logger.info("{} enviou {}", nome, mensagem);
            }

            saida.close();
        } catch (IOException ex) {
            logger.error(ex);
        }

    }

    private void recebeMensagem(Socket socket) {
        try {
            Scanner respostaServidor = new Scanner(socket.getInputStream());
            while (respostaServidor.hasNext()) {
                logger.info("Resposta Servidor: {}", respostaServidor.nextLine());
            }
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    public static void main(String[] args) {
        new Cliente("Cliente 1", "localhost", 12345).connect();
    }
}
