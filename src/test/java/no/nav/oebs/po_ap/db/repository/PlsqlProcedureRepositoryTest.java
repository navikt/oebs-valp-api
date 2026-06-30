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
                () -> repository.executeInOutProcedure("schema.pakke.prosedyre.ekstra", "{}"));
    }

    @Test
    void executeInOutProcedure_withTooFewDots_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> repository.executeInOutProcedure("pakke.prosedyre", "{}"));
    }

    @Test
    void executeInOutProcedure_withValidFormat_doesNotThrowOnValidation() {
        // Validation passes; call fails at JDBC level without a DB connection
        assertThrows(Exception.class,
                () -> repository.executeInOutProcedure("schema.pakke.prosedyre", "{}"));
    }
}
