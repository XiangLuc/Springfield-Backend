package fr.efrei2026.springfield.shared.dto;

import java.time.LocalDateTime;

public class EvaluationDto {

    private int id;
    private int incidentId;
    private String prestataire;
    private int note;
    private String commentaire;
    private LocalDateTime dateEvaluation;

    public EvaluationDto() {
    }

    public EvaluationDto(int id, int incidentId, String prestataire, int note, String commentaire,
                         LocalDateTime dateEvaluation) {
        this.id = id;
        this.incidentId = incidentId;
        this.prestataire = prestataire;
        this.note = note;
        this.commentaire = commentaire;
        this.dateEvaluation = dateEvaluation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIncidentId() {
        return incidentId;
    }

    public void setIncidentId(int incidentId) {
        this.incidentId = incidentId;
    }

    public String getPrestataire() {
        return prestataire;
    }

    public void setPrestataire(String prestataire) {
        this.prestataire = prestataire;
    }

    public int getNote() {
        return note;
    }

    public void setNote(int note) {
        this.note = note;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public LocalDateTime getDateEvaluation() {
        return dateEvaluation;
    }

    public void setDateEvaluation(LocalDateTime dateEvaluation) {
        this.dateEvaluation = dateEvaluation;
    }
}
