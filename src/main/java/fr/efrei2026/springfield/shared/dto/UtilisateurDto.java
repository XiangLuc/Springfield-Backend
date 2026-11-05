package fr.efrei2026.springfield.shared.dto;

import fr.efrei2026.springfield.modeles.Utilisateur;
import fr.efrei2026.springfield.modeles.enums.Profil;

public class UtilisateurDto {

    private final String prenom;
    private final Profil profil;

    public UtilisateurDto(Utilisateur utilisateur) {
        this.prenom = utilisateur.getPrenom();
        this.profil = utilisateur.getProfil();
    }

    public String getPrenom() {
        return prenom;
    }

    public Profil getProfil() {
        return profil;
    }

    public String getLibelleProfil() {
        return profil.getLibelle();
    }
}
