package fr.efrei2026.springfield.utiles.configurations.security;

import fr.efrei2026.springfield.services.impls.JwtService;
import fr.efrei2026.springfield.services.interfaces.IJwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class JwtFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtFilter.class);

    private final JwtDecoder jwtDecoder;

    public JwtFilter(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Cookie cookie = WebUtils.getCookie(request, IJwtService.NOM_COOKIE);

        if (cookie != null) {
            authentifier(cookie.getValue());
        }
        filterChain.doFilter(request, response);
    }

    private void authentifier(String jeton) {
        try {
            Jwt jwt = jwtDecoder.decode(jeton);
            SimpleGrantedAuthority profil = new SimpleGrantedAuthority(Objects.requireNonNull(jwt.getClaimAsString(JwtService.CLAIM_PROFIL)));
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(Objects.requireNonNull(jwt.getSubject()), null, List.of(profil)));
        } catch (JwtException exception) {
            LOGGER.debug("Token invalide : {}", exception.getMessage());
        }
    }
}