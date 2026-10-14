package fr.efrei2026.springfield.repositories;

import fr.efrei2026.springfield.modeles.Equipement;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EquipementRepository extends JpaRepository<Equipement, Integer> {

    // Le type est chargé en même temps pour éviter une requête par ligne affichée (N+1)
    @EntityGraph(attributePaths = "type")
    List<Equipement> findByStationIdOrderByLibelleAsc(Integer stationId);

    @Query("""
            select e from Equipement e
            join fetch e.type
            join fetch e.station
            where e.id = :id
            """)
    Optional<Equipement> trouverAvecTypeEtStation(@Param("id") Integer id);
}