package fr.efrei2026.springfield.services.interfaces;

import fr.efrei2026.springfield.modeles.Utilisateur;

import java.time.Duration;

public interface IJwtService {
    String NOM_COOKIE = "jeton";

    String genererJeton(Utilisateur utilisateur);
    Duration getDureeValidite();
}
