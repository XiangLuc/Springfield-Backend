package fr.efrei2026.springfield.services;

import fr.efrei2026.springfield.modeles.Equipement;
import fr.efrei2026.springfield.modeles.Incident;
import fr.efrei2026.springfield.modeles.Prestataire;
import fr.efrei2026.springfield.modeles.Station;
import fr.efrei2026.springfield.modeles.TypeEquipement;
import fr.efrei2026.springfield.modeles.enums.StatutIncident;
import fr.efrei2026.springfield.repositories.IncidentRepository;
import fr.efrei2026.springfield.services.impls.IncidentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IncidentServiceTest {

    private static final String ENTETE = "station;equipement;description;ouverture;resolution;prestataire";
    private static final String FIN_DE_LIGNE = "\r\n";

    @Mock
    private IncidentRepository incidentRepository;

    @InjectMocks
    private IncidentService incidentService;

    @Test
    void exporterHistorique_ecritUneLigneParIncidentResolu() {
        Equipement ascenseur = creerEquipement("ASC-001", "Gare du Nord");
        Equipement portique = creerEquipement("PORT-012", "Part-Dieu");

        Incident avecPrestataire = creerIncidentResolu(ascenseur, "Porte bloquée",
                LocalDateTime.of(2026, 10, 28, 8, 15), LocalDateTime.of(2026, 10, 29, 14, 30),
                creerPrestataire());
        Incident sansPrestataire = creerIncidentResolu(portique, "Écran éteint; redémarrage effectué",
                LocalDateTime.of(2026, 10, 20, 9, 0), LocalDateTime.of(2026, 10, 20, 11, 45),
                null);

        when(incidentRepository.findHistoriquePourExport(StatutIncident.RESOLU))
                .thenReturn(List.of(avecPrestataire, sansPrestataire));

        StringWriter sortie = new StringWriter();
        incidentService.exporterHistorique(sortie);

        String[] attendu = {
                ENTETE,
                "Gare du Nord;ASC-001;Porte bloquée;28/10/2026 08:15;29/10/2026 14:30;Otis Maintenance",
                "Part-Dieu;PORT-012;\"Écran éteint; redémarrage effectué\";20/10/2026 09:00;20/10/2026 11:45;"
        };
        assertArrayEquals(attendu, sortie.toString().split(FIN_DE_LIGNE));
    }

    @Test
    void exporterHistorique_sansIncidentResoluEcritSeulementLEntete() {
        when(incidentRepository.findHistoriquePourExport(StatutIncident.RESOLU)).thenReturn(List.of());

        StringWriter sortie = new StringWriter();
        incidentService.exporterHistorique(sortie);

        assertArrayEquals(new String[]{ENTETE}, sortie.toString().split(FIN_DE_LIGNE));
    }

    private Equipement creerEquipement(String reference, String nomStation) {
        return new Equipement(reference, "Libellé " + reference, new TypeEquipement("Ascenseur"), new Station(nomStation));
    }

    private Prestataire creerPrestataire() {
        Prestataire prestataire = mock(Prestataire.class);
        when(prestataire.getNom()).thenReturn("Otis Maintenance");
        return prestataire;
    }

    private Incident creerIncidentResolu(Equipement equipement, String description, LocalDateTime ouverture, LocalDateTime resolution, Prestataire prestataire) {
        Incident incident = new Incident(equipement, description, ouverture);
        ReflectionTestUtils.setField(incident, "statut", StatutIncident.RESOLU);
        ReflectionTestUtils.setField(incident, "dateResolution", resolution);
        ReflectionTestUtils.setField(incident, "prestataire", prestataire);
        return incident;
    }
}