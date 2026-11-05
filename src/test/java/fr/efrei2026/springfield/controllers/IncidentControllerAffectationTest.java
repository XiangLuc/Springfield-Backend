package fr.efrei2026.springfield.controllers;

import fr.efrei2026.springfield.exceptions.ConfirmationSurveillanceRequiseException;
import fr.efrei2026.springfield.exceptions.GestionnaireErreurs;
import fr.efrei2026.springfield.services.interfaces.IIncidentService;
import fr.efrei2026.springfield.shared.dto.AffectationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class IncidentControllerAffectationTest {

    private static final String URL_AFFECTATION = "/api/incidents/7/prestataire";

    @Mock
    private IIncidentService incidentService;

    private MockMvc mockMvc;

    @BeforeEach
    void creerLeControleurAvecLeGestionnaireDErreurs() {
        mockMvc = MockMvcBuilders.standaloneSetup(new IncidentController(incidentService))
                .setControllerAdvice(new GestionnaireErreurs())
                .build();
    }

    @Test
    void affecter_prestataireSousSurveillanceSansConfirmationRepondConflitAvecUnMessage() throws Exception {
        when(incidentService.affecter(eq(7), any(AffectationDto.class)))
                .thenThrow(new ConfirmationSurveillanceRequiseException("Dupont Réseaux"));

        mockMvc.perform(put(URL_AFFECTATION).contentType(MediaType.APPLICATION_JSON).content("{\"prestataireId\":3}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(containsString("surveillance")));
    }

    @Test
    void affecter_transmetLaConfirmationDeSurveillanceAuService() throws Exception {
        mockMvc.perform(put(URL_AFFECTATION).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"prestataireId\":3,\"confirmationSurveillance\":true}"))
                .andExpect(status().isOk());

        ArgumentCaptor<AffectationDto> affectation = ArgumentCaptor.forClass(AffectationDto.class);
        verify(incidentService).affecter(eq(7), affectation.capture());
        assertTrue(affectation.getValue().isConfirmationSurveillance());
    }
}
