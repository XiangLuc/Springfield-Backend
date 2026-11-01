package fr.efrei2026.springfield.services.impls;

import fr.efrei2026.springfield.exceptions.IncidentIntrouvableException;
import fr.efrei2026.springfield.exceptions.TriInvalideException;
import fr.efrei2026.springfield.modeles.Incident;
import fr.efrei2026.springfield.modeles.enums.StatutIncident;
import fr.efrei2026.springfield.repositories.IncidentRepository;
import fr.efrei2026.springfield.services.interfaces.IIncidentService;
import fr.efrei2026.springfield.shared.dto.IncidentDto;
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
    private final Clock horloge;

    public IncidentService(IncidentRepository incidentRepository, Clock horloge) {
        this.incidentRepository = incidentRepository;
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
    public IncidentDto trouverIncident(Integer id) {
        return versDto(chargerIncident(id));
    }

    @Override
    public IncidentDto resoudre(Integer id) {
        Incident incident = chargerIncident(id);
        incident.resoudre(LocalDateTime.now(horloge));
        incidentRepository.save(incident);
        return versDto(incident);
    }

    @Override
    public void exporterHistorique(Writer writer) {
        List<Object[]> lignes = incidentRepository.findHistoriquePourExport(StatutIncident.RESOLU).stream()
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
        return incidentRepository.findById(id)
                .orElseThrow(() -> new IncidentIntrouvableException(id));
    }

    private IncidentDto versDto(Incident incident) {
        return new IncidentDto(incident, incident.dureeOuverture(horloge).toHours());
    }

    private String nomDuPrestataire(Incident incident) {
        return incident.getPrestataire() == null ? "" : incident.getPrestataire().getNom();
    }
}
