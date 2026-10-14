package fr.efrei2026.springfield.services;

import fr.efrei2026.springfield.modeles.Utilisateur;
import fr.efrei2026.springfield.modeles.enums.Profil;
import fr.efrei2026.springfield.modeles.repositories.UtilisateurRepository;
import fr.efrei2026.springfield.services.impls.JwtService;
import fr.efrei2026.springfield.services.impls.UtilisateurService;
import fr.efrei2026.springfield.shared.dto.ConnexionDto;
import fr.efrei2026.springfield.shared.dto.InscriptionDto;
import fr.efrei2026.springfield.utiles.exceptions.IdentifiantInvalideException;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UtilisateurServiceTest {

    private final UtilisateurRepository utilisateurRepository = mock(UtilisateurRepository.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final JwtService jwtService = mock(JwtService.class);
    private final UtilisateurService utilisateurService = new UtilisateurService(utilisateurRepository, passwordEncoder, jwtService);

    @Test
    void connecterAvecUnMauvaisMotDePasseLeveIdentifiantInvalideException() {
        Utilisateur agent = new Utilisateur();
        agent.setMotDePasse("hash");
        when(utilisateurRepository.findByLogin("agent1")).thenReturn(Optional.of(agent));
        when(passwordEncoder.matches("mauvais", "hash")).thenReturn(false);

        ConnexionDto connexion = new ConnexionDto();
        connexion.setLogin("agent1");
        connexion.setMotDePasse("mauvais");

        assertThrows(IdentifiantInvalideException.class, () -> utilisateurService.connecter(connexion));
    }

    @Test
    void inscrireCreeUnAgentAvecUnMotDePasseHache() {
        when(passwordEncoder.encode("une phrase de passe assez longue")).thenReturn("hash");
        when(utilisateurRepository.save(any(Utilisateur.class))).thenAnswer(appel -> appel.getArgument(0));

        InscriptionDto inscription = new InscriptionDto();
        inscription.setLogin("nouvel.agent");
        inscription.setMotDePasse("une phrase de passe assez longue");
        inscription.setPrenom("Hélène");
        utilisateurService.inscrire(inscription);

        org.mockito.ArgumentCaptor<Utilisateur> enregistre = org.mockito.ArgumentCaptor.forClass(Utilisateur.class);
        org.mockito.Mockito.verify(utilisateurRepository).save(enregistre.capture());
        assertEquals(Profil.AGENT, enregistre.getValue().getProfil());
        assertEquals("hash", enregistre.getValue().getMotDePasse());
    }
}
