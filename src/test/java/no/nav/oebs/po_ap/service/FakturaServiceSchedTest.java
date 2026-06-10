package no.nav.oebs.po_ap.service;

import no.nav.oebs.po_ap.api.fakturakvittering.v1.FakturaKvitteringsService;
import no.nav.oebs.po_ap.db.entity.KallLogg;
import no.nav.oebs.po_ap.db.repository.KallLoggRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FakturaServiceSchedTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private FakturaKvitteringsService fakturaKvitteringsService;

    @Mock
    private OppdaterFakturaService oppdaterFakturaService;

    @Mock
    private KallLoggRepository kallLoggRepository;

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
    }

    @Nested
    class SendFakturaTests {

        @Test
        void sendFaktura_whenPayloadHasNoFakturaer_setsStatusTom() {
            when(tokenService.fetchToken(anyString(), anyString())).thenReturn("token");
            when(fakturaKvitteringsService.finnFakturaTransaksjoner(anyInt(), anyString()))
                    .thenReturn("no data here");

            service.sendFaktura();

            assertEquals("TOM", service.STATUS);
        }

        @Test
        void sendFaktura_whenTokenServiceThrows_throwsRuntimeException() {
            when(tokenService.fetchToken(anyString(), anyString()))
                    .thenThrow(new RuntimeException("token error"));

            assertThrows(RuntimeException.class, () -> service.sendFaktura());
        }

        @Test
        void sendFaktura_initialStatusIsOk() {
            assertEquals("OK", service.STATUS);
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
