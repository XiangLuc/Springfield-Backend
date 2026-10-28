package fr.efrei2026.springfield.repositories;

import fr.efrei2026.springfield.modeles.Incident;
import fr.efrei2026.springfield.modeles.enums.StatutIncident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

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
}