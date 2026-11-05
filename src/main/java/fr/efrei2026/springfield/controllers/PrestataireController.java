package fr.efrei2026.springfield.controllers;

import fr.efrei2026.springfield.services.interfaces.IPrestataireService;
import fr.efrei2026.springfield.shared.dto.PrestataireDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/prestataires")
@Tag(name = "Prestataires", description = "Prestataires et leur note moyenne")
@ApiResponse(responseCode = "401", description = "Utilisateur non connecté")
public class PrestataireController {

    private final IPrestataireService prestataireService;

    public PrestataireController(IPrestataireService prestataireService) {
        this.prestataireService = prestataireService;
    }

    @GetMapping
    @Operation(summary = "Lister les prestataires avec leur note moyenne", description = "Du mieux noté au moins bien noté.")
    @ApiResponse(responseCode = "200", description = "Liste des prestataires")
    public ResponseEntity<List<PrestataireDto>> listerPrestataires() {
        return ResponseEntity.ok(prestataireService.listerPrestataires());
    }
}
