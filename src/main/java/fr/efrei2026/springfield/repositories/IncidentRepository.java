package fr.efrei2026.springfield.repositories;

import fr.efrei2026.springfield.modeles.Incident;
import fr.efrei2026.springfield.modeles.enums.StatutIncident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface IncidentRepository extends JpaRepository<Incident, Integer> {

    List<Incident> findAllByOrderByDateOuvertureDesc();

    @Query("""
            SELECT i FROM Incident i
            WHERE i.statut <> :statut
            """)
    List<Incident> trouverHorsStatut(@Param("statut") StatutIncident statut);

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

    @Query("SELECT i FROM Incident i "
            + "JOIN FETCH i.equipement e JOIN FETCH e.station "
            + "LEFT JOIN FETCH i.prestataire "
            + "WHERE i.statut = :statut ORDER BY i.dateResolution DESC")
    List<Incident> findHistoriquePourExport(@Param("statut") StatutIncident statut);
}