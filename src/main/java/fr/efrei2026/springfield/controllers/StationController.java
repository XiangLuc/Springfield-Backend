package fr.efrei2026.springfield.controllers;

import fr.efrei2026.springfield.modeles.Equipement;
import fr.efrei2026.springfield.modeles.Station;
import fr.efrei2026.springfield.services.interfaces.IEquipementService;
import fr.efrei2026.springfield.services.interfaces.IStationService;
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
@RequestMapping("/api/stations")
@Tag(name = "Stations", description = "Stations du réseau, leurs équipements et import CSV")
@ApiResponse(responseCode = "401", description = "Utilisateur non connecté")
public class StationController {

    private final IStationService stationService;
    private final IEquipementService equipementService;

    public StationController(IStationService stationService, IEquipementService equipementService) {
        this.stationService = stationService;
        this.equipementService = equipementService;
    }

    @GetMapping
    @Operation(summary = "Lister les stations", description = "Triées par nom.")
    @ApiResponse(responseCode = "200", description = "Liste des stations")
    public ResponseEntity<List<Station>> listerStations() {
        return ResponseEntity.ok(stationService.listerStations());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter une station")
    @ApiResponse(responseCode = "200", description = "Détail de la station")
    @ApiResponse(responseCode = "400", description = "Identifiant invalide")
    @ApiResponse(responseCode = "404", description = "Station inexistante")
    public ResponseEntity<Station> trouverStation(@PathVariable Integer id) {
        return ResponseEntity.ok(stationService.trouverStation(id));
    }

    @GetMapping("/{id}/equipements")
    @Operation(summary = "Lister les équipements d'une station", description = "Avec leur type et leur état de fonctionnement.")
    @ApiResponse(responseCode = "200", description = "Liste des équipements, éventuellement vide")
    @ApiResponse(responseCode = "400", description = "Identifiant invalide")
    @ApiResponse(responseCode = "404", description = "Station inexistante")
    public ResponseEntity<List<Equipement>> listerEquipementsDeLaStation(@PathVariable Integer id) {
        return ResponseEntity.ok(equipementService.listerEquipementsDeLaStation(id));
    }

    @PreAuthorize("hasAuthority('RESPONSABLE_EXPLOITATION')")
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Importer des stations depuis un fichier CSV",
            description = "Une colonne : nom. Les lignes valides sont enregistrées, les autres sont listées dans la réponse.")
    @ApiResponse(responseCode = "200", description = "Bilan de l'import")
    @ApiResponse(responseCode = "403", description = "Profil non autorisé")
    public ResponseEntity<ResultatImportCsvDto> importerStations(@RequestParam("fichier") MultipartFile fichier) {
        return ResponseEntity.ok(stationService.importerStations(fichier));
    }
}
