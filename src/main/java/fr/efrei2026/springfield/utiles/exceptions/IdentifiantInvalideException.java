package fr.efrei2026.springfield.utiles.exceptions;

public class IdentifiantInvalideException extends RuntimeException {
    public IdentifiantInvalideException() {
        super("Identifiant ou mot de passe incorrect.");
    }
}