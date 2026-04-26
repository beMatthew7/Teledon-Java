package ro.mpp2024.services;

import ro.mpp2024.model.CharityCase;
import ro.mpp2024.model.Donor;

public interface ITeledonObserver {
    void charityCaseUpdated(CharityCase charityCase) throws TeledonException;
    void donorUpdated(Donor donor) throws TeledonException;
}
