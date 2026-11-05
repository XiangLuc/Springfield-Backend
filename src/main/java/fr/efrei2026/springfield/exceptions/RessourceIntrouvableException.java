package fr.efrei2026.springfield.exceptions;

public abstract class RessourceIntrouvableException extends RuntimeException {

    protected RessourceIntrouvableException(String message) {
        super(message);
    }
}