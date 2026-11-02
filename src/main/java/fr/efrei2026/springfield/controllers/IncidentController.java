package fr.efrei2026.springfield.controllers;

import fr.efrei2026.springfield.modeles.Incident;
import fr.efrei2026.springfield.services.interfaces.IIncidentService;
import fr.efrei2026.springfield.shared.dto.AffectationDto;
import fr.efrei2026.springfield.shared.dto.DeclarationIncidentDto;
import fr.efrei2026.springfield.shared.dto.EvaluationDto;
import fr.efrei2026.springfield.shared.dto.IncidentDetailDto;
import fr.efrei2026.springfield.shared.dto.IncidentResumeDto;
import fr.efrei2026.springfield.shared.dto.ModificationIncidentDto;
import fr.efrei2026.springfield.utiles.configurations.security.Autorisations;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

    private final IIncidentService incidentService;

    public IncidentController(IIncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @GetMapping
    public ResponseEntity<List<IncidentResumeDto>> listerIncidentsEnCours() {
        return ResponseEntity.ok(versResumes(incidentService.listerIncidentsEnCours()));
    }

    @GetMapping("/historique")
    public ResponseEntity<List<IncidentResumeDto>> listerIncidentsResolus() {
        return ResponseEntity.ok(versResumes(incidentService.listerIncidentsResolus()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<IncidentDetailDto> trouverIncident(@PathVariable Integer id) {
        Incident incident = incidentService.trouverIncident(id);
        EvaluationDto evaluation = incidentService.trouverEvaluation(id).map(EvaluationDto::depuis).orElse(null);
        return ResponseEntity.ok(new IncidentDetailDto(versResume(incident), evaluation));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IncidentResumeDto declarer(@Valid @RequestBody DeclarationIncidentDto declaration) {
        return versResume(incidentService.declarer(declaration));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<IncidentResumeDto> modifierDescription(@PathVariable Integer id,
                                                                 @Valid @RequestBody ModificationIncidentDto modification) {
        return ResponseEntity.ok(versResume(incidentService.modifierDescription(id, modification)));
    }

    @PutMapping("/{id}/prestataire")
    @PreAuthorize(Autorisations.RESPONSABLE_EXPLOITATION)
    public ResponseEntity<IncidentResumeDto> affecter(@PathVariable Integer id,
                                                      @Valid @RequestBody AffectationDto affectation) {
        return ResponseEntity.ok(versResume(incidentService.affecter(id, affectation)));
    }

    @PostMapping("/{id}/resolution")
    @PreAuthorize(Autorisations.RESPONSABLE_EXPLOITATION)
    public ResponseEntity<IncidentResumeDto> resoudre(@PathVariable Integer id) {
        return ResponseEntity.ok(versResume(incidentService.resoudre(id)));
    }

    private List<IncidentResumeDto> versResumes(List<Incident> incidents) {
        return incidents.stream().map(this::versResume).toList();
    }

    private IncidentResumeDto versResume(Incident incident) {
        return IncidentResumeDto.depuis(incident, incidentService.calculerDureeOuverture(incident));
    }
}
