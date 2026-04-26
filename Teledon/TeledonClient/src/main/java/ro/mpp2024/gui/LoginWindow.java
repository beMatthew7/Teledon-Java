package ro.mpp2024.gui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginWindow {
    private LoginWindowController controller;
    private Stage stage;
    private TextField txtUsername;
    private PasswordField txtPassword;

    public LoginWindow(LoginWindowController controller) {
        this.controller = controller;
        initWindow();
    }

    private void initWindow() {
        stage = new Stage();
        stage.setTitle("Login - Teledon");

        txtUsername = new TextField();
        txtUsername.setPromptText("Username");

        txtPassword = new PasswordField();
        txtPassword.setPromptText("Password");

        Button btnLogin = new Button("Login");
        btnLogin.setOnAction(e -> controller.handleLogin(txtUsername.getText().trim(), txtPassword.getText().trim(), this));

        // apasă Enter în password field → login direct
        txtPassword.setOnAction(e -> controller.handleLogin(txtUsername.getText().trim(), txtPassword.getText().trim(), this));

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));
        layout.getChildren().addAll(
                new Label("Username:"), txtUsername,
                new Label("Password:"), txtPassword,
                btnLogin
        );

        Scene scene = new Scene(layout, 300, 200);
        stage.setScene(scene);

        stage.setOnCloseRequest(e -> Platform.exit());
    }

    public void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    public void clearFields() {
        txtUsername.clear();
        txtPassword.clear();
    }

    public void show() {
        stage.show();
    }

    public void close() {
        stage.close();
    }
}
