package no.nav.oebs.po_ap.api.fakturakvittering.v1;

import lombok.extern.slf4j.Slf4j;
import no.nav.oebs.po_ap.api.fakturakvittering.v1.model.FakturaInfoRequest;
import no.nav.oebs.po_ap.api.common.utils.ObjektMaps;
import no.nav.oebs.po_ap.db.repository.PlsqlProcedureRepository;
import no.nav.oebs.po_ap.db.repository.PlsqlProcedureResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Service
@Transactional(noRollbackFor = { Exception.class })
public class FakturaKvitteringsService extends ObjektMaps {

	private static final String PLSQL_PROCEDURE = "xxrtv_po_ap_api_pkg.xxrtv_fakturakvittering";

	private final PlsqlProcedureRepository plsqlProcedureRepository;

	public FakturaKvitteringsService(PlsqlProcedureRepository plsqlProcedureRepository, JsonMapper objectMapper) {
		super(objectMapper);
		this.plsqlProcedureRepository = plsqlProcedureRepository;
	}

	private FakturaInfoRequest buildRequest(Integer orgId, String poNumber) {
		return FakturaInfoRequest.builder() //
				.orgId(orgId) //
				.fakturaNum(poNumber) //
				.build();
	}

	private PlsqlProcedureResult executePlsqlProcedure(FakturaInfoRequest request) {

		return plsqlProcedureRepository.executeInOutProcedure(PLSQL_PROCEDURE, toJson(request));
	}


	public String finnFakturaTransaksjoner(Integer orgId, String poNumber) {

		PlsqlProcedureResult result = executePlsqlProcedure(buildRequest(orgId, poNumber));

		return result.getData();

	}

}
