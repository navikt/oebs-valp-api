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
import java.util.concurrent.atomic.AtomicBoolean;

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

        long startTime = System.currentTimeMillis();

        AtomicBoolean isError = new AtomicBoolean(false);

        String bearerToken = tokenService.fetchToken(identityProvider, target);

        String jsonPayLoad = service.finnFakturaTransaksjoner(ORG_ID, PROCESSED);

        try {
            if (jsonPayLoad.contains("fakturaNummer")) {
                restClient.post()
                        .uri(fakturaEndpointUrl)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
                        .body(jsonPayLoad)
                        .retrieve()
                        .onStatus(httpStatus -> true, (request, response) -> {
                            HttpStatusCode statusCode = response.getStatusCode();

                            isError.set(statusCode.is4xxClientError() || statusCode.is5xxServerError());

                            skrivLogg(System.currentTimeMillis() - startTime, jsonPayLoad,
                                    isError.get() ? new Exception() : null);

                            if (isError.get()) {
                                logger.info("statusCode: {}", statusCode);

                                throw new RuntimeException(statusCode + " occurred");
                            } else {
                                // Oppdater status i database
                                int antallKvitt = oppdaterFakturaService.updateKvitteringStatus(jsonPayLoad);
                                logger.info("Antall kvitteringer overført: {}", antallKvitt);
                            }
                        })
                        .body(String.class);
            }
            else {
                STATUS = "TOM";
                /*skrivLogg(System.currentTimeMillis() - startTime, jsonPayLoad,
                        isError.get() ? new Exception() : null);*/
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
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
