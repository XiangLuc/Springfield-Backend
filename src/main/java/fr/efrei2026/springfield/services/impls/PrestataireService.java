package fr.efrei2026.springfield.services.impls;

import fr.efrei2026.springfield.repositories.PrestataireRepository;
import fr.efrei2026.springfield.services.interfaces.IPrestataireService;
import fr.efrei2026.springfield.shared.dto.PrestataireDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrestataireService implements IPrestataireService {

    private final PrestataireRepository prestataireRepository;

    public PrestataireService(PrestataireRepository prestataireRepository) {
        this.prestataireRepository = prestataireRepository;
    }

    @Override
    public List<PrestataireDto> listerPrestataires() {
        return prestataireRepository.listerAvecNotes();
    }
}
