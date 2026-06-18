package no.nav.oebs.po_ap.api.faktura.v1;

import no.nav.oebs.po_ap.db.repository.PlsqlProcedureRepository;
import no.nav.oebs.po_ap.db.repository.PlsqlProcedureResult;
import no.nav.oebs.po_ap.exception.TechnicalPlsqlException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FakturaServiceTest {

    @Mock
    private PlsqlProcedureRepository plsqlProcedureRepository;

    private FakturaService service;

    @BeforeEach
    void setUp() {
        service = new FakturaService(plsqlProcedureRepository, new JsonMapper());
    }

    @Nested
    class LagreFakturaTests {

        @Test
        void lagreFaktura_whenProcedureSucceeds_returnsMessage() {
            var result = new PlsqlProcedureResult("data", 0, "OK");
            when(plsqlProcedureRepository.executeInOutProcedure(anyString(), anyString())).thenReturn(result);

            String melding = service.lagreFaktura("{\"test\": true}");

            assertEquals("OK", melding);
        }

        @Test
        void lagreFaktura_whenMessageNumberIsNegative_throwsTechnicalPlsqlException() {
            var result = new PlsqlProcedureResult("data", -1, "Technical error");
            when(plsqlProcedureRepository.executeInOutProcedure(anyString(), anyString())).thenReturn(result);

            assertThrows(TechnicalPlsqlException.class, () -> service.lagreFaktura("{\"test\": true}"));
        }

        @Test
        void lagreFaktura_whenMessageNumberIsInvalidInput_throwsTechnicalPlsqlException() {
            var result = new PlsqlProcedureResult("data", -20882, "Invalid input");
            when(plsqlProcedureRepository.executeInOutProcedure(anyString(), anyString())).thenReturn(result);

            assertThrows(TechnicalPlsqlException.class, () -> service.lagreFaktura("{\"test\": true}"));
        }

        @Test
        void lagreFaktura_whenRepositoryThrows_throwsTechnicalPlsqlException() {
            when(plsqlProcedureRepository.executeInOutProcedure(anyString(), anyString()))
                    .thenThrow(new RuntimeException("db error"));

            assertThrows(TechnicalPlsqlException.class, () -> service.lagreFaktura("{\"test\": true}"));
        }

        @Test
        void lagreFaktura_callsCorrectPlsqlProcedure() {
            var result = new PlsqlProcedureResult("data", 0, "OK");
            when(plsqlProcedureRepository.executeInOutProcedure(anyString(), anyString())).thenReturn(result);

            service.lagreFaktura("payload");

            verify(plsqlProcedureRepository).executeInOutProcedure(
                    eq("xxrtv_po_ap_api_pkg.xxrtv_faktura"), anyString());
        }
    }
}
