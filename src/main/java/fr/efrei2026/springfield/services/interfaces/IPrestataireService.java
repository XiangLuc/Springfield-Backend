package fr.efrei2026.springfield.services.interfaces;

import fr.efrei2026.springfield.shared.dto.PrestataireDto;

import java.util.List;

public interface IPrestataireService {
    List<PrestataireDto> listerPrestataires();

    boolean estSousSurveillance(int prestataireId);
}
