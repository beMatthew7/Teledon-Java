package ro.mpp2024.network.jsonprotocol;

import ro.mpp2024.model.CharityCase;
import ro.mpp2024.model.Donation;
import ro.mpp2024.model.Donor;
import ro.mpp2024.model.Volunteer;

public class JsonProtocolUtils {

    // ── REQUESTS ──────────────────────────────────────────

    public static Request createLoginRequest(Volunteer volunteer) {
        Request req = new Request();
        req.setType(RequestType.LOGIN);
        req.setVolunteer(volunteer);
        return req;
    }

    public static Request createLogoutRequest(Volunteer volunteer) {
        Request req = new Request();
        req.setType(RequestType.LOGOUT);
        req.setVolunteer(volunteer);
        return req;
    }

    public static Request createGetAllCasesRequest() {
        Request req = new Request();
        req.setType(RequestType.GET_ALL_CASES);
        return req;
    }

    public static Request createGetAllDonorsRequest() {
        Request req = new Request();
        req.setType(RequestType.GET_ALL_DONORS);
        return req;
    }

    public static Request createFindDonorsByNameRequest(String name) {
        Request req = new Request();
        req.setType(RequestType.FIND_DONORS_BY_NAME);
        req.setName(name);
        return req;
    }

    public static Request createSaveDonationRequest(Donation donation) {
        Request req = new Request();
        req.setType(RequestType.SAVE_DONATION);
        req.setDonation(donation);
        return req;
    }

    public static Request createUpdateDonorRequest(Donor donor) {
        Request req = new Request();
        req.setType(RequestType.UPDATE_DONOR);
        req.setDonor(donor);
        return req;
    }

    // ── RESPONSES ─────────────────────────────────────────

    public static Response createOkResponse() {
        Response resp = new Response();
        resp.setType(ResponseType.OK);
        return resp;
    }

    public static Response createErrorResponse(String message) {
        Response resp = new Response();
        resp.setType(ResponseType.ERROR);
        resp.setErrorMessage(message);
        return resp;
    }

    public static Response createLoginResponse(Volunteer volunteer) {
        Response resp = new Response();
        resp.setType(ResponseType.OK);
        resp.setVolunteer(volunteer);
        return resp;
    }

    public static Response createGetAllCasesResponse(CharityCase[] cases) {
        Response resp = new Response();
        resp.setType(ResponseType.GET_ALL_CASES);
        resp.setCases(cases);
        return resp;
    }

    public static Response createGetAllDonorsResponse(Donor[] donors) {
        Response resp = new Response();
        resp.setType(ResponseType.GET_ALL_DONORS);
        resp.setDonors(donors);
        return resp;
    }

    public static Response createFindDonorsByNameResponse(Donor[] donors) {
        Response resp = new Response();
        resp.setType(ResponseType.FIND_DONORS_BY_NAME);
        resp.setDonors(donors);
        return resp;
    }

    public static Response createCasesUpdatedResponse(CharityCase charityCase) {
        Response resp = new Response();
        resp.setType(ResponseType.CASES_UPDATED);
        resp.setCharityCase(charityCase);
        return resp;
    }
    public static Response createDonorUpdatedResponse(Donor donor) {
        Response resp = new Response();
        resp.setType(ResponseType.DONOR_UPDATED);
        resp.setDonor(donor);
        return resp;
    }

    public static Request createSaveDonorRequest(Donor donor) {
        Request req = new Request();
        req.setType(RequestType.SAVE_DONOR);
        req.setDonor(donor);
        return req;
    }

    public static Response createSaveDonorResponse(Donor donor) {
        Response resp = new Response();
        resp.setType(ResponseType.OK);
        resp.setDonor(donor);
        return resp;
    }


}
