package fr.efrei2026.springfield.exceptions;

public class TriInvalideException extends RuntimeException {

    public TriInvalideException(String propriete) {
        super("Le tri sur « " + propriete + " » n'est pas autorisé.");
    }
}
