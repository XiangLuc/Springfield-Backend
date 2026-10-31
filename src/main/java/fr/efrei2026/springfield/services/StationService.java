package fr.efrei2026.springfield.services;

import fr.efrei2026.springfield.exceptions.StationIntrouvableException;
import fr.efrei2026.springfield.modeles.Station;
import fr.efrei2026.springfield.repositories.StationRepository;
import fr.efrei2026.springfield.services.interfaces.IStationService;
import fr.efrei2026.springfield.shared.dto.ResultatImportCsvDto;
import fr.efrei2026.springfield.utiles.CsvUtil;
import fr.efrei2026.springfield.utiles.exceptions.DonneeInvalideException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
public class StationService implements IStationService {

    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @Override
    public List<Station> listerStations() {
        return stationRepository.findAllByOrderByNomAsc();
    }

    @Override
    public Station trouverStation(Integer id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new StationIntrouvableException(id));
    }

    @Override
    public ResultatImportCsvDto importerStations(MultipartFile fichier) {
        ResultatImportCsvDto resultat = new ResultatImportCsvDto();
        // on initialise à 1 pour pas compter le header
        int ligneEntete = 1;

        for (Map<String, String> ligne : CsvUtil.importerCsv(fichier)) {
            //on itère sur chaque ligne du csv
            ligneEntete++;
            try {
                creerStation(ligne.get("nom"));
                resultat.ajouterSucces();
            } catch (DonneeInvalideException e) {
                resultat.ajouterEchec(ligneEntete, ligne, e.getMessage());
            }
        }
        return resultat;
    }

    public void creerStation(String nom) {
        if (nom == null || nom.isBlank()) {
            throw new DonneeInvalideException("Le nom de la station est obligatoire.");
        }
        if (stationRepository.existsByNom(nom)) {
            throw new DonneeInvalideException("La station « " + nom + " » existe déjà.");
        }
        stationRepository.save(new Station(nom));
    }
}