package no.nav.oebs.po_ap.api.fakturakvittering.v1.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({ "org_id", "faktura_num" })
public class FakturaInfoRequest {

    @JsonProperty("org_id")
    private Integer orgId;

    @JsonProperty("faktura_num")
    private String fakturaNum;
}
