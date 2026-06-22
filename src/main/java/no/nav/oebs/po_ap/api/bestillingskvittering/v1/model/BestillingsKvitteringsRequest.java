package no.nav.oebs.po_ap.api.bestillingskvittering.v1.model;

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
@JsonPropertyOrder({ "org_id", "po_number" })
public class BestillingsKvitteringsRequest {

    @JsonProperty("org_id")
    private Integer orgId;

    @JsonProperty("po_number")
    private String poNumber;
}
