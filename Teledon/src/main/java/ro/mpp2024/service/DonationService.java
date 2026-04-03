package ro.mpp2024.service;

import ro.mpp2024.domain.Donation;
import ro.mpp2024.repository.DonationRepository;

public class DonationService {
    private DonationRepository donationRepository;

    public DonationService(DonationRepository donationRepository) {
        this.donationRepository = donationRepository;
    }

    public Donation saveDonation(Donation donation) {
        return donationRepository.save(donation);
    }
}
