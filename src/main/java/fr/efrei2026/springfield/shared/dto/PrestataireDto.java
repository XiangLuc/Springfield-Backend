package fr.efrei2026.springfield.shared.dto;

import fr.efrei2026.springfield.modeles.Prestataire;

public record PrestataireDto(int id, String nom, String email, String telephone) {

    public static PrestataireDto depuis(Prestataire prestataire) {
        return new PrestataireDto(prestataire.getId(), prestataire.getNom(), prestataire.getEmail(), prestataire.getTelephone());
    }
}
