package fr.efrei2026.springfield.services;

import fr.efrei2026.springfield.exceptions.ConfirmationSurveillanceRequiseException;
import fr.efrei2026.springfield.exceptions.EquipementIntrouvableException;
import fr.efrei2026.springfield.exceptions.IncidentDejaResoluException;
import fr.efrei2026.springfield.exceptions.IncidentIntrouvableException;
import fr.efrei2026.springfield.exceptions.PrestataireIntrouvableException;
import fr.efrei2026.springfield.exceptions.TriInvalideException;
import fr.efrei2026.springfield.modeles.Equipement;
import fr.efrei2026.springfield.modeles.Incident;
import fr.efrei2026.springfield.modeles.Prestataire;
import fr.efrei2026.springfield.modeles.Station;
import fr.efrei2026.springfield.modeles.TypeEquipement;
import fr.efrei2026.springfield.modeles.enums.StatutIncident;
import fr.efrei2026.springfield.repositories.IncidentRepository;
import fr.efrei2026.springfield.repositories.PrestataireRepository;
import fr.efrei2026.springfield.services.impls.IncidentService;
import fr.efrei2026.springfield.services.interfaces.IEquipementService;
import fr.efrei2026.springfield.services.interfaces.IPrestataireService;
import fr.efrei2026.springfield.shared.dto.AffectationDto;
import fr.efrei2026.springfield.shared.dto.DeclarationIncidentDto;
import fr.efrei2026.springfield.shared.dto.IncidentDto;
import fr.efrei2026.springfield.shared.dto.ModificationIncidentDto;
import fr.efrei2026.springfield.shared.dto.PageDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.StringWriter;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IncidentServiceTest {

    private static final String ENTETE = "station;equipement;description;ouverture;resolution;prestataire";
    private static final String FIN_DE_LIGNE = "\r\n";
    private static final LocalDateTime MAINTENANT = LocalDateTime.of(2026, 11, 1, 10, 30);
    private static final int ID_INCIDENT = 7;
    private static final int ID_PRESTATAIRE_SURVEILLE = 12;
    private static final Sort TRI_PAR_DEFAUT = Sort.by(Sort.Direction.DESC, "dateOuverture");
    private static final Pageable PAGINATION = PageRequest.of(0, 20, TRI_PAR_DEFAUT);
    private static final Pageable PAGINATION_AVEC_TRI_PAR_ID = PageRequest.of(0, 20, TRI_PAR_DEFAUT.and(Sort.by("id")));

    @Mock
    private IncidentRepository incidentRepository;

    @Mock
    private PrestataireRepository prestataireRepository;

    @Mock
    private IEquipementService equipementService;

    @Mock
    private IPrestataireService prestataireService;

    private IncidentService incidentService;

    @BeforeEach
    void creerServiceAvecHorlogeFixe() {
        Clock horlogeFixe = Clock.fixed(MAINTENANT.toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
        incidentService = new IncidentService(incidentRepository, prestataireRepository, equipementService, prestataireService, horlogeFixe);
    }

    @Test
    void rechercherIncidents_transmetLesFiltresEtInclutToutLeDernierJour() {
        simulerRecherche(new PageImpl<>(List.of()));

        incidentService.rechercherIncidents(StatutIncident.AFFECTE, 1, 2,
                LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 31), "porte", PAGINATION);

        verify(incidentRepository).rechercher(StatutIncident.AFFECTE, 1, 2,
                LocalDateTime.of(2026, 10, 1, 0, 0), LocalDateTime.of(2026, 11, 1, 0, 0), "porte", PAGINATION_AVEC_TRI_PAR_ID);
    }

    @Test
    void rechercherIncidents_sansFiltreNeRestreintRien() {
        simulerRecherche(new PageImpl<>(List.of()));

        incidentService.rechercherIncidents(null, null, null, null, null, null, PAGINATION);

        verify(incidentRepository).rechercher(null, null, null, null, null, null, PAGINATION_AVEC_TRI_PAR_ID);
    }

    @Test
    void rechercherIncidents_ignoreUnMotCleVide() {
        simulerRecherche(new PageImpl<>(List.of()));

        incidentService.rechercherIncidents(null, null, null, null, null, "   ", PAGINATION);

        verify(incidentRepository).rechercher(null, null, null, null, null, null, PAGINATION_AVEC_TRI_PAR_ID);
    }

    @Test
    void rechercherIncidents_retireLesEspacesAutourDuMotCle() {
        simulerRecherche(new PageImpl<>(List.of()));

        incidentService.rechercherIncidents(null, null, null, null, null, "  porte ", PAGINATION);

        verify(incidentRepository).rechercher(null, null, null, null, null, "porte", PAGINATION_AVEC_TRI_PAR_ID);
    }

    @Test
    void rechercherIncidents_refuseUnTriNonAutorise() {
        Pageable triInterdit = PageRequest.of(0, 20, Sort.by("prestataire.nom"));

        assertThrows(TriInvalideException.class, () ->
                incidentService.rechercherIncidents(null, null, null, null, null, null, triInterdit));

        verify(incidentRepository, never()).rechercher(any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void rechercherIncidents_indiqueLaPaginationDuResultat() {
        Incident incident = new Incident(creerEquipement("ASC-001", "Gare du Nord"), "Porte bloquée", MAINTENANT);
        simulerRecherche(new PageImpl<>(List.of(incident), PageRequest.of(1, 2), 5));

        PageDto<IncidentDto> page = incidentService.rechercherIncidents(null, null, null, null, null, null, PAGINATION);

        assertEquals(1, page.getContenu().size());
        assertEquals(1, page.getPage());
        assertEquals(2, page.getTaille());
        assertEquals(5, page.getTotalElements());
        assertEquals(3, page.getTotalPages());
    }

    @Test
    void rechercherIncidents_exposeEquipementStationStatutEtDuree() {
        Incident incident = new Incident(creerEquipement("ASC-001", "Gare du Nord"), "Porte bloquée", MAINTENANT.minusHours(47));
        simulerRecherche(new PageImpl<>(List.of(incident)));

        IncidentDto dto = incidentService.rechercherIncidents(null, null, null, null, null, null, PAGINATION).getContenu().getFirst();

        assertEquals("Porte bloquée", dto.getDescription());
        assertEquals(StatutIncident.OUVERT, dto.getStatut());
        assertEquals("Non affecté", dto.getLibelleStatut());
        assertEquals(47, dto.getDureeOuvertureEnHeures());
        assertEquals("ASC-001", dto.getReferenceEquipement());
        assertEquals("Gare du Nord", dto.getStation());
        assertNull(dto.getPrestataire());
        assertNull(dto.getDateResolution());
    }

    @Test
    void trouverIncident_arreteLaDureeDUnIncidentResoluASaResolution() {
        Incident incident = creerIncidentResolu(creerEquipement("ASC-001", "Gare du Nord"), "Porte bloquée",
                MAINTENANT.minusHours(100), MAINTENANT.minusHours(90), creerPrestataire());
        when(incidentRepository.findById(ID_INCIDENT)).thenReturn(Optional.of(incident));

        IncidentDto dto = incidentService.trouverIncident(ID_INCIDENT);

        assertEquals(10, dto.getDureeOuvertureEnHeures());
        assertEquals("Otis Maintenance", dto.getPrestataire());
    }

    @Test
    void trouverIncident_inconnuLeveUneExceptionIntrouvable() {
        when(incidentRepository.findById(ID_INCIDENT)).thenReturn(Optional.empty());

        assertThrows(IncidentIntrouvableException.class, () -> incidentService.trouverIncident(ID_INCIDENT));
    }

    @Test
    void resoudre_enregistreLeStatutResoluEtLaDateCourante() {
        Incident incident = new Incident(creerEquipement("ASC-001", "Gare du Nord"), "Porte bloquée", MAINTENANT.minusHours(5));
        when(incidentRepository.findById(ID_INCIDENT)).thenReturn(Optional.of(incident));

        IncidentDto dto = incidentService.resoudre(ID_INCIDENT);

        assertEquals(StatutIncident.RESOLU, dto.getStatut());
        assertEquals(MAINTENANT, dto.getDateResolution());
        verify(incidentRepository).save(incident);
    }

    @Test
    void resoudre_incidentAffecteConserveSonPrestataire() {
        Incident incident = new Incident(creerEquipement("ASC-001", "Gare du Nord"), "Porte bloquée", MAINTENANT.minusHours(5));
        ReflectionTestUtils.setField(incident, "statut", StatutIncident.AFFECTE);
        ReflectionTestUtils.setField(incident, "prestataire", creerPrestataire());
        when(incidentRepository.findById(ID_INCIDENT)).thenReturn(Optional.of(incident));

        IncidentDto dto = incidentService.resoudre(ID_INCIDENT);

        assertEquals(StatutIncident.RESOLU, dto.getStatut());
        assertEquals("Otis Maintenance", dto.getPrestataire());
    }

    @Test
    void resoudre_incidentDejaResoluEstRefuseEtConserveSaDate() {
        LocalDateTime dateResolution = MAINTENANT.minusHours(1);
        Incident incident = creerIncidentResolu(creerEquipement("ASC-001", "Gare du Nord"), "Porte bloquée",
                MAINTENANT.minusHours(5), dateResolution, null);
        when(incidentRepository.findById(ID_INCIDENT)).thenReturn(Optional.of(incident));

        assertThrows(IncidentDejaResoluException.class, () -> incidentService.resoudre(ID_INCIDENT));

        assertEquals(dateResolution, incident.getDateResolution());
        verify(incidentRepository, never()).save(any());
    }

    @Test
    void resoudre_incidentInconnuLeveUneExceptionIntrouvable() {
        when(incidentRepository.findById(ID_INCIDENT)).thenReturn(Optional.empty());

        assertThrows(IncidentIntrouvableException.class, () -> incidentService.resoudre(ID_INCIDENT));
    }

    @Test
    void declarer_ouvreLIncidentMaintenantSansPrestataire() {
        Equipement ascenseur = creerEquipement("ASC-001", "Gare du Nord");
        when(equipementService.trouverEquipement(1)).thenReturn(ascenseur);
        when(incidentRepository.save(any(Incident.class))).thenAnswer(appel -> appel.getArgument(0));

        IncidentDto dto = incidentService.declarer(new DeclarationIncidentDto(1, "  Porte bloquée  "));

        assertEquals("Porte bloquée", dto.getDescription());
        assertEquals(MAINTENANT, dto.getDateOuverture());
        assertEquals(StatutIncident.OUVERT, dto.getStatut());
        assertEquals("ASC-001", dto.getReferenceEquipement());
        assertNull(dto.getPrestataire());
    }

    @Test
    void declarer_surUnEquipementInconnuNEnregistreRien() {
        when(equipementService.trouverEquipement(99)).thenThrow(new EquipementIntrouvableException(99));

        assertThrows(EquipementIntrouvableException.class,
                () -> incidentService.declarer(new DeclarationIncidentDto(99, "Porte bloquée")));

        verify(incidentRepository, never()).save(any());
    }

    @Test
    void modifierDescription_remplaceLaDescriptionDUnIncidentEnCours() {
        Incident incident = new Incident(creerEquipement("ASC-001", "Gare du Nord"), "Porte bloquée", MAINTENANT.minusHours(2));
        when(incidentRepository.findById(ID_INCIDENT)).thenReturn(Optional.of(incident));

        IncidentDto dto = incidentService.modifierDescription(ID_INCIDENT, new ModificationIncidentDto(" Porte bloquée ouverte "));

        assertEquals("Porte bloquée ouverte", dto.getDescription());
        verify(incidentRepository).save(incident);
    }

    @Test
    void modifierDescription_incidentResoluEstRefuse() {
        Incident incident = creerIncidentResolu(creerEquipement("ASC-001", "Gare du Nord"), "Porte bloquée",
                MAINTENANT.minusHours(5), MAINTENANT.minusHours(1), null);
        when(incidentRepository.findById(ID_INCIDENT)).thenReturn(Optional.of(incident));

        assertThrows(IncidentDejaResoluException.class,
                () -> incidentService.modifierDescription(ID_INCIDENT, new ModificationIncidentDto("Autre")));

        assertEquals("Porte bloquée", incident.getDescription());
        verify(incidentRepository, never()).save(any());
    }

    @Test
    void affecter_passeLIncidentAuStatutAffecte() {
        Incident incident = new Incident(creerEquipement("ASC-001", "Gare du Nord"), "Porte bloquée", MAINTENANT.minusHours(2));
        when(incidentRepository.findById(ID_INCIDENT)).thenReturn(Optional.of(incident));
        when(prestataireRepository.findById(3)).thenReturn(Optional.of(new Prestataire("Otis Maintenance", null, null)));

        IncidentDto dto = incidentService.affecter(ID_INCIDENT, new AffectationDto(3));

        assertEquals(StatutIncident.AFFECTE, dto.getStatut());
        assertEquals("Otis Maintenance", dto.getPrestataire());
        verify(incidentRepository).save(incident);
    }

    @Test
    void affecter_unIncidentDejaAffecteLeReaffecte() {
        Incident incident = new Incident(creerEquipement("ASC-001", "Gare du Nord"), "Porte bloquée", MAINTENANT.minusHours(2));
        incident.affecter(new Prestataire("Otis Maintenance", null, null));
        when(incidentRepository.findById(ID_INCIDENT)).thenReturn(Optional.of(incident));
        when(prestataireRepository.findById(4)).thenReturn(Optional.of(new Prestataire("Kone Services", null, null)));

        IncidentDto dto = incidentService.affecter(ID_INCIDENT, new AffectationDto(4));

        assertEquals("Kone Services", dto.getPrestataire());
    }

    @Test
    void affecter_unPrestataireInconnuLaisseLIncidentInchange() {
        Incident incident = new Incident(creerEquipement("ASC-001", "Gare du Nord"), "Porte bloquée", MAINTENANT.minusHours(2));
        when(incidentRepository.findById(ID_INCIDENT)).thenReturn(Optional.of(incident));
        when(prestataireRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(PrestataireIntrouvableException.class,
                () -> incidentService.affecter(ID_INCIDENT, new AffectationDto(99)));

        assertEquals(StatutIncident.OUVERT, incident.getStatut());
        verify(incidentRepository, never()).save(any());
    }

    @Test
    void affecter_unIncidentResoluEstRefuse() {
        Incident incident = creerIncidentResolu(creerEquipement("ASC-001", "Gare du Nord"), "Porte bloquée",
                MAINTENANT.minusHours(5), MAINTENANT.minusHours(1), null);
        when(incidentRepository.findById(ID_INCIDENT)).thenReturn(Optional.of(incident));
        when(prestataireRepository.findById(3)).thenReturn(Optional.of(new Prestataire("Otis Maintenance", null, null)));

        assertThrows(IncidentDejaResoluException.class,
                () -> incidentService.affecter(ID_INCIDENT, new AffectationDto(3)));

        assertNull(incident.getPrestataire());
        verify(incidentRepository, never()).save(any());
    }

    @Test
    void affecter_prestataireSousSurveillanceSansConfirmationEstRefuseEtRienNEstEnregistre() {
        Incident incident = new Incident(creerEquipement("ASC-001", "Gare du Nord"), "Porte bloquée", MAINTENANT.minusHours(2));
        simulerPrestataireSurveille(incident);
        when(prestataireService.estSousSurveillance(ID_PRESTATAIRE_SURVEILLE)).thenReturn(true);

        ConfirmationSurveillanceRequiseException exception = assertThrows(ConfirmationSurveillanceRequiseException.class,
                () -> incidentService.affecter(ID_INCIDENT, new AffectationDto(ID_PRESTATAIRE_SURVEILLE)));

        assertEquals("Le prestataire « Dupont Réseaux » est sous surveillance : confirmez explicitement l'affectation pour continuer.",
                exception.getMessage());
        assertEquals(StatutIncident.OUVERT, incident.getStatut());
        assertNull(incident.getPrestataire());
        verify(incidentRepository, never()).save(any());
    }

    @Test
    void affecter_prestataireSousSurveillanceAvecConfirmationEstAffecte() {
        Incident incident = new Incident(creerEquipement("ASC-001", "Gare du Nord"), "Porte bloquée", MAINTENANT.minusHours(2));
        Prestataire prestataire = simulerPrestataireSurveille(incident);
        when(prestataireService.estSousSurveillance(ID_PRESTATAIRE_SURVEILLE)).thenReturn(true);

        IncidentDto dto = incidentService.affecter(ID_INCIDENT, new AffectationDto(ID_PRESTATAIRE_SURVEILLE, true));

        assertEquals(StatutIncident.AFFECTE, dto.getStatut());
        assertEquals("Dupont Réseaux", dto.getPrestataire());
        assertTrue(dto.isPrestataireSousSurveillance());
        assertSame(prestataire, incident.getPrestataire());
        verify(incidentRepository).save(incident);
    }

    @Test
    void affecter_reaffecterVersUnPrestataireSousSurveillanceSansConfirmationConserveLePrestataireActuel() {
        Prestataire prestataireActuel = new Prestataire("Otis Maintenance", null, null);
        Incident incident = new Incident(creerEquipement("ASC-001", "Gare du Nord"), "Porte bloquée", MAINTENANT.minusHours(2));
        incident.affecter(prestataireActuel);
        simulerPrestataireSurveille(incident);
        when(prestataireService.estSousSurveillance(ID_PRESTATAIRE_SURVEILLE)).thenReturn(true);

        assertThrows(ConfirmationSurveillanceRequiseException.class,
                () -> incidentService.affecter(ID_INCIDENT, new AffectationDto(ID_PRESTATAIRE_SURVEILLE)));

        assertSame(prestataireActuel, incident.getPrestataire());
        verify(incidentRepository, never()).save(any());
    }

    @Test
    void affecter_incidentResoluEstRefuseAvantDeDemanderLaConfirmationDeSurveillance() {
        Incident incident = creerIncidentResolu(creerEquipement("ASC-001", "Gare du Nord"), "Porte bloquée",
                MAINTENANT.minusHours(5), MAINTENANT.minusHours(1), null);
        simulerPrestataireSurveille(incident);

        assertThrows(IncidentDejaResoluException.class,
                () -> incidentService.affecter(ID_INCIDENT, new AffectationDto(ID_PRESTATAIRE_SURVEILLE)));

        verify(prestataireService, never()).estSousSurveillance(anyInt());
        verify(incidentRepository, never()).save(any());
    }

    @Test
    void trouverIncident_incidentAffecteAUnPrestataireSousSurveillanceLeSignale() {
        Incident incident = new Incident(creerEquipement("ASC-001", "Gare du Nord"), "Porte bloquée", MAINTENANT.minusHours(2));
        incident.affecter(creerPrestataireSurveille());
        when(incidentRepository.findById(ID_INCIDENT)).thenReturn(Optional.of(incident));
        when(prestataireService.estSousSurveillance(ID_PRESTATAIRE_SURVEILLE)).thenReturn(true);

        assertTrue(incidentService.trouverIncident(ID_INCIDENT).isPrestataireSousSurveillance());
    }

    @Test
    void trouverIncident_incidentNonAffecteOuResoluNInterrogePasLaSurveillance() {
        Incident nonAffecte = new Incident(creerEquipement("ASC-001", "Gare du Nord"), "Porte bloquée", MAINTENANT.minusHours(2));
        Incident resolu = creerIncidentResolu(creerEquipement("ASC-002", "Gare du Nord"), "Porte bloquée",
                MAINTENANT.minusHours(5), MAINTENANT.minusHours(1), creerPrestataireSurveille());
        when(incidentRepository.findById(1)).thenReturn(Optional.of(nonAffecte));
        when(incidentRepository.findById(2)).thenReturn(Optional.of(resolu));

        assertFalse(incidentService.trouverIncident(1).isPrestataireSousSurveillance());
        assertFalse(incidentService.trouverIncident(2).isPrestataireSousSurveillance());

        verify(prestataireService, never()).estSousSurveillance(anyInt());
    }

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

    private void simulerRecherche(Page<Incident> resultat) {
        when(incidentRepository.rechercher(any(), any(), any(), any(), any(), any(), any())).thenReturn(resultat);
    }

    private Equipement creerEquipement(String reference, String nomStation) {
        return new Equipement(reference, "Libellé " + reference, new TypeEquipement("Ascenseur"), new Station(nomStation));
    }

    private Prestataire creerPrestataireSurveille() {
        Prestataire prestataire = new Prestataire("Dupont Réseaux", null, null);
        ReflectionTestUtils.setField(prestataire, "id", ID_PRESTATAIRE_SURVEILLE);
        return prestataire;
    }

    private Prestataire simulerPrestataireSurveille(Incident incident) {
        Prestataire prestataire = creerPrestataireSurveille();
        when(incidentRepository.findById(ID_INCIDENT)).thenReturn(Optional.of(incident));
        when(prestataireRepository.findById(ID_PRESTATAIRE_SURVEILLE)).thenReturn(Optional.of(prestataire));
        return prestataire;
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