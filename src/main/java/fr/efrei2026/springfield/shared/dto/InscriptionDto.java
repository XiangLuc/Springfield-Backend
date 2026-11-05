package fr.efrei2026.springfield.shared.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class InscriptionDto {

    @NotBlank(message = "L'identifiant est obligatoire.")
    @Size(max = 50, message = "L'identifiant ne doit pas dépasser 50 caractères.")
    private String login;

    @NotBlank(message = "Le mot de passe est obligatoire.")
    @Size(min = 15, max = 128, message = "Le mot de passe doit contenir entre 15 et 128 caractères.")
    private String motDePasse;

    @NotBlank(message = "Le prénom est obligatoire.")
    @Size(max = 50, message = "Le prénom ne doit pas dépasser 50 caractères.")
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