package fr.efrei2026.springfield.shared.dto;

import fr.efrei2026.springfield.modeles.Incident;
import fr.efrei2026.springfield.modeles.enums.StatutIncident;

import java.time.LocalDateTime;

public record IncidentDto(int id,
                          String description,
                          LocalDateTime dateOuverture,
                          StatutIncident statut,
                          PrestataireDto prestataire,
                          LocalDateTime dateResolution) {

    public static IncidentDto depuis(Incident incident) {
        PrestataireDto prestataire = incident.getPrestataire() == null
                ? null
                : PrestataireDto.depuis(incident.getPrestataire());
        return new IncidentDto(
                incident.getId(),
                incident.getDescription(),
                incident.getDateOuverture(),
                incident.getStatut(),
                prestataire,
                incident.getDateResolution());
    }
}
