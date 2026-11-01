package fr.efrei2026.springfield.shared.dto;

public class PrestataireDto {

    private int id;
    private String nom;
    private String email;
    private String telephone;
    private Double noteMoyenne;
    private long nombreEvaluations;

    // Un seul constructeur : Spring Data s'en sert pour construire ce DTO à partir de listerAvecNotes.
    public PrestataireDto(Integer id, String nom, String email, String telephone, Double noteMoyenne,
                          Long nombreEvaluations) {
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

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Double getNoteMoyenne() {
        return noteMoyenne;
    }

    public void setNoteMoyenne(Double noteMoyenne) {
        this.noteMoyenne = noteMoyenne;
    }

    public long getNombreEvaluations() {
        return nombreEvaluations;
    }

    public void setNombreEvaluations(long nombreEvaluations) {
        this.nombreEvaluations = nombreEvaluations;
    }
}