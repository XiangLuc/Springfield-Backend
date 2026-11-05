package fr.efrei2026.springfield.exceptions;

public class ConfirmationSurveillanceRequiseException extends RuntimeException {

    public ConfirmationSurveillanceRequiseException(String nomPrestataire) {
        super("Le prestataire « " + nomPrestataire + " » est sous surveillance : confirmez explicitement l'affectation pour continuer.");
    }
}
