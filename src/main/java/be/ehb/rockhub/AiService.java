package be.ehb.rockhub;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class AiService {

    private final ObjectMapper objectMapper;
    //mag niet leeg zijn omdat de AI server anders weigert te antwoorden
    private static final String API_KEY = "ollama";
    private static final String AI_URL = "http://localhost:11434/v1/chat/completions";

    public AiService() {
        this.objectMapper = new ObjectMapper();
        // Zorgt ervoor dat datums (LocalDate) goed werken
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public String vraagAdvies(List<KlimSessie> sessies) {
        try {
            // Zet klimsessies om naar tekst (JSON)
            String sessiesAlsJson = objectMapper.writeValueAsString(sessies);

            // Bouw het bericht voor OpenAI
            // We maken een JSON object dat er zo uitziet:
            // {
            //   "model": "gpt-3.5-turbo",
            //   "messages": [ { "role": "user", "content": "..." } ]
            // }
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("model", "llama3");

            ArrayNode messages = requestBody.putArray("messages");
            ObjectNode userMessage = messages.addObject();
            //we geven user mee als role aan ChatGPT zodat die weet dat het van de gebruiker komt
            userMessage.put("role", "user");

            // Hier stellen we de vraag aan de AI
            String prompt = "Ik ben een klimmer. Hier is een logboek van mijn recente sessies in JSON formaat: "
                    + sessiesAlsJson
                    + ". Analyseer mijn progressie en geef kort, motiverend advies en specifieke tips om te verbeteren." +
                    "Of als je geen advies kan geven op basis van deze data, vat dan de evolutie van het klimmen samen." +
                    "Antwoord in goed en bestaand Nederlands. & antwoord in maximum 100 woorden.";
            userMessage.put("content", prompt);

            //wat er naar de AI gestuurd wordt
            String jsonPayload = objectMapper.writeValueAsString(requestBody);

            // Verstuur het verzoek naar het internet
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(AI_URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + API_KEY)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Het antwoord lezen
            if (response.statusCode() == 200) {
                // Het antwoord is een grote JSON, we willen alleen de tekst van de boodschap
                JsonNode rootNode = objectMapper.readTree(response.body());
                String aiAntwoord = rootNode.path("choices")
                        .get(0)
                        .path("message")
                        .path("content")
                        .asText();
                return aiAntwoord;
            } else {
                return "Foutmelding van AI: " + response.statusCode() + " - " + response.body();
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Kon geen verbinding maken met de AI coach.";
        }
    }
}