package fr.efrei2026.springfield.controllers;

import fr.efrei2026.springfield.exceptions.EquipementIntrouvableException;
import fr.efrei2026.springfield.exceptions.GestionnaireErreurs;
import fr.efrei2026.springfield.services.interfaces.IEquipementService;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class EquipementControllerTest {

    private final IEquipementService equipementService = mock(IEquipementService.class);
    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new EquipementController(equipementService))
            .setControllerAdvice(new GestionnaireErreurs())
            .build();

    @Test
    void consulterUnEquipementInconnuRenvoie404AvecUnMessageEnFrancais() throws Exception {
        when(equipementService.trouverEquipement(999)).thenThrow(new EquipementIntrouvableException(999));

        mockMvc.perform(get("/api/equipements/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statut").value(404))
                .andExpect(jsonPath("$.message").value("L'équipement n°999 n'existe pas."));
    }

    @Test
    void lesIncidentsDUnEquipementInconnuRenvoient404() throws Exception {
        when(equipementService.listerIncidentsEnCours(999)).thenThrow(new EquipementIntrouvableException(999));

        mockMvc.perform(get("/api/equipements/999/incidents/en-cours"))
                .andExpect(status().isNotFound());
    }

    @Test
    void unIdentifiantDEquipementNonNumeriqueRenvoie400() throws Exception {
        mockMvc.perform(get("/api/equipements/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Le paramètre 'id' a une valeur invalide : abc"));
    }
}
