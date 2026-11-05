package fr.efrei2026.springfield.modeles;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@Table(name = "evaluation")
public class Evaluation {

    public static final int NOTE_MINIMALE = 1;
    public static final int NOTE_MAXIMALE = 5;
    public static final int LONGUEUR_MAX_COMMENTAIRE = 500;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "incident_id", nullable = false)
    private Incident incident;

    @NotNull
    @Min(NOTE_MINIMALE)
    @Max(NOTE_MAXIMALE)
    @Column(name = "note", nullable = false)
    private Integer note;

    @Size(max = LONGUEUR_MAX_COMMENTAIRE)
    @Column(name = "commentaire", length = LONGUEUR_MAX_COMMENTAIRE)
    private String commentaire;

    @NotNull
    @ColumnDefault("current_timestamp()")
    @Column(name = "date_evaluation", nullable = false)
    private LocalDateTime dateEvaluation;

    protected Evaluation() {
    }

    public Evaluation(Incident incident, Integer note, String commentaire, LocalDateTime dateEvaluation) {
        this.incident = incident;
        this.note = note;
        this.commentaire = commentaire;
        this.dateEvaluation = dateEvaluation;
    }

    public Integer getId() {
        return id;
    }

    public Incident getIncident() {
        return incident;
    }

    public Integer getNote() {
        return note;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public LocalDateTime getDateEvaluation() {
        return dateEvaluation;
    }
}
