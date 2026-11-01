package fr.efrei2026.springfield.controllers;

import fr.efrei2026.springfield.modeles.enums.NiveauAttention;
import fr.efrei2026.springfield.services.interfaces.ITableauDeBordService;
import fr.efrei2026.springfield.shared.dto.TableauDeBordDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tableau-de-bord")
public class TableauDeBordController {

    private final ITableauDeBordService tableauDeBordService;

    public TableauDeBordController(ITableauDeBordService tableauDeBordService) {
        this.tableauDeBordService = tableauDeBordService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('RESPONSABLE_EXPLOITATION')")
    public ResponseEntity<TableauDeBordDto> consulterTableauDeBord(@RequestParam(required = false) Integer stationId, @RequestParam(required = false) NiveauAttention niveauAttention) {
        return ResponseEntity.ok(tableauDeBordService.construireTableauDeBord(stationId, niveauAttention));
    }
}
