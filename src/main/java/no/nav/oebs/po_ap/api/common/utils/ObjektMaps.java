package no.nav.oebs.po_ap.api.common.utils;

import no.nav.oebs.po_ap.db.repository.PlsqlMessageCodes;
import no.nav.oebs.po_ap.db.repository.PlsqlProcedureResult;
import no.nav.oebs.po_ap.exception.JsonMappingException;
import no.nav.oebs.po_ap.exception.TechnicalPlsqlException;
import no.nav.oebs.po_ap.exception.UgyldigInputException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

/**
 * Superklasse med felles funksjonalitet for implementasjon av tjenestespesifikke Service-klasser.
 */
public class ObjektMaps {

	private final JsonMapper objectMapper;

	protected ObjektMaps(JsonMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	/**
	 * Kaster exception iht. feilkoden returnert fra PL/SQL-prosedyren.
	 */
	protected void throwPlsqlException(PlsqlProcedureResult result) {
        if (result.getMessageNumber() == PlsqlMessageCodes.FEIL_I_INPUT) {
            throw new UgyldigInputException(result.getMessage());
        }
        throw new TechnicalPlsqlException(result.getMessageNumber(), result.getMessage());
    }

	/**
	 * Mapper fra Java- til JSON-objekt.
	 */
	protected <T> String toJson(T object) {
		try {
			return objectMapper.writeValueAsString(object);
		} catch (JacksonException e) {
			throw new JsonMappingException(e);
		}
	}

}
