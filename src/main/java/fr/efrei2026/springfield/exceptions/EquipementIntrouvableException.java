package fr.efrei2026.springfield.exceptions;

public class EquipementIntrouvableException extends RessourceIntrouvableException {

    public EquipementIntrouvableException(Integer id) {
        super("L'équipement n°" + id + " n'existe pas.");
    }
}