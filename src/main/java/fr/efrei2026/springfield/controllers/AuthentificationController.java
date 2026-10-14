package fr.efrei2026.springfield.controllers;

import fr.efrei2026.springfield.services.impls.UtilisateurService;
import fr.efrei2026.springfield.shared.dto.ConnexionDto;
import fr.efrei2026.springfield.shared.dto.InscriptionDto;
import fr.efrei2026.springfield.shared.dto.TokenDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthentificationController {

    private final UtilisateurService utilisateurService;

    public AuthentificationController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @PostMapping("/connexion")
    public ResponseEntity<TokenDto> connecter(@Valid @RequestBody ConnexionDto connexion) {
        return ResponseEntity.ok(utilisateurService.connecter(connexion));
    }

    @PostMapping("/inscription")
    public ResponseEntity<TokenDto> inscrire(@Valid @RequestBody InscriptionDto inscription) {
        return ResponseEntity.status(HttpStatus.CREATED).body(utilisateurService.inscrire(inscription));
    }
}
