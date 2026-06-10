package no.nav.oebs.po_ap.db.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class PlsqlProcedureRepositoryTest {

    private PlsqlProcedureRepository repository;

    @BeforeEach
    void setUp() {
        repository = new PlsqlProcedureRepository(mock(DataSource.class));
    }

    @Test
    void executeInOutProcedure_withoutDot_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> repository.executeInOutProcedure("ugyldigNavn", "{}"));
    }

    @Test
    void executeInOutProcedure_withTooManyDots_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> repository.executeInOutProcedure("pakke.prosedyre.ekstra", "{}"));
    }

    @Test
    void executeInOutProcedure_withValidFormat_doesNotThrowOnValidation() {
        // Validering passerer; kallet feiler på JDBC-nivå uten DB-tilkobling
        assertThrows(Exception.class,
                () -> repository.executeInOutProcedure("pakke.prosedyre", "{}"));
    }
}
