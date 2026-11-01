package fr.efrei2026.springfield.services.interfaces;

import fr.efrei2026.springfield.modeles.enums.NiveauAttention;
import fr.efrei2026.springfield.shared.dto.TableauDeBordDto;

public interface ITableauDeBordService {
    TableauDeBordDto construireTableauDeBord(Integer stationId, NiveauAttention niveauAttention);
}
