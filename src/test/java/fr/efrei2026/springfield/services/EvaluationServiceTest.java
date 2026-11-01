package fr.efrei2026.springfield.services;

import fr.efrei2026.springfield.exceptions.EvaluationInterditeException;
import fr.efrei2026.springfield.exceptions.EvaluationIntrouvableException;
import fr.efrei2026.springfield.exceptions.IncidentIntrouvableException;
import fr.efrei2026.springfield.modeles.Evaluation;
import fr.efrei2026.springfield.modeles.Incident;
import fr.efrei2026.springfield.modeles.Prestataire;
import fr.efrei2026.springfield.modeles.enums.StatutIncident;
import fr.efrei2026.springfield.modeles.repositories.EvaluationRepository;
import fr.efrei2026.springfield.repositories.IncidentRepository;
import fr.efrei2026.springfield.services.impls.EvaluationService;
import fr.efrei2026.springfield.shared.dto.EvaluationCreationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvaluationServiceTest {

    private static final int INCIDENT_ID = 7;
    private static final LocalDateTime MAINTENANT = LocalDateTime.of(2026, 11, 1, 10, 30);
    private static final EvaluationCreationDto NOUVELLE_EVALUATION = new EvaluationCreationDto(4, "Intervention rapide");
    private static final Prestataire PRESTATAIRE = new Prestataire("Otis Maintenance", null, null);

    @Mock
    private EvaluationRepository evaluationRepository;

    @Mock
    private IncidentRepository incidentRepository;

    private EvaluationService evaluationService;

    @BeforeEach
    void creerServiceAvecHorlogeFixe() {
        Clock horlogeFixe = Clock.fixed(MAINTENANT.toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
        evaluationService = new EvaluationService(evaluationRepository, incidentRepository, horlogeFixe);
    }

    @Test
    void evaluer_incidentResoluEtAffecteEnregistreLEvaluationALaDateCourante() {
        Incident incident = simulerIncident(StatutIncident.RESOLU, PRESTATAIRE);
        when(evaluationRepository.save(any(Evaluation.class))).then(returnsFirstArg());

        Evaluation evaluation = evaluationService.evaluer(INCIDENT_ID, NOUVELLE_EVALUATION);

        assertSame(incident, evaluation.getIncident());
        assertEquals(4, evaluation.getNote());
        assertEquals("Intervention rapide", evaluation.getCommentaire());
        assertEquals(MAINTENANT, evaluation.getDateEvaluation());
    }

    @Test
    void evaluer_commentaireVideEnregistreUneEvaluationSansCommentaire() {
        simulerIncident(StatutIncident.RESOLU, PRESTATAIRE);
        when(evaluationRepository.save(any(Evaluation.class))).then(returnsFirstArg());

        Evaluation evaluation = evaluationService.evaluer(INCIDENT_ID, new EvaluationCreationDto(4, "   "));

        assertNull(evaluation.getCommentaire());
    }

    @Test
    void evaluer_incidentInexistantEstSignale() {
        when(incidentRepository.trouverAvecPrestataire(INCIDENT_ID)).thenReturn(Optional.empty());

        IncidentIntrouvableException exception = assertThrows(IncidentIntrouvableException.class,
                () -> evaluationService.evaluer(INCIDENT_ID, NOUVELLE_EVALUATION));

        assertEquals("L'incident n°7 n'existe pas.", exception.getMessage());
    }

    @Test
    void evaluer_incidentNonAffecteEstRefuse() {
        simulerIncident(StatutIncident.OUVERT, null);

        verifierEvaluationRefusee("L'incident n°7 n'est pas résolu : il ne peut pas encore être évalué.");
    }

    @Test
    void evaluer_incidentAffecteNonResoluEstRefuse() {
        simulerIncident(StatutIncident.AFFECTE, PRESTATAIRE);

        verifierEvaluationRefusee("L'incident n°7 n'est pas résolu : il ne peut pas encore être évalué.");
    }

    @Test
    void evaluer_incidentResoluSansPrestataireEstRefuse() {
        simulerIncident(StatutIncident.RESOLU, null);

        verifierEvaluationRefusee("L'incident n°7 n'a été affecté à aucun prestataire : il ne peut pas être évalué.");
    }

    @Test
    void evaluer_incidentDejaEvalueEstRefuse() {
        simulerIncident(StatutIncident.RESOLU, PRESTATAIRE);
        when(evaluationRepository.existsByIncidentId(INCIDENT_ID)).thenReturn(true);

        verifierEvaluationRefusee("L'incident n°7 a déjà été évalué.");
    }

    @Test
    void evaluer_violationDeLUniciteEnBaseEstTraduiteEnConservantLaCause() {
        simulerIncident(StatutIncident.RESOLU, PRESTATAIRE);
        DataIntegrityViolationException violation = new DataIntegrityViolationException("uq_evaluation_incident");
        when(evaluationRepository.save(any(Evaluation.class))).thenThrow(violation);

        EvaluationInterditeException exception = assertThrows(EvaluationInterditeException.class,
                () -> evaluationService.evaluer(INCIDENT_ID, NOUVELLE_EVALUATION));

        assertEquals("L'incident n°7 a déjà été évalué.", exception.getMessage());
        assertSame(violation, exception.getCause());
    }

    @Test
    void trouverEvaluation_incidentEvalueRetourneSonEvaluation() {
        Evaluation evaluation = new Evaluation(null, 5, null, MAINTENANT);
        when(evaluationRepository.findByIncidentIdAvecPrestataire(INCIDENT_ID)).thenReturn(Optional.of(evaluation));

        assertSame(evaluation, evaluationService.trouverEvaluation(INCIDENT_ID));
    }

    @Test
    void trouverEvaluation_incidentSansEvaluationEstSignale() {
        when(evaluationRepository.findByIncidentIdAvecPrestataire(INCIDENT_ID)).thenReturn(Optional.empty());

        EvaluationIntrouvableException exception = assertThrows(EvaluationIntrouvableException.class,
                () -> evaluationService.trouverEvaluation(INCIDENT_ID));

        assertEquals("Aucune évaluation n'existe pour l'incident n°7.", exception.getMessage());
    }

    private void verifierEvaluationRefusee(String messageAttendu) {
        EvaluationInterditeException exception = assertThrows(EvaluationInterditeException.class,
                () -> evaluationService.evaluer(INCIDENT_ID, NOUVELLE_EVALUATION));

        assertEquals(messageAttendu, exception.getMessage());
        verify(evaluationRepository, never()).save(any(Evaluation.class));
    }

    private Incident simulerIncident(StatutIncident statut, Prestataire prestataire) {
        Incident incident = new Incident(null, "Porte bloquée", MAINTENANT.minusDays(3));
        ReflectionTestUtils.setField(incident, "statut", statut);
        ReflectionTestUtils.setField(incident, "prestataire", prestataire);
        when(incidentRepository.trouverAvecPrestataire(INCIDENT_ID)).thenReturn(Optional.of(incident));
        return incident;
    }
}
