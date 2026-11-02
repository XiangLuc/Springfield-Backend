package fr.efrei2026.springfield.services;

import fr.efrei2026.springfield.exceptions.EquipementIntrouvableException;
import fr.efrei2026.springfield.exceptions.IncidentIntrouvableException;
import fr.efrei2026.springfield.exceptions.PrestataireIntrouvableException;
import fr.efrei2026.springfield.modeles.Equipement;
import fr.efrei2026.springfield.modeles.Incident;
import fr.efrei2026.springfield.modeles.Prestataire;
import fr.efrei2026.springfield.modeles.Station;
import fr.efrei2026.springfield.modeles.TypeEquipement;
import fr.efrei2026.springfield.modeles.enums.StatutIncident;
import fr.efrei2026.springfield.repositories.EvaluationRepository;
import fr.efrei2026.springfield.repositories.IncidentRepository;
import fr.efrei2026.springfield.services.interfaces.IEquipementService;
import fr.efrei2026.springfield.services.interfaces.IPrestataireService;
import fr.efrei2026.springfield.shared.dto.AffectationDto;
import fr.efrei2026.springfield.shared.dto.DeclarationIncidentDto;
import fr.efrei2026.springfield.shared.dto.ModificationIncidentDto;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class IncidentServiceTest {

    private static final ZoneId FUSEAU = ZoneId.of("Europe/Paris");
    private static final LocalDateTime MAINTENANT = LocalDateTime.of(2026, 11, 2, 18, 0);

    private final IncidentRepository incidentRepository = mock(IncidentRepository.class);
    private final EvaluationRepository evaluationRepository = mock(EvaluationRepository.class);
    private final IEquipementService equipementService = mock(IEquipementService.class);
    private final IPrestataireService prestataireService = mock(IPrestataireService.class);
    private final Clock horloge = Clock.fixed(MAINTENANT.atZone(FUSEAU).toInstant(), FUSEAU);
    private final IncidentService incidentService = new IncidentService(
            incidentRepository, evaluationRepository, equipementService, prestataireService, horloge);

    private final Equipement ascenseur = new Equipement("ASC-001", "Ascenseur quai A",
            new TypeEquipement("Ascenseur"), new Station("Springfield Centre"));

    @Test
    void declarerUnIncidentLOuvreMaintenantSansPrestataire() {
        when(equipementService.trouverEquipement(1)).thenReturn(ascenseur);
        when(incidentRepository.save(any(Incident.class))).thenAnswer(appel -> appel.getArgument(0));

        Incident incident = incidentService.declarer(new DeclarationIncidentDto(1, "  Porte bloquée  "));

        assertEquals(ascenseur, incident.getEquipement());
        assertEquals("Porte bloquée", incident.getDescription());
        assertEquals(MAINTENANT, incident.getDateOuverture());
        assertEquals(StatutIncident.OUVERT, incident.getStatut());
        assertNull(incident.getPrestataire());
    }

    @Test
    void declarerUnIncidentSurUnEquipementInconnuNEnregistreRien() {
        when(equipementService.trouverEquipement(999)).thenThrow(new EquipementIntrouvableException(999));

        assertThrows(EquipementIntrouvableException.class,
                () -> incidentService.declarer(new DeclarationIncidentDto(999, "Porte bloquée")));
        verify(incidentRepository, never()).save(any());
    }

    @Test
    void trouverUnIncidentInconnuLeveIncidentIntrouvableException() {
        when(incidentRepository.trouverAvecDetails(999)).thenReturn(Optional.empty());

        IncidentIntrouvableException exception = assertThrows(IncidentIntrouvableException.class,
                () -> incidentService.trouverIncident(999));
        assertEquals("L'incident n°999 n'existe pas.", exception.getMessage());
    }

    @Test
    void modifierLaDescriptionRemplaceLAncienneDescription() {
        Incident incident = new Incident(ascenseur, "Porte bloquée", MAINTENANT.minusHours(2));
        when(incidentRepository.trouverAvecDetails(1)).thenReturn(Optional.of(incident));

        incidentService.modifierDescription(1, new ModificationIncidentDto(" Porte bloquée en position ouverte "));

        assertEquals("Porte bloquée en position ouverte", incident.getDescription());
    }

    @Test
    void affecterUnPrestataireInconnuLaisseLIncidentInchange() {
        Incident incident = new Incident(ascenseur, "Porte bloquée", MAINTENANT.minusHours(2));
        when(incidentRepository.trouverAvecDetails(1)).thenReturn(Optional.of(incident));
        when(prestataireService.trouverPrestataire(999)).thenThrow(new PrestataireIntrouvableException(999));

        assertThrows(PrestataireIntrouvableException.class,
                () -> incidentService.affecter(1, new AffectationDto(999)));
        assertEquals(StatutIncident.OUVERT, incident.getStatut());
    }

    @Test
    void affecterUnPrestataireExistantPasseLIncidentAuStatutAffecte() {
        Incident incident = new Incident(ascenseur, "Porte bloquée", MAINTENANT.minusHours(2));
        Prestataire elevation = new Prestataire("Élévation Services", null, null);
        when(incidentRepository.trouverAvecDetails(1)).thenReturn(Optional.of(incident));
        when(prestataireService.trouverPrestataire(3)).thenReturn(elevation);

        incidentService.affecter(1, new AffectationDto(3));

        assertEquals(StatutIncident.AFFECTE, incident.getStatut());
        assertEquals(elevation, incident.getPrestataire());
    }

    @Test
    void resoudreUnIncidentEnregistreLHeureCourante() {
        Incident incident = new Incident(ascenseur, "Porte bloquée", MAINTENANT.minusHours(2));
        when(incidentRepository.trouverAvecDetails(1)).thenReturn(Optional.of(incident));

        incidentService.resoudre(1);

        assertEquals(StatutIncident.RESOLU, incident.getStatut());
        assertEquals(MAINTENANT, incident.getDateResolution());
    }

    @Test
    void laDureeDOuvertureSeCalculeAPartirDeLHorloge() {
        Incident incident = new Incident(ascenseur, "Porte bloquée", MAINTENANT.minusHours(47));

        assertEquals(Duration.ofHours(47), incidentService.calculerDureeOuverture(incident));
    }

    @Test
    void lHistoriqueNeContientQueLesIncidentsResolusDuPlusRecentAuPlusAncien() {
        incidentService.listerIncidentsResolus();

        verify(incidentRepository).findByStatutOrderByDateResolutionDesc(StatutIncident.RESOLU);
    }

    @Test
    void lesIncidentsEnCoursSontLesIncidentsNonResolus() {
        incidentService.listerIncidentsEnCours();

        verify(incidentRepository).trouverParStatuts(StatutIncident.EN_COURS);
    }
}
