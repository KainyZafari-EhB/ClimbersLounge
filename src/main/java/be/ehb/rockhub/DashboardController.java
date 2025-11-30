package be.ehb.rockhub;

import be.ehb.rockhub.KlimSessie;
import be.ehb.rockhub.AiService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.util.StringConverter;
import javafx.util.StringConverter;
import java.util.LinkedHashMap;
import java.util.Map;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

import java.time.LocalDate;
import java.util.Map;

public class DashboardController {

    @FXML private DatePicker datePicker;
    @FXML private TextField txtLocatie;
    @FXML private TextField txtGraad;
    @FXML private ComboBox<String> comboType;
    @FXML private TextArea txtNotities;
    @FXML private TextField txtDuration;
    @FXML private ListView<String> listSessies; // Simpele weergave
    @FXML private TextArea txtAiAdvies;
    @FXML private NumberAxis gradeAxis;
    @FXML private LineChart<String, Number> progressieGrafiek;

    private final File dataFile = new File("klim_sessies.json");
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final ObservableList<KlimSessie> sessieLijst = FXCollections.observableArrayList();
    private final AiService aiService = new AiService();

    public DashboardController(){
        objectMapper.registerModule(new JavaTimeModule());
    }
    @FXML
    public void initialize() {
        datePicker.setValue(LocalDate.now());
        comboType.getItems().addAll("Boulderen", "Toprope", "Lead", "Speed");
        comboType.getSelectionModel().selectFirst();
        configureGradeAxisLabels();

        laadSessies();

        updateGrafiek();
    }

    @FXML
    public void onVoegToe() {
        KlimSessie nieuweSessie = new KlimSessie(
                datePicker.getValue(),
                txtLocatie.getText(),
                txtGraad.getText(),
                comboType.getValue(),
                txtNotities.getText(),
                txtDuration.getText()
        );

        sessieLijst.add(nieuweSessie);
        listSessies.getItems().add(nieuweSessie.getDatum() + ": " + nieuweSessie.getLocatie() + " " + nieuweSessie.getGraad() + " " + nieuweSessie.getDuration() + " (" + nieuweSessie.getType() + ")");


        slaSessiesOp();

        updateGrafiek();

        // Reset velden
        txtLocatie.clear();
        txtGraad.clear();
        txtNotities.clear();

    }

    @FXML
    public void onVraagAi() {
        txtAiAdvies.setText("AI is aan het nadenken...");

        // Roep de service aan
        String advies = aiService.vraagAdvies(sessieLijst);

        txtAiAdvies.setText(advies);
    }



        private void updateGrafiek() {
            progressieGrafiek.getData().clear(); // Oude data wissen

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Mijn Progressie");

            for (KlimSessie sessie : sessieLijst) {
                // We gebruiken de datum als X-waarde en de geconverteerde graad als Y-waarde
                int score = converteerGraadNaarScore(sessie.getGraad());
                if (score > 0) { // Alleen geldige graden plotten
                    series.getData().add(new XYChart.Data<>(sessie.getDatum().toString(), score));
                }
            }

            progressieGrafiek.getData().add(series);
        }

        // Zet graad (bv "6a+") om naar getal
        private int converteerGraadNaarScore(String graad) {
            if (graad == null || graad.isEmpty()) return 0;

            String g = graad.toLowerCase().trim();

            // 4e graads
            if (g.startsWith("4")) return 40;

            // 5e graads (Start bij 50)
            if (g.startsWith("5a")) return g.contains("+") ? 51 : 50;
            if (g.startsWith("5b")) return g.contains("+") ? 53 : 52;
            if (g.startsWith("5c")) return g.contains("+") ? 55 : 54;

            // 6e graads (Start bij 60)
            if (g.startsWith("6a")) return g.contains("+") ? 61 : 60;
            if (g.startsWith("6b")) return g.contains("+") ? 63 : 62;
            if (g.startsWith("6c")) return g.contains("+") ? 65 : 64;

            // 7e graads (Start bij 70)
            if (g.startsWith("7a")) return g.contains("+") ? 71 : 70;
            if (g.startsWith("7b")) return g.contains("+") ? 73 : 72;
            if (g.startsWith("7c")) return g.contains("+") ? 75 : 74;

            // 8e graads (Start bij 80)
            if (g.startsWith("8")) return 80;

            return 0;
        }

    private void slaSessiesOp() {
        try {
            // Schrijf de lijst naar "klimsessies.json"
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(dataFile, sessieLijst);
            System.out.println("Data succesvol opgeslagen!");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Kon data niet opslaan: " + e.getMessage());
        }
    }

    private void laadSessies() {
        if (!dataFile.exists()) {
            System.out.println("Nog geen data bestand gevonden. Start met lege lijst.");
            return;
        }

        try {
            // Lees het bestand en zet het om naar een Lijst van KlimSessies
            List<KlimSessie> geladenData = objectMapper.readValue(dataFile, new TypeReference<List<KlimSessie>>(){});

            // Voeg alles toe aan de observable list
            sessieLijst.addAll(geladenData);

            // Update ook de zichtbare lijst in de UI
            for (KlimSessie k : sessieLijst) {
                listSessies.getItems().add(k.getDatum() + ": " + k.getLocatie() + " " + k.getGraad() + " " + k.getDuration() + " (" + k.getType() + ")");
            }

            System.out.println("Data succesvol geladen: " + sessieLijst.size() + " sessies.");

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Fout bij laden data: " + e.getMessage());
            }
        }
    @FXML
    public void onVerwijder() {
        // 1. Welke rij heeft de gebruiker aangeklikt?
        int index = listSessies.getSelectionModel().getSelectedIndex();

        // Als index -1 is, is er niets geselecteerd
        if (index == -1) {
            System.out.println("Selecteer eerst een sessie uit de lijst!");
            return;
        }

        // 2. Verwijder de data uit de "echte" lijst
        sessieLijst.remove(index);

        // 3. Verwijder de tekst van het scherm
        listSessies.getItems().remove(index);

        updateGrafiek();
        slaSessiesOp();

        System.out.println("Sessie verwijderd.");
    }

    private void configureGradeAxisLabels() {
        // 1. We maken een "Map" (een woordenboek) aan.
        // De Integer is de score (bv. 60), de String is wat we op het scherm willen zien (bv. "6a").
        // LinkedHashMap zorgt ervoor dat de volgorde behouden blijft (handig voor overzicht).
        Map<Integer, String> gradeLabels = new LinkedHashMap<>();

        // 2. We vullen het woordenboek met onze gekozen schaalwaarden.
        // Alleen deze specifieke getallen krijgen een tekstje op de as.
        gradeLabels.put(40, "4");
        gradeLabels.put(50, "5a");
        gradeLabels.put(60, "6a");
        gradeLabels.put(70, "7a");
        gradeLabels.put(80, "8a");
        gradeLabels.put(90, "9a"); // Alvast voor de toekomst

        // 3. We schakelen "AutoRanging" uit.
        // Dit betekent dat de grafiek niet zelf mag gokken hoe de as eruitziet.
        // Wij bepalen de start en het einde.
        gradeAxis.setAutoRanging(false);

        // 4. Stel de ondergrens in.
        // We kiezen 35 (iets lager dan 40) zodat het laagste punt niet op de bodem plakt.
        gradeAxis.setLowerBound(35);

        // 5. Stel de bovengrens in.
        // We kiezen 85 (iets hoger dan 80) zodat er ruimte is aan de bovenkant.
        gradeAxis.setUpperBound(85);

        // 6. Stel de stapgrootte in.
        // We willen om de 10 punten een streepje, want onze graden (40, 50, 60) springen per 10.
        gradeAxis.setTickUnit(10);

        // 7. De "TickLabelFormatter" is de vertaler van de grafiek.
        // Hij krijgt een getal (bv. 60) en moet tekst teruggeven ("6a").
        gradeAxis.setTickLabelFormatter(new StringConverter<Number>() {

            @Override
            public String toString(Number object) {
                // We pakken de waarde als een heel getal (int)
                int score = object.intValue();

                // We kijken in ons woordenboek (gradeLabels) of dit getal een naam heeft.
                // .getOrDefault(score, "") betekent:
                // "Als de score in de lijst staat, geef de naam. Zo niet, geef lege tekst."
                // Hierdoor krijgen tussenliggende getallen (zoals 63) geen label, wat de as rustig houdt.
                return gradeLabels.getOrDefault(score, "");
            }

            @Override
            public Number fromString(String string) {
                // Deze methode wordt bijna nooit gebruikt voor alleen weergave,
                // maar het is netjes om hem werkend te hebben (andersom zoeken).
                for (Map.Entry<Integer, String> e : gradeLabels.entrySet()) {
                    if (e.getValue().equals(string)) {
                        return e.getKey(); // Gevonden! Geef het getal terug.
                    }
                }
                return 0; // Niet gevonden
            }
        });
    }
}