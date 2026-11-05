package fr.efrei2026.springfield.services.interfaces;

import fr.efrei2026.springfield.modeles.Equipement;
import fr.efrei2026.springfield.modeles.Incident;
import fr.efrei2026.springfield.shared.dto.ResultatImportCsvDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IEquipementService {
    List<Equipement> listerEquipementsDeLaStation(Integer stationId);
    Equipement trouverEquipement(Integer id);
    List<Incident> listerIncidentsEnCours(Integer equipementId);
    List<Incident> listerIncidentsPasses(Integer equipementId);
    ResultatImportCsvDto importerEquipements(MultipartFile fichier);
}
