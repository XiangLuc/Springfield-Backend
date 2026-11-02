package fr.efrei2026.springfield.shared.dto;

import fr.efrei2026.springfield.modeles.Equipement;
import fr.efrei2026.springfield.modeles.enums.EtatEquipement;

public record EquipementDto(int id,
                            String reference,
                            String libelle,
                            String description,
                            TypeEquipementDto type,
                            StationDto station,
                            EtatEquipement etat) {

    public static EquipementDto depuis(Equipement equipement) {
        return new EquipementDto(
                equipement.getId(),
                equipement.getReference(),
                equipement.getLibelle(),
                equipement.getDescription(),
                TypeEquipementDto.depuis(equipement.getType()),
                StationDto.depuis(equipement.getStation()),
                equipement.getEtat());
    }
}
