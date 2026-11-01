package fr.efrei2026.springfield.repositories;

import fr.efrei2026.springfield.modeles.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {

    Optional<Utilisateur> findByLogin(String login);
    boolean existsByLogin(String login);
}
