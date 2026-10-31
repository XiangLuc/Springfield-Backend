package fr.efrei2026.springfield.controllers;

import fr.efrei2026.springfield.modeles.Equipement;
import fr.efrei2026.springfield.modeles.Incident;
import fr.efrei2026.springfield.services.interfaces.IEquipementService;
import fr.efrei2026.springfield.shared.dto.ResultatImportCsvDto;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/equipements")
public class EquipementController {

    private final IEquipementService equipementService;

    public EquipementController(IEquipementService equipementService) {
        this.equipementService = equipementService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Equipement> trouverEquipement(@PathVariable Integer id) {
        return ResponseEntity.ok(equipementService.trouverEquipement(id));
    }

    @GetMapping("/{id}/incidents/en-cours")
    public ResponseEntity<List<Incident>> listerIncidentsEnCours(@PathVariable Integer id) {
        return ResponseEntity.ok(equipementService.listerIncidentsEnCours(id));
    }

    @GetMapping("/{id}/incidents/passes")
    public ResponseEntity<List<Incident>> listerIncidentsPasses(@PathVariable Integer id) {
        return ResponseEntity.ok(equipementService.listerIncidentsPasses(id));
    }

    @PreAuthorize("hasAuthority('RESPONSABLE_EXPLOITATION')")
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResultatImportCsvDto> importerEquipements(@RequestParam("fichier") MultipartFile fichier) {
        return ResponseEntity.ok(equipementService.importerEquipements(fichier));
    }
}