package fr.efrei2026.springfield.services.interfaces;

import fr.efrei2026.springfield.modeles.enums.StatutIncident;
import fr.efrei2026.springfield.shared.dto.IncidentDto;
import fr.efrei2026.springfield.shared.dto.PageDto;
import org.springframework.data.domain.Pageable;

import java.io.Writer;
import java.time.LocalDate;

public interface IIncidentService {
    PageDto<IncidentDto> rechercherIncidents(StatutIncident statut, Integer stationId, Integer prestataireId,
                                             LocalDate ouvertDepuis, LocalDate ouvertJusquAu, String motCle,
                                             Pageable pagination);

    IncidentDto trouverIncident(Integer id);

    IncidentDto resoudre(Integer id);

    void exporterHistorique(Writer writer);
}