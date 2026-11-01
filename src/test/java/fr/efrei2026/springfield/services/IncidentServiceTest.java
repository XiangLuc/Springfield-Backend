package fr.efrei2026.springfield.services;

import fr.efrei2026.springfield.exceptions.IncidentDejaResoluException;
import fr.efrei2026.springfield.exceptions.IncidentIntrouvableException;
import fr.efrei2026.springfield.exceptions.TriInvalideException;
import fr.efrei2026.springfield.modeles.Equipement;
import fr.efrei2026.springfield.modeles.Incident;
import fr.efrei2026.springfield.modeles.Prestataire;
import fr.efrei2026.springfield.modeles.Station;
import fr.efrei2026.springfield.modeles.TypeEquipement;
import fr.efrei2026.springfield.modeles.enums.StatutIncident;
import fr.efrei2026.springfield.repositories.IncidentRepository;
import fr.efrei2026.springfield.services.impls.IncidentService;
import fr.efrei2026.springfield.shared.dto.IncidentDto;
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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
    private static final Sort TRI_PAR_DEFAUT = Sort.by(Sort.Direction.DESC, "dateOuverture");
    private static final Pageable PAGINATION = PageRequest.of(0, 20, TRI_PAR_DEFAUT);
    private static final Pageable PAGINATION_AVEC_TRI_PAR_ID = PageRequest.of(0, 20, TRI_PAR_DEFAUT.and(Sort.by("id")));

    @Mock
    private IncidentRepository incidentRepository;

    private IncidentService incidentService;

    @BeforeEach
    void creerServiceAvecHorlogeFixe() {
        Clock horlogeFixe = Clock.fixed(MAINTENANT.toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
        incidentService = new IncidentService(incidentRepository, horlogeFixe);
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