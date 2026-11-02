package fr.efrei2026.springfield.shared.dto;

import fr.efrei2026.springfield.modeles.Equipement;
import fr.efrei2026.springfield.modeles.Incident;
import fr.efrei2026.springfield.modeles.Prestataire;
import fr.efrei2026.springfield.modeles.enums.StatutIncident;

import java.time.LocalDateTime;

public class IncidentDto {

    private final int id;
    private final String description;
    private final LocalDateTime dateOuverture;
    private final StatutIncident statut;
    private final Prestataire prestataire;
    private final LocalDateTime dateResolution;
    private final Equipement equipement;
    private final long dureeOuvertureEnMinutes;

    public IncidentDto(Incident incident, long dureeOuvertureEnMinutes) {
        this.id = incident.getId();
        this.description = incident.getDescription();
        this.dateOuverture = incident.getDateOuverture();
        this.statut = incident.getStatut();
        this.prestataire = incident.getPrestataire();
        this.dateResolution = incident.getDateResolution();
        this.equipement = incident.getEquipement();
        this.dureeOuvertureEnMinutes = dureeOuvertureEnMinutes;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getDateOuverture() {
        return dateOuverture;
    }

    public StatutIncident getStatut() {
        return statut;
    }

    public Prestataire getPrestataire() {
        return prestataire;
    }

    public LocalDateTime getDateResolution() {
        return dateResolution;
    }

    public Equipement getEquipement() {
        return equipement;
    }

    public long getDureeOuvertureEnMinutes() {
        return dureeOuvertureEnMinutes;
    }
}
