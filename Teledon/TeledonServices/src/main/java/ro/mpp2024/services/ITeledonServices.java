package ro.mpp2024.services;

import ro.mpp2024.model.CharityCase;
import ro.mpp2024.model.Donation;
import ro.mpp2024.model.Donor;
import ro.mpp2024.model.Volunteer;

public interface ITeledonServices {
    Volunteer login(String username, String password, ITeledonObserver client) throws TeledonException;
    void logout(Volunteer volunteer, ITeledonObserver client) throws TeledonException;
    Iterable<CharityCase> findAllCharityCases() throws TeledonException;
    Donation saveDonation(Donation donation) throws TeledonException;

    Iterable<Donor> findAllDonors() throws TeledonException;

    Iterable<Donor> findDonorsByName(String name) throws TeledonException;
    void updateDonor(Donor donor) throws TeledonException;
    void updateCharityCaseTotal(CharityCase charityCase) throws TeledonException;

    Donor saveDonor(Donor donor) throws TeledonException;
}
