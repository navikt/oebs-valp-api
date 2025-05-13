package no.nav.oebs.po_ap.exception;

import no.nav.oebs.po_ap.config.common.logging.LoggingUtils;

public abstract class PlsqlException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public PlsqlException(String message) {
		super((message));
	}
}
