package ro.mpp2024.network.jsonprotocol;

public enum ResponseType {
    OK, ERROR,
    GET_ALL_CASES,
    GET_ALL_DONORS,
    FIND_DONORS_BY_NAME,
    CASES_UPDATED,
    DONOR_UPDATED
}
