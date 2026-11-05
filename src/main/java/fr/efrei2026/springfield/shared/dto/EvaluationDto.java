package fr.efrei2026.springfield.shared.dto;

import fr.efrei2026.springfield.modeles.Evaluation;

import java.time.LocalDateTime;

public class EvaluationDto {

    private final int id;
    private final int incidentId;
    private final String prestataire;
    private final int note;
    private final String commentaire;
    private final LocalDateTime dateEvaluation;

    public EvaluationDto(Evaluation evaluation) {
        this.id = evaluation.getId();
        this.incidentId = evaluation.getIncident().getId();
        this.prestataire = evaluation.getIncident().getPrestataire().getNom();
        this.note = evaluation.getNote();
        this.commentaire = evaluation.getCommentaire();
        this.dateEvaluation = evaluation.getDateEvaluation();
    }

    public int getId() {
        return id;
    }

    public int getIncidentId() {
        return incidentId;
    }

    public String getPrestataire() {
        return prestataire;
    }

    public int getNote() {
        return note;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public LocalDateTime getDateEvaluation() {
        return dateEvaluation;
    }
}
