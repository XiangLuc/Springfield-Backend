package fr.efrei2026.springfield.services;

import fr.efrei2026.springfield.exceptions.EquipementIntrouvableException;
import fr.efrei2026.springfield.modeles.Equipement;
import fr.efrei2026.springfield.modeles.Incident;
import fr.efrei2026.springfield.modeles.enums.StatutIncident;
import fr.efrei2026.springfield.repositories.EquipementRepository;
import fr.efrei2026.springfield.repositories.IncidentRepository;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Service
public class EquipementService {

    private static final Set<StatutIncident> STATUTS_EN_COURS = EnumSet.of(StatutIncident.OUVERT, StatutIncident.AFFECTE);
    private static final Set<StatutIncident> STATUTS_PASSES = EnumSet.of(StatutIncident.RESOLU);

    private final EquipementRepository equipementRepository;
    private final IncidentRepository incidentRepository;
    private final StationService stationService;

    public EquipementService(EquipementRepository equipementRepository,
                             IncidentRepository incidentRepository,
                             StationService stationService) {
        this.equipementRepository = equipementRepository;
        this.incidentRepository = incidentRepository;
        this.stationService = stationService;
    }

    public List<Equipement> listerEquipementsDeLaStation(Integer stationId) {
        // Vérifie l'existence de la station pour distinguer « station inconnue » de « station sans équipement »
        stationService.trouverStation(stationId);
        return equipementRepository.findByStationIdOrderByLibelleAsc(stationId);
    }

    public Equipement trouverEquipement(Integer id) {
        return equipementRepository.trouverAvecTypeEtStation(id)
                .orElseThrow(() -> new EquipementIntrouvableException(id));
    }

    public List<Incident> listerIncidentsEnCours(Integer equipementId) {
        return incidentRepository.trouverParEquipementEtStatuts(equipementId, STATUTS_EN_COURS);
    }

    public List<Incident> listerIncidentsPasses(Integer equipementId) {
        return incidentRepository.trouverParEquipementEtStatuts(equipementId, STATUTS_PASSES);
    }
}