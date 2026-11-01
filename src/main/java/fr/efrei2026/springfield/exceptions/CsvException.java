package fr.efrei2026.springfield.exceptions;

public class CsvException extends RuntimeException {
    public CsvException(String message, Throwable erreur) {
        super(message, erreur);
    }
    public CsvException(String message) {
        super(message);
    }
}
