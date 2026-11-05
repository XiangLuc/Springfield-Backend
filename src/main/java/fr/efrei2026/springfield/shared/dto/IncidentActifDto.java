package fr.efrei2026.springfield.shared.dto;

import fr.efrei2026.springfield.modeles.Incident;
import fr.efrei2026.springfield.modeles.enums.NiveauAttention;

import java.time.LocalDateTime;

public class IncidentActifDto {

    private final int id;
    private final String description;
    private final LocalDateTime dateOuverture;
    private final long dureeOuvertureEnHeures;
    private final NiveauAttention niveauAttention;
    private final String libelleNiveauAttention;
    private final int stationId;
    private final String station;
    private final String referenceEquipement;
    private final String libelleEquipement;
    private final String typeEquipement;
    private final String prestataire;

    public IncidentActifDto(Incident incident, NiveauAttention niveauAttention, long dureeOuvertureEnHeures) {
        this.id = incident.getId();
        this.description = incident.getDescription();
        this.dateOuverture = incident.getDateOuverture();
        this.dureeOuvertureEnHeures = dureeOuvertureEnHeures;
        this.niveauAttention = niveauAttention;
        this.libelleNiveauAttention = niveauAttention.getLibelle();
        this.stationId = incident.getEquipement().getStation().getId();
        this.station = incident.getEquipement().getStation().getNom();
        this.referenceEquipement = incident.getEquipement().getReference();
        this.libelleEquipement = incident.getEquipement().getLibelle();
        this.typeEquipement = incident.getEquipement().getType().getNom();
        this.prestataire = incident.aUnPrestataire() ? incident.getPrestataire().getNom() : null;
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

    public long getDureeOuvertureEnHeures() {
        return dureeOuvertureEnHeures;
    }

    public NiveauAttention getNiveauAttention() {
        return niveauAttention;
    }

    public String getLibelleNiveauAttention() {
        return libelleNiveauAttention;
    }

    public int getStationId() {
        return stationId;
    }

    public String getStation() {
        return station;
    }

    public String getReferenceEquipement() {
        return referenceEquipement;
    }

    public String getLibelleEquipement() {
        return libelleEquipement;
    }

    public String getTypeEquipement() {
        return typeEquipement;
    }

    public String getPrestataire() {
        return prestataire;
    }
}
