package ro.mpp2024.network.jsonprotocol;

import ro.mpp2024.model.Donation;
import ro.mpp2024.model.Donor;
import ro.mpp2024.model.Volunteer;

public class Request {
    private RequestType type;
    private Volunteer volunteer;
    private Donation donation;
    private Donor donor;
    private String name; // pentru search

    public Request() {}

    public RequestType getType() { return type; }
    public void setType(RequestType type) { this.type = type; }

    public Volunteer getVolunteer() { return volunteer; }
    public void setVolunteer(Volunteer volunteer) { this.volunteer = volunteer; }

    public Donation getDonation() { return donation; }
    public void setDonation(Donation donation) { this.donation = donation; }

    public Donor getDonor() { return donor; }
    public void setDonor(Donor donor) { this.donor = donor; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
