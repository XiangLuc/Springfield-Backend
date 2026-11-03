package fr.efrei2026.springfield.controllers;

import fr.efrei2026.springfield.services.interfaces.IPrestataireService;
import fr.efrei2026.springfield.shared.dto.PrestataireDto;
import org.springframework.http.ResponseEntity;
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

    @GetMapping
    public ResponseEntity<List<PrestataireDto>> listerPrestataires() {
        return ResponseEntity.ok(prestataireService.listerPrestataires());
    }
}
