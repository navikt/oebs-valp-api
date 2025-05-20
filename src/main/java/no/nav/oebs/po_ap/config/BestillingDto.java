package no.nav.oebs.po_ap.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BestillingDto {

    @JsonProperty("bestillingsNummer")
    private String bestillingsNummer;

    // Getters and setters
    public String getBestillingsNummer() {
        return bestillingsNummer;
    }

    public void setBestillingsNummer(String bestillingsNummer) {
        this.bestillingsNummer = bestillingsNummer;
    }
}

