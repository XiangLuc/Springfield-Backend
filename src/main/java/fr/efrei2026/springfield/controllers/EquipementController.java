package fr.efrei2026.springfield.controllers;

import fr.efrei2026.springfield.services.EquipementService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/equipements")
public class EquipementController {

    private final EquipementService equipementService;

    public EquipementController(EquipementService equipementService) {
        this.equipementService = equipementService;
    }

    @GetMapping("/{id}")
    public String afficherFicheEquipement(@PathVariable Integer id, Model model) {
        model.addAttribute("equipement", equipementService.trouverEquipement(id));
        model.addAttribute("incidentsEnCours", equipementService.listerIncidentsEnCours(id));
        model.addAttribute("incidentsPasses", equipementService.listerIncidentsPasses(id));
        return "equipements/fiche";
    }
}