package fr.efrei2026.springfield.repositories;

import fr.efrei2026.springfield.modeles.Incident;
import fr.efrei2026.springfield.modeles.enums.StatutIncident;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface IncidentRepository extends JpaRepository<Incident, Integer> {

    @Query("""
            select i from Incident i
            left join fetch i.prestataire
            where i.equipement.id = :equipementId
              and i.statut in :statuts
            order by i.dateOuverture desc
            """)
    List<Incident> trouverParEquipementEtStatuts(@Param("equipementId") Integer equipementId,
                                                 @Param("statuts") Collection<StatutIncident> statuts);

    // Tout ce qu'affiche la fiche d'un incident est chargé en une seule requête
    @Query("""
            select i from Incident i
            join fetch i.equipement e
            join fetch e.type
            join fetch e.station
            left join fetch i.prestataire
            where i.id = :id
            """)
    Optional<Incident> trouverAvecDetails(@Param("id") Integer id);

    @Query("""
            select i from Incident i
            join fetch i.equipement e
            join fetch e.type
            join fetch e.station
            left join fetch i.prestataire
            where i.statut in :statuts
            order by i.dateOuverture desc
            """)
    List<Incident> trouverParStatuts(@Param("statuts") Collection<StatutIncident> statuts);

    // RG7 : historique du plus récent au plus ancien, selon la date de résolution
    @EntityGraph(attributePaths = {"equipement", "equipement.type", "equipement.station", "prestataire"})
    List<Incident> findByStatutOrderByDateResolutionDesc(StatutIncident statut);
}
