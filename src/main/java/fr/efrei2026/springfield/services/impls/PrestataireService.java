package fr.efrei2026.springfield.services.impls;

import fr.efrei2026.springfield.repositories.EvaluationRepository;
import fr.efrei2026.springfield.repositories.PrestataireRepository;
import fr.efrei2026.springfield.services.interfaces.IPrestataireService;
import fr.efrei2026.springfield.shared.dto.PrestataireDto;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrestataireService implements IPrestataireService {

    private static final int NOMBRE_EVALUATIONS_SURVEILLEES = 5;
    private static final double SEUIL_SURVEILLANCE = 3.0;

    private final PrestataireRepository prestataireRepository;
    private final EvaluationRepository evaluationRepository;

    public PrestataireService(PrestataireRepository prestataireRepository, EvaluationRepository evaluationRepository) {
        this.prestataireRepository = prestataireRepository;
        this.evaluationRepository = evaluationRepository;
    }

    @Override
    public List<PrestataireDto> listerPrestataires() {
        List<PrestataireDto> prestataires = prestataireRepository.listerAvecNotes();
        prestataires.forEach(prestataire -> prestataire.setSousSurveillance(estSousSurveillance(prestataire.getId())));
        return prestataires;
    }

    @Override
    public boolean estSousSurveillance(int prestataireId) {
        Double moyenne = calculerMoyenneRecente(prestataireId);
        return moyenne != null && moyenne < SEUIL_SURVEILLANCE;
    }

    private Double calculerMoyenneRecente(int prestataireId) {
        List<Integer> notes = evaluationRepository.trouverNotesLesPlusRecentes(prestataireId, Limit.of(NOMBRE_EVALUATIONS_SURVEILLEES));
        if (notes.size() < NOMBRE_EVALUATIONS_SURVEILLEES) {
            return null;
        }
        return notes.stream().mapToInt(Integer::intValue).average().orElseThrow();
    }
}
