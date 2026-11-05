package fr.efrei2026.springfield.services;

import fr.efrei2026.springfield.repositories.EvaluationRepository;
import fr.efrei2026.springfield.repositories.PrestataireRepository;
import fr.efrei2026.springfield.services.impls.PrestataireService;
import fr.efrei2026.springfield.shared.dto.PrestataireDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Limit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PrestataireServiceTest {

    private static final int ID_PRESTATAIRE = 4;
    private static final Limit CINQ_DERNIERES = Limit.of(5);

    @Mock
    private PrestataireRepository prestataireRepository;

    @Mock
    private EvaluationRepository evaluationRepository;

    @InjectMocks
    private PrestataireService prestataireService;

    @ParameterizedTest(name = "notes {0} : sous surveillance = {1}")
    @CsvSource(textBlock = """
            '4 1 1 1',         false
            '3 3 3 3 3',       false
            '3 3 3 3 3 2',     true
            '3 3 3 3 3 2 5',   false
            '1 5 5 5 5 5',     false
            '2 3 3 3 3',       true
            '1 1 5 5 5',       false
            """)
    void estSousSurveillance_respecteLesExemplesDuSujet(String notesDuPlusAncienAuPlusRecent, boolean attendu) {
        simulerEvaluations(notesDuPlusAncienAuPlusRecent);

        assertEquals(attendu, prestataireService.estSousSurveillance(ID_PRESTATAIRE));
    }

    @Test
    void estSousSurveillance_sansEvaluationNEstPasSurveille() {
        simulerEvaluations("");

        assertFalse(prestataireService.estSousSurveillance(ID_PRESTATAIRE));
    }

    @Test
    void listerPrestataires_indiqueLesPrestatairesSousSurveillance() {
        PrestataireDto surveille = new PrestataireDto(1, "Dupont Réseaux", null, null, null, 0L);
        PrestataireDto serein = new PrestataireDto(2, "Otis Maintenance", null, null, null, 0L);
        when(prestataireRepository.listerAvecNotes()).thenReturn(new ArrayList<>(List.of(surveille, serein)));
        when(evaluationRepository.trouverNotesLesPlusRecentes(1, CINQ_DERNIERES)).thenReturn(List.of(2, 3, 3, 3, 3));
        when(evaluationRepository.trouverNotesLesPlusRecentes(2, CINQ_DERNIERES)).thenReturn(List.of(5, 4, 5, 4, 5));

        prestataireService.listerPrestataires();

        assertTrue(surveille.isSousSurveillance());
        assertFalse(serein.isSousSurveillance());
    }

    private void simulerEvaluations(String notesDuPlusAncienAuPlusRecent) {
        List<Integer> notes = Arrays.stream(notesDuPlusAncienAuPlusRecent.split(" "))
                .filter(note -> !note.isEmpty())
                .map(Integer::valueOf)
                .toList();
        when(evaluationRepository.trouverNotesLesPlusRecentes(ID_PRESTATAIRE, CINQ_DERNIERES))
                .thenReturn(notes.reversed().stream().limit(5).toList());
    }
}
