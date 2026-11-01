package fr.efrei2026.springfield.services;

import fr.efrei2026.springfield.exceptions.IdentifiantInvalideException;
import fr.efrei2026.springfield.exceptions.UtilisateurIntrouvableException;
import fr.efrei2026.springfield.modeles.Utilisateur;
import fr.efrei2026.springfield.modeles.enums.Profil;
import fr.efrei2026.springfield.repositories.UtilisateurRepository;
import fr.efrei2026.springfield.services.impls.UtilisateurService;
import fr.efrei2026.springfield.shared.dto.ConnexionDto;
import fr.efrei2026.springfield.shared.dto.InscriptionDto;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UtilisateurServiceTest {

    private final UtilisateurRepository utilisateurRepository = mock(UtilisateurRepository.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final UtilisateurService utilisateurService = new UtilisateurService(utilisateurRepository, passwordEncoder);

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
    void trouverUnLoginInconnuLeveUtilisateurIntrouvableException() {
        when(utilisateurRepository.findByLogin("inconnu")).thenReturn(Optional.empty());

        assertThrows(UtilisateurIntrouvableException.class, () -> utilisateurService.trouverParLogin("inconnu"));
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

        ArgumentCaptor<Utilisateur> enregistre = ArgumentCaptor.forClass(Utilisateur.class);
        verify(utilisateurRepository).save(enregistre.capture());
        assertEquals(Profil.AGENT, enregistre.getValue().getProfil());
        assertEquals("hash", enregistre.getValue().getMotDePasse());
    }
}
