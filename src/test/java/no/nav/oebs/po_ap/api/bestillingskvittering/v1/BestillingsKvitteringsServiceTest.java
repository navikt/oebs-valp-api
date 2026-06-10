package no.nav.oebs.po_ap.api.bestillingskvittering.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.oebs.po_ap.db.repository.PlsqlProcedureRepository;
import no.nav.oebs.po_ap.db.repository.PlsqlProcedureResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BestillingsKvitteringsServiceTest {

    @Mock
    private PlsqlProcedureRepository plsqlProcedureRepository;

    private BestillingsKvitteringsService service;

    @BeforeEach
    void setUp() {
        service = new BestillingsKvitteringsService(plsqlProcedureRepository, new ObjectMapper());
    }

    @Test
    void finnBestillingsTransaksjoner_returnsDataFromProcedure() {
        var result = new PlsqlProcedureResult("[{\"bestillingsNummer\": \"B001\"}]", 0, null);
        when(plsqlProcedureRepository.executeInOutProcedure(anyString(), anyString())).thenReturn(result);

        String data = service.finnBestillingsTransaksjoner(202, "PROCESSED");

        assertEquals("[{\"bestillingsNummer\": \"B001\"}]", data);
    }

    @Test
    void finnBestillingsTransaksjoner_whenDataIsNull_returnsNull() {
        var result = new PlsqlProcedureResult((String) null, 0, null);
        when(plsqlProcedureRepository.executeInOutProcedure(anyString(), anyString())).thenReturn(result);

        String data = service.finnBestillingsTransaksjoner(202, "PROCESSED");

        assertNull(data);
    }

    @Test
    void finnBestillingsTransaksjoner_sendsCorrectOrgIdAndPoNumber() throws Exception {
        var result = new PlsqlProcedureResult("[]", 0, null);
        when(plsqlProcedureRepository.executeInOutProcedure(anyString(), anyString())).thenReturn(result);

        service.finnBestillingsTransaksjoner(202, "PROCESSED");

        ArgumentCaptor<String> jsonCaptor = ArgumentCaptor.forClass(String.class);
        verify(plsqlProcedureRepository).executeInOutProcedure(
                eq("xxrtv_po_ap_api_pkg.xxrtv_bestillingskvittering"),
                jsonCaptor.capture());

        String json = jsonCaptor.getValue();
        assertTrue(json.contains("202"));
        assertTrue(json.contains("PROCESSED"));
    }

    @Test
    void finnBestillingsTransaksjoner_callsCorrectPlsqlProcedure() {
        var result = new PlsqlProcedureResult("[]", 0, null);
        when(plsqlProcedureRepository.executeInOutProcedure(anyString(), anyString())).thenReturn(result);

        service.finnBestillingsTransaksjoner(202, "PROCESSED");

        verify(plsqlProcedureRepository).executeInOutProcedure(
                eq("xxrtv_po_ap_api_pkg.xxrtv_bestillingskvittering"), anyString());
    }
}
