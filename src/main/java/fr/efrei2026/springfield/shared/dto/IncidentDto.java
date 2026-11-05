package fr.efrei2026.springfield.shared.dto;

import fr.efrei2026.springfield.modeles.Incident;
import fr.efrei2026.springfield.modeles.enums.StatutIncident;

import java.time.LocalDateTime;

public class IncidentDto {

    private final int id;
    private final String description;
    private final StatutIncident statut;
    private final String libelleStatut;
    private final LocalDateTime dateOuverture;
    private final LocalDateTime dateResolution;
    private final long dureeOuvertureEnHeures;
    private final int equipementId;
    private final String referenceEquipement;
    private final String libelleEquipement;
    private final String station;
    private final Integer prestataireId;
    private final String prestataire;
    private final boolean prestataireSousSurveillance;

    public IncidentDto(Incident incident, long dureeOuvertureEnHeures, boolean prestataireSousSurveillance) {
        this.id = incident.getId();
        this.description = incident.getDescription();
        this.statut = incident.getStatut();
        this.libelleStatut = incident.getStatut().getLibelle();
        this.dateOuverture = incident.getDateOuverture();
        this.dateResolution = incident.getDateResolution();
        this.dureeOuvertureEnHeures = dureeOuvertureEnHeures;
        this.equipementId = incident.getEquipement().getId();
        this.referenceEquipement = incident.getEquipement().getReference();
        this.libelleEquipement = incident.getEquipement().getLibelle();
        this.station = incident.getEquipement().getStation().getNom();
        this.prestataireId = incident.aUnPrestataire() ? incident.getPrestataire().getId() : null;
        this.prestataire = incident.aUnPrestataire() ? incident.getPrestataire().getNom() : null;
        this.prestataireSousSurveillance = prestataireSousSurveillance;
    }

    public int getEquipementId() {
        return equipementId;
    }

    public Integer getPrestataireId() {
        return prestataireId;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public StatutIncident getStatut() {
        return statut;
    }

    public String getLibelleStatut() {
        return libelleStatut;
    }

    public LocalDateTime getDateOuverture() {
        return dateOuverture;
    }

    public LocalDateTime getDateResolution() {
        return dateResolution;
    }

    public long getDureeOuvertureEnHeures() {
        return dureeOuvertureEnHeures;
    }

    public String getReferenceEquipement() {
        return referenceEquipement;
    }

    public String getLibelleEquipement() {
        return libelleEquipement;
    }

    public String getStation() {
        return station;
    }

    public String getPrestataire() {
        return prestataire;
    }

    public boolean isPrestataireSousSurveillance() {
        return prestataireSousSurveillance;
    }
}
