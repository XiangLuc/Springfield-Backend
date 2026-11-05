package fr.efrei2026.springfield.modeles.enums;

public enum Profil {
    AGENT("Agent"),
    RESPONSABLE_EXPLOITATION("Responsable d'exploitation");

    private final String libelle;

    Profil(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}