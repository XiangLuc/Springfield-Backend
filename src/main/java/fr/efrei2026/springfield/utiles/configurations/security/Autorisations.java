package fr.efrei2026.springfield.utiles.configurations.security;

// Expressions @PreAuthorize centralisées : l'autorité porte le nom du profil (cf. JwtFilter)
public final class Autorisations {

    public static final String RESPONSABLE_EXPLOITATION = "hasAuthority('RESPONSABLE_EXPLOITATION')";

    private Autorisations() {
    }
}
