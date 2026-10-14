package fr.efrei2026.springfield.services;

import fr.efrei2026.springfield.exceptions.StationIntrouvableException;
import fr.efrei2026.springfield.modeles.Station;
import fr.efrei2026.springfield.repositories.StationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StationService {

    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public List<Station> listerStations() {
        return stationRepository.findAllByOrderByNomAsc();
    }

    public Station trouverStation(Integer id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new StationIntrouvableException(id));
    }
}