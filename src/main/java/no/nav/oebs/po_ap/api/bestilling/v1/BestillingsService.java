package no.nav.oebs.po_ap.api.bestilling.v1;

import lombok.extern.slf4j.Slf4j;
import no.nav.oebs.po_ap.api.common.utils.ObjektMaps;
import no.nav.oebs.po_ap.db.repository.PlsqlProcedureRepository;
import no.nav.oebs.po_ap.db.repository.PlsqlProcedureResult;
import no.nav.oebs.po_ap.exception.TechnicalPlsqlException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;


@Slf4j
@Service
@Transactional(noRollbackFor = { Exception.class })
public class BestillingsService extends ObjektMaps {

	private static final String PLSQL_PROCEDURE = "apps.xxrtv_po_ap_api_pkg.xxrtv_bestilling";

	private final PlsqlProcedureRepository plsqlProcedureRepository;

	public BestillingsService(PlsqlProcedureRepository plsqlProcedureRepository, JsonMapper objectMapper) {
		super(objectMapper);
		this.plsqlProcedureRepository = plsqlProcedureRepository;
	}

	private PlsqlProcedureResult executePlsqlProcedure(String message) {

		return plsqlProcedureRepository.executeInOutProcedure(PLSQL_PROCEDURE, message);
	}

	public String lageBestilling(String message) {
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
