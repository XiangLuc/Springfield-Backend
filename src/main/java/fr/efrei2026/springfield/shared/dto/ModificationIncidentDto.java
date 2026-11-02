package fr.efrei2026.springfield.shared.dto;

import fr.efrei2026.springfield.modeles.Incident;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ModificationIncidentDto(
        @NotBlank(message = "La description est obligatoire.")
        @Size(max = Incident.LONGUEUR_MAX_DESCRIPTION, message = "La description ne doit pas dépasser {max} caractères.")
        String description) {
}
