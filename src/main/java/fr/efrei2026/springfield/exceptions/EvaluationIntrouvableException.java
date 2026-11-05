package fr.efrei2026.springfield.exceptions;

public class EvaluationIntrouvableException extends RessourceIntrouvableException {

    public EvaluationIntrouvableException(Integer incidentId) {
        super("Aucune évaluation n'existe pour l'incident n°" + incidentId + ".");
    }
}
