package no.nav.oebs.po_ap.service;

import no.nav.oebs.po_ap.api.bestillingskvittering.v1.BestillingsKvitteringsService;
import no.nav.oebs.po_ap.db.entity.KallLogg;
import no.nav.oebs.po_ap.db.repository.KallLoggRepository;
import no.nav.oebs.po_ap.db.repository.PlsqlMessageCodes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"unchecked", "rawtypes"})
@ExtendWith(MockitoExtension.class)
class BestillingServiceSchedTest {

    private static final String PAYLOAD_WITH_BESTILLING = "[{\"bestillingsNummer\": \"B001\"}]";

    @Mock private TokenService tokenService;
    @Mock private BestillingsKvitteringsService bestillingsKvitteringsService;
    @Mock private OppdaterBestillingService oppdaterBestillingService;
    @Mock private KallLoggRepository kallLoggRepository;
    @Mock private RestClient restClient;
    @Mock private RestClient.RequestBodyUriSpec postSpec;
    @Mock private RestClient.ResponseSpec responseSpec;

    private BestillingServiceSched service;

    @BeforeEach
    void setUp() {
        service = new BestillingServiceSched();
        ReflectionTestUtils.setField(service, "tokenService", tokenService);
        ReflectionTestUtils.setField(service, "service", bestillingsKvitteringsService);
        ReflectionTestUtils.setField(service, "oppdaterBestillingService", oppdaterBestillingService);
        ReflectionTestUtils.setField(service, "kallLoggRepository", kallLoggRepository);
        ReflectionTestUtils.setField(service, "baseUrl", "http://localhost");
        ReflectionTestUtils.setField(service, "bestillingEndpointUrl", "/api/v1/okonomi/kvittering/bestilling");
        ReflectionTestUtils.setField(service, "identityProvider", "azuread");
        ReflectionTestUtils.setField(service, "target", "api://test/.default");
        service.init();
        ReflectionTestUtils.setField(service, "restClient", restClient);
    }

    private void stubRestClientSuccess(){
        when(restClient.post()).thenReturn(postSpec);
        when(postSpec.uri(anyString())).thenReturn(postSpec);
        when(postSpec.header(any(), any())).thenReturn(postSpec);
        when(postSpec.body(anyString())).thenReturn(postSpec);
        when(postSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.body(String.class)).thenReturn("OK");
    }

    private void stubPayloadWithBestilling() {
        when(tokenService.fetchToken(anyString(), anyString())).thenReturn("token");
        when(bestillingsKvitteringsService.finnBestillingsTransaksjoner(anyInt(), anyString()))
                .thenReturn(PAYLOAD_WITH_BESTILLING);
    }

    @Nested
    class SendBestillingTests {

        @Test
        void sendBestilling_initialStatusIsOk() {
            assertEquals("OK", service.STATUS);
        }

        @Test
        void sendBestilling_whenPayloadHasNoBestillinger_setsStatusTom() {
            when(tokenService.fetchToken(anyString(), anyString())).thenReturn("token");
            when(bestillingsKvitteringsService.finnBestillingsTransaksjoner(anyInt(), anyString()))
                    .thenReturn("no data here");

            service.sendBestilling();

            assertEquals("TOM", service.STATUS);
        }

        @Test
        void sendBestilling_fetchesTokenWithCorrectArguments() {
            when(tokenService.fetchToken(anyString(), anyString())).thenReturn("token");
            when(bestillingsKvitteringsService.finnBestillingsTransaksjoner(anyInt(), anyString()))
                    .thenReturn("no data here");

            service.sendBestilling();

            verify(tokenService).fetchToken("azuread", "api://test/.default");
        }

        @Test
        void sendBestilling_queriesBestillingsKvitteringsServiceWithCorrectOrgIdAndStatus() {
            when(tokenService.fetchToken(anyString(), anyString())).thenReturn("token");
            when(bestillingsKvitteringsService.finnBestillingsTransaksjoner(anyInt(), anyString()))
                    .thenReturn("no data here");

            service.sendBestilling();

            verify(bestillingsKvitteringsService).finnBestillingsTransaksjoner(202, "PROCESSED");
        }

        @Test
        void sendBestilling_whenTokenServiceThrows_throwsRuntimeException() {
            when(tokenService.fetchToken(anyString(), anyString()))
                    .thenThrow(new RuntimeException("token error"));

            assertThrows(RuntimeException.class, () -> service.sendBestilling());
        }

        @Test
        void sendBestilling_whenPayloadContainsBestillingsNummer_callsHttpEndpoint() throws Exception {
            stubPayloadWithBestilling();
            when(oppdaterBestillingService.updateKvitteringStatus(anyString())).thenReturn(1);
            stubRestClientSuccess();

            service.sendBestilling();

            verify(restClient).post();
        }

        @Test
        void sendBestilling_whenPayloadContainsBestillingsNummer_callsOppdaterBestillingService() throws Exception {
            stubPayloadWithBestilling();
            when(oppdaterBestillingService.updateKvitteringStatus(anyString())).thenReturn(1);
            stubRestClientSuccess();

            service.sendBestilling();

            verify(oppdaterBestillingService).updateKvitteringStatus(PAYLOAD_WITH_BESTILLING);
        }

        @Test
        void sendBestilling_whenPayloadContainsBestillingsNummer_savesKallLoggWithSuccessStatus() throws Exception {
            stubPayloadWithBestilling();
            when(oppdaterBestillingService.updateKvitteringStatus(anyString())).thenReturn(1);
            stubRestClientSuccess();

            service.sendBestilling();

            ArgumentCaptor<KallLogg> captor = ArgumentCaptor.forClass(KallLogg.class);
            verify(kallLoggRepository).save(captor.capture());
            assertEquals(200, captor.getValue().getStatus());
        }

        @Test
        void sendBestilling_whenRestClientThrows_throwsRuntimeExceptionWithCause() {
            stubPayloadWithBestilling();
            when(restClient.post()).thenThrow(new RuntimeException("connection refused"));

            RuntimeException ex = assertThrows(RuntimeException.class, () -> service.sendBestilling());
            assertEquals("Kunne ikke sende forespørselen", ex.getMessage());
            assertNotNull(ex.getCause());
        }

        @Test
        void sendBestilling_whenRestClientThrows_savesKallLoggWithErrorStatus() {
            stubPayloadWithBestilling();
            when(restClient.post()).thenThrow(new RuntimeException("connection refused"));

            assertThrows(RuntimeException.class, () -> service.sendBestilling());

            ArgumentCaptor<KallLogg> captor = ArgumentCaptor.forClass(KallLogg.class);
            verify(kallLoggRepository).save(captor.capture());
            assertEquals(PlsqlMessageCodes.EXCEPTION, captor.getValue().getStatus());
        }

        @Test
        void sendBestilling_whenOppdaterServiceThrows_throwsRuntimeException() throws Exception {
            stubPayloadWithBestilling();
            stubRestClientSuccess();
            when(oppdaterBestillingService.updateKvitteringStatus(anyString()))
                    .thenThrow(new RuntimeException("update failed"));

            assertThrows(RuntimeException.class, () -> service.sendBestilling());
        }
    }

    @Nested
    class SaveKallLoggTests {

        @Test
        void saveKallLogg_whenSaveThrows_logsAndDoesNotRethrow() {
            KallLogg kallLogg = KallLogg.builder().korrelasjonId("korrId").build();
            doThrow(new RuntimeException("db error")).when(kallLoggRepository).save(any());

            assertDoesNotThrow(() -> service.saveKallLogg(kallLogg));
        }

        @Test
        void saveKallLogg_callsRepositorySave() {
            KallLogg kallLogg = KallLogg.builder().korrelasjonId("korrId").build();

            service.saveKallLogg(kallLogg);

            verify(kallLoggRepository).save(kallLogg);
        }
    }
}

