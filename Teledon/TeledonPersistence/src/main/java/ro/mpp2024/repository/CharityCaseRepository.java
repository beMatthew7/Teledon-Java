package ro.mpp2024.repository;

import ro.mpp2024.model.CharityCase;

public interface CharityCaseRepository extends Repository<Long, CharityCase>{
    Iterable<CharityCase> findByAmountLessThan(Double maxAmount);
}
