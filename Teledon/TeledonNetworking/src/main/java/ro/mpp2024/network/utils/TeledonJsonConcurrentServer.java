package ro.mpp2024.network.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ro.mpp2024.network.jsonprotocol.TeledonClientJsonWorker;
import ro.mpp2024.services.ITeledonServices;

import java.net.Socket;

public class TeledonJsonConcurrentServer extends AbstractServer {
    private ITeledonServices teledonServices;
    private static Logger logger = LogManager.getLogger(TeledonJsonConcurrentServer.class);

    public TeledonJsonConcurrentServer(int port, ITeledonServices services) {
        super(port);
        this.teledonServices = services;
    }

    @Override
    protected void processRequest(Socket client) {
        TeledonClientJsonWorker worker =
                new TeledonClientJsonWorker(teledonServices, client);
        Thread thread = new Thread(worker);
        thread.start();
    }
}
