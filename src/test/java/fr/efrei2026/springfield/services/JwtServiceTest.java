package fr.efrei2026.springfield.services;

import fr.efrei2026.springfield.modeles.Utilisateur;
import fr.efrei2026.springfield.modeles.enums.Profil;
import fr.efrei2026.springfield.services.impls.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JwtServiceTest {

    private final SecretKey cle = new SecretKeySpec(
            "password12345678910111213141513424242424242426"
                    .getBytes(StandardCharsets.UTF_8), "HmacSHA256"
    );

    @Test
    void leJetonContientLeLoginLeProfilEtLePrenom() {
        JwtService jwtService = new JwtService(NimbusJwtEncoder.withSecretKey(cle).build(), Duration.ofHours(1));
        Utilisateur agent = new Utilisateur();
        agent.setLogin("agent1");
        agent.setPrenom("Hélène");
        agent.setProfil(Profil.AGENT);

        String jeton = jwtService.genererJeton(agent);

        Jwt jwt = NimbusJwtDecoder.withSecretKey(cle).macAlgorithm(MacAlgorithm.HS256).build().decode(jeton);
        assertEquals("agent1", jwt.getSubject());
        assertEquals("AGENT", jwt.getClaimAsString(JwtService.CLAIM_PROFIL));
        assertEquals("Hélène", jwt.getClaimAsString("prenom"));
    }

    @Test
    void leJetonExpireApresLaDureeConfiguree() {
        JwtService jwtService = new JwtService(NimbusJwtEncoder.withSecretKey(cle).build(), Duration.ofMinutes(30));
        Utilisateur agent = new Utilisateur();
        agent.setLogin("agent1");
        agent.setPrenom("Hélène");
        agent.setProfil(Profil.AGENT);

        Jwt jwt = NimbusJwtDecoder.withSecretKey(cle).macAlgorithm(MacAlgorithm.HS256).build()
                .decode(jwtService.genererJeton(agent));

        assertEquals(Duration.ofMinutes(30), Duration.between(jwt.getIssuedAt(), jwt.getExpiresAt()));
    }
}