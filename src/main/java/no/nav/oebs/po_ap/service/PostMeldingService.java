package no.nav.oebs.po_ap.service;

import no.nav.oebs.po_ap.api.bestillingskvittering.v1.BestillingsKvitteringsService;
import no.nav.oebs.po_ap.config.common.logging.LoggingUtils;
import no.nav.oebs.po_ap.config.common.mdc.MdcOperations;
import no.nav.oebs.po_ap.db.entity.KallLogg;
import no.nav.oebs.po_ap.db.repository.KallLoggRepository;
import no.nav.oebs.po_ap.db.repository.PlsqlMessageCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.http.HttpHeaders;
import java.time.LocalDateTime;
import static no.nav.oebs.po_ap.config.common.mdc.MdcOperations.generateCorrelationId;

@Service
public class PostMeldingService {

    private RestClient restClient;
    private final Logger logger = LoggerFactory.getLogger(TokenService.class);

    private static final String PROCESSED = "PROCESSED";
    private static final Integer ORG_ID = 202;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private BestillingsKvitteringsService service;

    @Value("${faktura.endpoint.url}")
    private String fakturaEndpointUrl;

    @Value("${base.url}")
    private String baseUrl;

    @Value("${bestilling.endpoint.url}")
    private String bestillingEndpointUrl;

    @Value("${identityProvider}")
    private String identityProvider;

    @Value("${target}")
    private String target;

    private final static String procName = "/api/v1/bestillingskvittering";

    @Autowired
    private KallLoggRepository kallLoggRepository;

    public PostMeldingService() {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .build();
    }

    public String postmelding() {

        long startTime = System.currentTimeMillis();


        /* Hent Token fra Texas .. */
        String bearerToken = tokenService.fetchToken(identityProvider, target);

        /* Hent Json data fra Oebs .. */
        String jsonPayLoad = service.finnBestillingsTransaksjoner(ORG_ID, PROCESSED);

        /* Send POST requesten .. */
        try {
            return restClient.post()
                    .uri(bestillingEndpointUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
                    .body(jsonPayLoad)
                    .retrieve()
                    .onStatus(httpStatus -> true, (request, response) -> {
                        HttpStatusCode statusCode = response.getStatusCode();
                        String statusType = getStatusTypeDescription(statusCode);

                        logger.error("{} response: Status {}, Body: {}",
                                statusType,
                                statusCode,
                                response.getBody());

                        boolean isError = statusCode.is4xxClientError() || statusCode.is5xxServerError();
                        skrivLogg(System.currentTimeMillis() - startTime, jsonPayLoad,
                                isError ? new Exception() : null);

                        if (isError) {
                            throw new RuntimeException(statusType + " occurred");
                        }
                    })
                    .body(String.class);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException("Kunne ikke sende forespørselen", e);
        }
    }

    private String getStatusTypeDescription(HttpStatusCode statusCode) {
        if (statusCode.is4xxClientError()) {
            return "Klient feil";
        } else if (statusCode.is5xxServerError()) {
            return "Server feil";
        } else {
            return "Ukjent feil";
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
                    .operation(PostMeldingService.procName) //
                    .status(exception != null //
                            ? PlsqlMessageCodes.EXCEPTION //
                            : 200) //
                    .kalltid(executionTime) //
                    .request("Schedulert jobb") //
                    .response(jsonPayLoad) //
                    .logginfo(exception != null //
                            ? LoggingUtils.formatExceptionAsString(exception) //
                            : jsonPayLoad) //
                    .build();

            saveKallLogg(kallLogg);
        }
}

    public void saveKallLogg(KallLogg kallLogg) {
        try {
            kallLoggRepository.save(kallLogg);
        } catch (Exception e) {
            logger.error("Feil ved logging av kalloggdata til databasen; feilmelding=" + e.getMessage(), e);
        }
    }
}
