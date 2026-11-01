package fr.efrei2026.springfield.shared.dto;

public class EvaluationCreationDto {
    private Integer note;
    private String commentaire;

    public EvaluationCreationDto() {
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
