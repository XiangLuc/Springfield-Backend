package fr.efrei2026.springfield.modeles.enums;

import java.util.Set;

public enum StatutIncident {

    OUVERT("Non affecté"),
    AFFECTE("Affecté"),
    RESOLU("Résolu");

    public static final Set<StatutIncident> EN_COURS = Set.of(OUVERT, AFFECTE);

    private final String libelle;

    StatutIncident(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}