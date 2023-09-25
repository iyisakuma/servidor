package br.com.iyisakuma.server;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Servidor {

    private final static Logger logger = LogManager.getLogger(Servidor.class);
    private final ServerSocket servidor;

    public Servidor(@NotNull ServerSocket servidor) {
        this.servidor = servidor;
        logger.info("Servidor inicializado na porta: {}", servidor.getLocalPort());
    }

    public void start() {
        Executor poolDeThread = Executors.newCachedThreadPool();
        try {
            while (true) {
                Socket socket = servidor.accept();
                poolDeThread.execute(new DistribuidorDeTarefa(socket));
                logger.info("Conexão estabelecida na porta {}", socket.getPort());
            }
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    public static void main(String[] args) {
        try{
            new Servidor(new ServerSocket(12345)).start();
        }catch (IOException ex){
            logger.error(ex);
        }
    }


}
