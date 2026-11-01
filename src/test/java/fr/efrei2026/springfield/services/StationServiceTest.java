package fr.efrei2026.springfield.services;

import fr.efrei2026.springfield.exceptions.CsvException;
import fr.efrei2026.springfield.modeles.Station;
import fr.efrei2026.springfield.repositories.StationRepository;
import fr.efrei2026.springfield.services.impls.StationService;
import fr.efrei2026.springfield.shared.dto.LigneCsvEchecDto;
import fr.efrei2026.springfield.shared.dto.ResultatImportCsvDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class StationServiceTest {

    @Mock
    private StationRepository stationRepository;

    @InjectMocks
    private StationService stationService;

    @Test
    void importerStations_enregistreLesLignesValidesEtIgnoreLesInvalides() throws IOException {
        when(stationRepository.existsByNom("Châtelet")).thenReturn(true);

        ResultatImportCsvDto resultat = stationService.importerStations(chargerCsv());

        assertEquals(2, resultat.getNombreSucces());
        assertEquals(2, resultat.getNombreEchecs());
        verify(stationRepository, times(2)).save(any(Station.class));

        LigneCsvEchecDto nomVide = resultat.getLignesEchouees().getFirst();
        assertEquals(4, nomVide.getNumeroLigne());
        assertTrue(nomVide.getRaison().contains("obligatoire"));

        LigneCsvEchecDto doublon = resultat.getLignesEchouees().get(1);
        assertEquals(5, doublon.getNumeroLigne());
        assertTrue(doublon.getRaison().contains("existe déjà"));
    }

    @Test
    void importerStations_refuseUnFichierVide() {
        MockMultipartFile fichierVide = new MockMultipartFile("fichier", "vide.csv", "text/csv", new byte[0]);

        assertThrows(CsvException.class, () -> stationService.importerStations(fichierVide));
        verify(stationRepository, never()).save(any(Station.class));
    }

    private MockMultipartFile chargerCsv() throws IOException {
        try (InputStream contenu = getClass().getResourceAsStream("/" + "stations.csv")) {
            assertNotNull(contenu, "Fichier de test introuvable : src/test/resources/" + "stations.csv");
            return new MockMultipartFile("fichier", "stations.csv", "text/csv", contenu);
        }
    }
}