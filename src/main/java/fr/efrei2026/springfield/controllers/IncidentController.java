package fr.efrei2026.springfield.controllers;

import fr.efrei2026.springfield.services.interfaces.IIncidentService;
import fr.efrei2026.springfield.shared.dto.IncidentDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/incidents")
@Tag(name = "Incidents", description = "Consultation des incidents et déclaration de leur résolution")
@ApiResponse(responseCode = "401", description = "Utilisateur non connecté")
public class IncidentController {

    private final IIncidentService incidentService;

    public IncidentController(IIncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @GetMapping
    @Operation(summary = "Lister tous les incidents, du plus récemment ouvert au plus ancien")
    @ApiResponse(responseCode = "200", description = "Liste des incidents, éventuellement vide")
    public ResponseEntity<List<IncidentDto>> listerIncidents() {
        return ResponseEntity.ok(incidentService.listerIncidents());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter le détail d'un incident")
    @ApiResponse(responseCode = "200", description = "Détail de l'incident")
    @ApiResponse(responseCode = "400", description = "Identifiant invalide")
    @ApiResponse(responseCode = "404", description = "Incident inexistant")
    public ResponseEntity<IncidentDto> trouverIncident(@PathVariable Integer id) {
        return ResponseEntity.ok(incidentService.trouverIncident(id));
    }

    @PatchMapping("/{id}/resolution")
    @PreAuthorize("hasAuthority('RESPONSABLE_EXPLOITATION')")
    @Operation(summary = "Déclarer un incident résolu, à la date et à l'heure courantes")
    @ApiResponse(responseCode = "200", description = "Incident résolu")
    @ApiResponse(responseCode = "400", description = "Identifiant invalide")
    @ApiResponse(responseCode = "403", description = "Profil non autorisé ou jeton CSRF absent")
    @ApiResponse(responseCode = "404", description = "Incident inexistant")
    @ApiResponse(responseCode = "409", description = "Incident déjà résolu")
    public ResponseEntity<IncidentDto> resoudreIncident(@PathVariable Integer id) {
        return ResponseEntity.ok(incidentService.resoudre(id));
    }

    @GetMapping("/historique/export")
    @Operation(summary = "Exporter en CSV l'historique des incidents résolus")
    @ApiResponse(responseCode = "200", description = "Fichier CSV de l'historique")
    public void exporterHistorique(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=historique_incidents.csv");
        incidentService.exporterHistorique(response.getWriter());
    }
}
