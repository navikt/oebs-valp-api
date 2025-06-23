package no.nav.oebs.po_ap.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class FakturaDto {
    // Getters and setters
    @JsonProperty("fakturaNummer")
    private String fakturaNummer;

}
