package fr.efrei2026.springfield.shared.dto;

import fr.efrei2026.springfield.modeles.Evaluation;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class EvaluationCreationDto {

    private static final String MESSAGE_NOTE_HORS_INTERVALLE = "La note doit être comprise entre "
            + Evaluation.NOTE_MINIMALE + " et " + Evaluation.NOTE_MAXIMALE + ".";

    @NotNull(message = "La note est obligatoire.")
    @Min(value = Evaluation.NOTE_MINIMALE, message = MESSAGE_NOTE_HORS_INTERVALLE)
    @Max(value = Evaluation.NOTE_MAXIMALE, message = MESSAGE_NOTE_HORS_INTERVALLE)
    private Integer note;

    @Size(max = Evaluation.LONGUEUR_MAX_COMMENTAIRE,
            message = "Le commentaire ne doit pas dépasser " + Evaluation.LONGUEUR_MAX_COMMENTAIRE + " caractères.")
    private String commentaire;

    public EvaluationCreationDto() {
    }

    public EvaluationCreationDto(Integer note, String commentaire) {
        this.note = note;
        this.commentaire = commentaire;
    }

    public Integer getNote() {
        return note;
    }

    public void setNote(Integer note) {
        this.note = note;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }
}
