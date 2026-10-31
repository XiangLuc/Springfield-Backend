package fr.efrei2026.springfield.repositories;

import fr.efrei2026.springfield.modeles.Station;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StationRepository extends JpaRepository<Station, Integer> {

    List<Station> findAllByOrderByNomAsc();
    boolean existsByNom(String nom);
}