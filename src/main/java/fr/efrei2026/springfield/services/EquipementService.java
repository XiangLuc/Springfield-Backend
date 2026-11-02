package fr.efrei2026.springfield.services;

import fr.efrei2026.springfield.exceptions.EquipementIntrouvableException;
import fr.efrei2026.springfield.modeles.Equipement;
import fr.efrei2026.springfield.modeles.Incident;
import fr.efrei2026.springfield.modeles.enums.StatutIncident;
import fr.efrei2026.springfield.repositories.EquipementRepository;
import fr.efrei2026.springfield.repositories.IncidentRepository;
import fr.efrei2026.springfield.services.interfaces.IEquipementService;
import fr.efrei2026.springfield.services.interfaces.IStationService;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Service
public class EquipementService implements IEquipementService {

    private static final Set<StatutIncident> STATUTS_PASSES = EnumSet.of(StatutIncident.RESOLU);

    private final EquipementRepository equipementRepository;
    private final IncidentRepository incidentRepository;
    private final IStationService stationService;

    public EquipementService(EquipementRepository equipementRepository,
                             IncidentRepository incidentRepository,
                             IStationService stationService) {
        this.equipementRepository = equipementRepository;
        this.incidentRepository = incidentRepository;
        this.stationService = stationService;
    }

    @Override
    public List<Equipement> listerEquipementsDeLaStation(Integer stationId) {
        // Vérifie l'existence de la station pour distinguer « station inconnue » de « station sans équipement »
        stationService.trouverStation(stationId);
        return equipementRepository.findByStationIdOrderByLibelleAsc(stationId);
    }

    @Override
    public Equipement trouverEquipement(Integer id) {
        return equipementRepository.trouverAvecTypeEtStation(id)
                .orElseThrow(() -> new EquipementIntrouvableException(id));
    }

    @Override
    public List<Incident> listerIncidentsEnCours(Integer equipementId) {
        return listerIncidents(equipementId, StatutIncident.EN_COURS);
    }

    @Override
    public List<Incident> listerIncidentsPasses(Integer equipementId) {
        return listerIncidents(equipementId, STATUTS_PASSES);
    }

    private List<Incident> listerIncidents(Integer equipementId, Set<StatutIncident> statuts) {
        // Même raison que pour la station : un équipement inconnu doit donner une 404, pas une liste vide
        verifierExistence(equipementId);
        return incidentRepository.trouverParEquipementEtStatuts(equipementId, statuts);
    }

    private void verifierExistence(Integer equipementId) {
        if (!equipementRepository.existsById(equipementId)) {
            throw new EquipementIntrouvableException(equipementId);
        }
    }
}
