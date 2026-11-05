package fr.efrei2026.springfield.shared.dto;

import jakarta.validation.constraints.NotNull;

public class AffectationDto {

    @NotNull(message = "Le prestataire est obligatoire.")
    private Integer prestataireId;

    private boolean confirmationSurveillance;

    public AffectationDto() {
    }

    public AffectationDto(Integer prestataireId) {
        this.prestataireId = prestataireId;
    }

    public AffectationDto(Integer prestataireId, boolean confirmationSurveillance) {
        this.prestataireId = prestataireId;
        this.confirmationSurveillance = confirmationSurveillance;
    }

    public Integer getPrestataireId() {
        return prestataireId;
    }

    public void setPrestataireId(Integer prestataireId) {
        this.prestataireId = prestataireId;
    }

    public boolean isConfirmationSurveillance() {
        return confirmationSurveillance;
    }

    public void setConfirmationSurveillance(boolean confirmationSurveillance) {
        this.confirmationSurveillance = confirmationSurveillance;
    }
}
