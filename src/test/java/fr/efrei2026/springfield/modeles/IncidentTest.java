package fr.efrei2026.springfield.modeles;

import fr.efrei2026.springfield.exceptions.DateResolutionInvalideException;
import fr.efrei2026.springfield.exceptions.IncidentDejaResoluException;
import fr.efrei2026.springfield.modeles.enums.StatutIncident;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class IncidentTest {

    private static final LocalDateTime OUVERTURE = LocalDateTime.of(2026, 11, 2, 8, 0);

    private final Equipement ascenseur = new Equipement("ASC-001", "Ascenseur quai A",
            new TypeEquipement("Ascenseur"), new Station("Springfield Centre"));
    private final Prestataire elevation = new Prestataire("Élévation Services", null, null);
    private final Prestataire mecatrans = new Prestataire("Mécatrans Maintenance", null, null);

    private Incident nouvelIncident() {
        return new Incident(ascenseur, "Porte bloquée", OUVERTURE);
    }

    @Test
    void unIncidentDeclareEstNonAffecteEtSansPrestataire() {
        Incident incident = nouvelIncident();

        assertEquals(StatutIncident.OUVERT, incident.getStatut());
        assertNull(incident.getPrestataire());
        assertNull(incident.getDateResolution());
    }

    @Test
    void affecterUnPrestatairePasseLIncidentAuStatutAffecte() {
        Incident incident = nouvelIncident();

        incident.affecter(elevation);

        assertEquals(StatutIncident.AFFECTE, incident.getStatut());
        assertEquals(elevation, incident.getPrestataire());
    }

    @Test
    void reaffecterUnIncidentRemplaceLePrestataire() {
        Incident incident = nouvelIncident();
        incident.affecter(elevation);

        incident.affecter(mecatrans);

        assertEquals(mecatrans, incident.getPrestataire());
    }

    @Test
    void resoudreUnIncidentEnregistreLaDateDeResolution() {
        Incident incident = nouvelIncident();
        LocalDateTime resolution = OUVERTURE.plusHours(5);

        incident.resoudre(resolution);

        assertEquals(StatutIncident.RESOLU, incident.getStatut());
        assertEquals(resolution, incident.getDateResolution());
    }

    @Test
    void unIncidentPeutEtreResoluALInstantMemeDeSonOuverture() {
        Incident incident = nouvelIncident();

        incident.resoudre(OUVERTURE);

        assertEquals(OUVERTURE, incident.getDateResolution());
    }

    @Test
    void resoudreAvantLOuvertureEstInterdit() {
        Incident incident = nouvelIncident();

        assertThrows(DateResolutionInvalideException.class, () -> incident.resoudre(OUVERTURE.minusMinutes(1)));
        assertEquals(StatutIncident.OUVERT, incident.getStatut());
    }

    @Test
    void unIncidentResoluNePeutPlusEtreAffecte() {
        Incident incident = nouvelIncident();
        incident.resoudre(OUVERTURE.plusHours(1));

        assertThrows(IncidentDejaResoluException.class, () -> incident.affecter(elevation));
        assertNull(incident.getPrestataire());
    }

    @Test
    void unIncidentResoluNePeutPasEtreResoluUneSecondeFois() {
        Incident incident = nouvelIncident();
        LocalDateTime premiereResolution = OUVERTURE.plusHours(1);
        incident.resoudre(premiereResolution);

        assertThrows(IncidentDejaResoluException.class, () -> incident.resoudre(OUVERTURE.plusHours(2)));
        assertEquals(premiereResolution, incident.getDateResolution());
    }

    @Test
    void laDescriptionDUnIncidentResoluNEstPlusModifiable() {
        Incident incident = nouvelIncident();
        incident.resoudre(OUVERTURE.plusHours(1));

        assertThrows(IncidentDejaResoluException.class, () -> incident.modifierDescription("Autre description"));
        assertEquals("Porte bloquée", incident.getDescription());
    }

    @Test
    void laDureeDOuvertureDUnIncidentEnCoursSeCalculeJusquAMaintenant() {
        Incident incident = nouvelIncident();

        assertEquals(Duration.ofHours(47), incident.dureeOuverture(OUVERTURE.plusHours(47)));
    }

    @Test
    void laDureeDOuvertureDUnIncidentResoluEstFigeeALaResolution() {
        Incident incident = nouvelIncident();
        incident.resoudre(OUVERTURE.plusHours(3));

        assertEquals(Duration.ofHours(3), incident.dureeOuverture(OUVERTURE.plusDays(10)));
    }
}
