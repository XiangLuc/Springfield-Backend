package fr.efrei2026.springfield.utiles.exceptions;

public class LoginExistantException extends RuntimeException  {
    public LoginExistantException() {
        super("L'utilisateur existe déjà ...");
    }
}
