package ro.mpp2024.gui;

import ro.mpp2024.domain.CharityCase;
import ro.mpp2024.domain.Donation;
import ro.mpp2024.domain.Donor;
import ro.mpp2024.domain.Volunteer;
import ro.mpp2024.repository.DonorRepository;
import ro.mpp2024.service.CharityCaseService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import ro.mpp2024.service.DonationService;
import ro.mpp2024.service.DonorService;

import java.time.LocalDateTime;

public class MainWindow {

    private CharityCaseService charityCaseService;
    private DonorService donorService;
    private DonationService donationService;
    private Volunteer volunteer;
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

    public MainWindow(Volunteer volunteer,CharityCaseService charityCaseService, DonorService donorService, DonationService donationService) {
        this.volunteer = volunteer;
        this.charityCaseService = charityCaseService;
        this.donorService = donorService;
        this.donationService = donationService;
        initWindow();
        loadCharityCases();
        loadAllDonors();
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

        Scene scene = new Scene(root, 1100, 680);
        stage.setScene(scene);
        stage.show();
    }

    // DATA LOADING

    private void loadCharityCases() {
        charityCasesModel.clear();
        charityCaseService.findAllCharityCases().forEach(charityCasesModel::add);
    }

    private void loadAllDonors() {
        donorsModel.clear();
        donorService.findAllDonors().forEach(donorsModel::add);
    }

    // HANDLERS

    private void handleSaveDonation() {
        if (selectedCase == null) {
            showAlert("Warning", "Please select a charity case first.");
            return;
        }
        if (!validateDonationForm()) return;

        try {
            // Find or create donor
            String firstName = txtDonorFirstName.getText().trim();
            String lastName  = txtDonorLastName.getText().trim();
            String address  = txtDonorAddress.getText().trim();
            String phone    = txtDonorPhone.getText().trim();
            double amount   = Double.parseDouble(txtDonationAmount.getText().trim());

            Donor donor;
            if (selectedDonor != null &&
                    selectedDonor.getFirstName().equals(firstName) &&
                    selectedDonor.getLastName().equals(lastName)) {
                donor = selectedDonor;
            } else {
                donor = new Donor(firstName, lastName, address, phone);
                donor = donorService.saveDonor(donor);
            }

            Donation donation = new Donation(amount, LocalDateTime.now(), donor, selectedCase);
            donationService.saveDonation(donation);
            selectedCase.setTotalAmount(selectedCase.getTotalAmount() + amount);

            charityCaseService.updateCharityCaseTotal(selectedCase);

            clearDonationForm();
            loadCharityCases();
            showAlert("Success", "Donation saved successfully.");
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Amount must be a valid number.");
        } catch (RuntimeException e) {
            showAlert("Error", "Failed to save donation: " + e.getMessage());
        }
    }

    private void handleSearch() {
        String query = txtSearch.getText().trim();
        if (query.isBlank()) {
            loadAllDonors();
            return;
        }
        donorsModel.clear();
        donorService.findDonorsByName(query).forEach(donorsModel::add);
        clearDonorForm();
    }

    private void handleUpdateDonor() {
        if (selectedDonor == null) {
            showAlert("Warning", "Please select a donor to update.");
            return;
        }
        if (!validateDonorForm()) return;

        selectedDonor.setFirstName(txtEditFirstName.getText().trim());
        selectedDonor.setLastName(txtEditLastName.getText().trim());
        selectedDonor.setAddress(txtEditAddress.getText().trim());
        selectedDonor.setPhoneNumber(txtEditPhone.getText().trim());

        try {
            donorService.updateDonor(selectedDonor);
            clearDonorForm();
            handleSearch();
            showAlert("Success", "Donor updated successfully.");
        } catch (RuntimeException e) {
            showAlert("Error", "Failed to update donor: " + e.getMessage());
        }
    }

    //HELPERS

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
            showAlert("Validation Error", "All donation fields are required.");
            return false;
        }
        return true;
    }

    private boolean validateDonorForm() {
        if (txtEditFirstName.getText().isBlank()
                || txtEditLastName.getText().isBlank()
                || txtEditAddress.getText().isBlank()
                || txtEditPhone.getText().isBlank()) {
            showAlert("Validation Error", "All donor fields are required.");
            return false;
        }
        return true;
    }

    private void clearDonationForm() {
        txtDonorFirstName.clear();
        txtDonorLastName.clear();
        txtDonorAddress.clear();
        txtDonorPhone.clear();
        txtDonationAmount.clear();
        selectedDonor = null;
        donorsTable.getSelectionModel().clearSelection();
    }

    private void clearDonorForm() {
        txtEditFirstName.clear();
        txtEditLastName.clear();
        txtEditAddress.clear();
        txtEditPhone.clear();
        donorsTable.getSelectionModel().clearSelection();
        selectedDonor = null;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    public void show() {
        stage.show();
    }
}