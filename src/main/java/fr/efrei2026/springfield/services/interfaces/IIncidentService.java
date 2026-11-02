package fr.efrei2026.springfield.services.interfaces;

import fr.efrei2026.springfield.modeles.Evaluation;
import fr.efrei2026.springfield.modeles.Incident;
import fr.efrei2026.springfield.shared.dto.AffectationDto;
import fr.efrei2026.springfield.shared.dto.DeclarationIncidentDto;
import fr.efrei2026.springfield.shared.dto.ModificationIncidentDto;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

public interface IIncidentService {
    List<Incident> listerIncidentsEnCours();
    List<Incident> listerIncidentsResolus();
    Incident trouverIncident(Integer id);
    Optional<Evaluation> trouverEvaluation(Integer incidentId);
    Duration calculerDureeOuverture(Incident incident);
    Incident declarer(DeclarationIncidentDto declaration);
    Incident modifierDescription(Integer id, ModificationIncidentDto modification);
    Incident affecter(Integer id, AffectationDto affectation);
    Incident resoudre(Integer id);
}
