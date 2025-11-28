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

import java.time.LocalDate;

public class DashboardController {

    @FXML private DatePicker datePicker;
    @FXML private TextField txtLocatie;
    @FXML private TextField txtGraad;
    @FXML private ComboBox<String> comboType;
    @FXML private TextArea txtNotities;
    @FXML private ListView<String> listSessies; // Simpele weergave
    @FXML private TextArea txtAiAdvies;

    private ObservableList<KlimSessie> sessieLijst = FXCollections.observableArrayList();
    private AiService aiService = new AiService();

    @FXML
    public void initialize() {
        datePicker.setValue(LocalDate.now());
        comboType.getItems().addAll("Boulderen", "Toprope", "Lead", "Speed");
        comboType.getSelectionModel().selectFirst();
        updateGrafiek();
    }

    @FXML
    public void onVoegToe() {
        KlimSessie nieuweSessie = new KlimSessie(
                datePicker.getValue(),
                txtLocatie.getText(),
                txtGraad.getText(),
                comboType.getValue(),
                txtNotities.getText()
        );

        sessieLijst.add(nieuweSessie);
        listSessies.getItems().add(nieuweSessie.getDatum() + ": " + nieuweSessie.getGraad() + " (" + nieuweSessie.getType() + ")");
        updateGrafiek();

        // Reset velden
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
        // ... bestaande velden ...

        // NIEUWE VELDEN VOOR DE GRAFIEK
        @FXML private LineChart<String, Number> progressieGrafiek;
        @FXML private CategoryAxis dateAxis;
        @FXML private NumberAxis gradeAxis;


        // NIEUWE METHODE: Update de grafiek
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

        // NIEUWE HELPER: Zet graad (bv "6a+") om naar getal
        private int converteerGraadNaarScore(String graad) {
            if (graad == null) return 0;
            String g = graad.toLowerCase().trim();

            // Simpele conversietabel (kan je uitbreiden)
            // 4 = 40, 5a = 50, 5b = 52, 6a = 60, etc.
            if (g.startsWith("4")) return 40;
            if (g.startsWith("5a")) return g.contains("+") ? 51 : 50;
            if (g.startsWith("5b")) return g.contains("+") ? 53 : 52;
            if (g.startsWith("5c")) return g.contains("+") ? 55 : 54;

            if (g.startsWith("6a")) return g.contains("+") ? 61 : 60;
            if (g.startsWith("6b")) return g.contains("+") ? 63 : 62;
            if (g.startsWith("6c")) return g.contains("+") ? 65 : 64;

            if (g.startsWith("7a")) return g.contains("+") ? 71 : 70;
            if (g.startsWith("7b")) return g.contains("+") ? 73 : 72;
            if (g.startsWith("7c")) return g.contains("+") ? 75 : 74;

            if (g.startsWith("8")) return 80; // Enzovoort

            return 0; // Onbekende graad
        }

        // ... rest van de code ...
    }