package fr.efrei2026.springfield.services.interfaces;

import fr.efrei2026.springfield.modeles.Prestataire;

import java.util.List;

public interface IPrestataireService {
    List<Prestataire> listerPrestataires();
    Prestataire trouverPrestataire(Integer id);
}
