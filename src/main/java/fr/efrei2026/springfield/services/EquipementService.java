package fr.efrei2026.springfield.services;

import fr.efrei2026.springfield.exceptions.EquipementIntrouvableException;
import fr.efrei2026.springfield.modeles.Equipement;
import fr.efrei2026.springfield.modeles.Station;
import fr.efrei2026.springfield.modeles.TypeEquipement;
import fr.efrei2026.springfield.repositories.StationRepository;
import fr.efrei2026.springfield.repositories.TypeEquipementRepository;
import fr.efrei2026.springfield.services.interfaces.IEquipementService;
import fr.efrei2026.springfield.modeles.Incident;
import fr.efrei2026.springfield.modeles.enums.StatutIncident;
import fr.efrei2026.springfield.repositories.EquipementRepository;
import fr.efrei2026.springfield.repositories.IncidentRepository;
import fr.efrei2026.springfield.shared.dto.ResultatImportCsvDto;
import fr.efrei2026.springfield.utiles.CsvUtil;
import fr.efrei2026.springfield.utiles.exceptions.DonneeInvalideException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class EquipementService implements IEquipementService {

    private static final Set<StatutIncident> STATUTS_EN_COURS = EnumSet.of(StatutIncident.OUVERT, StatutIncident.AFFECTE);
    private static final Set<StatutIncident> STATUTS_PASSES = EnumSet.of(StatutIncident.RESOLU);

    private final EquipementRepository equipementRepository;
    private final IncidentRepository incidentRepository;
    private final StationService stationService;
    private final StationRepository stationRepository;
    private final TypeEquipementRepository typeEquipementRepository;

    public EquipementService(EquipementRepository equipementRepository,
                             IncidentRepository incidentRepository,
                             StationService stationService, StationRepository stationRepository, TypeEquipementRepository typeEquipementRepository) {
        this.equipementRepository = equipementRepository;
        this.incidentRepository = incidentRepository;
        this.stationService = stationService;
        this.stationRepository = stationRepository;
        this.typeEquipementRepository = typeEquipementRepository;
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

    @Override
    public ResultatImportCsvDto importerEquipements(MultipartFile fichier) {
        ResultatImportCsvDto resultat = new ResultatImportCsvDto();
        int ligneEntete = 1;

        for (Map<String, String> ligne : CsvUtil.importerCsv(fichier)) {
            ligneEntete++;
            try {
                creerEquipement(ligne);
                resultat.ajouterSucces();
            } catch (DonneeInvalideException e) {
                resultat.ajouterEchec(ligneEntete, ligne, e.getMessage());
            }
        }
        return resultat;
    }

    private void creerEquipement(Map<String, String> ligne) {
        String reference = lireChampObligatoire(ligne, "reference");
        String libelle = lireChampObligatoire(ligne, "libelle");
        String nomType = lireChampObligatoire(ligne, "type");
        String nomStation = lireChampObligatoire(ligne, "station");

        if (equipementRepository.existsByReference(reference)) {
            throw new DonneeInvalideException("La référence « " + reference + " » existe déjà.");
        }
        TypeEquipement type = typeEquipementRepository.findByNom(nomType)
                .orElseThrow(() -> new DonneeInvalideException("Le type d'équipement « " + nomType + " » est inconnu."));
        Station station = stationRepository.findByNom(nomStation)
                .orElseThrow(() -> new DonneeInvalideException("La station « " + nomStation + " » est inconnue."));

        equipementRepository.save(new Equipement(reference, libelle, type, station));
    }

    private String lireChampObligatoire(Map<String, String> ligne, String colonne) {
        String valeur = ligne.get(colonne);
        if (valeur == null || valeur.isBlank()) {
            throw new DonneeInvalideException("La colonne « " + colonne + " » est obligatoire.");
        }
        return valeur;
    }
}