package ro.mpp2024.repository;

import ro.mpp2024.model.Volunteer;

public interface VolunteerRepository extends Repository<Long, Volunteer> {

    /**
     * Finds a volunteer by username and password (for login).
     * @param username the volunteer's username
     * @param password the volunteer's password
     * @return the volunteer if found, or null if no match exists
     */
    Volunteer findByUsernameAndPassword(String username, String password);

    /**
     * Finds a volunteer by username.
     * @param username the volunteer's username
     * @return the volunteer if found, or null if no match exists
     */
    Volunteer findByUsername(String username);
}


