module be.ehb.rockhub {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires java.net.http;
    requires javafx.base; // Nodig voor JSON

    // JavaFX toegang geven tot controllers
    opens be.ehb.rockhub to javafx.fxml, com.fasterxml.jackson.databind;

    // Jackson toegang geven tot de data-modellen (voor JSON export)

    exports be.ehb.rockhub;
}