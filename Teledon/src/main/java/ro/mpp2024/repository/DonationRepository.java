package ro.mpp2024.repository;

import ro.mpp2024.domain.Donation;

public interface DonationRepository extends Repository<Long, Donation> {

    /**
     * Returns all donations for a given charity case.
     * @param charityCaseId the id of the charity case
     * @return the donations for the given charity case
     */
    Iterable<Donation> findByCharityCaseId(Long charityCaseId);

    /**
     * Returns the total amount donated for a given charity case.
     * @param charityCaseId the id of the charity case
     * @return the total donated amount
     */
    Double getTotalAmountForCharityCase(Long charityCaseId);
}


