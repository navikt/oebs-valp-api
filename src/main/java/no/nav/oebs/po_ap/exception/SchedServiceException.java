package no.nav.oebs.po_ap.exception;

public class SchedServiceException extends RuntimeException {

	public SchedServiceException(String message) {
		super(message);
	}

	public SchedServiceException(String message, Exception cause) {
		super(message, cause);
	}
}
