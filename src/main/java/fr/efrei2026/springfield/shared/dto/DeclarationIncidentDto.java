package fr.efrei2026.springfield.shared.dto;

import fr.efrei2026.springfield.modeles.Incident;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class DeclarationIncidentDto {

    @NotNull(message = "L'équipement concerné est obligatoire.")
    private Integer equipementId;

    @NotBlank(message = "La description est obligatoire.")
    @Size(max = Incident.LONGUEUR_MAX_DESCRIPTION,
            message = "La description ne doit pas dépasser " + Incident.LONGUEUR_MAX_DESCRIPTION + " caractères.")
    private String description;

    public DeclarationIncidentDto() {
    }

    public DeclarationIncidentDto(Integer equipementId, String description) {
        this.equipementId = equipementId;
        this.description = description;
    }

    public Integer getEquipementId() {
        return equipementId;
    }

    public void setEquipementId(Integer equipementId) {
        this.equipementId = equipementId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
