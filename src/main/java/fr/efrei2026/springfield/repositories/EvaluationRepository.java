package fr.efrei2026.springfield.repositories;

import fr.efrei2026.springfield.modeles.Evaluation;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EvaluationRepository extends JpaRepository<Evaluation, Integer> {
    boolean existsByIncidentId(int incidentId);

    @Query("SELECT e FROM Evaluation e "
            + "INNER JOIN FETCH e.incident i "
            + "INNER JOIN FETCH i.prestataire "
            + "WHERE i.id = :incidentId")
    Optional<Evaluation> findByIncidentIdAvecPrestataire(@Param("incidentId") int incidentId);

    @Query("""
            SELECT e.note FROM Evaluation e
            WHERE e.incident.prestataire.id = :prestataireId
            ORDER BY e.dateEvaluation DESC, e.id DESC
            """)
    List<Integer> trouverNotesLesPlusRecentes(@Param("prestataireId") int prestataireId, Limit limite);
}
