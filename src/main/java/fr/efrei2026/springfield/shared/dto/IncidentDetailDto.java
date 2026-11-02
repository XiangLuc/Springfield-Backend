package fr.efrei2026.springfield.shared.dto;

public class IncidentDetailDto {

    private final IncidentDto incident;
    private final EvaluationDto evaluation;

    public IncidentDetailDto(IncidentDto incident, EvaluationDto evaluation) {
        this.incident = incident;
        this.evaluation = evaluation;
    }

    public IncidentDto getIncident() {
        return incident;
    }

    public EvaluationDto getEvaluation() {
        return evaluation;
    }
}
