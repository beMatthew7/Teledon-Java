package ro.mpp2024.service;

import ro.mpp2024.domain.Volunteer;
import ro.mpp2024.repository.VolunteerRepository;
import ro.mpp2024.utils.PasswordUtils;

import static ro.mpp2024.utils.PasswordUtils.hashPassword;

public class VolunteerService {
    private VolunteerRepository volunteerRepository;

    public VolunteerService(VolunteerRepository volunteerRepository) {
        this.volunteerRepository = volunteerRepository;
    }

    public Volunteer login(String username, String password) {
        Volunteer volunteer = volunteerRepository.findByUsername(username);
        if (volunteer == null || !PasswordUtils.verifyPassword(password, volunteer.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }
        return volunteer;

    }
}
