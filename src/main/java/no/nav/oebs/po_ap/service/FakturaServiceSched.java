package no.nav.oebs.po_ap.service;

import jakarta.annotation.PostConstruct;
import no.nav.oebs.po_ap.api.fakturakvittering.v1.FakturaKvitteringsService;
import no.nav.oebs.po_ap.config.common.logging.LoggingUtils;
import no.nav.oebs.po_ap.config.common.mdc.MdcOperations;
import no.nav.oebs.po_ap.db.entity.KallLogg;
import no.nav.oebs.po_ap.db.repository.KallLoggRepository;
import no.nav.oebs.po_ap.db.repository.PlsqlMessageCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;

import static no.nav.oebs.po_ap.config.common.mdc.MdcOperations.generateCorrelationId;

@Service
public class FakturaServiceSched {

    public String STATUS = "OK" ;

    @Autowired
    private  OppdaterFakturaService oppdaterFakturaService;

    private RestClient restClient;
    private final Logger logger = LoggerFactory.getLogger(FakturaServiceSched.class);

    private static final String PROCESSED = "PROCESSED";
    private static final Integer ORG_ID = 202;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private FakturaKvitteringsService service;

    @Value("${tiltaksokonomi.faktura.endpoint.url}")
    private String fakturaEndpointUrl;

    @Value("${tiltaksokonomi.base.url}")
    private String baseUrl;

    @Value("${token.identityProvider}")
    private String identityProvider;

    @Value("${token.target}")
    private String target;

    private final static String procName = "/api/v1/fakturakvittering";

    @Autowired
    private KallLoggRepository kallLoggRepository;

    @PostConstruct
    public void init() {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .build();
    }

    public void sendFaktura() {

        STATUS = "OK";
        long startTime = System.currentTimeMillis();

        String bearerToken = tokenService.fetchToken(identityProvider, target);
        String jsonPayLoad = service.finnFakturaTransaksjoner(ORG_ID, PROCESSED);

        if (!jsonPayLoad.contains("fakturaNummer")) {
            STATUS = "TOM";
            return;
        }

        try {
            restClient.post()
                    .uri(fakturaEndpointUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
                    .body(jsonPayLoad)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, response) -> {
                        String responseBody = new String(response.getBody().readAllBytes());
                        throw new RuntimeException("HTTP " + response.getStatusCode() +
                                " when calling " + fakturaEndpointUrl +
                                ". Response: " + responseBody);
                    })
                    .body(String.class);

            skrivLogg(System.currentTimeMillis() - startTime, jsonPayLoad, null);
            int antallKvitt = oppdaterFakturaService.updateKvitteringStatus(jsonPayLoad);
            logger.info("Antall kvitteringer overført: {}", antallKvitt);

        } catch (Exception e) {
            skrivLogg(System.currentTimeMillis() - startTime, jsonPayLoad, e);
            logger.error("Feil ved kall til {}: {}", fakturaEndpointUrl, e.getMessage(), e);
            throw new RuntimeException("Kunne ikke sende forespørselen", e);
        }
    }

    private void skrivLogg(long executionTime, String jsonPayLoad, Exception exception ) {

        if (MdcOperations.get(MdcOperations.MDC_CORRELATION_ID) == null) {
            KallLogg kallLogg = KallLogg.builder() //
                    .korrelasjonId(generateCorrelationId())
                    .tidspunkt(LocalDateTime.now()) //
                    .type(KallLogg.TYPE_PLSQL) //
                    .kallRetning(KallLogg.RETNING_UT) //
                    .method(KallLogg.METHOD_POST) //
                    .operation(FakturaServiceSched.procName) //
                    .status(exception != null //
                            ? PlsqlMessageCodes.EXCEPTION //
                            : 200) //
                    .kalltid(executionTime) //
                    .request("SKEDULERT /api/v1/fakturakvittering") //
                    .response(jsonPayLoad) //
                    .logginfo(exception != null //
                            ? LoggingUtils.formatExceptionAsString(exception) //
                            : "") //
                    .build();

            saveKallLogg(kallLogg);
        }
    }

    public void saveKallLogg(KallLogg kallLogg) {
        try {
            kallLoggRepository.save(kallLogg);
        } catch (Exception e) {
            logger.error("Feil ved logging av data til databasen; feilmelding=" + e.getMessage(), e);
        }
    }
}
