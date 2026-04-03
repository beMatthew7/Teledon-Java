package ro.mpp2024.service;

import ro.mpp2024.domain.Donor;

import ro.mpp2024.repository.DonorRepository;

import java.util.List;
public class DonorService {

    private DonorRepository donorRepository;

    public DonorService(DonorRepository donorRepository) {
        this.donorRepository = donorRepository;
    }

    public List<Donor> findAllDonors() {
        return (List<Donor>) donorRepository.findAll();
    }

    public List<Donor> findDonorsByName(String query) {
        return (List<Donor>) donorRepository.findByNameContaining(query);
    }

    public void updateDonor(Donor selectedDonor) {
        donorRepository.update(selectedDonor);
    }

    public Donor saveDonor(Donor donor) {
        return donorRepository.save(donor);
    }
}
