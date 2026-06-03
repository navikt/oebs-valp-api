package no.nav.oebs.po_ap.api.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.oebs.po_ap.db.repository.PlsqlMessageCodes;
import no.nav.oebs.po_ap.db.repository.PlsqlProcedureResult;
import no.nav.oebs.po_ap.exception.JsonMappingException;
import no.nav.oebs.po_ap.exception.TechnicalPlsqlException;
import no.nav.oebs.po_ap.exception.UgyldigInputException;

/**
 * Superklasse med felles funksjonalitet for implementasjon av tjenestespesifikke Service-klasser.
 */
public class ObjektMaps {

	private ObjectMapper objectMapper;

	protected ObjektMaps(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	/**
	 * Kaster exception iht. feilkoden returnert fra PL/SQL-prosedyren.
	 */
	protected void throwPlsqlException(PlsqlProcedureResult result) {
		switch (result.getMessageNumber()) {
		case PlsqlMessageCodes.FEIL_I_INPUT:
			throw new UgyldigInputException(result.getMessage());
		default:
			throw new TechnicalPlsqlException(result.getMessageNumber(), result.getMessage());
		}
	}

	/**
	 * Mapper fra Java- til JSON-objekt.
	 */
	protected <T> String toJson(T object) {
		try {
			return objectMapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			throw new JsonMappingException(e);
		}
	}

}
