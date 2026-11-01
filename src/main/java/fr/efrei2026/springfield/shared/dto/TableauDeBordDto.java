package fr.efrei2026.springfield.shared.dto;

import fr.efrei2026.springfield.modeles.enums.NiveauAttention;

import java.util.List;
import java.util.Map;

public class TableauDeBordDto {

    private final long nombreIncidentsActifs;
    private final Map<NiveauAttention, Long> nombreParNiveau;
    private final long dureeMoyenneOuvertureEnHeures;
    private final long nombreStationsTouchees;
    private final List<IncidentActifDto> incidents;

    public TableauDeBordDto(long nombreIncidentsActifs, Map<NiveauAttention, Long> nombreParNiveau, long dureeMoyenneOuvertureEnHeures, long nombreStationsTouchees, List<IncidentActifDto> incidents) {
        this.nombreIncidentsActifs = nombreIncidentsActifs;
        this.nombreParNiveau = nombreParNiveau;
        this.dureeMoyenneOuvertureEnHeures = dureeMoyenneOuvertureEnHeures;
        this.nombreStationsTouchees = nombreStationsTouchees;
        this.incidents = incidents;
    }

    public long getNombreIncidentsActifs() {
        return nombreIncidentsActifs;
    }

    public Map<NiveauAttention, Long> getNombreParNiveau() {
        return nombreParNiveau;
    }

    public long getDureeMoyenneOuvertureEnHeures() {
        return dureeMoyenneOuvertureEnHeures;
    }

    public long getNombreStationsTouchees() {
        return nombreStationsTouchees;
    }

    public List<IncidentActifDto> getIncidents() {
        return incidents;
    }
}
