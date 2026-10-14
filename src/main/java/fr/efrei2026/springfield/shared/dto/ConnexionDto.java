package fr.efrei2026.springfield.shared.dto;

import jakarta.validation.constraints.NotBlank;

public class ConnexionDto {

    @NotBlank
    private String login;

    @NotBlank
    private String motDePasse;

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
}