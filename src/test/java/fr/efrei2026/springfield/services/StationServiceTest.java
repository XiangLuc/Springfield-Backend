package fr.efrei2026.springfield.services;

import fr.efrei2026.springfield.exceptions.StationIntrouvableException;
import fr.efrei2026.springfield.modeles.Station;
import fr.efrei2026.springfield.repositories.StationRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StationServiceTest {

    private final StationRepository stationRepository = mock(StationRepository.class);
    private final StationService stationService = new StationService(stationRepository);

    @Test
    void trouverUneStationExistanteLaRetourne() {
        Station station = new Station("Springfield Centre");
        when(stationRepository.findById(1)).thenReturn(Optional.of(station));

        assertEquals(station, stationService.trouverStation(1));
    }

    @Test
    void trouverUneStationInconnueLeveStationIntrouvableException() {
        when(stationRepository.findById(999)).thenReturn(Optional.empty());

        StationIntrouvableException exception = assertThrows(StationIntrouvableException.class,
                () -> stationService.trouverStation(999));
        assertEquals("La station n°999 n'existe pas.", exception.getMessage());
    }
}
