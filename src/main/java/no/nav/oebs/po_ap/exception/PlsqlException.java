package no.nav.oebs.po_ap.exception;

public abstract class PlsqlException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	protected PlsqlException(String message) {
		super((message));
	}
}
