package fr.efrei2026.springfield.repositories;

import fr.efrei2026.springfield.modeles.Prestataire;
import fr.efrei2026.springfield.shared.dto.PrestataireDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PrestataireRepository extends JpaRepository<Prestataire, Integer> {

    @Query("""
            SELECT p.id, p.nom, p.email, p.telephone, AVG(e.note), COUNT(e)
            FROM Prestataire p
            LEFT JOIN Incident i ON i.prestataire = p
            LEFT JOIN Evaluation e ON e.incident = i
            GROUP BY p.id, p.nom, p.email, p.telephone
            ORDER BY AVG(e.note) DESC NULLS LAST, p.nom ASC
            """)
    List<PrestataireDto> listerAvecNotes();
}