package fr.efrei2026.springfield.controllers;

import fr.efrei2026.springfield.modeles.Equipement;
import fr.efrei2026.springfield.modeles.Station;
import fr.efrei2026.springfield.services.interfaces.IEquipementService;
import fr.efrei2026.springfield.services.interfaces.IStationService;
import fr.efrei2026.springfield.shared.dto.ResultatImportCsvDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/stations")
public class StationController {

    private final IStationService stationService;
    private final IEquipementService equipementService;

    public StationController(IStationService stationService, IEquipementService equipementService) {
        this.stationService = stationService;
        this.equipementService = equipementService;
    }

    @GetMapping
    public ResponseEntity<List<Station>> listerStations() {
        return ResponseEntity.ok(stationService.listerStations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Station> trouverStation(@PathVariable Integer id) {
        return ResponseEntity.ok(stationService.trouverStation(id));
    }

    @GetMapping("/{id}/equipements")
    public ResponseEntity<List<Equipement>> listerEquipementsDeLaStation(@PathVariable Integer id) {
        return ResponseEntity.ok(equipementService.listerEquipementsDeLaStation(id));
    }

    @PostMapping("/import")
    public ResponseEntity<ResultatImportCsvDto> importerStations(@RequestParam("fichier") MultipartFile fichier) {
        return ResponseEntity.ok(stationService.importerStations(fichier));
    }
}