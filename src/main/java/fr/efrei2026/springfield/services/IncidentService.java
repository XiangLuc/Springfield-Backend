package fr.efrei2026.springfield.services;

import fr.efrei2026.springfield.exceptions.IncidentIntrouvableException;
import fr.efrei2026.springfield.modeles.Equipement;
import fr.efrei2026.springfield.modeles.Evaluation;
import fr.efrei2026.springfield.modeles.Incident;
import fr.efrei2026.springfield.modeles.Prestataire;
import fr.efrei2026.springfield.modeles.enums.StatutIncident;
import fr.efrei2026.springfield.repositories.EvaluationRepository;
import fr.efrei2026.springfield.repositories.IncidentRepository;
import fr.efrei2026.springfield.services.interfaces.IEquipementService;
import fr.efrei2026.springfield.services.interfaces.IIncidentService;
import fr.efrei2026.springfield.services.interfaces.IPrestataireService;
import fr.efrei2026.springfield.shared.dto.AffectationDto;
import fr.efrei2026.springfield.shared.dto.DeclarationIncidentDto;
import fr.efrei2026.springfield.shared.dto.ModificationIncidentDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class IncidentService implements IIncidentService {

    private final IncidentRepository incidentRepository;
    private final EvaluationRepository evaluationRepository;
    private final IEquipementService equipementService;
    private final IPrestataireService prestataireService;
    private final Clock horloge;

    public IncidentService(IncidentRepository incidentRepository,
                           EvaluationRepository evaluationRepository,
                           IEquipementService equipementService,
                           IPrestataireService prestataireService,
                           Clock horloge) {
        this.incidentRepository = incidentRepository;
        this.evaluationRepository = evaluationRepository;
        this.equipementService = equipementService;
        this.prestataireService = prestataireService;
        this.horloge = horloge;
    }

    @Override
    public List<Incident> listerIncidentsEnCours() {
        return incidentRepository.trouverParStatuts(StatutIncident.EN_COURS);
    }

    @Override
    public List<Incident> listerIncidentsResolus() {
        return incidentRepository.findByStatutOrderByDateResolutionDesc(StatutIncident.RESOLU);
    }

    @Override
    public Incident trouverIncident(Integer id) {
        return incidentRepository.trouverAvecDetails(id)
                .orElseThrow(() -> new IncidentIntrouvableException(id));
    }

    @Override
    public Optional<Evaluation> trouverEvaluation(Integer incidentId) {
        return evaluationRepository.findByIncidentId(incidentId);
    }

    @Override
    public Duration calculerDureeOuverture(Incident incident) {
        return incident.dureeOuverture(maintenant());
    }

    // RG1 : rattaché à un équipement, ouvert maintenant, sans prestataire
    @Override
    public Incident declarer(DeclarationIncidentDto declaration) {
        Equipement equipement = equipementService.trouverEquipement(declaration.equipementId());
        Incident incident = new Incident(equipement, declaration.description().strip(), maintenant());
        return incidentRepository.save(incident);
    }

    // Lecture puis modification : la transaction garantit que l'état vérifié est celui qui est enregistré (RG10)
    @Override
    @Transactional
    public Incident modifierDescription(Integer id, ModificationIncidentDto modification) {
        Incident incident = trouverIncident(id);
        incident.modifierDescription(modification.description().strip());
        return incident;
    }

    @Override
    @Transactional
    public Incident affecter(Integer id, AffectationDto affectation) {
        Incident incident = trouverIncident(id);
        Prestataire prestataire = prestataireService.trouverPrestataire(affectation.prestataireId());
        incident.affecter(prestataire);
        return incident;
    }

    @Override
    @Transactional
    public Incident resoudre(Integer id) {
        Incident incident = trouverIncident(id);
        incident.resoudre(maintenant());
        return incident;
    }

    private LocalDateTime maintenant() {
        return LocalDateTime.now(horloge);
    }
}
