package fr.efrei2026.springfield.repositories;

import fr.efrei2026.springfield.modeles.Prestataire;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrestataireRepository extends JpaRepository<Prestataire, Integer> {
}