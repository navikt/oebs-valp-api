package no.nav.oebs.po_ap.config;

import no.nav.oebs.po_ap.service.BestillingServiceSched;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduledTaskBestillingTest {

    @Mock
    BestillingServiceSched bestillingServiceSched;

    @InjectMocks
    ScheduledTaskBestilling scheduledTaskBestilling;

    @Test
    void process_callsSendBestilling() {
        scheduledTaskBestilling.process();

        verify(bestillingServiceSched).sendBestilling();
    }

    @Test
    void process_whenSendBestillingThrows_doesNotRethrow() {
        doThrow(new RuntimeException("feil")).when(bestillingServiceSched).sendBestilling();

        assertDoesNotThrow(() -> scheduledTaskBestilling.process());
    }

    @Test
    void process_whenStatusIsNotOk_doesNotSleep() {
        bestillingServiceSched.STATUS = "TOM";

        scheduledTaskBestilling.process();

        verify(bestillingServiceSched).sendBestilling();
    }
}
