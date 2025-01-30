package no.nav.oebs.valp.api.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import java.time.LocalDate;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({ "org_id", "segmentname", "segmentverdi", "lastupdatedate" })
public class KontRequest {

    private Integer org_id;

    private String segmentname;

    private String segmentverdi;

    private LocalDate lastupdatedate;
}

