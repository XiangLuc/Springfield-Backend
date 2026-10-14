package fr.efrei2026.springfield.exceptions;

public class StationIntrouvableException extends RessourceIntrouvableException {

    public StationIntrouvableException(Integer id) {
        super("La station n°" + id + " n'existe pas.");
    }
}