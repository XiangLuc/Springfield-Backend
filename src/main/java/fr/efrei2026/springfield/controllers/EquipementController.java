package fr.efrei2026.springfield.controllers;

import fr.efrei2026.springfield.modeles.Equipement;
import fr.efrei2026.springfield.modeles.Incident;
import fr.efrei2026.springfield.services.interfaces.IEquipementService;
import fr.efrei2026.springfield.shared.dto.ResultatImportCsvDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/equipements")
@Tag(name = "Équipements", description = "Fiche d'un équipement, ses incidents et import CSV")
@ApiResponse(responseCode = "401", description = "Utilisateur non connecté")
public class EquipementController {

    private final IEquipementService equipementService;

    public EquipementController(IEquipementService equipementService) {
        this.equipementService = equipementService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter la fiche d'un équipement")
    @ApiResponse(responseCode = "200", description = "Fiche de l'équipement")
    @ApiResponse(responseCode = "400", description = "Identifiant invalide")
    @ApiResponse(responseCode = "404", description = "Équipement inexistant")
    public ResponseEntity<Equipement> trouverEquipement(@PathVariable Integer id) {
        return ResponseEntity.ok(equipementService.trouverEquipement(id));
    }

    @GetMapping("/{id}/incidents/en-cours")
    @Operation(summary = "Lister les incidents en cours d'un équipement", description = "Du plus récemment ouvert au plus ancien.")
    @ApiResponse(responseCode = "200", description = "Liste des incidents, éventuellement vide")
    @ApiResponse(responseCode = "400", description = "Identifiant invalide")
    public ResponseEntity<List<Incident>> listerIncidentsEnCours(@PathVariable Integer id) {
        return ResponseEntity.ok(equipementService.listerIncidentsEnCours(id));
    }

    @GetMapping("/{id}/incidents/passes")
    @Operation(summary = "Lister les incidents résolus d'un équipement", description = "Du plus récemment ouvert au plus ancien.")
    @ApiResponse(responseCode = "200", description = "Liste des incidents, éventuellement vide")
    @ApiResponse(responseCode = "400", description = "Identifiant invalide")
    public ResponseEntity<List<Incident>> listerIncidentsPasses(@PathVariable Integer id) {
        return ResponseEntity.ok(equipementService.listerIncidentsPasses(id));
    }

    @PreAuthorize("hasAuthority('RESPONSABLE_EXPLOITATION')")
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Importer des équipements depuis un fichier CSV",
            description = "Colonnes séparées par un point-virgule : reference;libelle;type;station. Les lignes valides sont enregistrées, les autres sont listées dans la réponse.")
    @ApiResponse(responseCode = "200", description = "Bilan de l'import")
    @ApiResponse(responseCode = "403", description = "Profil non autorisé")
    public ResponseEntity<ResultatImportCsvDto> importerEquipements(@RequestParam("fichier") MultipartFile fichier) {
        return ResponseEntity.ok(equipementService.importerEquipements(fichier));
    }
}