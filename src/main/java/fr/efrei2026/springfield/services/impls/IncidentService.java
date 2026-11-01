package fr.efrei2026.springfield.services.impls;

import fr.efrei2026.springfield.exceptions.IncidentIntrouvableException;
import fr.efrei2026.springfield.modeles.Incident;
import fr.efrei2026.springfield.modeles.enums.StatutIncident;
import fr.efrei2026.springfield.repositories.IncidentRepository;
import fr.efrei2026.springfield.services.interfaces.IIncidentService;
import fr.efrei2026.springfield.shared.dto.IncidentDto;
import fr.efrei2026.springfield.utiles.CsvUtil;
import org.springframework.stereotype.Service;

import java.io.Writer;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class IncidentService implements IIncidentService {

    private static final DateTimeFormatter FORMAT_DATE_CSV = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String[] ENTETES_HISTORIQUE = {"station", "equipement", "description", "ouverture", "resolution", "prestataire"};

    private final IncidentRepository incidentRepository;
    private final Clock horloge;

    public IncidentService(IncidentRepository incidentRepository, Clock horloge) {
        this.incidentRepository = incidentRepository;
        this.horloge = horloge;
    }

    @Override
    public List<IncidentDto> listerIncidents() {
        return incidentRepository.findAllByOrderByDateOuvertureDesc().stream()
                .map(this::versDto)
                .toList();
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
