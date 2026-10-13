package fr.efrei2026.springfield.services.impls;

import fr.efrei2026.springfield.modeles.Utilisateur;
import fr.efrei2026.springfield.modeles.enums.Profil;
import fr.efrei2026.springfield.modeles.repositories.UtilisateurRepository;
import fr.efrei2026.springfield.services.interfaces.IUtilisateurService;
import fr.efrei2026.springfield.shared.dto.ConnexionDto;
import fr.efrei2026.springfield.shared.dto.InscriptionDto;
import fr.efrei2026.springfield.shared.dto.TokenDto;
import fr.efrei2026.springfield.utiles.exceptions.IdentifiantInvalideException;
import fr.efrei2026.springfield.utiles.exceptions.LoginExistantException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UtilisateurService implements IUtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UtilisateurService(UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public TokenDto connecter(ConnexionDto connexion) {
        Utilisateur utilisateur = utilisateurRepository.findByLogin(connexion.getLogin())
                .filter(trouve -> passwordEncoder.matches(connexion.getMotDePasse(), trouve.getMotDePasse()))
                .orElseThrow(IdentifiantInvalideException::new);
        return new TokenDto(jwtService.genererJeton(utilisateur));
    }

    @Override
    public TokenDto inscrire(InscriptionDto inscription) {
        if (utilisateurRepository.existsByLogin(inscription.getLogin())) {
            throw new LoginExistantException();
        }
        Utilisateur utilisateur = utilisateurRepository.save(creerUtilisateurAgent(inscription));
        return new TokenDto(jwtService.genererJeton(utilisateur));
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
