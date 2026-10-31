package fr.efrei2026.springfield.services.interfaces;

import fr.efrei2026.springfield.modeles.Equipement;
import fr.efrei2026.springfield.modeles.Incident;

import java.util.List;

public interface IEquipementService {
    List<Equipement> listerEquipementsDeLaStation(Integer stationId);
    Equipement trouverEquipement(Integer id);
    List<Incident> listerIncidentsEnCours(Integer equipementId);
    List<Incident> listerIncidentsPasses(Integer equipementId);
}
