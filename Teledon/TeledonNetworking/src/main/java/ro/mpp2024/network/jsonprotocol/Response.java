package ro.mpp2024.network.jsonprotocol;

import ro.mpp2024.model.CharityCase;
import ro.mpp2024.model.Donor;
import ro.mpp2024.model.Volunteer;

public class Response {
    private ResponseType type;
    private String errorMessage;
    private Volunteer volunteer;
    private CharityCase[] cases;
    private Donor[] donors;
    private Donor donor;
    private CharityCase charityCase;

    public Response() {}

    public ResponseType getType() { return type; }
    public void setType(ResponseType type) { this.type = type; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public Volunteer getVolunteer() { return volunteer; }
    public void setVolunteer(Volunteer volunteer) { this.volunteer = volunteer; }

    public CharityCase[] getCases() { return cases; }
    public void setCases(CharityCase[] cases) { this.cases = cases; }

    public Donor[] getDonors() { return donors; }
    public void setDonors(Donor[] donors) { this.donors = donors; }

    public Donor getDonor(){ return donor; }
    public void setDonor(Donor donor){ this.donor = donor; }

    public CharityCase getCharityCase() {
        return charityCase;
    }

    public void setCharityCase(CharityCase charityCase) {
        this.charityCase = charityCase;
    }
}
