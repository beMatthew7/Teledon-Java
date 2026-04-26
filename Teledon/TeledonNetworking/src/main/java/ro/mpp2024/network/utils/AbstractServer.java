package ro.mpp2024.network.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.ServerException;

public abstract class AbstractServer {
    private int port;
    private ServerSocket server = null;
    private static Logger logger = LogManager.getLogger(AbstractServer.class);

    public AbstractServer(int port) {
        this.port = port;
    }

    public void start() throws Exception {
        try {
            server = new ServerSocket(port);
            while (true) {
                logger.info("Waiting for clients ...");
                Socket client = server.accept();
                logger.info("Client connected ...");
                processRequest(client);
            }
        } catch (IOException e) {
            throw new Exception("Starting server error: " + e);
        } finally {
            stop();
        }
    }

    protected abstract void processRequest(Socket client);

    public void stop() throws Exception {
        try {
            if(server != null) {
                server.close();
            }
        } catch (IOException e) {
            throw new Exception("Closing server error: " + e);
        }
    }
}
