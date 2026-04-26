package ro.mpp2024.gui;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import ro.mpp2024.model.CharityCase;
import ro.mpp2024.model.Donor;

public class MainWindow {

    private MainWindowController controller;
    private Stage stage;

    // Left side – Charity Cases
    private TableView<CharityCase> charityCasesTable;
    private ObservableList<CharityCase> charityCasesModel = FXCollections.observableArrayList();

    // Donation form fields
    private TextField txtDonorFirstName;
    private TextField txtDonorLastName;
    private TextField txtDonorAddress;
    private TextField txtDonorPhone;
    private TextField txtDonationAmount;
    private Button btnSaveDonation;
    private Button btnClearDonation;

    // Right side – Donors
    private TableView<Donor> donorsTable;
    private ObservableList<Donor> donorsModel = FXCollections.observableArrayList();

    private TextField txtSearch;
    private Button btnSearch;

    // Donor detail / edit fields
    private TextField txtEditFirstName;
    private TextField txtEditLastName;
    private TextField txtEditAddress;
    private TextField txtEditPhone;
    private Button btnUpdateDonor;
    private Button btnClearDonor;

    private CharityCase selectedCase = null;
    private Donor selectedDonor = null;

    private Label alertLabel;

    public MainWindow(MainWindowController controller) {
        this.controller = controller;
        initWindow();
    }

    public void setTitle(String title) {
        if (stage != null) {
            stage.setTitle(title);
        }
    }

    private void initWindow() {
        stage = new Stage();
        stage.setTitle("Charity Manager");

        // LEFT PANEL

        Label lblCases = new Label("Charity Cases");

        charityCasesTable = new TableView<>();
        charityCasesTable.setItems(charityCasesModel);
        charityCasesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<CharityCase, Long> colCaseId = new TableColumn<>("ID");
        colCaseId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCaseId.setMaxWidth(60);

        TableColumn<CharityCase, String> colCaseName = new TableColumn<>("Name");
        colCaseName.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<CharityCase, Double> colCaseTotal = new TableColumn<>("Total Amount");
        colCaseTotal.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));

        charityCasesTable.getColumns().addAll(colCaseId, colCaseName, colCaseTotal);

        charityCasesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedCase = newVal;
            clearDonationForm();
        });

        VBox casesSection = new VBox(6, lblCases, charityCasesTable);
        VBox.setVgrow(charityCasesTable, Priority.ALWAYS);

        // ── DONATION FORM

        txtDonorFirstName = new TextField(); txtDonorFirstName.setPromptText("First name");
        txtDonorLastName  = new TextField(); txtDonorLastName.setPromptText("Last name");
        txtDonorAddress = new TextField(); txtDonorAddress.setPromptText("Address");
        txtDonorPhone  = new TextField(); txtDonorPhone.setPromptText("Phone number");
        txtDonationAmount = new TextField(); txtDonationAmount.setPromptText("Amount");

        btnSaveDonation  = new Button("Save Donation");
        btnClearDonation = new Button("Clear");

        btnSaveDonation.setOnAction(e  -> handleSaveDonation());
        btnClearDonation.setOnAction(e -> clearDonationForm());

        GridPane donationForm = new GridPane();
        donationForm.setHgap(10);
        donationForm.setVgap(8);
        donationForm.addRow(0, new Label("First Name:"),  txtDonorFirstName);
        donationForm.addRow(1, new Label("Last Name:"),   txtDonorLastName);
        donationForm.addRow(3, new Label("Address:"),     txtDonorAddress);
        donationForm.addRow(4, new Label("Phone:"),       txtDonorPhone);
        donationForm.addRow(5, new Label("Amount:"),      txtDonationAmount);

        HBox donationButtons = new HBox(8, btnSaveDonation, btnClearDonation);
        donationButtons.setAlignment(Pos.CENTER_LEFT);

        VBox donationPanel = new VBox(8, new Label("New Donation"), donationForm, donationButtons);
        donationPanel.setPadding(new Insets(8));

        VBox leftPanel = new VBox(10, casesSection, new Separator(), donationPanel);
        leftPanel.setPadding(new Insets(10));
        VBox.setVgrow(casesSection, Priority.ALWAYS);

        // RIGHT PANEL

        Label lblDonors = new Label("Donors");

        donorsTable = new TableView<>();
        donorsTable.setItems(donorsModel);
        donorsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Donor, Long> colDonorId = new TableColumn<>("ID");
        colDonorId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDonorId.setMaxWidth(60);

        TableColumn<Donor, String> colFirstName = new TableColumn<>("First Name");
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<Donor, String> colLastName = new TableColumn<>("Last Name");
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<Donor, String> colAddress = new TableColumn<>("Address");
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));

        TableColumn<Donor, String> colPhone = new TableColumn<>("Phone");
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

        donorsTable.getColumns().addAll(colDonorId, colFirstName, colLastName, colAddress, colPhone);

        donorsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedDonor = newVal;
            if (newVal != null) {
                populateDonorFields(newVal);
                // autofill donation form name/address/phone
                txtDonorFirstName.setText(newVal.getFirstName());
                txtDonorLastName.setText(newVal.getLastName());
                txtDonorAddress.setText(newVal.getAddress());
                txtDonorPhone.setText(newVal.getPhoneNumber());
            }
        });

        // Search bar
        txtSearch = new TextField();
        txtSearch.setPromptText("Search donor by name...");
        btnSearch = new Button("Search");
        btnSearch.setOnAction(e -> handleSearch());
        txtSearch.setOnAction(e -> handleSearch());

        HBox searchBar = new HBox(8, txtSearch, btnSearch);
        searchBar.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(txtSearch, Priority.ALWAYS);

        // Donor edit fields
        txtEditFirstName = new TextField(); txtEditFirstName.setPromptText("First name");
        txtEditLastName  = new TextField(); txtEditLastName.setPromptText("Last name");
        txtEditAddress   = new TextField(); txtEditAddress.setPromptText("Address");
        txtEditPhone     = new TextField(); txtEditPhone.setPromptText("Phone number");

        btnUpdateDonor = new Button("Update Donor");
        btnClearDonor  = new Button("Clear");

        btnUpdateDonor.setOnAction(e -> handleUpdateDonor());
        btnClearDonor.setOnAction(e  -> clearDonorForm());

        GridPane donorForm = new GridPane();
        donorForm.setHgap(10);
        donorForm.setVgap(8);
        donorForm.addRow(0, new Label("First Name:"), txtEditFirstName);
        donorForm.addRow(1, new Label("Last Name:"),  txtEditLastName);
        donorForm.addRow(2, new Label("Address:"),    txtEditAddress);
        donorForm.addRow(3, new Label("Phone:"),      txtEditPhone);

        HBox donorButtons = new HBox(8, btnUpdateDonor, btnClearDonor);
        donorButtons.setAlignment(Pos.CENTER_LEFT);

        VBox donorEditPanel = new VBox(8, new Label("Donor Details"), donorForm, donorButtons);
        donorEditPanel.setPadding(new Insets(8));

        VBox rightPanel = new VBox(6,
                lblDonors,
                donorsTable,
                searchBar,
                new Separator(),
                donorEditPanel);
        rightPanel.setPadding(new Insets(10));
        VBox.setVgrow(donorsTable, Priority.ALWAYS);

        // ROOT LAYOUT

        HBox root = new HBox(10, leftPanel, new Separator(), rightPanel);
        root.setPadding(new Insets(15));
        HBox.setHgrow(leftPanel,  Priority.ALWAYS);
        HBox.setHgrow(rightPanel, Priority.ALWAYS);

        VBox mainLayout = new VBox(10);
        alertLabel = new Label();
        mainLayout.getChildren().add(root);
        mainLayout.getChildren().add(alertLabel);
        mainLayout.setAlignment(Pos.TOP_CENTER);


        Scene scene = new Scene(mainLayout, 1100, 680);
        stage.setScene(scene);
    }

    public void updateCharityCases(Iterable<CharityCase> cases) {
        charityCasesModel.clear();
        cases.forEach(charityCasesModel::add);
    }

    public void updateDonors(Iterable<Donor> donors) {
        donorsModel.clear();
        donors.forEach(donorsModel::add);
        clearDonorForm();
    }

    private void handleSaveDonation() {
        if (selectedCase == null) {
            showAlert("orange", "Please select a charity case first.");
            return;
        }
        if (!validateDonationForm()) return;

        try {
            double amount = Double.parseDouble(txtDonationAmount.getText().trim());
            controller.saveDonation(
                    selectedCase,
                    selectedDonor,
                    txtDonorFirstName.getText().trim(),
                    txtDonorLastName.getText().trim(),
                    txtDonorAddress.getText().trim(),
                    txtDonorPhone.getText().trim(),
                    amount
            );
        } catch (NumberFormatException e) {
            showAlert("red", "Amount must be a valid number.");
        }
    }

    private void handleSearch() {
        controller.handleSearch(getSearchQuery());
    }

    private void handleUpdateDonor() {
        if (selectedDonor == null) {
            showAlert("orange", "Please select a donor to update.");
            return;
        }
        if (!validateDonorForm()) return;

        controller.updateDonor(
                selectedDonor,
                txtEditFirstName.getText().trim(),
                txtEditLastName.getText().trim(),
                txtEditAddress.getText().trim(),
                txtEditPhone.getText().trim()
        );
    }

    private void populateDonorFields(Donor donor) {
        txtEditFirstName.setText(donor.getFirstName());
        txtEditLastName.setText(donor.getLastName());
        txtEditAddress.setText(donor.getAddress());
        txtEditPhone.setText(donor.getPhoneNumber());
    }

    private boolean validateDonationForm() {
        if (txtDonorFirstName.getText().isBlank()
                || txtDonorLastName.getText().isBlank()
                || txtDonorAddress.getText().isBlank()
                || txtDonorPhone.getText().isBlank()
                || txtDonationAmount.getText().isBlank()) {
            showAlert("orange", "All donation fields are required.");
            return false;
        }
        return true;
    }

    private boolean validateDonorForm() {
        if (txtEditFirstName.getText().isBlank()
                || txtEditLastName.getText().isBlank()
                || txtEditAddress.getText().isBlank()
                || txtEditPhone.getText().isBlank()) {
            showAlert("orange", "All donor fields are required.");
            return false;
        }
        return true;
    }

    public void clearDonationForm() {
        txtDonorFirstName.clear();
        txtDonorLastName.clear();
        txtDonorAddress.clear();
        txtDonorPhone.clear();
        txtDonationAmount.clear();
        selectedDonor = null;
        donorsTable.getSelectionModel().clearSelection();
    }

    public void clearDonorForm() {
        txtEditFirstName.clear();
        txtEditLastName.clear();
        txtEditAddress.clear();
        txtEditPhone.clear();
        donorsTable.getSelectionModel().clearSelection();
        selectedDonor = null;
    }

    public String getSearchQuery() {
        return txtSearch.getText().trim();
    }

    public void showAlert(String color, String message) {
        alertLabel.setText(message);
        alertLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");

        FadeTransition fadeIn = new FadeTransition(javafx.util.Duration.millis(500), alertLabel);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        PauseTransition pause = new PauseTransition(javafx.util.Duration.seconds(5));
        FadeTransition fadeOut = new FadeTransition(javafx.util.Duration.millis(500), alertLabel);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        fadeIn.setOnFinished(e -> pause.play());
        pause.setOnFinished(e -> fadeOut.play());
        fadeOut.setOnFinished(e-> alertLabel.setText(""));

        fadeIn.play();
    }

    public void show() {
        stage.show();
    }

    public void updateCharityCase(CharityCase charityCase) {
        for (int i = 0; i < charityCasesModel.size(); i++) {
            if (charityCasesModel.get(i).getId().equals(charityCase.getId())) {
                charityCasesModel.set(i, charityCase);
                return;
            }
        }
        charityCasesModel.add(charityCase);
    }

    public void updateDonor(Donor donor) {
        for (int i = 0; i < donorsModel.size(); i++) {
            if (donorsModel.get(i).getId().equals(donor.getId())) {
                donorsModel.set(i, donor);
                return;
            }
        }
        donorsModel.add(donor);
    }
}