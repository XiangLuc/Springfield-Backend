package fr.efrei2026.springfield.shared.dto;

import fr.efrei2026.springfield.modeles.TypeEquipement;

public record TypeEquipementDto(int id, String nom) {

    public static TypeEquipementDto depuis(TypeEquipement type) {
        return new TypeEquipementDto(type.getId(), type.getNom());
    }
}
