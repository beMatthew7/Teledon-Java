package ro.mpp2024.services;

public class TeledonException extends Exception {
    public TeledonException() {
    }

    public TeledonException(String message) {
        super(message);
    }

    public TeledonException(String message, Throwable cause) {
        super(message, cause);
    }
}
