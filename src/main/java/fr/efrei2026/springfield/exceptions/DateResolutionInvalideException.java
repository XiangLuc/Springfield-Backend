package fr.efrei2026.springfield.exceptions;

public class DateResolutionInvalideException extends RegleMetierException {

    public DateResolutionInvalideException(Integer id) {
        super("L'incident n°" + id + " ne peut pas être résolu avant sa date d'ouverture.");
    }
}
