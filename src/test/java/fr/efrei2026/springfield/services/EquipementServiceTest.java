package fr.efrei2026.springfield.services;

import fr.efrei2026.springfield.exceptions.EquipementIntrouvableException;
import fr.efrei2026.springfield.exceptions.StationIntrouvableException;
import fr.efrei2026.springfield.modeles.Equipement;
import fr.efrei2026.springfield.modeles.Incident;
import fr.efrei2026.springfield.modeles.Station;
import fr.efrei2026.springfield.modeles.TypeEquipement;
import fr.efrei2026.springfield.modeles.enums.StatutIncident;
import fr.efrei2026.springfield.repositories.EquipementRepository;
import fr.efrei2026.springfield.repositories.IncidentRepository;
import fr.efrei2026.springfield.services.interfaces.IStationService;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EquipementServiceTest {

    private final EquipementRepository equipementRepository = mock(EquipementRepository.class);
    private final IncidentRepository incidentRepository = mock(IncidentRepository.class);
    private final IStationService stationService = mock(IStationService.class);
    private final EquipementService equipementService =
            new EquipementService(equipementRepository, incidentRepository, stationService);

    private final Station station = new Station("Springfield Centre");
    private final Equipement ascenseur =
            new Equipement("ASC-001", "Ascenseur quai A", new TypeEquipement("Ascenseur"), station);

    @Test
    void listerLesEquipementsDUneStationInconnueLeveStationIntrouvableException() {
        when(stationService.trouverStation(999)).thenThrow(new StationIntrouvableException(999));

        assertThrows(StationIntrouvableException.class, () -> equipementService.listerEquipementsDeLaStation(999));
        verify(equipementRepository, never()).findByStationIdOrderByLibelleAsc(anyInt());
    }

    @Test
    void listerLesEquipementsDUneStationExistanteLesRetourne() {
        when(stationService.trouverStation(1)).thenReturn(station);
        when(equipementRepository.findByStationIdOrderByLibelleAsc(1)).thenReturn(List.of(ascenseur));

        assertEquals(List.of(ascenseur), equipementService.listerEquipementsDeLaStation(1));
    }

    @Test
    void trouverUnEquipementInconnuLeveEquipementIntrouvableException() {
        when(equipementRepository.trouverAvecTypeEtStation(999)).thenReturn(Optional.empty());

        EquipementIntrouvableException exception = assertThrows(EquipementIntrouvableException.class,
                () -> equipementService.trouverEquipement(999));
        assertEquals("L'équipement n°999 n'existe pas.", exception.getMessage());
    }

    @Test
    void listerLesIncidentsEnCoursDUnEquipementInconnuLeveEquipementIntrouvableException() {
        when(equipementRepository.existsById(999)).thenReturn(false);

        assertThrows(EquipementIntrouvableException.class, () -> equipementService.listerIncidentsEnCours(999));
        verify(incidentRepository, never()).trouverParEquipementEtStatuts(any(), any());
    }

    @Test
    void listerLesIncidentsPassesDUnEquipementInconnuLeveEquipementIntrouvableException() {
        when(equipementRepository.existsById(999)).thenReturn(false);

        assertThrows(EquipementIntrouvableException.class, () -> equipementService.listerIncidentsPasses(999));
        verify(incidentRepository, never()).trouverParEquipementEtStatuts(any(), any());
    }

    @Test
    void lesIncidentsEnCoursSontLesIncidentsOuvertsOuAffectes() {
        Incident incident = new Incident(ascenseur, "Porte bloquée", LocalDateTime.now());
        when(equipementRepository.existsById(1)).thenReturn(true);
        when(incidentRepository.trouverParEquipementEtStatuts(1, EnumSet.of(StatutIncident.OUVERT, StatutIncident.AFFECTE)))
                .thenReturn(List.of(incident));

        assertEquals(List.of(incident), equipementService.listerIncidentsEnCours(1));
    }

    @Test
    void lesIncidentsPassesSontLesIncidentsResolus() {
        when(equipementRepository.existsById(1)).thenReturn(true);

        equipementService.listerIncidentsPasses(1);

        verify(incidentRepository).trouverParEquipementEtStatuts(1, EnumSet.of(StatutIncident.RESOLU));
    }
}
