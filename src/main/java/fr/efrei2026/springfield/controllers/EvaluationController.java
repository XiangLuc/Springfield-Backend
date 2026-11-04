package fr.efrei2026.springfield.controllers;

import fr.efrei2026.springfield.services.interfaces.IEvaluationService;
import fr.efrei2026.springfield.shared.dto.EvaluationCreationDto;
import fr.efrei2026.springfield.shared.dto.EvaluationDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/incidents/{incidentId}/evaluation")
@Tag(name = "Évaluations", description = "Évaluation du prestataire d'un incident résolu")
@ApiResponse(responseCode = "401", description = "Utilisateur non connecté")
public class EvaluationController {

    private final IEvaluationService evaluationService;

    public EvaluationController(IEvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('RESPONSABLE_EXPLOITATION')")
    @Operation(summary = "Évaluer le prestataire d'un incident", description = "L'incident doit être résolu et affecté. Une seule évaluation par incident, avec une note de 1 à 5.")
    @ApiResponse(responseCode = "201", description = "Évaluation enregistrée")
    @ApiResponse(responseCode = "400", description = "Note manquante ou hors limites, commentaire trop long")
    @ApiResponse(responseCode = "403", description = "Profil non autorisé")
    @ApiResponse(responseCode = "404", description = "Incident inexistant")
    @ApiResponse(responseCode = "409", description = "Incident non résolu, sans prestataire ou déjà évalué")
    public ResponseEntity<EvaluationDto> evaluer(@PathVariable Integer incidentId, @Valid @RequestBody EvaluationCreationDto evaluation) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new EvaluationDto(evaluationService.evaluer(incidentId, evaluation)));
    }

    @GetMapping
    @Operation(summary = "Consulter l'évaluation d'un incident")
    @ApiResponse(responseCode = "200", description = "Évaluation de l'incident")
    @ApiResponse(responseCode = "404", description = "Aucune évaluation pour cet incident")
    public ResponseEntity<EvaluationDto> trouverEvaluation(@PathVariable Integer incidentId) {
        return ResponseEntity.ok(new EvaluationDto(evaluationService.trouverEvaluation(incidentId)));
    }
}
