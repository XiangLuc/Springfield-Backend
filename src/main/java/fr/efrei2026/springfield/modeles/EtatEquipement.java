package fr.efrei2026.springfield.modeles;

public enum EtatEquipement {

    EN_SERVICE("En service"),
    DEGRADE("Dégradé"),
    HORS_SERVICE("Hors service");

    private final String libelle;

    EtatEquipement(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}