package fr.efrei2026.springfield.controllers;

import fr.efrei2026.springfield.modeles.enums.StatutIncident;
import fr.efrei2026.springfield.services.interfaces.IIncidentService;
import fr.efrei2026.springfield.shared.dto.AffectationDto;
import fr.efrei2026.springfield.shared.dto.DeclarationIncidentDto;
import fr.efrei2026.springfield.shared.dto.IncidentDto;
import fr.efrei2026.springfield.shared.dto.ModificationIncidentDto;
import fr.efrei2026.springfield.shared.dto.PageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/incidents")
@Tag(name = "Incidents", description = "Consultation des incidents et déclaration de leur résolution")
@ApiResponse(responseCode = "401", description = "Utilisateur non connecté")
public class IncidentController {

    private static final int TAILLE_PAGE_PAR_DEFAUT = 20;

    private final IIncidentService incidentService;

    public IncidentController(IIncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @GetMapping
    @Operation(summary = "Rechercher les incidents avec des filtres combinables, un tri et une pagination", description = "Par défaut : du plus récemment ouvert au plus ancien. "
                    + "Tri possible sur dateOuverture, dateResolution, statut et equipement.station.nom.")
    @ApiResponse(responseCode = "200", description = "Page d'incidents, éventuellement vide")
    @ApiResponse(responseCode = "400", description = "Filtre ou tri invalide")
    public ResponseEntity<PageDto<IncidentDto>> rechercherIncidents(
            @RequestParam(required = false) StatutIncident statut,
            @RequestParam(required = false) Integer stationId,
            @RequestParam(required = false) Integer prestataireId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ouvertDepuis,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ouvertJusquAu,
            @RequestParam(required = false) String motCle,
            @ParameterObject @PageableDefault(size = TAILLE_PAGE_PAR_DEFAUT, sort = "dateOuverture", direction = Sort.Direction.DESC) Pageable pagination) {
        return ResponseEntity.ok(incidentService.rechercherIncidents(statut, stationId, prestataireId, ouvertDepuis, ouvertJusquAu, motCle, pagination));
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

    @PostMapping
    @Operation(summary = "Déclarer un incident sur un équipement", description = "L'incident est ouvert à la date et à l'heure courantes, sans prestataire.")
    @ApiResponse(responseCode = "201", description = "Incident déclaré")
    @ApiResponse(responseCode = "400", description = "Équipement ou description manquant, description trop longue")
    @ApiResponse(responseCode = "404", description = "Équipement inexistant")
    public ResponseEntity<IncidentDto> declarerIncident(@Valid @RequestBody DeclarationIncidentDto declaration) {
        return ResponseEntity.status(HttpStatus.CREATED).body(incidentService.declarer(declaration));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Modifier la description d'un incident non résolu")
    @ApiResponse(responseCode = "200", description = "Description modifiée")
    @ApiResponse(responseCode = "400", description = "Description manquante ou trop longue")
    @ApiResponse(responseCode = "404", description = "Incident inexistant")
    @ApiResponse(responseCode = "409", description = "Incident déjà résolu")
    public ResponseEntity<IncidentDto> modifierDescription(@PathVariable Integer id,
                                                           @Valid @RequestBody ModificationIncidentDto modification) {
        return ResponseEntity.ok(incidentService.modifierDescription(id, modification));
    }

    @PutMapping("/{id}/prestataire")
    @PreAuthorize("hasAuthority('RESPONSABLE_EXPLOITATION')")
    @Operation(summary = "Affecter ou réaffecter un prestataire à un incident non résolu")
    @ApiResponse(responseCode = "200", description = "Prestataire affecté")
    @ApiResponse(responseCode = "400", description = "Prestataire manquant")
    @ApiResponse(responseCode = "403", description = "Profil non autorisé ou jeton CSRF absent")
    @ApiResponse(responseCode = "404", description = "Incident ou prestataire inexistant")
    @ApiResponse(responseCode = "409", description = "Incident déjà résolu")
    public ResponseEntity<IncidentDto> affecterPrestataire(@PathVariable Integer id,
                                                           @Valid @RequestBody AffectationDto affectation) {
        return ResponseEntity.ok(incidentService.affecter(id, affectation));
    }

    @GetMapping("/historique/export")
    @Operation(summary = "Exporter en CSV l'historique des incidents résolus")
    @ApiResponse(responseCode = "200", description = "Fichier CSV de l'historique")
    public ResponseEntity<String> exporterHistorique() {
        StringWriter csv = new StringWriter();
        incidentService.exporterHistorique(csv);
        return ResponseEntity.ok()
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=historique_incidents.csv")
                .body(csv.toString());
    }
}
