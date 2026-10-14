package fr.efrei2026.springfield.modeles;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "TypeEquipement")
public class TypeEquipement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true, length = 100)
    private String nom;

    protected TypeEquipement() {
    }

    public TypeEquipement(String nom) {
        this.nom = nom;
    }

    public int getId() {

        return id;
    }

    public String getNom() {
        return nom;
    }

}

