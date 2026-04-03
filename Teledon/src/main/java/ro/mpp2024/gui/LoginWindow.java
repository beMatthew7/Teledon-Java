package ro.mpp2024.gui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.w3c.dom.Text;
import ro.mpp2024.domain.Volunteer;
import ro.mpp2024.service.CharityCaseService;
import ro.mpp2024.service.DonationService;
import ro.mpp2024.service.DonorService;
import ro.mpp2024.service.VolunteerService;



public class LoginWindow {
    private VolunteerService volunteerService;
    private CharityCaseService charityCaseService;
    private DonorService donorService;
    private DonationService donationService;

    private Stage stage;


    private TextField txtFieldUsername;
    private PasswordField passwordField;

    public LoginWindow(VolunteerService volunteerService, CharityCaseService charityCaseSerrvice, DonorService donorService, DonationService donationService) {
        this.charityCaseService = charityCaseSerrvice;
        this.donorService = donorService;
        this.donationService = donationService;
        this.volunteerService = volunteerService;
        initWindow();
    }

    private void initWindow(){
        stage = new Stage();
        stage.setTitle("Login");

        txtFieldUsername = new TextField();
        txtFieldUsername.setPromptText("username");
        passwordField = new PasswordField();
        passwordField.setPromptText("password");

        Button loginBtn = new Button("Login");

        loginBtn.setOnAction(e->handleLogin());

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));
        layout.getChildren().addAll(txtFieldUsername, passwordField, loginBtn);

        Scene scene = new Scene(layout, 600, 500);
        stage.setScene(scene);
        stage.show();

    }


    private void handleLogin(){
        String username = txtFieldUsername.getText();
        String password = passwordField.getText();
        try{
            Volunteer volunteer = volunteerService.login(username, password);
            new MainWindow(volunteer, charityCaseService, donorService, donationService);
            stage.close();
        }catch(RuntimeException e){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid username or password");
            alert.show();
            txtFieldUsername.clear();
            passwordField.clear();
        }
    }
}
