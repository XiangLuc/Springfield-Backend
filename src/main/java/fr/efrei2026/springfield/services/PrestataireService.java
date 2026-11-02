package fr.efrei2026.springfield.services;

import fr.efrei2026.springfield.exceptions.PrestataireIntrouvableException;
import fr.efrei2026.springfield.modeles.Prestataire;
import fr.efrei2026.springfield.repositories.PrestataireRepository;
import fr.efrei2026.springfield.services.interfaces.IPrestataireService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrestataireService implements IPrestataireService {

    private final PrestataireRepository prestataireRepository;

    public PrestataireService(PrestataireRepository prestataireRepository) {
        this.prestataireRepository = prestataireRepository;
    }

    @Override
    public List<Prestataire> listerPrestataires() {
        return prestataireRepository.findAllByOrderByNomAsc();
    }

    @Override
    public Prestataire trouverPrestataire(Integer id) {
        return prestataireRepository.findById(id)
                .orElseThrow(() -> new PrestataireIntrouvableException(id));
    }
}
