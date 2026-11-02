package fr.efrei2026.springfield.shared.dto;

import jakarta.validation.constraints.NotNull;

public class AffectationDto {

    @NotNull(message = "Le prestataire est obligatoire.")
    private Integer prestataireId;

    public AffectationDto() {
    }

    public AffectationDto(Integer prestataireId) {
        this.prestataireId = prestataireId;
    }

    public Integer getPrestataireId() {
        return prestataireId;
    }

    public void setPrestataireId(Integer prestataireId) {
        this.prestataireId = prestataireId;
    }
}
