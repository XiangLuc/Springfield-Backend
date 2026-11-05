package fr.efrei2026.springfield.services.impls;

import fr.efrei2026.springfield.modeles.Incident;
import fr.efrei2026.springfield.modeles.enums.NiveauAttention;
import fr.efrei2026.springfield.modeles.enums.StatutIncident;
import fr.efrei2026.springfield.repositories.IncidentRepository;
import fr.efrei2026.springfield.utiles.PolitiqueUrgence;
import fr.efrei2026.springfield.services.interfaces.ITableauDeBordService;
import fr.efrei2026.springfield.shared.dto.IncidentActifDto;
import fr.efrei2026.springfield.shared.dto.TableauDeBordDto;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class TableauDeBordService implements ITableauDeBordService {

    private static final Comparator<IncidentActifDto> ORDRE_D_AFFICHAGE = Comparator.comparing(IncidentActifDto::getNiveauAttention)
            .thenComparing(IncidentActifDto::getDateOuverture);

    private final IncidentRepository incidentRepository;
    private final PolitiqueUrgence politiqueUrgence;
    private final Clock horloge;

    public TableauDeBordService(IncidentRepository incidentRepository, PolitiqueUrgence politiqueUrgence, Clock horloge) {
        this.incidentRepository = incidentRepository;
        this.politiqueUrgence = politiqueUrgence;
        this.horloge = horloge;
    }

    @Override
    public TableauDeBordDto construireTableauDeBord(Integer stationId, NiveauAttention niveauAttention) {
        List<IncidentActifDto> incidentsActifs = incidentRepository.trouverHorsStatut(StatutIncident.RESOLU).stream()
                .map(this::versIncidentActif)
                .sorted(ORDRE_D_AFFICHAGE)
                .toList();

        return new TableauDeBordDto(
                incidentsActifs.size(),
                compterParNiveau(incidentsActifs),
                calculerDureeMoyenneEnHeures(incidentsActifs),
                incidentRepository.compterStationsTouchees(),
                incidentsActifs.stream()
                        .filter(incident -> stationId == null || stationId == incident.getStationId())
                        .filter(incident -> niveauAttention == null || niveauAttention == incident.getNiveauAttention())
                        .toList());
    }

    private IncidentActifDto versIncidentActif(Incident incident) {
        return new IncidentActifDto(incident, politiqueUrgence.evaluer(incident), incident.dureeOuverture(horloge).toHours());
    }

    private Map<NiveauAttention, Long> compterParNiveau(List<IncidentActifDto> incidentsActifs) {
        Map<NiveauAttention, Long> nombreParNiveau = new EnumMap<>(NiveauAttention.class);
        for (NiveauAttention niveau : NiveauAttention.values()) {
            nombreParNiveau.put(niveau, 0L);
        }
        incidentsActifs.forEach(incident -> nombreParNiveau.merge(incident.getNiveauAttention(), 1L, Long::sum));
        return nombreParNiveau;
    }

    private long calculerDureeMoyenneEnHeures(List<IncidentActifDto> incidentsActifs) {
        return Math.round(incidentsActifs.stream()
                .mapToLong(IncidentActifDto::getDureeOuvertureEnHeures)
                .average()
                .orElse(0));
    }
}
