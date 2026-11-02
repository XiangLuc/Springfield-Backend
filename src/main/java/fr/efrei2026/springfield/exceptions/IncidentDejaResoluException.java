package fr.efrei2026.springfield.exceptions;

public class IncidentDejaResoluException extends RegleMetierException {

    public IncidentDejaResoluException(Integer id) {
        super("L'incident n°" + id + " est déjà résolu : il ne peut plus être modifié, affecté ni résolu à nouveau.");
    }
}
