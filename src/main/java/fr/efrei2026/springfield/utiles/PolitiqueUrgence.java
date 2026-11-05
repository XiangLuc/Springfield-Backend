package fr.efrei2026.springfield.utiles;

import fr.efrei2026.springfield.modeles.Incident;
import fr.efrei2026.springfield.modeles.enums.NiveauAttention;
import org.springframework.stereotype.Component;

import java.time.Clock;

@Component
public class PolitiqueUrgence {

    private static final int SEUIL_ORANGE_EN_HEURES = 48;
    private static final int SEUIL_ROUGE_EN_HEURES = 72;

    private final Clock horloge;

    public PolitiqueUrgence(Clock horloge) {
        this.horloge = horloge;
    }

    public NiveauAttention evaluer(Incident incident) {
        if (incident.aUnPrestataire()) {
            return NiveauAttention.AFFECTE;
        }
        long dureeOuvertureEnHeures = incident.dureeOuverture(horloge).toHours();
        if (dureeOuvertureEnHeures >= SEUIL_ROUGE_EN_HEURES) {
            return NiveauAttention.ROUGE;
        }
        if (dureeOuvertureEnHeures >= SEUIL_ORANGE_EN_HEURES) {
            return NiveauAttention.ORANGE;
        }
        return NiveauAttention.VERT;
    }
}
