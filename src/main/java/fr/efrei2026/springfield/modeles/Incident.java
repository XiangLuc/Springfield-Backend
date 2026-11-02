package fr.efrei2026.springfield.modeles;

import fr.efrei2026.springfield.exceptions.DateResolutionInvalideException;
import fr.efrei2026.springfield.exceptions.IncidentDejaResoluException;
import fr.efrei2026.springfield.modeles.enums.StatutIncident;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "incident")
public class Incident {

    public static final int LONGUEUR_MAX_DESCRIPTION = 500;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "equipement_id")
    private Equipement equipement;


    @Column(nullable = false, length = LONGUEUR_MAX_DESCRIPTION)
    private String description;

    @Column(name = "date_ouverture", nullable = false)
    private LocalDateTime dateOuverture;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutIncident statut = StatutIncident.OUVERT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prestataire_id")
    private Prestataire prestataire;

    @Column(name = "date_resolution")
    private LocalDateTime dateResolution;

    protected Incident() {
    }

    public Incident(Equipement equipement, String description, LocalDateTime dateOuverture) {
        this.equipement = equipement;
        this.description = description;
        this.dateOuverture = dateOuverture;
    }

    public int getId() {
        return id;
    }

    public Equipement getEquipement() {
        return equipement;
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

    public boolean estResolu() {
        return statut == StatutIncident.RESOLU;
    }

    public void modifierDescription(String nouvelleDescription) {
        verifierNonResolu();
        this.description = nouvelleDescription;
    }

    // RG5 : un incident résolu ne peut plus être affecté ni réaffecté
    public void affecter(Prestataire nouveauPrestataire) {
        verifierNonResolu();
        this.prestataire = nouveauPrestataire;
        this.statut = StatutIncident.AFFECTE;
    }

    // RG5 : la date de résolution est postérieure ou égale à l'ouverture
    public void resoudre(LocalDateTime date) {
        verifierNonResolu();
        if (date.isBefore(dateOuverture)) {
            throw new DateResolutionInvalideException(id);
        }
        this.dateResolution = date;
        this.statut = StatutIncident.RESOLU;
    }

    // Une fois résolu, la durée est figée à la date de résolution
    public Duration dureeOuverture(LocalDateTime maintenant) {
        LocalDateTime fin = estResolu() ? dateResolution : maintenant;
        return Duration.between(dateOuverture, fin);
    }

    private void verifierNonResolu() {
        if (estResolu()) {
            throw new IncidentDejaResoluException(id);
        }
    }
}