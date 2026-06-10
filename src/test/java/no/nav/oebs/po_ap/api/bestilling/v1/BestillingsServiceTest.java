package no.nav.oebs.po_ap.api.bestilling.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.oebs.po_ap.db.repository.PlsqlProcedureRepository;
import no.nav.oebs.po_ap.db.repository.PlsqlProcedureResult;
import no.nav.oebs.po_ap.exception.TechnicalPlsqlException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BestillingsServiceTest {

    @Mock
    private PlsqlProcedureRepository plsqlProcedureRepository;

    private BestillingsService service;

    @BeforeEach
    void setUp() {
        service = new BestillingsService(plsqlProcedureRepository, new ObjectMapper());
    }

    @Nested
    class LageBestillingTests {

        @Test
        void lageBestilling_whenProcedureSucceeds_returnsMessage() {
            var result = new PlsqlProcedureResult("data", 0, "OK");
            when(plsqlProcedureRepository.executeInOutProcedure(anyString(), anyString())).thenReturn(result);

            String melding = service.lageBestilling("{\"test\": true}");

            assertEquals("OK", melding);
        }

        @Test
        void lageBestilling_whenMessageNumberIsNegative_throwsTechnicalPlsqlException() {
            var result = new PlsqlProcedureResult("data", -1, "Technical error");
            when(plsqlProcedureRepository.executeInOutProcedure(anyString(), anyString())).thenReturn(result);

            assertThrows(TechnicalPlsqlException.class, () -> service.lageBestilling("{\"test\": true}"));
        }

        @Test
        void lageBestilling_whenMessageNumberIsInvalidInput_throwsTechnicalPlsqlException() {
            var result = new PlsqlProcedureResult("data", -20882, "Invalid input");
            when(plsqlProcedureRepository.executeInOutProcedure(anyString(), anyString())).thenReturn(result);

            assertThrows(TechnicalPlsqlException.class, () -> service.lageBestilling("{\"test\": true}"));
        }

        @Test
        void lageBestilling_whenRepositoryThrows_throwsTechnicalPlsqlException() {
            when(plsqlProcedureRepository.executeInOutProcedure(anyString(), anyString()))
                    .thenThrow(new RuntimeException("db error"));

            assertThrows(TechnicalPlsqlException.class, () -> service.lageBestilling("{\"test\": true}"));
        }

        @Test
        void lageBestilling_callsCorrectPlsqlProcedure() {
            var result = new PlsqlProcedureResult("data", 0, "OK");
            when(plsqlProcedureRepository.executeInOutProcedure(anyString(), anyString())).thenReturn(result);

            service.lageBestilling("payload");

            verify(plsqlProcedureRepository).executeInOutProcedure(
                    eq("xxrtv_po_ap_api_pkg.xxrtv_bestilling"), anyString());
        }
    }
}
