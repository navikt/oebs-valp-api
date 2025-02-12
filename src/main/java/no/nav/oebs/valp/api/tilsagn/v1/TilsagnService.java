package no.nav.oebs.valp.api.tilsagn.v1;

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
public class TilsagnService extends ObjektMaps {

	private static final String PLSQL_PROCEDURE = "xxrtv_valp_api_pkg.xxrtv_tilsagn";

	private final PlsqlProcedureRepository plsqlProcedureRepository;

	public TilsagnService(PlsqlProcedureRepository plsqlProcedureRepository, ObjectMapper objectMapper) {
		super(objectMapper);
		this.plsqlProcedureRepository = plsqlProcedureRepository;
	}

	private PlsqlProcedureResult executePlsqlProcedure(String message) {

		return plsqlProcedureRepository.executeInOutProcedure(PLSQL_PROCEDURE, message);
	}

	public String lageTilsagn(String message) {
		try {

			PlsqlProcedureResult result = executePlsqlProcedure(message);
			if (result.getMessageNumber() < 0) {
				throwPlsqlException(result);
			}

			return result.getMessage();

		} catch (Exception e) {
			throw new TechnicalPlsqlException(e.getMessage());
		}
	}
}
