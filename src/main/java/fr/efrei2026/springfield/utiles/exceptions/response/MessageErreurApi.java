package fr.efrei2026.springfield.utiles.exceptions.response;

public class MessageErreurApi {
    private final String message;

    public MessageErreurApi(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}