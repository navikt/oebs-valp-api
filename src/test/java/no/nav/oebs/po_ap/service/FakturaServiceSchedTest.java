package no.nav.oebs.po_ap.service;

import no.nav.oebs.po_ap.api.fakturakvittering.v1.FakturaKvitteringsService;
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
class FakturaServiceSchedTest {

    private static final String PAYLOAD_WITH_FAKTURA = "[{\"fakturaNummer\": \"F001\"}]";

    @Mock private TokenService tokenService;
    @Mock private FakturaKvitteringsService fakturaKvitteringsService;
    @Mock private OppdaterFakturaService oppdaterFakturaService;
    @Mock private KallLoggRepository kallLoggRepository;
    @Mock private RestClient restClient;
    @Mock private RestClient.RequestBodyUriSpec postSpec;
    @Mock private RestClient.ResponseSpec responseSpec;

    private FakturaServiceSched service;

    @BeforeEach
    void setUp() {
        service = new FakturaServiceSched();
        ReflectionTestUtils.setField(service, "tokenService", tokenService);
        ReflectionTestUtils.setField(service, "service", fakturaKvitteringsService);
        ReflectionTestUtils.setField(service, "oppdaterFakturaService", oppdaterFakturaService);
        ReflectionTestUtils.setField(service, "kallLoggRepository", kallLoggRepository);
        ReflectionTestUtils.setField(service, "baseUrl", "http://localhost");
        ReflectionTestUtils.setField(service, "fakturaEndpointUrl", "/api/v1/okonomi/kvittering/faktura");
        ReflectionTestUtils.setField(service, "identityProvider", "azuread");
        ReflectionTestUtils.setField(service, "target", "api://test/.default");
        service.init();
        ReflectionTestUtils.setField(service, "restClient", restClient);
    }

    private void stubRestClientSuccess() {
        when(restClient.post()).thenReturn(postSpec);
        when(postSpec.uri(anyString())).thenReturn(postSpec);
        when(postSpec.header(any(), any())).thenReturn(postSpec);
        when(postSpec.body(anyString())).thenReturn(postSpec);
        when(postSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.body(String.class)).thenReturn("OK");
    }

    private void stubPayloadWithFaktura() {
        when(tokenService.fetchToken(anyString(), anyString())).thenReturn("token");
        when(fakturaKvitteringsService.finnFakturaTransaksjoner(anyInt(), anyString()))
                .thenReturn(PAYLOAD_WITH_FAKTURA);
    }

    @Nested
    class SendFakturaTests {

        @Test
        void sendFaktura_initialStatusIsOk() {
            assertEquals("OK", service.STATUS);
        }

        @Test
        void sendFaktura_whenPayloadHasNoFakturaer_setsStatusTom() {
            when(tokenService.fetchToken(anyString(), anyString())).thenReturn("token");
            when(fakturaKvitteringsService.finnFakturaTransaksjoner(anyInt(), anyString()))
                    .thenReturn("no data here");

            service.sendFaktura();

            assertEquals("TOM", service.STATUS);
        }

        @Test
        void sendFaktura_fetchesTokenWithCorrectArguments() {
            when(tokenService.fetchToken(anyString(), anyString())).thenReturn("token");
            when(fakturaKvitteringsService.finnFakturaTransaksjoner(anyInt(), anyString()))
                    .thenReturn("no data here");

            service.sendFaktura();

            verify(tokenService).fetchToken("azuread", "api://test/.default");
        }

        @Test
        void sendFaktura_queriesFakturaKvitteringsServiceWithCorrectOrgIdAndStatus() {
            when(tokenService.fetchToken(anyString(), anyString())).thenReturn("token");
            when(fakturaKvitteringsService.finnFakturaTransaksjoner(anyInt(), anyString()))
                    .thenReturn("no data here");

            service.sendFaktura();

            verify(fakturaKvitteringsService).finnFakturaTransaksjoner(202, "PROCESSED");
        }

        @Test
        void sendFaktura_whenTokenServiceThrows_throwsRuntimeException() {
            when(tokenService.fetchToken(anyString(), anyString()))
                    .thenThrow(new RuntimeException("token error"));

            assertThrows(RuntimeException.class, () -> service.sendFaktura());
        }

        @Test
        void sendFaktura_whenPayloadContainsFakturaNummer_callsHttpEndpoint() throws Exception {
            stubPayloadWithFaktura();
            when(oppdaterFakturaService.updateKvitteringStatus(anyString())).thenReturn(1);
            stubRestClientSuccess();

            service.sendFaktura();

            verify(restClient).post();
        }

        @Test
        void sendFaktura_whenPayloadContainsFakturaNummer_callsOppdaterFakturaService() throws Exception {
            stubPayloadWithFaktura();
            when(oppdaterFakturaService.updateKvitteringStatus(anyString())).thenReturn(1);
            stubRestClientSuccess();

            service.sendFaktura();

            verify(oppdaterFakturaService).updateKvitteringStatus(PAYLOAD_WITH_FAKTURA);
        }

        @Test
        void sendFaktura_whenPayloadContainsFakturaNummer_savesKallLoggWithSuccessStatus() throws Exception {
            stubPayloadWithFaktura();
            when(oppdaterFakturaService.updateKvitteringStatus(anyString())).thenReturn(1);
            stubRestClientSuccess();

            service.sendFaktura();

            ArgumentCaptor<KallLogg> captor = ArgumentCaptor.forClass(KallLogg.class);
            verify(kallLoggRepository).save(captor.capture());
            assertEquals(200, captor.getValue().getStatus());
        }

        @Test
        void sendFaktura_whenRestClientThrows_throwsRuntimeExceptionWithCause() {
            stubPayloadWithFaktura();
            when(restClient.post()).thenThrow(new RuntimeException("connection refused"));

            RuntimeException ex = assertThrows(RuntimeException.class, () -> service.sendFaktura());
            assertEquals("Kunne ikke sende forespørselen", ex.getMessage());
            assertNotNull(ex.getCause());
        }

        @Test
        void sendFaktura_whenRestClientThrows_savesKallLoggWithErrorStatus() {
            stubPayloadWithFaktura();
            when(restClient.post()).thenThrow(new RuntimeException("connection refused"));

            assertThrows(RuntimeException.class, () -> service.sendFaktura());

            ArgumentCaptor<KallLogg> captor = ArgumentCaptor.forClass(KallLogg.class);
            verify(kallLoggRepository).save(captor.capture());
            assertEquals(PlsqlMessageCodes.EXCEPTION, captor.getValue().getStatus());
        }

        @Test
        void sendFaktura_whenOppdaterServiceThrows_throwsRuntimeException() throws Exception {
            stubPayloadWithFaktura();
            stubRestClientSuccess();
            when(oppdaterFakturaService.updateKvitteringStatus(anyString()))
                    .thenThrow(new RuntimeException("update failed"));

            assertThrows(RuntimeException.class, () -> service.sendFaktura());
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
