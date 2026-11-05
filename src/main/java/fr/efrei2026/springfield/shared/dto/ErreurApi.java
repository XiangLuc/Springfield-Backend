package fr.efrei2026.springfield.shared.dto;

public class ErreurApi {

    private final String message;

    public ErreurApi(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}