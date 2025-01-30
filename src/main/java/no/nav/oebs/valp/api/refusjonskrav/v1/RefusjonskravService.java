package no.nav.oebs.valp.api.refusjonskrav.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.oebs.valp.api.common.utils.ObjektMaps;
import no.nav.oebs.valp.db.repository.PlsqlProcedureRepository;
import no.nav.oebs.valp.db.repository.PlsqlProcedureResult;
import no.nav.oebs.valp.exception.TechnicalPlsqlException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(noRollbackFor = { Exception.class })
public class RefusjonskravService extends ObjektMaps {

	private static final String PLSQL_PROCEDURE = "xxrtv_valp_api_pkg.xxrtv_refusjonskrav";

	private final PlsqlProcedureRepository plsqlProcedureRepository;

	public RefusjonskravService(PlsqlProcedureRepository plsqlProcedureRepository, ObjectMapper objectMapper) {
		super(objectMapper);
		this.plsqlProcedureRepository = plsqlProcedureRepository;
	}


	private PlsqlProcedureResult executePlsqlProcedure(String message) {

		return plsqlProcedureRepository.executeInOutProcedure(PLSQL_PROCEDURE, message);
	}

	public String lageRefusjonskrav(String message) {
		try {

			PlsqlProcedureResult result = executePlsqlProcedure(message);
			if (result.getMessageNumber() < 0) {
			 	throwPlsqlException(result);
		     }

			return result.getMessage();

		} catch (Exception e) {
			String error = "Feilet under lagring av refusjonskrav i Oebs; feilmelding=" + e.getMessage();

			throw new TechnicalPlsqlException(error); // + e);
		}
	}
}
