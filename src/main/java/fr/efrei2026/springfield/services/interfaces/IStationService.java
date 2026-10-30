package fr.efrei2026.springfield.services.interfaces;

import fr.efrei2026.springfield.modeles.Station;

import java.util.List;

public interface IStationService {
    List<Station> listerStations();
    Station trouverStation(Integer id);
}
