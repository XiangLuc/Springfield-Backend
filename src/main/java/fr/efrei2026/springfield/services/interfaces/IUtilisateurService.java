package fr.efrei2026.springfield.services.interfaces;

import fr.efrei2026.springfield.modeles.Utilisateur;
import fr.efrei2026.springfield.shared.dto.ConnexionDto;
import fr.efrei2026.springfield.shared.dto.InscriptionDto;

public interface IUtilisateurService {
    Utilisateur connecter(ConnexionDto connexion);
    Utilisateur inscrire(InscriptionDto inscription);
    Utilisateur trouverParLogin(String login);
}
