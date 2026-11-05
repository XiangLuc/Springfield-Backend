package fr.efrei2026.springfield.repositories;

import fr.efrei2026.springfield.modeles.TypeEquipement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TypeEquipementRepository extends JpaRepository<TypeEquipement, Integer> {
    Optional<TypeEquipement> findByNom(String nom);
}