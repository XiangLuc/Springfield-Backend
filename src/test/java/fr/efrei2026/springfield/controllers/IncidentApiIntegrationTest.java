package fr.efrei2026.springfield.controllers;

import com.jayway.jsonpath.JsonPath;
import fr.efrei2026.springfield.repositories.EquipementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Parcours complet sur la vraie base ; la transaction du test est annulée à la fin, rien n'est conservé
@SpringBootTest
@Transactional
public class IncidentApiIntegrationTest {

    private static final RequestPostProcessor AGENT =
            user("agent").authorities(new SimpleGrantedAuthority("AGENT"));
    private static final RequestPostProcessor RESPONSABLE =
            user("responsable").authorities(new SimpleGrantedAuthority("RESPONSABLE_EXPLOITATION"));

    @Autowired
    private WebApplicationContext contexte;

    @Autowired
    private EquipementRepository equipementRepository;

    private MockMvc mockMvc;
    private int equipementId;

    @BeforeEach
    void preparer() {
        mockMvc = MockMvcBuilders.webAppContextSetup(contexte).apply(springSecurity()).build();
        equipementId = equipementRepository.findAll().getFirst().getId();
    }

    private int declarer(RequestPostProcessor profil) throws Exception {
        String reponse = mockMvc.perform(post("/api/incidents").with(profil).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"equipementId\": " + equipementId + ", \"description\": \"Porte bloquée\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statut").value("OUVERT"))
                .andExpect(jsonPath("$.prestataire").doesNotExist())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(reponse, "$.id");
    }

    @Test
    void unAgentDeclareEtModifieUnIncidentMaisNePeutNiLAffecterNiLeResoudre() throws Exception {
        int id = declarer(AGENT);

        mockMvc.perform(patch("/api/incidents/" + id).with(AGENT).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\": \"Porte bloquée en position ouverte\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Porte bloquée en position ouverte"));
        mockMvc.perform(put("/api/incidents/" + id + "/prestataire").with(AGENT).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"prestataireId\": 1}"))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/incidents/" + id + "/resolution").with(AGENT).with(csrf()))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/prestataires").with(AGENT))
                .andExpect(status().isForbidden());
    }

    @Test
    void leResponsableAffecteReaffecteEtResoutUnIncidentQuiPasseAlorsDansLHistorique() throws Exception {
        int id = declarer(RESPONSABLE);
        List<Integer> prestataires = JsonPath.read(mockMvc.perform(get("/api/prestataires").with(RESPONSABLE))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(), "$[*].id");

        mockMvc.perform(put("/api/incidents/" + id + "/prestataire").with(RESPONSABLE).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"prestataireId\": " + prestataires.get(0) + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("AFFECTE"));
        mockMvc.perform(put("/api/incidents/" + id + "/prestataire").with(RESPONSABLE).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"prestataireId\": " + prestataires.get(1) + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prestataire.id").value(prestataires.get(1)));
        mockMvc.perform(post("/api/incidents/" + id + "/resolution").with(RESPONSABLE).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("RESOLU"))
                .andExpect(jsonPath("$.dateResolution").exists());

        mockMvc.perform(post("/api/incidents/" + id + "/resolution").with(RESPONSABLE).with(csrf()))
                .andExpect(status().isConflict());
        mockMvc.perform(put("/api/incidents/" + id + "/prestataire").with(RESPONSABLE).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"prestataireId\": " + prestataires.get(0) + "}"))
                .andExpect(status().isConflict());
        mockMvc.perform(get("/api/incidents").with(AGENT))
                .andExpect(jsonPath("$[*].id", not(hasItem(id))));
        mockMvc.perform(get("/api/incidents/historique").with(AGENT))
                .andExpect(jsonPath("$[*].id", hasItem(id)));
    }

    @Test
    void lHistoriqueEstOrdonneDuPlusRecentAuPlusAncien() throws Exception {
        List<String> dates = JsonPath.read(mockMvc.perform(get("/api/incidents/historique").with(AGENT))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(), "$[*].dateResolution");

        for (int i = 1; i < dates.size(); i++) {
            assertTrue(dates.get(i - 1).compareTo(dates.get(i)) >= 0,
                    "L'historique n'est pas trié : " + dates.get(i - 1) + " avant " + dates.get(i));
        }
    }

    @Test
    void consulterUnIncidentInexistantRenvoie404() throws Exception {
        mockMvc.perform(get("/api/incidents/999999").with(AGENT))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("L'incident n°999999 n'existe pas."));
    }
}
