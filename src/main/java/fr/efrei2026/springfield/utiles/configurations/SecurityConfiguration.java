package fr.efrei2026.springfield.utiles.configurations;

import fr.efrei2026.springfield.utiles.configurations.security.CsrfCookieFilter;
import fr.efrei2026.springfield.utiles.configurations.security.JwtFilter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableMethodSecurity
public class SecurityConfiguration {

    private static final String[] ROUTES_PUBLIQUES = {
            "/api/auth/connexion",
            "/api/auth/info",
            "/api/auth/deconnexion",
            "/error",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**"
    };
    private static final String ALGORITHME_CLE = "HmacSHA256";

    // Configuration Argon2id minimale recommandée par l'OWASP
    private static final int ARGON2_TAILLE_SEL_OCTETS = 16;
    private static final int ARGON2_TAILLE_HASH_OCTETS = 32;
    private static final int ARGON2_PARALLELISME = 1;
    private static final int ARGON2_MEMOIRE_KIO = 19 * 1024;
    private static final int ARGON2_ITERATIONS = 2;

    private final SecretKey cleJwt;
    private final HandlerExceptionResolver gestionnaireErreurs;

    public SecurityConfiguration(@Value("${jwt.secret}") String secretJwt, @Qualifier("handlerExceptionResolver") HandlerExceptionResolver gestionnaireErreurs) {
        this.cleJwt = new SecretKeySpec(secretJwt.getBytes(StandardCharsets.UTF_8), ALGORITHME_CLE);
        this.gestionnaireErreurs = gestionnaireErreurs;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(CsrfConfigurer::spa)
                .authorizeHttpRequests(acces -> acces
                        .requestMatchers(ROUTES_PUBLIQUES).permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(erreurs -> erreurs
                        .authenticationEntryPoint((requete, reponse, exception) ->
                                gestionnaireErreurs.resolveException(requete, reponse, null, exception))
                        .accessDeniedHandler((requete, reponse, exception) ->
                                gestionnaireErreurs.resolveException(requete, reponse, null, exception)))
                .addFilterAfter(new CsrfCookieFilter(), CsrfFilter.class)
                .addFilterBefore(new JwtFilter(jwtDecoder()), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder(
                ARGON2_TAILLE_SEL_OCTETS,
                ARGON2_TAILLE_HASH_OCTETS,
                ARGON2_PARALLELISME,
                ARGON2_MEMOIRE_KIO,
                ARGON2_ITERATIONS
        );
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        return NimbusJwtEncoder.withSecretKey(cleJwt).build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(cleJwt).macAlgorithm(MacAlgorithm.HS256).build();
    }
}