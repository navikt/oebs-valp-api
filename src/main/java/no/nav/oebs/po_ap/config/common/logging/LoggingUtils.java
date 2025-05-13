package no.nav.oebs.po_ap.config.common.logging;

import java.io.PrintWriter;
import java.io.StringWriter;

public class LoggingUtils {

	private LoggingUtils() {

	}

	public static String formatExceptionAsString(Throwable exception) {
		if (exception == null) {
			return null;
		}
		var stringWriter = new StringWriter();
		exception.printStackTrace(new PrintWriter(stringWriter));

		return stringWriter.toString();
	}
}
