package fr.efrei2026.springfield.modeles.enums;

public enum NiveauAttention {

    ROUGE("Rouge"),
    ORANGE("Orange"),
    VERT("Vert"),
    AFFECTE("Pris en charge");

    private final String libelle;

    NiveauAttention(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}