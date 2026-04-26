package ro.mpp2024.gui;

import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ro.mpp2024.model.CharityCase;
import ro.mpp2024.model.Donation;
import ro.mpp2024.model.Donor;
import ro.mpp2024.model.Volunteer;
import ro.mpp2024.services.ITeledonObserver;
import ro.mpp2024.services.ITeledonServices;
import ro.mpp2024.services.TeledonException;

import java.time.LocalDateTime;

public class MainWindowController implements ITeledonObserver {
    private ITeledonServices server;
    private MainWindow view;
    private Volunteer volunteer;

    private static Logger logger = LogManager.getLogger(MainWindowController.class);

    public MainWindowController(ITeledonServices server) {
        this.server = server;
    }

    public void setView(MainWindow view) {
        this.view = view;
    }

    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
        if (view != null) {
            view.setTitle("Teledon - " + volunteer.getUsername());
        }
        loadCharityCases();
        loadAllDonors();
    }

    public void loadCharityCases() {
        try {
            var cases = server.findAllCharityCases();
            Platform.runLater(() -> view.updateCharityCases(cases));
        } catch (TeledonException e) {
            Platform.runLater(() -> view.showAlert("red", "Failed to load cases: " + e.getMessage()));
        }
    }

    public void loadAllDonors() {
        try {
            var donors = server.findAllDonors();
            Platform.runLater(() -> view.updateDonors(donors));
        } catch (TeledonException e) {
            Platform.runLater(() -> view.showAlert("red", "Failed to load donors: " + e.getMessage()));
        }
    }

    public void handleSearch(String query) {
        try {
            Iterable<Donor> donors;
            if (query.isBlank()) {
                donors = server.findAllDonors();
            } else {
                donors = server.findDonorsByName(query);
            }
            Platform.runLater(() -> view.updateDonors(donors));
        } catch (TeledonException e) {
            Platform.runLater(() -> view.showAlert("red", "Search failed: " + e.getMessage()));
        }
    }

    public void saveDonation(CharityCase selectedCase, Donor selectedDonor, String firstName, String lastName, String address, String phone, double amount) {
        try {
            Donor donor;
            if (selectedDonor != null &&
                    selectedDonor.getFirstName().equals(firstName) &&
                    selectedDonor.getLastName().equals(lastName)) {
                donor = selectedDonor;
            } else {
                donor = new Donor(firstName, lastName, address, phone);
                donor = server.saveDonor(donor);
            }

            Donation donation = new Donation(amount, LocalDateTime.now(), donor, selectedCase);
            server.saveDonation(donation);

            Platform.runLater(() -> {
                view.clearDonationForm();
                view.showAlert("green", "Donation saved successfully.");
            });
        } catch (TeledonException e) {
            Platform.runLater(() -> view.showAlert("red", "Failed to save donation: " + e.getMessage()));
        }
    }

    public void updateDonor(Donor donorToUpdate, String newFirstName, String newLastName, String newAddress, String newPhone) {
        donorToUpdate.setFirstName(newFirstName);
        donorToUpdate.setLastName(newLastName);
        donorToUpdate.setAddress(newAddress);
        donorToUpdate.setPhoneNumber(newPhone);

        try {
            server.updateDonor(donorToUpdate);
            Platform.runLater(() -> {
                view.clearDonorForm();
                handleSearch(view.getSearchQuery());
                view.showAlert("green", "Donor updated successfully.");
            });
        } catch (TeledonException e) {
            Platform.runLater(() -> view.showAlert("red", "Failed to update donor: " + e.getMessage()));
        }
    }

    @Override
    public void charityCaseUpdated(CharityCase charityCase) throws TeledonException {
        Platform.runLater(() -> {
            view.updateCharityCase(charityCase);
        });
    }

    @Override
    public void donorUpdated(Donor donor) throws TeledonException {
        Platform.runLater(() -> {
            if (view != null) {
                view.updateDonor(donor);
            }
        });
    }
}
