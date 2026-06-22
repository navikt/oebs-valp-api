package no.nav.oebs.po_ap.service;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import no.nav.oebs.po_ap.api.bestillingskvittering.v1.BestillingsKvitteringsService;
import no.nav.oebs.po_ap.config.common.logging.LoggingUtils;
import no.nav.oebs.po_ap.config.common.mdc.MdcOperations;
import no.nav.oebs.po_ap.db.entity.KallLogg;
import no.nav.oebs.po_ap.db.repository.KallLoggRepository;
import no.nav.oebs.po_ap.db.repository.PlsqlMessageCodes;
import no.nav.oebs.po_ap.exception.SchedServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClientException;

import java.time.LocalDateTime;

import static no.nav.oebs.po_ap.config.common.mdc.MdcOperations.generateCorrelationId;

@Service
public class BestillingServiceSched {

    private final Logger logger = LoggerFactory.getLogger(BestillingServiceSched.class);

    private static final String PROCNAME = "/api/v1/bestillingskvittering";
    private static final String PROCESSED = "PROCESSED";
    private static final Integer ORG_ID = 202;

    @Getter
    @Setter
    private String status = "OK" ;
    private RestClient restClient;

    private final OppdaterBestillingService oppdaterBestillingService;
    private final TokenService tokenService;
    private final BestillingsKvitteringsService service;
    private final KallLoggRepository kallLoggRepository;

    public BestillingServiceSched(
            OppdaterBestillingService oppdaterBestillingService,
            TokenService tokenService,
            BestillingsKvitteringsService service,
            KallLoggRepository kallLoggRepository
    ) {
        this.oppdaterBestillingService = oppdaterBestillingService;
        this.tokenService = tokenService;
        this.service = service;
        this.kallLoggRepository = kallLoggRepository;
     }


    @Value("${tiltaksokonomi.base.url}")
    private String baseUrl;

    @Value("${tiltaksokonomi.bestilling.endpoint.url}")
    private String bestillingEndpointUrl;

    @Value("${token.identityProvider}")
    private String identityProvider;

    @Value("${token.target}")
    private String target;

    @PostConstruct
    public void init() {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .build();
    }

    public void sendBestilling() {

        setStatus("OK");
        long startTime = System.currentTimeMillis();

        String bearerToken = tokenService.fetchToken(identityProvider, target);
        String jsonPayLoad = service.finnBestillingsTransaksjoner(ORG_ID, PROCESSED);

        if (!jsonPayLoad.contains("bestillingsNummer")) {
            setStatus("TOM");
            return;
        }

        try {
            restClient.post()
                    .uri(bestillingEndpointUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
                    .body(jsonPayLoad)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, response) -> {
                        String responseBody = new String(response.getBody().readAllBytes());
                        throw new SchedServiceException("HTTP " + response.getStatusCode() +
                                " when calling " + bestillingEndpointUrl +
                                ". Response: " + responseBody);
                    })
                    .body(String.class);

            skrivLogg(System.currentTimeMillis() - startTime, jsonPayLoad, null);
            int antallKvitt = oppdaterBestillingService.updateKvitteringStatus(jsonPayLoad);
            logger.info("Antall kvitteringer overført: {}", antallKvitt);

        } catch (SchedServiceException e) {
            skrivLogg(System.currentTimeMillis() - startTime, jsonPayLoad, e);
            throw e;
        } catch (RestClientException | DataAccessException e) {
            skrivLogg(System.currentTimeMillis() - startTime, jsonPayLoad, e);
            throw new SchedServiceException("Feil ved kall til "+ bestillingEndpointUrl+" : "+ e.getMessage(), e);
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
                    .operation(BestillingServiceSched.PROCNAME) //
                    .status(exception != null //
                            ? PlsqlMessageCodes.EXCEPTION //
                            : 200) //
                    .kalltid(executionTime) //
                    .request("SKEDULERT /api/v1/bestillingskvittering") //
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
