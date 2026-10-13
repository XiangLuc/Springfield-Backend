package fr.efrei2026.springfield.modeles.repositories;

import fr.efrei2026.springfield.modeles.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {
}
