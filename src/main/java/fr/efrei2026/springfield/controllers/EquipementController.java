package fr.efrei2026.springfield.controllers;

import fr.efrei2026.springfield.modeles.Incident;
import fr.efrei2026.springfield.services.interfaces.IEquipementService;
import fr.efrei2026.springfield.shared.dto.EquipementDto;
import fr.efrei2026.springfield.shared.dto.IncidentDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/equipements")
public class EquipementController {

    private final IEquipementService equipementService;

    public EquipementController(IEquipementService equipementService) {
        this.equipementService = equipementService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipementDto> trouverEquipement(@PathVariable Integer id) {
        return ResponseEntity.ok(EquipementDto.depuis(equipementService.trouverEquipement(id)));
    }

    @GetMapping("/{id}/incidents/en-cours")
    public ResponseEntity<List<IncidentDto>> listerIncidentsEnCours(@PathVariable Integer id) {
        return ResponseEntity.ok(versDto(equipementService.listerIncidentsEnCours(id)));
    }

    @GetMapping("/{id}/incidents/passes")
    public ResponseEntity<List<IncidentDto>> listerIncidentsPasses(@PathVariable Integer id) {
        return ResponseEntity.ok(versDto(equipementService.listerIncidentsPasses(id)));
    }

    private List<IncidentDto> versDto(List<Incident> incidents) {
        return incidents.stream().map(IncidentDto::depuis).toList();
    }
}
