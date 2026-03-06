package ro.mpp2024.repository;

import ro.mpp2024.domain.Volunteer;

public interface VolunteerRepository extends Repository<Long, Volunteer> {

    /**
     * Finds a volunteer by username and password (for login).
     * @param username the volunteer's username
     * @param password the volunteer's password
     * @return the volunteer if found, or null if no match exists
     */
    Volunteer findByUsernameAndPassword(String username, String password);
}


