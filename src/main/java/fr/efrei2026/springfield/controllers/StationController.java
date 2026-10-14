package fr.efrei2026.springfield.controllers;

import fr.efrei2026.springfield.services.EquipementService;
import fr.efrei2026.springfield.services.StationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/stations")
public class StationController {

    private final StationService stationService;
    private final EquipementService equipementService;

    public StationController(StationService stationService, EquipementService equipementService) {
        this.stationService = stationService;
        this.equipementService = equipementService;
    }

    @GetMapping
    public String afficherStations(Model model) {
        model.addAttribute("stations", stationService.listerStations());
        return "stations/liste";
    }

    @GetMapping("/{id}")
    public String afficherEquipementsDeLaStation(@PathVariable Integer id, Model model) {
        model.addAttribute("station", stationService.trouverStation(id));
        model.addAttribute("equipements", equipementService.listerEquipementsDeLaStation(id));
        return "stations/equipements";
    }
}