package ro.mpp2024.repository;

import ro.mpp2024.domain.Donor;

public interface DonorRepository extends Repository<Long, Donor> {

    /**
     * Searches for donors whose name contains the given substring.
     * @param nameSubstring part of the donor's name
     * @return the list of donors whose name contains the given substring
     */
    Iterable<Donor> findByNameContaining(String nameSubstring);
}


