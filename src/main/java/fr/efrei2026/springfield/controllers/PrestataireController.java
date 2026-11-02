package fr.efrei2026.springfield.controllers;

import fr.efrei2026.springfield.services.interfaces.IPrestataireService;
import fr.efrei2026.springfield.shared.dto.PrestataireDto;
import fr.efrei2026.springfield.utiles.configurations.security.Autorisations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/prestataires")
public class PrestataireController {

    private final IPrestataireService prestataireService;

    public PrestataireController(IPrestataireService prestataireService) {
        this.prestataireService = prestataireService;
    }

    // Seul le responsable affecte les incidents : lui seul a besoin de la liste
    @GetMapping
    @PreAuthorize(Autorisations.RESPONSABLE_EXPLOITATION)
    public ResponseEntity<List<PrestataireDto>> listerPrestataires() {
        return ResponseEntity.ok(prestataireService.listerPrestataires().stream().map(PrestataireDto::depuis).toList());
    }
}
