package fr.efrei2026.springfield.controllers;

import fr.efrei2026.springfield.modeles.enums.NiveauAttention;
import fr.efrei2026.springfield.services.interfaces.ITableauDeBordService;
import fr.efrei2026.springfield.shared.dto.TableauDeBordDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tableau-de-bord")
@Tag(name = "Tableau de bord", description = "Supervision des incidents actifs")
@ApiResponse(responseCode = "401", description = "Utilisateur non connecté")
public class TableauDeBordController {

    private final ITableauDeBordService tableauDeBordService;

    public TableauDeBordController(ITableauDeBordService tableauDeBordService) {
        this.tableauDeBordService = tableauDeBordService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('RESPONSABLE_EXPLOITATION')")
    @Operation(summary = "Consulter le tableau de bord", description = "Incidents non résolus avec leur niveau d'attention, et indicateurs. Les filtres ne changent pas les indicateurs.")
    @ApiResponse(responseCode = "200", description = "Tableau de bord")
    @ApiResponse(responseCode = "400", description = "Filtre invalide")
    @ApiResponse(responseCode = "403", description = "Profil non autorisé")
    public ResponseEntity<TableauDeBordDto> consulterTableauDeBord(
            @Parameter(description = "Limiter la liste à une station") @RequestParam(required = false) Integer stationId,
            @Parameter(description = "Limiter la liste à un niveau d'attention") @RequestParam(required = false) NiveauAttention niveauAttention) {
        return ResponseEntity.ok(tableauDeBordService.construireTableauDeBord(stationId, niveauAttention));
    }
}
