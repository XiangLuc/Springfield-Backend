package fr.efrei2026.springfield;

import fr.efrei2026.springfield.modeles.Incident;
import fr.efrei2026.springfield.modeles.Prestataire;
import fr.efrei2026.springfield.modeles.enums.NiveauAttention;
import fr.efrei2026.springfield.utiles.PolitiqueUrgence;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PolitiqueUrgenceTest {

    private static final LocalDateTime MAINTENANT = LocalDateTime.of(2026, 11, 1, 10, 30);

    private final PolitiqueUrgence politiqueUrgence =
            new PolitiqueUrgence(Clock.fixed(MAINTENANT.toInstant(ZoneOffset.UTC), ZoneOffset.UTC));

    @Test
    void incidentNonAffecteOuvertDepuis47HeuresEstVert() {
        assertEquals(NiveauAttention.VERT, politiqueUrgence.evaluer(incidentOuvertDepuis(Duration.ofHours(47))));
    }

    @Test
    void incidentNonAffecteJusteAvant48HeuresResteVert() {
        Incident incident = incidentOuvertDepuis(Duration.ofHours(48).minusSeconds(1));

        assertEquals(NiveauAttention.VERT, politiqueUrgence.evaluer(incident));
    }

    @Test
    void incidentNonAffecteOuvertDepuis48HeuresPileEstOrange() {
        assertEquals(NiveauAttention.ORANGE, politiqueUrgence.evaluer(incidentOuvertDepuis(Duration.ofHours(48))));
    }

    @Test
    void incidentNonAffecteJusteAvant72HeuresResteOrange() {
        Incident incident = incidentOuvertDepuis(Duration.ofHours(72).minusSeconds(1));

        assertEquals(NiveauAttention.ORANGE, politiqueUrgence.evaluer(incident));
    }

    @Test
    void incidentNonAffecteOuvertDepuis72HeuresPileEstRouge() {
        assertEquals(NiveauAttention.ROUGE, politiqueUrgence.evaluer(incidentOuvertDepuis(Duration.ofHours(72))));
    }

    @Test
    void incidentAffecteRecentPossedeUnEtatDistinct() {
        Incident incident = incidentOuvertDepuis(Duration.ofHours(1));
        ReflectionTestUtils.setField(incident, "prestataire", new Prestataire("Otis Maintenance", null, null));

        assertEquals(NiveauAttention.AFFECTE, politiqueUrgence.evaluer(incident));
    }

    @Test
    void incidentAffecteDepuisPlusDe72HeuresNePortePasLIndicateurRouge() {
        Incident incident = incidentOuvertDepuis(Duration.ofHours(80));
        ReflectionTestUtils.setField(incident, "prestataire", new Prestataire("Otis Maintenance", null, null));

        assertEquals(NiveauAttention.AFFECTE, politiqueUrgence.evaluer(incident));
    }

    private Incident incidentOuvertDepuis(Duration duree) {
        return new Incident(null, "Porte bloquée", MAINTENANT.minus(duree));
    }
}
