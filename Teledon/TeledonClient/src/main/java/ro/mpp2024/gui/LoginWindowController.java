package ro.mpp2024.gui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ro.mpp2024.model.Volunteer;
import ro.mpp2024.services.ITeledonServices;
import ro.mpp2024.services.TeledonException;

public class LoginWindowController {
    private ITeledonServices server;
    private static Logger logger = LogManager.getLogger(LoginWindowController.class);

    public LoginWindowController(ITeledonServices server) {
        this.server = server;
    }

    public void handleLogin(String username, String password, LoginWindow loginView) {
        if (username.isBlank() || password.isBlank()) {
            loginView.showAlert("Validation Error", "Username and password are required.");
            return;
        }

        try {
            MainWindowController mainController = new MainWindowController(server);
            MainWindow mainWindow = new MainWindow(mainController);
            mainController.setView(mainWindow);

            Volunteer volunteer = server.login(username, password, mainController);
            
            mainController.setVolunteer(volunteer);
            mainWindow.show();
            
            loginView.close();
        } catch (TeledonException e) {
            logger.error("Login failed: " + e.getMessage());
            loginView.showAlert("Login Failed", e.getMessage());
            loginView.clearFields();
        }
    }
}
