package fr.efrei2026.springfield.controllers;

import fr.efrei2026.springfield.exceptions.GestionnaireErreurs;
import fr.efrei2026.springfield.exceptions.IncidentDejaResoluException;
import fr.efrei2026.springfield.exceptions.IncidentIntrouvableException;
import fr.efrei2026.springfield.modeles.Equipement;
import fr.efrei2026.springfield.modeles.Incident;
import fr.efrei2026.springfield.modeles.Station;
import fr.efrei2026.springfield.modeles.TypeEquipement;
import fr.efrei2026.springfield.services.interfaces.IIncidentService;
import fr.efrei2026.springfield.shared.dto.DeclarationIncidentDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class IncidentControllerTest {

    private final IIncidentService incidentService = mock(IIncidentService.class);
    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new IncidentController(incidentService))
            .setControllerAdvice(new GestionnaireErreurs())
            .build();

    private final Incident incident = new Incident(
            new Equipement("ASC-001", "Ascenseur quai A", new TypeEquipement("Ascenseur"), new Station("Springfield Centre")),
            "Porte bloquée",
            LocalDateTime.of(2026, 11, 2, 8, 0));

    @Test
    void consulterUnIncidentInexistantRenvoie404() throws Exception {
        when(incidentService.trouverIncident(999)).thenThrow(new IncidentIntrouvableException(999));

        mockMvc.perform(get("/api/incidents/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("L'incident n°999 n'existe pas."));
    }

    @Test
    void consulterUnIncidentRenvoieSonEquipementSaStationEtSaDureeDOuverture() throws Exception {
        when(incidentService.trouverIncident(1)).thenReturn(incident);
        when(incidentService.trouverEvaluation(1)).thenReturn(Optional.empty());
        when(incidentService.calculerDureeOuverture(incident)).thenReturn(Duration.ofHours(47));

        mockMvc.perform(get("/api/incidents/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.incident.description").value("Porte bloquée"))
                .andExpect(jsonPath("$.incident.equipement.station.nom").value("Springfield Centre"))
                .andExpect(jsonPath("$.incident.dureeOuvertureEnMinutes").value(47 * 60))
                .andExpect(jsonPath("$.evaluation").doesNotExist());
    }

    @Test
    void declarerUnIncidentRenvoie201() throws Exception {
        when(incidentService.declarer(any(DeclarationIncidentDto.class))).thenReturn(incident);
        when(incidentService.calculerDureeOuverture(incident)).thenReturn(Duration.ZERO);

        mockMvc.perform(post("/api/incidents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"equipementId\": 1, \"description\": \"Porte bloquée\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statut").value("OUVERT"));
    }

    @Test
    void declarerUnIncidentSansDescriptionRenvoie400AvecUnMessageClair() throws Exception {
        mockMvc.perform(post("/api/incidents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"equipementId\": 1, \"description\": \"   \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("La description est obligatoire."));
        verify(incidentService, never()).declarer(any());
    }

    @Test
    void resoudreUnIncidentDejaResoluRenvoie409() throws Exception {
        when(incidentService.resoudre(1)).thenThrow(new IncidentDejaResoluException(1));

        mockMvc.perform(post("/api/incidents/1/resolution"))
                .andExpect(status().isConflict());
    }
}
