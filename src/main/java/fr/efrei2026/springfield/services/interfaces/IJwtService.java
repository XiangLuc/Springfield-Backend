package fr.efrei2026.springfield.services.interfaces;

import fr.efrei2026.springfield.modeles.Utilisateur;

public interface IJwtService {
    String genererJeton(Utilisateur utilisateur);
}