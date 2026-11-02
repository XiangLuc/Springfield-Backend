package fr.efrei2026.springfield.services.impls;

import fr.efrei2026.springfield.exceptions.IncidentIntrouvableException;
import fr.efrei2026.springfield.exceptions.PrestataireIntrouvableException;
import fr.efrei2026.springfield.exceptions.TriInvalideException;
import fr.efrei2026.springfield.modeles.Equipement;
import fr.efrei2026.springfield.modeles.Incident;
import fr.efrei2026.springfield.modeles.Prestataire;
import fr.efrei2026.springfield.modeles.enums.StatutIncident;
import fr.efrei2026.springfield.repositories.EvaluationRepository;
import fr.efrei2026.springfield.repositories.IncidentRepository;
import fr.efrei2026.springfield.repositories.PrestataireRepository;
import fr.efrei2026.springfield.services.interfaces.IEquipementService;
import fr.efrei2026.springfield.services.interfaces.IIncidentService;
import fr.efrei2026.springfield.shared.dto.AffectationDto;
import fr.efrei2026.springfield.shared.dto.DeclarationIncidentDto;
import fr.efrei2026.springfield.shared.dto.EvaluationDto;
import fr.efrei2026.springfield.shared.dto.IncidentDetailDto;
import fr.efrei2026.springfield.shared.dto.IncidentDto;
import fr.efrei2026.springfield.shared.dto.ModificationIncidentDto;
import fr.efrei2026.springfield.shared.dto.PageDto;
import fr.efrei2026.springfield.utiles.CsvUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.Writer;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Service
public class IncidentService implements IIncidentService {

    private static final DateTimeFormatter FORMAT_DATE_CSV = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String[] ENTETES_HISTORIQUE = {"station", "equipement", "description", "ouverture", "resolution", "prestataire"};
    private static final Set<String> TRIS_AUTORISES = Set.of("dateOuverture", "dateResolution", "statut", "equipement.station.nom");

    private final IncidentRepository incidentRepository;
    private final PrestataireRepository prestataireRepository;
    private final EvaluationRepository evaluationRepository;
    private final IEquipementService equipementService;
    private final Clock horloge;

    public IncidentService(IncidentRepository incidentRepository, PrestataireRepository prestataireRepository,
                           EvaluationRepository evaluationRepository, IEquipementService equipementService, Clock horloge) {
        this.incidentRepository = incidentRepository;
        this.prestataireRepository = prestataireRepository;
        this.evaluationRepository = evaluationRepository;
        this.equipementService = equipementService;
        this.horloge = horloge;
    }

    @Override
    public PageDto<IncidentDto> rechercherIncidents(StatutIncident statut, Integer stationId, Integer prestataireId, LocalDate ouvertDepuis, LocalDate ouvertJusquAu, String motCle, Pageable pagination) {
        verifierTri(pagination.getSort());
        Page<Incident> incidents = incidentRepository.rechercher(
                statut,
                stationId,
                prestataireId,
                ouvertDepuis == null ? null : ouvertDepuis.atStartOfDay(),
                ouvertJusquAu == null ? null : ouvertJusquAu.plusDays(1).atStartOfDay(),
                StringUtils.hasText(motCle) ? motCle.strip() : null,
                ajouterTriParId(pagination));
        return new PageDto<>(incidents.map(this::versDto));
    }

    @Override
    public List<IncidentDto> listerIncidentsEnCours() {
        return incidentRepository.trouverHorsStatutAvecDetails(StatutIncident.RESOLU).stream()
                .map(this::versDto)
                .toList();
    }

    @Override
    public List<IncidentDto> listerIncidentsResolus() {
        return incidentRepository.trouverHistorique(StatutIncident.RESOLU).stream()
                .map(this::versDto)
                .toList();
    }

    @Override
    public IncidentDetailDto trouverIncident(Integer id) {
        EvaluationDto evaluation = evaluationRepository.findByIncidentIdAvecPrestataire(id)
                .map(EvaluationDto::new)
                .orElse(null);
        return new IncidentDetailDto(versDto(chargerIncident(id)), evaluation);
    }

    @Override
    public IncidentDto resoudre(Integer id) {
        Incident incident = chargerIncident(id);
        incident.resoudre(LocalDateTime.now(horloge));
        incidentRepository.save(incident);
        return versDto(incident);
    }

    // RG1 : rattaché à un équipement, ouvert maintenant, sans prestataire
    @Override
    public IncidentDto declarer(DeclarationIncidentDto declaration) {
        Equipement equipement = equipementService.trouverEquipement(declaration.getEquipementId());
        Incident incident = new Incident(equipement, declaration.getDescription().strip(), LocalDateTime.now(horloge));
        return versDto(incidentRepository.save(incident));
    }

    @Override
    public IncidentDto modifierDescription(Integer id, ModificationIncidentDto modification) {
        Incident incident = chargerIncident(id);
        incident.modifierDescription(modification.getDescription().strip());
        incidentRepository.save(incident);
        return versDto(incident);
    }

    @Override
    public IncidentDto affecter(Integer id, AffectationDto affectation) {
        Incident incident = chargerIncident(id);
        incident.affecter(chargerPrestataire(affectation.getPrestataireId()));
        incidentRepository.save(incident);
        return versDto(incident);
    }

    @Override
    public void exporterHistorique(Writer writer) {
        List<Object[]> lignes = incidentRepository.trouverHistorique(StatutIncident.RESOLU).stream()
                .map(incident -> new Object[]{
                        incident.getEquipement().getStation().getNom(),
                        incident.getEquipement().getReference(),
                        incident.getDescription(),
                        incident.getDateOuverture().format(FORMAT_DATE_CSV),
                        incident.getDateResolution().format(FORMAT_DATE_CSV),
                        nomDuPrestataire(incident)})
                .toList();

        CsvUtil.exporterCsv(writer, ENTETES_HISTORIQUE, lignes);
    }

    private void verifierTri(Sort tri) {
        tri.stream()
                .map(Sort.Order::getProperty)
                .filter(propriete -> !TRIS_AUTORISES.contains(propriete))
                .findFirst()
                .ifPresent(propriete -> {
                    throw new TriInvalideException(propriete);
                });
    }

    private Pageable ajouterTriParId(Pageable pagination) {
        return PageRequest.of(
                pagination.getPageNumber(),
                pagination.getPageSize(),
                pagination.getSort().and(Sort.by("id"))
        );
    }

    private Incident chargerIncident(Integer id) {
        return incidentRepository.trouverAvecDetails(id)
                .orElseThrow(() -> new IncidentIntrouvableException(id));
    }

    private Prestataire chargerPrestataire(Integer id) {
        return prestataireRepository.findById(id)
                .orElseThrow(() -> new PrestataireIntrouvableException(id));
    }

    private IncidentDto versDto(Incident incident) {
        return new IncidentDto(incident, incident.dureeOuverture(horloge).toMinutes());
    }

    private String nomDuPrestataire(Incident incident) {
        return incident.getPrestataire() == null ? "" : incident.getPrestataire().getNom();
    }
}
