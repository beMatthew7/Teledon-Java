package ro.mpp2024;

import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ro.mpp2024.gui.LoginWindow;
import ro.mpp2024.gui.LoginWindowController;
import ro.mpp2024.network.jsonprotocol.TeledonServicesJsonProxy;
import ro.mpp2024.services.ITeledonServices;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class StartJsonClient extends Application {

    private static int defaultPort = 55555;
    private static String defaultServer = "localhost";
    private static Logger logger = LogManager.getLogger(StartJsonClient.class);

    @Override
    public void start(Stage primaryStage) throws Exception {
        Properties clientProps = new Properties();
        try {
            clientProps.load(StartJsonClient.class
                    .getResourceAsStream("/teleclient.properties"));
            logger.info("Client properties loaded");
        } catch (IOException e) {
            logger.error("Cannot find teleclient.properties " + e);
            logger.debug("Looking in folder {}", (new File(".")).getAbsolutePath());
            return;
        }

        String serverIP = clientProps.getProperty("teledon.server.host", defaultServer);
        int serverPort = defaultPort;
        try {
            serverPort = Integer.parseInt(clientProps.getProperty("teledon.server.port"));
        } catch (NumberFormatException e) {
            logger.error("Wrong port number, using default: " + defaultPort);
        }

        logger.info("Connecting to " + serverIP + ":" + serverPort);

        ITeledonServices server = new TeledonServicesJsonProxy(serverIP, serverPort);

        LoginWindowController controller = new LoginWindowController(server);
        LoginWindow loginWindow = new LoginWindow(controller);
        loginWindow.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}