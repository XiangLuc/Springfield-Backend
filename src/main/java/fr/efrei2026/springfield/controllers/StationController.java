package fr.efrei2026.springfield.controllers;

import fr.efrei2026.springfield.services.interfaces.IEquipementService;
import fr.efrei2026.springfield.services.interfaces.IStationService;
import fr.efrei2026.springfield.shared.dto.EquipementDto;
import fr.efrei2026.springfield.shared.dto.StationDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<List<StationDto>> listerStations() {
        return ResponseEntity.ok(stationService.listerStations().stream().map(StationDto::depuis).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StationDto> trouverStation(@PathVariable Integer id) {
        return ResponseEntity.ok(StationDto.depuis(stationService.trouverStation(id)));
    }

    @GetMapping("/{id}/equipements")
    public ResponseEntity<List<EquipementDto>> listerEquipementsDeLaStation(@PathVariable Integer id) {
        return ResponseEntity.ok(equipementService.listerEquipementsDeLaStation(id).stream().map(EquipementDto::depuis).toList());
    }
}
