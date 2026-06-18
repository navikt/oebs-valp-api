package no.nav.oebs.po_ap.api.fakturakvittering.v1;

import no.nav.oebs.po_ap.db.repository.PlsqlProcedureRepository;
import no.nav.oebs.po_ap.db.repository.PlsqlProcedureResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FakturaKvitteringsServiceTest {

    @Mock
    private PlsqlProcedureRepository plsqlProcedureRepository;

    private FakturaKvitteringsService service;

    @BeforeEach
    void setUp() {
        service = new FakturaKvitteringsService(plsqlProcedureRepository, new JsonMapper());
    }

    @Test
    void finnFakturaTransaksjoner_returnsDataFromProcedure() {
        var result = new PlsqlProcedureResult("[{\"fakturaNummer\": \"F001\"}]", 0, null);
        when(plsqlProcedureRepository.executeInOutProcedure(anyString(), anyString())).thenReturn(result);

        String data = service.finnFakturaTransaksjoner(202, "PROCESSED");

        assertEquals("[{\"fakturaNummer\": \"F001\"}]", data);
    }

    @Test
    void finnFakturaTransaksjoner_whenDataIsNull_returnsNull() {
        var result = new PlsqlProcedureResult((String) null, 0, null);
        when(plsqlProcedureRepository.executeInOutProcedure(anyString(), anyString())).thenReturn(result);

        String data = service.finnFakturaTransaksjoner(202, "PROCESSED");

        assertNull(data);
    }

    @Test
    void finnFakturaTransaksjoner_sendsCorrectOrgIdAndFakturaNum() {
        var result = new PlsqlProcedureResult("[]", 0, null);
        when(plsqlProcedureRepository.executeInOutProcedure(anyString(), anyString())).thenReturn(result);

        service.finnFakturaTransaksjoner(202, "PROCESSED");

        ArgumentCaptor<String> jsonCaptor = ArgumentCaptor.forClass(String.class);
        verify(plsqlProcedureRepository).executeInOutProcedure(
                eq("xxrtv_po_ap_api_pkg.xxrtv_fakturakvittering"),
                jsonCaptor.capture());

        String json = jsonCaptor.getValue();
        assertTrue(json.contains("202"));
        assertTrue(json.contains("PROCESSED"));
    }

    @Test
    void finnFakturaTransaksjoner_callsCorrectPlsqlProcedure() {
        var result = new PlsqlProcedureResult("[]", 0, null);
        when(plsqlProcedureRepository.executeInOutProcedure(anyString(), anyString())).thenReturn(result);

        service.finnFakturaTransaksjoner(202, "PROCESSED");

        verify(plsqlProcedureRepository).executeInOutProcedure(
                eq("xxrtv_po_ap_api_pkg.xxrtv_fakturakvittering"), anyString());
    }
}
