package fr.efrei2026.springfield.utiles.exceptions;

public class CsvException extends RuntimeException {
    public CsvException(String message, Throwable erreur) {
        super(message, erreur);
    }
}
