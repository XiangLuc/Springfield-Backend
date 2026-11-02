package fr.efrei2026.springfield.exceptions;

// Opération refusée parce qu'elle enfreindrait une règle de gestion (transition d'état interdite…)
public abstract class RegleMetierException extends RuntimeException {

    protected RegleMetierException(String message) {
        super(message);
    }
}
