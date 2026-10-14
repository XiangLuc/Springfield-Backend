package fr.efrei2026.springfield.services.impls;

import fr.efrei2026.springfield.modeles.Utilisateur;
import fr.efrei2026.springfield.services.interfaces.IJwtService;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class JwtService implements IJwtService {

    public static final String CLAIM_PROFIL = "profil";
    private static final String CLAIM_PRENOM = "prenom";
    private static final Duration DUREE_VALIDITE = Duration.ofHours(1);
    private final JwtEncoder jwtEncoder;

    public JwtService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    @Override
    public String genererJeton(Utilisateur utilisateur) {
        JwtClaimsSet contenu = JwtClaimsSet.builder()
                .subject(utilisateur.getLogin())
                .claim(CLAIM_PROFIL, utilisateur.getProfil().name())
                .claim(CLAIM_PRENOM, utilisateur.getPrenom())
                .expiresAt(Instant.now().plus(DUREE_VALIDITE))
                .build();
        JwsHeader entete = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(entete, contenu)).getTokenValue();
    }
}
