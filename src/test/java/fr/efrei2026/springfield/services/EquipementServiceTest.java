package fr.efrei2026.springfield.services;


import fr.efrei2026.springfield.modeles.Equipement;
import fr.efrei2026.springfield.modeles.Station;
import fr.efrei2026.springfield.modeles.TypeEquipement;
import fr.efrei2026.springfield.repositories.EquipementRepository;
import fr.efrei2026.springfield.repositories.StationRepository;
import fr.efrei2026.springfield.repositories.TypeEquipementRepository;
import fr.efrei2026.springfield.services.impls.EquipementService;
import fr.efrei2026.springfield.shared.dto.LigneCsvEchecDto;
import fr.efrei2026.springfield.shared.dto.ResultatImportCsvDto;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EquipementServiceTest {

    @Mock
    private EquipementRepository equipementRepository;

    @Mock
    private StationRepository stationRepository;

    @Mock
    private TypeEquipementRepository typeEquipementRepository;

    @InjectMocks
    private EquipementService equipementService;

    @BeforeEach
    void simulerDonneesExistantes() {
        when(typeEquipementRepository.findByNom("Ascenseur")).thenReturn(Optional.of(new TypeEquipement("Ascenseur")));
        when(typeEquipementRepository.findByNom("Portique")).thenReturn(Optional.of(new TypeEquipement("Portique")));
        when(stationRepository.findByNom("Gare du Nord")).thenReturn(Optional.of(new Station("Gare du Nord")));
        when(stationRepository.findByNom("Part-Dieu")).thenReturn(Optional.of(new Station("Part-Dieu")));
        when(equipementRepository.existsByReference("ASC-099")).thenReturn(true);
    }

    @Test
    void importerEquipements_enregistreLesLignesValidesEtIgnoreLesInvalides() throws IOException {
        ResultatImportCsvDto resultat = equipementService.importerEquipements(chargerCsv());

        assertEquals(2, resultat.getNombreSucces());
        assertEquals(4, resultat.getNombreEchecs());
        verify(equipementRepository, times(2)).save(any(Equipement.class));

        List<LigneCsvEchecDto> echecs = resultat.getLignesEchouees();
        verifierEchec(echecs.get(0), 4, "Châtelet");
        verifierEchec(echecs.get(1), 5, "Inconnu");
        verifierEchec(echecs.get(2), 6, "reference");
        verifierEchec(echecs.get(3), 7, "ASC-099");
    }

    private void verifierEchec(LigneCsvEchecDto echec, int numeroLigneAttendu, String motAttendu) {
        assertEquals(numeroLigneAttendu, echec.getNumeroLigne());
        assertTrue(echec.getRaison().contains(motAttendu));
    }

    private MockMultipartFile chargerCsv() throws IOException {
        try (InputStream contenu = getClass().getResourceAsStream("/" + "equipements.csv")) {
            assertNotNull(contenu, "Fichier de test introuvable : src/test/resources/" + "equipements.csv");
            return new MockMultipartFile("fichier", "equipements.csv", "text/csv", contenu);
        }
    }
}