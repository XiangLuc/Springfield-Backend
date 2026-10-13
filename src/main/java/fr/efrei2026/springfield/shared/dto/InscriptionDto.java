package fr.efrei2026.springfield.shared.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class InscriptionDto {

    @NotBlank
    @Size(max = 50)
    private String login;

    @NotBlank
    @Size(min = 15, max = 128)
    private String motDePasse;

    @NotBlank
    @Size(max = 50)
    private String prenom;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }
}