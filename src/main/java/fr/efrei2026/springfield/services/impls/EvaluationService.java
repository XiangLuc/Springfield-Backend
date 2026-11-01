package fr.efrei2026.springfield.services.impls;

import fr.efrei2026.springfield.exceptions.EvaluationInterditeException;
import fr.efrei2026.springfield.exceptions.EvaluationIntrouvableException;
import fr.efrei2026.springfield.exceptions.IncidentIntrouvableException;
import fr.efrei2026.springfield.modeles.Evaluation;
import fr.efrei2026.springfield.modeles.Incident;
import fr.efrei2026.springfield.repositories.EvaluationRepository;
import fr.efrei2026.springfield.repositories.IncidentRepository;
import fr.efrei2026.springfield.services.interfaces.IEvaluationService;
import fr.efrei2026.springfield.shared.dto.EvaluationCreationDto;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
public class EvaluationService implements IEvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final IncidentRepository incidentRepository;
    private final Clock horloge;

    public EvaluationService(EvaluationRepository evaluationRepository, IncidentRepository incidentRepository, Clock horloge) {
        this.evaluationRepository = evaluationRepository;
        this.incidentRepository = incidentRepository;
        this.horloge = horloge;
    }

    @Override
    public Evaluation evaluer(Integer incidentId, EvaluationCreationDto evaluation) {
        Incident incident = incidentRepository.trouverAvecPrestataire(incidentId)
                .orElseThrow(() -> new IncidentIntrouvableException(incidentId));
        verifierIncidentEvaluable(incidentId, incident);
        return enregistrer(incidentId, new Evaluation(incident, evaluation.getNote(), normaliserCommentaire(evaluation.getCommentaire()), LocalDateTime.now(horloge)));
    }

    @Override
    public Evaluation trouverEvaluation(Integer incidentId) {
        return evaluationRepository.findByIncidentIdAvecPrestataire(incidentId)
                .orElseThrow(() -> new EvaluationIntrouvableException(incidentId));
    }

    private void verifierIncidentEvaluable(Integer incidentId, Incident incident) {
        if (!incident.estResolu()) {
            throw new EvaluationInterditeException("L'incident n°" + incidentId + " n'est pas résolu : il ne peut pas encore être évalué.");
        }
        if (!incident.aUnPrestataire()) {
            throw new EvaluationInterditeException("L'incident n°" + incidentId + " n'a été affecté à aucun prestataire : il ne peut pas être évalué.");
        }
        if (evaluationRepository.existsByIncidentId(incidentId)) {
            throw new EvaluationInterditeException(messageDejaEvalue(incidentId));
        }
    }

    private Evaluation enregistrer(Integer incidentId, Evaluation evaluation) {
        try {
            return evaluationRepository.save(evaluation);
        } catch (DataIntegrityViolationException exception) {
            throw new EvaluationInterditeException(messageDejaEvalue(incidentId), exception);
        }
    }

    private String normaliserCommentaire(String commentaire) {
        return StringUtils.hasText(commentaire) ? commentaire.strip() : null;
    }

    private String messageDejaEvalue(Integer incidentId) {
        return "L'incident n°" + incidentId + " a déjà été évalué.";
    }
}