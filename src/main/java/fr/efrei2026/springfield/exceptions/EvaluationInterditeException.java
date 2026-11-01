package fr.efrei2026.springfield.exceptions;

public class EvaluationInterditeException extends RuntimeException {

    public EvaluationInterditeException(String message) {
        super(message);
    }

    public EvaluationInterditeException(String message, Throwable cause) {
        super(message, cause);
    }
}
