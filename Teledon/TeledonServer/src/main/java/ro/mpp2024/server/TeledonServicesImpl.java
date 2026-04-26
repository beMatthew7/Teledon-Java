package ro.mpp2024.server;

import ro.mpp2024.model.CharityCase;
import ro.mpp2024.model.Donation;
import ro.mpp2024.model.Donor;
import ro.mpp2024.model.Volunteer;
import ro.mpp2024.repository.CharityCaseRepository;
import ro.mpp2024.repository.DonationRepository;
import ro.mpp2024.repository.DonorRepository;
import ro.mpp2024.repository.VolunteerRepository;
import ro.mpp2024.repository.jdbc.PasswordUtils;
import ro.mpp2024.services.ITeledonObserver;
import ro.mpp2024.services.ITeledonServices;
import ro.mpp2024.services.TeledonException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TeledonServicesImpl implements ITeledonServices {

    private VolunteerRepository volunteerRepository;
    private DonorRepository donorRepository;
    private DonationRepository donationRepository;
    private CharityCaseRepository charityCaseRepository;

    private Map<Long, ITeledonObserver> loggedVolunteers;

    public TeledonServicesImpl(VolunteerRepository volunteerRepository, DonorRepository donorRepository, DonationRepository donationRepository, CharityCaseRepository charityCaseRepository) {
        this.volunteerRepository = volunteerRepository;
        this.donorRepository = donorRepository;
        this.donationRepository = donationRepository;
        this.charityCaseRepository = charityCaseRepository;
        loggedVolunteers = new ConcurrentHashMap<>();
    }

    @Override
    public Volunteer login(String username, String password, ITeledonObserver client) throws TeledonException {
        Volunteer volunteer = volunteerRepository.findByUsername(username);
        if (volunteer == null || !PasswordUtils.verifyPassword(password, volunteer.getPassword())) {
            throw new TeledonException("Invalid username or password");
        }
        if(loggedVolunteers.containsKey(volunteer.getId())) {
            throw new TeledonException("Volunteer already logged in.");
        }
        loggedVolunteers.put(volunteer.getId(), client);
        return volunteer;
    }

    @Override
    public void logout(Volunteer volunteer, ITeledonObserver client) throws TeledonException {
        if(loggedVolunteers.get(volunteer.getId()) == null) {
            throw new TeledonException("Volunteer not logged in.");
        }
        loggedVolunteers.remove(volunteer.getId());
    }

    @Override
    public Iterable<CharityCase> findAllCharityCases() throws TeledonException {
        return (List<CharityCase>) charityCaseRepository.findAll();
    }

    @Override
    public Donation saveDonation(Donation donation) throws TeledonException {
        Donation savedDonation = donationRepository.save(donation);
        CharityCase charityCase = donation.getCharityCase();
        charityCase.setTotalAmount(charityCase.getTotalAmount() + donation.getAmount());
        charityCaseRepository.update(charityCase);

        for(ITeledonObserver observer : loggedVolunteers.values()) {
            try{
                observer.charityCaseUpdated(charityCase);
            } catch (TeledonException e){
                System.out.println("Error notifying volunteer: " + e.getMessage());
            }
        }
        return savedDonation;
    }

    @Override
    public Iterable<Donor> findAllDonors() throws TeledonException {
        return donorRepository.findAll();
    }

    @Override
    public Iterable<Donor> findDonorsByName(String name) throws TeledonException {
        return (List<Donor>) donorRepository.findByNameContaining(name);
    }

    @Override
    public void updateDonor(Donor donor) throws TeledonException {
        donorRepository.update(donor);
        notifyDonorsUpdated(donor);

    }
    @Override
    public void updateCharityCaseTotal(CharityCase charityCase) {
        charityCaseRepository.update(charityCase);
    }

    @Override
    public Donor saveDonor(Donor donor) {
        Donor savedDonor = donorRepository.save(donor);
        notifyDonorsUpdated(donor);
        return savedDonor;
    }


    private void notifyDonorsUpdated(Donor donor) {
        for (ITeledonObserver client : loggedVolunteers.values()) {
            try {
                client.donorUpdated(donor);
            } catch (TeledonException e) {
                System.err.println("Eroare la notificarea unui client: " + e);
            }
        }
    }
}
