package fr.efrei2026.springfield.services;

import fr.efrei2026.springfield.modeles.Equipement;
import fr.efrei2026.springfield.modeles.Incident;
import fr.efrei2026.springfield.modeles.Prestataire;
import fr.efrei2026.springfield.modeles.Station;
import fr.efrei2026.springfield.modeles.TypeEquipement;
import fr.efrei2026.springfield.modeles.enums.NiveauAttention;
import fr.efrei2026.springfield.modeles.enums.StatutIncident;
import fr.efrei2026.springfield.repositories.IncidentRepository;
import fr.efrei2026.springfield.services.impls.TableauDeBordService;
import fr.efrei2026.springfield.shared.dto.IncidentActifDto;
import fr.efrei2026.springfield.shared.dto.TableauDeBordDto;
import fr.efrei2026.springfield.utiles.PolitiqueUrgence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TableauDeBordServiceTest {

    private static final LocalDateTime MAINTENANT = LocalDateTime.of(2026, 11, 1, 10, 30);
    private static final int STATION_CENTRE = 1;
    private static final int STATION_PORT = 2;

    @Mock
    private IncidentRepository incidentRepository;

    private TableauDeBordService tableauDeBordService;

    @BeforeEach
    void creerServiceAvecHorlogeFixe() {
        Clock horlogeFixe = Clock.fixed(MAINTENANT.toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
        tableauDeBordService = new TableauDeBordService(incidentRepository, new PolitiqueUrgence(horlogeFixe), horlogeFixe);
    }

    @Test
    void construireTableauDeBord_trieLesIncidentsDuPlusUrgentAuMoinsUrgent() {
        simulerIncidents(
                creerIncident(1, 2, STATION_CENTRE, null),
                creerIncident(2, 80, STATION_CENTRE, creerPrestataire()),
                creerIncident(3, 60, STATION_PORT, null),
                creerIncident(4, 72, STATION_PORT, null),
                creerIncident(5, 90, STATION_PORT, null));

        TableauDeBordDto tableauDeBord = tableauDeBordService.construireTableauDeBord(null, null);

        assertEquals(List.of(5, 4, 3, 1, 2), identifiants(tableauDeBord));
    }

    @Test
    void construireTableauDeBord_exposeStationEquipementEtDureeDeLIncident() {
        simulerIncidents(creerIncident(7, 47, STATION_CENTRE, null));

        IncidentActifDto incident = tableauDeBordService.construireTableauDeBord(null, null).getIncidents().getFirst();

        assertEquals(7, incident.getId());
        assertEquals(47, incident.getDureeOuvertureEnHeures());
        assertEquals(NiveauAttention.VERT, incident.getNiveauAttention());
        assertEquals("Vert", incident.getLibelleNiveauAttention());
        assertEquals(STATION_CENTRE, incident.getStationId());
        assertEquals("Station 1", incident.getStation());
        assertEquals("REF-7", incident.getReferenceEquipement());
        assertEquals("Ascenseur", incident.getTypeEquipement());
        assertNull(incident.getPrestataire());
    }

    @Test
    void construireTableauDeBord_indiqueLePrestataireDUnIncidentAffecte() {
        simulerIncidents(creerIncident(7, 80, STATION_CENTRE, creerPrestataire()));

        IncidentActifDto incident = tableauDeBordService.construireTableauDeBord(null, null).getIncidents().getFirst();

        assertEquals(NiveauAttention.AFFECTE, incident.getNiveauAttention());
        assertEquals("Pris en charge", incident.getLibelleNiveauAttention());
        assertEquals("Otis Maintenance", incident.getPrestataire());
    }

    @Test
    void construireTableauDeBord_filtreParStation() {
        simulerIncidentsVaries();

        TableauDeBordDto tableauDeBord = tableauDeBordService.construireTableauDeBord(STATION_PORT, null);

        assertEquals(List.of(3, 2), identifiants(tableauDeBord));
    }

    @Test
    void construireTableauDeBord_filtreParNiveauDAttention() {
        simulerIncidentsVaries();

        TableauDeBordDto tableauDeBord = tableauDeBordService.construireTableauDeBord(null, NiveauAttention.ROUGE);

        assertEquals(List.of(1), identifiants(tableauDeBord));
    }

    @Test
    void construireTableauDeBord_combineLesFiltres() {
        simulerIncidentsVaries();

        TableauDeBordDto tableauDeBord = tableauDeBordService.construireTableauDeBord(STATION_PORT, NiveauAttention.VERT);

        assertEquals(List.of(3), identifiants(tableauDeBord));
    }

    @Test
    void construireTableauDeBord_filtreSansCorrespondanceRetourneUneListeVide() {
        simulerIncidentsVaries();

        TableauDeBordDto tableauDeBord = tableauDeBordService.construireTableauDeBord(999, null);

        assertTrue(tableauDeBord.getIncidents().isEmpty());
    }

    @Test
    void construireTableauDeBord_calculeLesIndicateursSurTousLesIncidentsActifsMalgreLeFiltre() {
        simulerIncidentsVaries();

        TableauDeBordDto tableauDeBord = tableauDeBordService.construireTableauDeBord(null, NiveauAttention.ROUGE);

        assertEquals(3, tableauDeBord.getNombreIncidentsActifs());
        assertEquals(1, tableauDeBord.getNombreParNiveau().get(NiveauAttention.ROUGE));
        assertEquals(0, tableauDeBord.getNombreParNiveau().get(NiveauAttention.ORANGE));
        assertEquals(1, tableauDeBord.getNombreParNiveau().get(NiveauAttention.VERT));
        assertEquals(1, tableauDeBord.getNombreParNiveau().get(NiveauAttention.AFFECTE));
        assertEquals(41, tableauDeBord.getDureeMoyenneOuvertureEnHeures());
        assertEquals(2, tableauDeBord.getNombreStationsTouchees());
    }

    @Test
    void construireTableauDeBord_sansIncidentActifRetourneDesIndicateursAZero() {
        when(incidentRepository.trouverHorsStatut(StatutIncident.RESOLU)).thenReturn(List.of());

        TableauDeBordDto tableauDeBord = tableauDeBordService.construireTableauDeBord(null, null);

        assertTrue(tableauDeBord.getIncidents().isEmpty());
        assertEquals(0, tableauDeBord.getNombreIncidentsActifs());
        assertEquals(0, tableauDeBord.getDureeMoyenneOuvertureEnHeures());
        assertEquals(0, tableauDeBord.getNombreParNiveau().get(NiveauAttention.ROUGE));
    }

    private void simulerIncidentsVaries() {
        simulerIncidents(
                creerIncident(1, 72, STATION_CENTRE, null),
                creerIncident(2, 50, STATION_PORT, creerPrestataire()),
                creerIncident(3, 2, STATION_PORT, null));
        when(incidentRepository.compterStationsTouchees()).thenReturn(2L);
    }

    private void simulerIncidents(Incident... incidents) {
        when(incidentRepository.trouverHorsStatut(StatutIncident.RESOLU)).thenReturn(List.of(incidents));
    }

    private List<Integer> identifiants(TableauDeBordDto tableauDeBord) {
        return tableauDeBord.getIncidents().stream().map(IncidentActifDto::getId).toList();
    }

    private Incident creerIncident(int id, int ouvertDepuisEnHeures, int stationId, Prestataire prestataire) {
        Station station = new Station("Station " + stationId);
        ReflectionTestUtils.setField(station, "id", stationId);
        Equipement equipement = new Equipement("REF-" + id, "Équipement " + id, new TypeEquipement("Ascenseur"), station);
        Incident incident = new Incident(equipement, "Panne " + id, MAINTENANT.minusHours(ouvertDepuisEnHeures));
        ReflectionTestUtils.setField(incident, "id", id);
        ReflectionTestUtils.setField(incident, "prestataire", prestataire);
        return incident;
    }

    private Prestataire creerPrestataire() {
        return new Prestataire("Otis Maintenance", null, null);
    }
}
