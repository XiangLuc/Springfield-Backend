package fr.efrei2026.springfield.services.impls;

import fr.efrei2026.springfield.modeles.Incident;
import fr.efrei2026.springfield.modeles.enums.StatutIncident;
import fr.efrei2026.springfield.repositories.IncidentRepository;
import fr.efrei2026.springfield.services.interfaces.IIncidentService;
import fr.efrei2026.springfield.utiles.CsvUtil;
import org.springframework.stereotype.Service;

import java.io.Writer;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class IncidentService implements IIncidentService {

    private static final DateTimeFormatter FORMAT_DATE_CSV = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String[] ENTETES_HISTORIQUE = {"station", "equipement", "description", "ouverture", "resolution", "prestataire"};

    private final IncidentRepository incidentRepository;

    public IncidentService(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
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

    private String nomDuPrestataire(Incident incident) {
        return incident.getPrestataire() == null ? "" : incident.getPrestataire().getNom();
    }
}
