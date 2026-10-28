package fr.efrei2026.springfield.modeles;

import fr.efrei2026.springfield.modeles.enums.EtatEquipement;
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

@Entity
@Table(name = "equipement")
public class Equipement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true, length = 30)
    private String reference;

    @Column(nullable = false, length = 100)
    private String libelle;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "type_id")
    private TypeEquipement type;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "station_id")
    private Station station;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EtatEquipement etat = EtatEquipement.EN_SERVICE;

    protected Equipement() {
    }

    public Equipement(String reference, String libelle, TypeEquipement type, Station station) {
        this.reference = reference;
        this.libelle = libelle;
        this.type = type;
        this.station = station;
    }

    public int getId() {
        return id;
    }

    public String getReference() {
        return reference;
    }

    public String getLibelle() {
        return libelle;
    }

    public String getDescription() {
        return description;
    }

    public TypeEquipement getType() {
        return type;
    }

    public Station getStation() {
        return station;
    }

    public EtatEquipement getEtat() {
        return etat;
    }
}