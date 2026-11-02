package fr.efrei2026.springfield.exceptions;

public class PrestataireIntrouvableException extends RessourceIntrouvableException {

    public PrestataireIntrouvableException(Integer id) {
        super("Le prestataire n°" + id + " n'existe pas.");
    }
}
