package fr.efrei2026.springfield.modeles.repositories;

import fr.efrei2026.springfield.modeles.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EvaluationRepository extends JpaRepository<Evaluation, Integer> {
    boolean existsByIncidentId(int incidentId);

    @Query("SELECT e FROM Evaluation e "
            + "INNER JOIN FETCH e.incident i "
            + "INNER JOIN FETCH i.prestataire "
            + "WHERE i.id = :incidentId")
    Optional<Evaluation> findByIncidentIdAvecPrestataire(@Param("incidentId") int incidentId);
}
