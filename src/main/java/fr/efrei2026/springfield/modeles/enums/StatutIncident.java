package fr.efrei2026.springfield.modeles.enums;

public enum StatutIncident {

    OUVERT("Non affecté"),
    AFFECTE("Affecté"),
    RESOLU("Résolu");

    private final String libelle;

    StatutIncident(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}