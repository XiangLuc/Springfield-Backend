package fr.efrei2026.springfield.utiles;

import fr.efrei2026.springfield.utiles.exceptions.CsvException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CsvUtil {

    private static final char SEPARATEUR = ';';

    public CsvUtil() {}

    public static List<Map<String, String>> importerCsv(MultipartFile fichier) {
        // configuration sur le format du fichier csv qu'on va traiter
        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setDelimiter(SEPARATEUR)
                .setHeader()
                .setSkipHeaderRecord(true)
                .get();

        List<Map<String, String>> lignes = new ArrayList<>();
        try (CSVParser parser = format.parse(new InputStreamReader(fichier.getInputStream(), StandardCharsets.UTF_8))) {
            for (CSVRecord ligne : parser) {
                lignes.add(ligne.toMap());
            }
        } catch (IOException e) {
            throw new CsvException("Impossible de lire le fichier CSV.", e);
        }
        return lignes;
    }

}
