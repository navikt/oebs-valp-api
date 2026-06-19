package no.nav.oebs.po_ap.api.bestillingskvittering.v1;

import lombok.extern.slf4j.Slf4j;
import no.nav.oebs.po_ap.api.common.utils.ObjektMaps;
import no.nav.oebs.po_ap.api.bestillingskvittering.v1.model.BestillingsKvitteringsRequest;
import no.nav.oebs.po_ap.db.repository.PlsqlProcedureRepository;
import no.nav.oebs.po_ap.db.repository.PlsqlProcedureResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;


@Slf4j
@Service
@Transactional(noRollbackFor = { Exception.class })
public class BestillingsKvitteringsService extends ObjektMaps {

	private static final String PLSQL_PROCEDURE = "xxrtv_po_ap_api_pkg.xxrtv_bestillingskvittering";

	private final PlsqlProcedureRepository plsqlProcedureRepository;

	public BestillingsKvitteringsService(PlsqlProcedureRepository plsqlProcedureRepository, JsonMapper objectMapper) {
		super(objectMapper);
		this.plsqlProcedureRepository = plsqlProcedureRepository;
	}

	/**
	 * Bygger et requestobjekt som skal konverteres til JSON.
	 */
	private BestillingsKvitteringsRequest buildRequest(Integer orgId, String poNumber) {
		return BestillingsKvitteringsRequest.builder() //
				.orgId(orgId) //
				.poNumber(poNumber) //
				.build();
	}

	/**
	 * Kaller PL/SQL-prosedyren som utfører forretningslogikken til operasjonen.
	 * @param request
	 */
	private PlsqlProcedureResult executePlsqlProcedure(BestillingsKvitteringsRequest request) {

		return plsqlProcedureRepository.executeInOutProcedure(PLSQL_PROCEDURE, toJson(request));
	}

	public String finnBestillingsTransaksjoner(Integer orgId, String poNumber) {

		PlsqlProcedureResult result = executePlsqlProcedure(buildRequest(orgId, poNumber));

		return result.getData();

	}
}
