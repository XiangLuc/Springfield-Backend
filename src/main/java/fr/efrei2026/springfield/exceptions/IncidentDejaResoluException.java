package fr.efrei2026.springfield.exceptions;

public class IncidentDejaResoluException extends RuntimeException {

    public IncidentDejaResoluException(Integer id) {
        super("L'incident n°" + id + " est déjà résolu.");
    }
}
