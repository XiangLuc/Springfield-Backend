package fr.efrei2026.springfield.services.impls;

import fr.efrei2026.springfield.exceptions.IdentifiantInvalideException;
import fr.efrei2026.springfield.exceptions.LoginExistantException;
import fr.efrei2026.springfield.exceptions.UtilisateurIntrouvableException;
import fr.efrei2026.springfield.modeles.Utilisateur;
import fr.efrei2026.springfield.modeles.enums.Profil;
import fr.efrei2026.springfield.repositories.UtilisateurRepository;
import fr.efrei2026.springfield.services.interfaces.IUtilisateurService;
import fr.efrei2026.springfield.shared.dto.ConnexionDto;
import fr.efrei2026.springfield.shared.dto.InscriptionDto;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UtilisateurService implements IUtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public UtilisateurService(UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Utilisateur connecter(ConnexionDto connexion) {
        return utilisateurRepository.findByLogin(connexion.getLogin())
                .filter(trouve -> passwordEncoder.matches(connexion.getMotDePasse(), trouve.getMotDePasse()))
                .orElseThrow(IdentifiantInvalideException::new);
    }

    @Transactional
    @Override
    public Utilisateur inscrire(InscriptionDto inscription) {
        if (utilisateurRepository.existsByLogin(inscription.getLogin())) {
            throw new LoginExistantException(inscription.getLogin());
        }
        return utilisateurRepository.save(creerUtilisateurAgent(inscription));
    }

    @Override
    public Utilisateur trouverParLogin(String login) {
        return utilisateurRepository.findByLogin(login)
                .orElseThrow(() -> new UtilisateurIntrouvableException(login));
    }

    private Utilisateur creerUtilisateurAgent(InscriptionDto inscription) {
        Utilisateur agent = new Utilisateur();
        agent.setLogin(inscription.getLogin());
        agent.setMotDePasse(passwordEncoder.encode(inscription.getMotDePasse()));
        agent.setPrenom(inscription.getPrenom());
        agent.setProfil(Profil.AGENT);
        return agent;
    }
}
