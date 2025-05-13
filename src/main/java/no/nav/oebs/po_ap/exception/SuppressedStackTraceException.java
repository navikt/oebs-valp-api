package no.nav.oebs.po_ap.exception;

public class SuppressedStackTraceException extends RuntimeException {

    public SuppressedStackTraceException(String message) {
        super(message);
        // This prevents capturing the stack trace for performance
        this.setStackTrace(new StackTraceElement[0]);
    }

    public SuppressedStackTraceException(String message, Throwable cause) {
        super(message, cause);
        this.setStackTrace(new StackTraceElement[0]);
    }

    // Override fillInStackTrace to improve performance
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}

