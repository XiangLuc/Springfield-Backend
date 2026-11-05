package fr.efrei2026.springfield.shared.dto;

import fr.efrei2026.springfield.modeles.Incident;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ModificationIncidentDto {

    @NotBlank(message = "La description est obligatoire.")
    @Size(max = Incident.LONGUEUR_MAX_DESCRIPTION,
            message = "La description ne doit pas dépasser " + Incident.LONGUEUR_MAX_DESCRIPTION + " caractères.")
    private String description;

    public ModificationIncidentDto() {
    }

    public ModificationIncidentDto(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
