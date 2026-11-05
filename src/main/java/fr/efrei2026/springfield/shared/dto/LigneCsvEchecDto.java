package fr.efrei2026.springfield.shared.dto;

public class LigneCsvEchecDto {

    private int numeroLigne;
    private String contenu;
    private String raison;

    public LigneCsvEchecDto(int numeroLigne, String contenu, String raison) {
        this.numeroLigne = numeroLigne;
        this.contenu = contenu;
        this.raison = raison;
    }

    public int getNumeroLigne() {
        return numeroLigne;
    }

    public void setNumeroLigne(int numeroLigne) {
        this.numeroLigne = numeroLigne;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public String getRaison() {
        return raison;
    }

    public void setRaison(String raison) {
        this.raison = raison;
    }
}
