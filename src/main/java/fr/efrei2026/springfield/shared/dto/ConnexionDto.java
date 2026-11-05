package fr.efrei2026.springfield.shared.dto;

import jakarta.validation.constraints.NotBlank;

public class ConnexionDto {

    @NotBlank(message = "L'identifiant est obligatoire.")
    private String login;

    @NotBlank(message = "Le mot de passe est obligatoire.")
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