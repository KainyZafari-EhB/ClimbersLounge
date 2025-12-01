🧗 RockHub (ClimbersLounge)
RockHub is een JavaFX-applicatie ontworpen voor klimmers om hun klimsessies bij te houden, hun progressie te visualiseren en gepersonaliseerd advies te krijgen van een AI-coach.

- Features
Sessies Loggen: Houd eenvoudig je klimsessies bij door datum, locatie, graad (bv. 6a, 7b), type (Boulderen, Lead, etc.) en notities in te voeren.

Data Opslag: Sessies worden automatisch lokaal opgeslagen in een klimsessies.json bestand, zodat je data bewaard blijft bij het afsluiten van de app.

Progressie Grafiek: Een visuele weergave van je voortgang. De app converteert klimgraden (zoals 5a, 6b+) naar numerieke scores om een lijn in de tijd te plotten.

 AI Coach: Integratie met Ollama: llama3 om je klimdata te analyseren. De AI geeft advies en tips op basis van je gelogde sessies.

Beheer: Mogelijkheid om foutieve sessies uit de lijst te verwijderen.

- Technologieën
Java 21+

JavaFX: Voor de gebruikersinterface (FXML).

Maven: Voor dependency management.

Jackson: Voor het verwerken van JSON-data (opslaan van sessies en communicatie met AI).

OpenAI API: Voor de slimme coach functionaliteit.

- Installatie & Setup
Clone de repository:

Bash

git clone https://github.com/KainyZafari-EhB/ClimbersLounge.git
Configureer de AI Service.


Build en Run: Gebruik Maven om het project te bouwen en te starten.

Bash

mvn clean javafx:run

Side Note:
Dit project is gemaakt als onderdeel van een schoolopdracht aan de Erasmushogeschool Brussel (EhB).
