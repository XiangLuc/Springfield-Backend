package fr.efrei2026.springfield.shared.dto;

import fr.efrei2026.springfield.modeles.Station;

public record StationDto(int id, String nom) {

    public static StationDto depuis(Station station) {
        return new StationDto(station.getId(), station.getNom());
    }
}
