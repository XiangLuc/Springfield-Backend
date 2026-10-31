package fr.efrei2026.springfield.services.interfaces;

import fr.efrei2026.springfield.modeles.Station;
import fr.efrei2026.springfield.shared.dto.ResultatImportCsvDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IStationService {
    List<Station> listerStations();
    Station trouverStation(Integer id);
    ResultatImportCsvDto importerStations(MultipartFile fichier);
}
