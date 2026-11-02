package fr.efrei2026.springfield.shared.dto;

import jakarta.validation.constraints.NotNull;

public record AffectationDto(
        @NotNull(message = "Le prestataire est obligatoire.")
        Integer prestataireId) {
}
