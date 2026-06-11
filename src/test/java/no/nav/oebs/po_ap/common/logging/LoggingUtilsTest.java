package no.nav.oebs.po_ap.common.logging;

import no.nav.oebs.po_ap.config.common.logging.LoggingUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoggingUtilsTest {

    @Test
    void returnsNullWhenExceptionIsNull() {
        assertNull(LoggingUtils.formatExceptionAsString(null));
    }

    @Test
    void returnsStackTraceAsString() {
        var exception = new RuntimeException("test error");
        var result = LoggingUtils.formatExceptionAsString(exception);

        assertNotNull(result);
        assertTrue(result.contains("RuntimeException"));
        assertTrue(result.contains("test error"));
    }

    @Test
    void includesCauseInOutput() {
        var cause = new IllegalArgumentException("root cause");
        var exception = new RuntimeException("wrapper", cause);
        var result = LoggingUtils.formatExceptionAsString(exception);

        assertNotNull(result);
        assertTrue(result.contains("IllegalArgumentException"));
        assertTrue(result.contains("root cause"));
    }

    @Test
    void includesStackFramesInOutput() {
        var exception = new RuntimeException("with stack");
        var result = LoggingUtils.formatExceptionAsString(exception);

        assertNotNull(result);
        assertTrue(result.contains("\tat "), "Expected stack frames in output");
    }
}
