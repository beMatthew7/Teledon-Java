package ro.mpp2024;

import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ro.mpp2024.gui.LoginWindow;
import ro.mpp2024.gui.LoginWindowController;
import ro.mpp2024.network.grpc.TeledonGrpcProxy;
import ro.mpp2024.services.ITeledonServices;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class StartGrpcClient extends Application {

    private static int defaultPort;
    private static String defaultServer;
    private static Logger logger = LogManager.getLogger(StartGrpcClient.class);

    @Override
    public void start(Stage primaryStage) throws Exception {
        Properties clientProps = new Properties();
        try {
            clientProps.load(StartGrpcClient.class.getResourceAsStream("/teleclient.properties"));
            logger.info("Client properties loaded");
        } catch (IOException | NullPointerException e) {
            logger.error("Cannot find teleclient.properties " + e);
            logger.debug("Looking in folder {}", (new File(".")).getAbsolutePath());
            return;
        }

        String serverIP = clientProps.getProperty("teledon.server.host", defaultServer);

        int serverPort = defaultPort;
        try {
            String portString = clientProps.getProperty("teledon.server.port");
            if (portString != null && !portString.trim().isEmpty()) {
                serverPort = Integer.parseInt(portString.trim());
            }
        } catch (NumberFormatException e) {
            logger.error("Wrong port number in properties file, using default: " + defaultPort);
        }

        logger.info("Connecting to gRPC server at " + serverIP + ":" + serverPort);

        ITeledonServices server = new TeledonGrpcProxy(serverIP, serverPort);

        LoginWindowController controller = new LoginWindowController(server);
        LoginWindow loginWindow = new LoginWindow(controller);
        loginWindow.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}