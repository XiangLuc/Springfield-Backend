package fr.efrei2026.springfield.services.interfaces;

import fr.efrei2026.springfield.shared.dto.ConnexionDto;
import fr.efrei2026.springfield.shared.dto.InscriptionDto;
import fr.efrei2026.springfield.shared.dto.TokenDto;

public interface IUtilisateurService {
    TokenDto connecter(ConnexionDto connexion);
    TokenDto inscrire(InscriptionDto inscription);
}
