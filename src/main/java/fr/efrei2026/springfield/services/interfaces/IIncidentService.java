package fr.efrei2026.springfield.services.interfaces;

import fr.efrei2026.springfield.shared.dto.IncidentDto;

import java.io.Writer;
import java.util.List;

public interface IIncidentService {
    List<IncidentDto> listerIncidents();

    IncidentDto trouverIncident(Integer id);

    IncidentDto resoudre(Integer id);

    void exporterHistorique(Writer writer);
}