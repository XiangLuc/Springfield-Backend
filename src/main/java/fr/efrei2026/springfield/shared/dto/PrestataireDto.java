package fr.efrei2026.springfield.shared.dto;

public class PrestataireDto {

    private final int id;
    private final String nom;
    private final String email;
    private final String telephone;
    private final Double noteMoyenne;
    private final long nombreEvaluations;

    public PrestataireDto(int id, String nom, String email, String telephone, Double noteMoyenne, Long nombreEvaluations) {
        this.id = id;
        this.nom = nom;
        this.email = email;
        this.telephone = telephone;
        this.noteMoyenne = noteMoyenne;
        this.nombreEvaluations = nombreEvaluations;
    }

    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getEmail() {
        return email;
    }

    public String getTelephone() {
        return telephone;
    }

    public Double getNoteMoyenne() {
        return noteMoyenne;
    }

    public long getNombreEvaluations() {
        return nombreEvaluations;
    }
}
