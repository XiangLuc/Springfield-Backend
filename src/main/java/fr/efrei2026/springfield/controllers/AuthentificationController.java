package fr.efrei2026.springfield.controllers;

import fr.efrei2026.springfield.modeles.Utilisateur;
import fr.efrei2026.springfield.services.interfaces.IJwtService;
import fr.efrei2026.springfield.services.interfaces.IUtilisateurService;
import fr.efrei2026.springfield.shared.dto.ConnexionDto;
import fr.efrei2026.springfield.shared.dto.InscriptionDto;
import fr.efrei2026.springfield.shared.dto.UtilisateurDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
public class AuthentificationController {

    private final IUtilisateurService utilisateurService;
    private final IJwtService jwtService;

    public AuthentificationController(IUtilisateurService utilisateurService, IJwtService jwtService) {
        this.utilisateurService = utilisateurService;
        this.jwtService = jwtService;
    }

    @PostMapping("/connexion")
    public ResponseEntity<UtilisateurDto> connecter(@Valid @RequestBody ConnexionDto connexion) {
        Utilisateur utilisateur = utilisateurService.connecter(connexion);
        ResponseCookie cookie = creerCookie(jwtService.genererJeton(utilisateur), jwtService.getDureeValidite());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new UtilisateurDto(utilisateur));
    }

    @PostMapping("/inscription")
    @PreAuthorize("hasAuthority('RESPONSABLE_EXPLOITATION')")
    public ResponseEntity<UtilisateurDto> inscrire(@Valid @RequestBody InscriptionDto inscription) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new UtilisateurDto(utilisateurService.inscrire(inscription)));
    }

    @GetMapping("/info")
    public ResponseEntity<UtilisateurDto> recupererUtilisateurConnecte(Authentication authentification) {
        if (authentification == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(new UtilisateurDto(utilisateurService.trouverParLogin(authentification.getName())));
    }

    @PostMapping("/deconnexion")
    public ResponseEntity<Void> deconnecter() {
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, creerCookie("", Duration.ZERO).toString())
                .build();
    }

    private ResponseCookie creerCookie(String jeton, Duration duree) {
        return ResponseCookie.from(IJwtService.NOM_COOKIE, jeton)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/api")
                .maxAge(duree)
                .build();
    }
}
