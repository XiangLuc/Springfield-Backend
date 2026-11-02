package fr.efrei2026.springfield.repositories;

import fr.efrei2026.springfield.modeles.Incident;
import fr.efrei2026.springfield.modeles.enums.StatutIncident;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface IncidentRepository extends JpaRepository<Incident, Integer> {

    @Query("""
            SELECT i FROM Incident i
            INNER JOIN FETCH i.equipement e
            INNER JOIN FETCH e.station
            INNER JOIN FETCH e.type
            LEFT JOIN FETCH i.prestataire
            WHERE (:statut IS NULL OR i.statut = :statut)
              AND (:stationId IS NULL OR e.station.id = :stationId)
              AND (:prestataireId IS NULL OR i.prestataire.id = :prestataireId)
              AND (:ouvertDepuis IS NULL OR i.dateOuverture >= :ouvertDepuis)
              AND (:ouvertAvant IS NULL OR i.dateOuverture < :ouvertAvant)
              AND (:motCle IS NULL
                   OR i.description LIKE CONCAT('%', :motCle, '%')
                   OR e.reference LIKE CONCAT('%', :motCle, '%'))
            """)
    Page<Incident> rechercher(@Param("statut") StatutIncident statut,
                              @Param("stationId") Integer stationId,
                              @Param("prestataireId") Integer prestataireId,
                              @Param("ouvertDepuis") LocalDateTime ouvertDepuis,
                              @Param("ouvertAvant") LocalDateTime ouvertAvant,
                              @Param("motCle") String motCle,
                              Pageable pagination);

    @Query("""
            SELECT i FROM Incident i
            WHERE i.statut <> :statut
            """)
    List<Incident> trouverHorsStatut(@Param("statut") StatutIncident statut);

    @Query("""
            SELECT i FROM Incident i
            INNER JOIN FETCH i.equipement e
            INNER JOIN FETCH e.station
            INNER JOIN FETCH e.type
            LEFT JOIN FETCH i.prestataire
            WHERE i.statut <> :statut
            ORDER BY i.dateOuverture DESC
            """)
    List<Incident> trouverHorsStatutAvecDetails(@Param("statut") StatutIncident statut);

    @Query("""
            SELECT i FROM Incident i
            INNER JOIN FETCH i.equipement e
            INNER JOIN FETCH e.station
            INNER JOIN FETCH e.type
            LEFT JOIN FETCH i.prestataire
            WHERE i.id = :id
            """)
    Optional<Incident> trouverAvecDetails(@Param("id") Integer id);

    @Query(value = """
            SELECT COUNT(DISTINCT e.station_id)
            FROM incident i
            INNER JOIN equipement e ON e.id = i.equipement_id
            WHERE i.statut <> 'RESOLU'
            """, nativeQuery = true)
    long compterStationsTouchees();

    @Query("""
            select i from Incident i
            left join fetch i.prestataire
            where i.id = :id
            """)
    Optional<Incident> trouverAvecPrestataire(@Param("id") Integer id);

    @Query("""
            select i from Incident i
            left join fetch i.prestataire
            where i.equipement.id = :equipementId
              and i.statut in :statuts
            order by i.dateOuverture desc
            """)
    List<Incident> trouverParEquipementEtStatuts(@Param("equipementId") Integer equipementId,
                                                 @Param("statuts") Collection<StatutIncident> statuts);

    @Query("""
            SELECT i FROM Incident i
            INNER JOIN FETCH i.equipement e
            INNER JOIN FETCH e.station
            INNER JOIN FETCH e.type
            LEFT JOIN FETCH i.prestataire
            WHERE i.statut = :statut
            ORDER BY i.dateResolution DESC
            """)
    List<Incident> trouverHistorique(@Param("statut") StatutIncident statut);
}