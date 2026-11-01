package fr.efrei2026.springfield.shared.dto;

public class NotePrestataireDto {
    private int prestataireId;
    private String nom;
    private double noteMoyenne;
    private long nombreEvaluations;

    public NotePrestataireDto() {
    }

    public NotePrestataireDto(int prestataireId, String nom, Double noteMoyenne, Long nombreEvaluations) {
        this.prestataireId = prestataireId;
        this.nom = nom;
        this.noteMoyenne = noteMoyenne;
        this.nombreEvaluations = nombreEvaluations;
    }

    public int getPrestataireId() {
        return prestataireId;
    }

    public void setPrestataireId(int prestataireId) {
        this.prestataireId = prestataireId;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public double getNoteMoyenne() {
        return noteMoyenne;
    }

    public void setNoteMoyenne(double noteMoyenne) {
        this.noteMoyenne = noteMoyenne;
    }

    public long getNombreEvaluations() {
        return nombreEvaluations;
    }

    public void setNombreEvaluations(long nombreEvaluations) {
        this.nombreEvaluations = nombreEvaluations;
    }
}
