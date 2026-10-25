package fr.efrei2026.springfield.utiles.exceptions;

public class UtilisateurIntrouvableException extends RuntimeException {
    public UtilisateurIntrouvableException(String login) {
        super("L'utilisateur « " + login + " » n'existe pas.");
    }
}
