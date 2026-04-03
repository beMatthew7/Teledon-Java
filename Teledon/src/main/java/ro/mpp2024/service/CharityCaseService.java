package ro.mpp2024.service;

import ro.mpp2024.domain.CharityCase;
import ro.mpp2024.repository.CharityCaseRepository;

import java.util.List;

public class CharityCaseService {
    private CharityCaseRepository charityCaseRepository;

    public CharityCaseService(CharityCaseRepository charityCaseRepository) {
        this.charityCaseRepository = charityCaseRepository;
    }

    public List<CharityCase> findAllCharityCases() {
        return (List<CharityCase>) charityCaseRepository.findAll();
    }

    public void updateCharityCaseTotal(CharityCase charityCase) {
        charityCaseRepository.update(charityCase);
    }
}
