package fr.efrei2026.springfield.controllers;

import fr.efrei2026.springfield.modeles.Equipement;
import fr.efrei2026.springfield.modeles.Station;
import fr.efrei2026.springfield.services.EquipementService;
import fr.efrei2026.springfield.services.StationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/stations")
public class StationController {

    private final StationService stationService;
    private final EquipementService equipementService;

    public StationController(StationService stationService, EquipementService equipementService) {
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
}