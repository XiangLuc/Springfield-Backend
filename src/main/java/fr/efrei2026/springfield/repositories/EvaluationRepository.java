package fr.efrei2026.springfield.repositories;

import fr.efrei2026.springfield.modeles.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EvaluationRepository extends JpaRepository<Evaluation, Integer> {

    Optional<Evaluation> findByIncidentId(Integer incidentId);
}
