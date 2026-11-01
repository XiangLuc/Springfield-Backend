package fr.efrei2026.springfield.controllers;

import fr.efrei2026.springfield.services.interfaces.IEvaluationService;
import fr.efrei2026.springfield.shared.dto.EvaluationCreationDto;
import fr.efrei2026.springfield.shared.dto.EvaluationDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/incidents/{incidentId}/evaluation")
public class EvaluationController {

    private final IEvaluationService evaluationService;

    public EvaluationController(IEvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('RESPONSABLE_EXPLOITATION')")
    public EvaluationDto evaluer(@PathVariable Integer incidentId, @Valid @RequestBody EvaluationCreationDto evaluation) {
        return new EvaluationDto(evaluationService.evaluer(incidentId, evaluation));
    }

    @GetMapping
    public EvaluationDto trouverEvaluation(@PathVariable Integer incidentId) {
        return new EvaluationDto(evaluationService.trouverEvaluation(incidentId));
    }
}
