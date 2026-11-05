package fr.efrei2026.springfield.exceptions;

public class IncidentIntrouvableException extends RessourceIntrouvableException {

    public IncidentIntrouvableException(Integer id) {
        super("L'incident n°" + id + " n'existe pas.");
    }
}
