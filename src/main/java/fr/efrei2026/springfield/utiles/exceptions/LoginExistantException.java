package fr.efrei2026.springfield.utiles.exceptions;

public class LoginExistantException extends RuntimeException {
    public LoginExistantException(String login) {
        super("L'identifiant « " + login + " » est déjà utilisé par un autre compte.");
    }
}
