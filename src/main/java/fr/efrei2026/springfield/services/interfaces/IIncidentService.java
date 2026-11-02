package fr.efrei2026.springfield.services.interfaces;

import fr.efrei2026.springfield.modeles.enums.StatutIncident;
import fr.efrei2026.springfield.shared.dto.AffectationDto;
import fr.efrei2026.springfield.shared.dto.DeclarationIncidentDto;
import fr.efrei2026.springfield.shared.dto.IncidentDetailDto;
import fr.efrei2026.springfield.shared.dto.IncidentDto;
import fr.efrei2026.springfield.shared.dto.ModificationIncidentDto;
import fr.efrei2026.springfield.shared.dto.PageDto;
import org.springframework.data.domain.Pageable;

import java.io.Writer;
import java.time.LocalDate;
import java.util.List;

public interface IIncidentService {
    PageDto<IncidentDto> rechercherIncidents(StatutIncident statut, Integer stationId, Integer prestataireId,
                                             LocalDate ouvertDepuis, LocalDate ouvertJusquAu, String motCle,
                                             Pageable pagination);

    List<IncidentDto> listerIncidentsEnCours();

    List<IncidentDto> listerIncidentsResolus();

    IncidentDetailDto trouverIncident(Integer id);

    IncidentDto resoudre(Integer id);

    IncidentDto declarer(DeclarationIncidentDto declaration);

    IncidentDto modifierDescription(Integer id, ModificationIncidentDto modification);

    IncidentDto affecter(Integer id, AffectationDto affectation);

    void exporterHistorique(Writer writer);
}