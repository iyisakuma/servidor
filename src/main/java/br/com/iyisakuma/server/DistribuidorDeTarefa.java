package br.com.iyisakuma.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class DistribuidorDeTarefa implements Runnable {

    private static final Logger logger = LogManager.getLogger(DistribuidorDeTarefa.class);

    private final Socket socket;

    public DistribuidorDeTarefa(Socket socket) {
        this.socket = socket;
    }

    private static final Map<String, Handler> commander = new HashMap<>();

    static {
        commander.put("C1", (distribuidorDeTarefa) -> "Confirmação comando: C1");
        commander.put("C2", (distribuidorDeTarefa) -> "Confirmação comando: C2");
        commander.put("SHUTDOWN", (distribuidorDeTarefa) ->  {
            distribuidorDeTarefa.fim();
            return "Servidor Finalizado";
        });
    }

    @Override
    public void run() {
        try {
            logger.info(socket);
            var entrada = new Scanner(socket.getInputStream());
            var saida = new PrintStream(socket.getOutputStream());
            while (entrada.hasNext()) {
                var requisao = entrada.nextLine();
                var cliente = requisao.split(":")[0];
                var comando = requisao.split(":")[1];
                logger.info("Servidor recebeu do {} : {}",cliente, comando);
                String resposta = commander
                        .getOrDefault(comando.trim().toUpperCase(),
                                (distribuidorDeTarefa) -> "NOT FOUND")
                        .handler(this);
                saida.println(resposta);
            }
            saida.close();
            Thread.sleep(2000);
        } catch (InterruptedException | IOException ex) {
            logger.error(ex);
        }
    }

    private void fim() {
    }

}
