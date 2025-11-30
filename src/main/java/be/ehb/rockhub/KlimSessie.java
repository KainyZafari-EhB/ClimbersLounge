package be.ehb.rockhub;

import java.time.LocalDate;

public class KlimSessie {
    private LocalDate datum;
    private String locatie;
    private String graad; // bv. "6a+"
    private String type;  // bv. "Boulderen", "Lead"
    private String notities;
    private String duration;

    public KlimSessie(){

    }
    public KlimSessie(LocalDate datum, String locatie, String graad, String type, String notities, String duration) {
        this.datum = datum;
        this.locatie = locatie;
        this.graad = graad;
        this.type = type;
        this.notities = notities;
        this.duration = duration;
    }

    // Getters zijn VERPLICHT voor Jackson JSON export
    public LocalDate getDatum() { return datum; }
    public String getLocatie() { return locatie; }
    public String getGraad() { return graad; }
    public String getType() { return type; }
    public String getNotities() { return notities; }
    public String getDuration() { return duration; }
}