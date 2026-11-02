package fr.efrei2026.springfield.shared.dto;

import fr.efrei2026.springfield.modeles.Evaluation;

import java.time.Instant;

public record EvaluationDto(int note, String commentaire, Instant dateEvaluation) {

    public static EvaluationDto depuis(Evaluation evaluation) {
        return new EvaluationDto(evaluation.getNote(), evaluation.getCommentaire(), evaluation.getDateEvaluation());
    }
}
