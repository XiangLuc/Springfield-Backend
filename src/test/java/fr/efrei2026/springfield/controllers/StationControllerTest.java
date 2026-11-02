package fr.efrei2026.springfield.controllers;

import fr.efrei2026.springfield.exceptions.GestionnaireErreurs;
import fr.efrei2026.springfield.exceptions.StationIntrouvableException;
import fr.efrei2026.springfield.modeles.Equipement;
import fr.efrei2026.springfield.modeles.Station;
import fr.efrei2026.springfield.modeles.TypeEquipement;
import fr.efrei2026.springfield.services.interfaces.IEquipementService;
import fr.efrei2026.springfield.services.interfaces.IStationService;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class StationControllerTest {

    private final IStationService stationService = mock(IStationService.class);
    private final IEquipementService equipementService = mock(IEquipementService.class);
    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new StationController(stationService, equipementService))
            .setControllerAdvice(new GestionnaireErreurs())
            .build();

    @Test
    void listerLesEquipementsDUneStationRenvoieLeurTypeEtLeurEtat() throws Exception {
        Station station = new Station("Springfield Centre");
        Equipement ascenseur = new Equipement("ASC-001", "Ascenseur quai A", new TypeEquipement("Ascenseur"), station);
        when(equipementService.listerEquipementsDeLaStation(1)).thenReturn(List.of(ascenseur));

        mockMvc.perform(get("/api/stations/1/equipements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].reference").value("ASC-001"))
                .andExpect(jsonPath("$[0].type.nom").value("Ascenseur"))
                .andExpect(jsonPath("$[0].station.nom").value("Springfield Centre"))
                .andExpect(jsonPath("$[0].etat").value("EN_SERVICE"));
    }

    @Test
    void listerLesEquipementsDUneStationInconnueRenvoie404() throws Exception {
        when(equipementService.listerEquipementsDeLaStation(999)).thenThrow(new StationIntrouvableException(999));

        mockMvc.perform(get("/api/stations/999/equipements"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("La station n°999 n'existe pas."));
    }

    @Test
    void unIdentifiantDeStationNonNumeriqueRenvoie400() throws Exception {
        mockMvc.perform(get("/api/stations/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statut").value(400));
    }
}
