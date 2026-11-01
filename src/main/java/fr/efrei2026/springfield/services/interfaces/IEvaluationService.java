package fr.efrei2026.springfield.services.interfaces;

import fr.efrei2026.springfield.modeles.Evaluation;
import fr.efrei2026.springfield.shared.dto.EvaluationCreationDto;

public interface IEvaluationService {
    Evaluation evaluer(Integer incidentId, EvaluationCreationDto evaluation);
    Evaluation trouverEvaluation(Integer incidentId);
}
