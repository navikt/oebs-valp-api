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
@JsonPropertyOrder({ "org_id", "leverandornavn", "leverandornummer", "leverandorsted", "lastupdatedate" })
public class LevRequest {

    private Integer org_id;

    private String leverandornavn;

    private String leverandornummer;

    private String leverandorsted;

    private LocalDate lastupdatedate;

}
