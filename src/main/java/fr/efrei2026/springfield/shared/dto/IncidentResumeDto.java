package fr.efrei2026.springfield.shared.dto;

import fr.efrei2026.springfield.modeles.Incident;
import fr.efrei2026.springfield.modeles.enums.StatutIncident;

import java.time.Duration;
import java.time.LocalDateTime;

public record IncidentResumeDto(int id,
                                String description,
                                LocalDateTime dateOuverture,
                                StatutIncident statut,
                                PrestataireDto prestataire,
                                LocalDateTime dateResolution,
                                EquipementDto equipement,
                                long dureeOuvertureEnMinutes) {

    public static IncidentResumeDto depuis(Incident incident, Duration dureeOuverture) {
        PrestataireDto prestataire = incident.getPrestataire() == null
                ? null
                : PrestataireDto.depuis(incident.getPrestataire());
        return new IncidentResumeDto(
                incident.getId(),
                incident.getDescription(),
                incident.getDateOuverture(),
                incident.getStatut(),
                prestataire,
                incident.getDateResolution(),
                EquipementDto.depuis(incident.getEquipement()),
                dureeOuverture.toMinutes());
    }
}
