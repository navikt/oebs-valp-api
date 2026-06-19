package no.nav.oebs.po_ap.config;

import no.nav.oebs.po_ap.service.FakturaServiceSched;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduledTaskFakturaTest {

    @Mock
    FakturaServiceSched fakturaServiceSched;

    @InjectMocks
    ScheduledTaskFaktura scheduledTaskFaktura;

    @Test
    void process_callsSendFaktura() {
        scheduledTaskFaktura.process();

        verify(fakturaServiceSched).sendFaktura();
    }

    @Test
    void process_whenSendFakturaThrows_doesNotRethrow() {
        doThrow(new RuntimeException("feil")).when(fakturaServiceSched).sendFaktura();

        assertDoesNotThrow(() -> scheduledTaskFaktura.process());
    }

    @Test
    void process_whenStatusIsNotOk_doesNotSleep() {
        fakturaServiceSched.setStatus("TOM");

        scheduledTaskFaktura.process();

        verify(fakturaServiceSched).sendFaktura();
    }
}
