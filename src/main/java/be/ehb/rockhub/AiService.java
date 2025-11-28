package be.ehb.rockhub;

import be.ehb.rockhub.KlimSessie;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class AiService {

    private final ObjectMapper objectMapper;

    public AiService() {
        this.objectMapper = new ObjectMapper();
        // Nodig om LocalDate correct naar JSON te schrijven
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public String vraagAdvies(List<KlimSessie> sessies) {
        try {
            // 1. Converteer de lijst sessies naar JSON String
            String jsonPayload = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(sessies);

            System.out.println("Versturen naar AI: " + jsonPayload);

            // 2. (Simulatie) Stuur naar "AI API" en krijg antwoord
            // In het echt: HttpRequest.newBuilder().uri(...).POST(...)
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8000/predict"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());


            try(InputStream is = response.body()) {
                String aiResponse = new String(is.readAllBytes());
            return "Antwoord van AI: " + aiResponse;}

        } catch (Exception e) {
            e.printStackTrace();
            return "Er ging iets mis met de AI verbinding.";
        }
    }
}

//    private String simuleerAiAntwoord(List<KlimSessie> sessies) {
//        // Dummy logica
//        if (sessies.isEmpty()) return "Geen data om te analyseren. Ga klimmen!";
//
//        KlimSessie laatste = sessies.get(sessies.size() - 1);
//        return "Op basis van je sessie in " + laatste.getLocatie() + ":\n" +
//                "Je klom " + laatste.getGraad() + ". Goed bezig!\n" +
//                "Tip: Werk aan je voetplaatsing om naar 7a te gaan.";