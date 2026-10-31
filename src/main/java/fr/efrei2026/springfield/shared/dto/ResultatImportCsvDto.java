package fr.efrei2026.springfield.shared.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ResultatImportCsvDto {

    private int nombreSucces;
    private List<LigneCsvEchecDto> lignesEchouees = new ArrayList<>();

    public void ajouterSucces() {
        nombreSucces++;
    }

    public void ajouterEchec(int numeroLigne, Map<String, String> ligne, String raison) {
        lignesEchouees.add(new LigneCsvEchecDto(numeroLigne, String.join(";", ligne.values()), raison));
    }

    public int getNombreEchecs() {
        return lignesEchouees.size();
    }

    public int getNombreSucces() {
        return nombreSucces;
    }

    public void setNombreSucces(int nombreSucces) {
        this.nombreSucces = nombreSucces;
    }

    public List<LigneCsvEchecDto> getLignesEchouees() {
        return lignesEchouees;
    }

    public void setLignesEchouees(List<LigneCsvEchecDto> lignesEchouees) {
        this.lignesEchouees = lignesEchouees;
    }
}
