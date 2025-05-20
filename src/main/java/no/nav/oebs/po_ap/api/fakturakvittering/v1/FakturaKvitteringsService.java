package no.nav.oebs.po_ap.api.fakturakvittering.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.oebs.po_ap.api.fakturakvittering.v1.model.FakturaInfoRequest;
import no.nav.oebs.po_ap.api.common.utils.ObjektMaps;
import no.nav.oebs.po_ap.db.repository.PlsqlProcedureRepository;
import no.nav.oebs.po_ap.db.repository.PlsqlProcedureResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(noRollbackFor = { Exception.class })
public class FakturaKvitteringsService extends ObjektMaps {

	private static final String PLSQL_PROCEDURE = "xxrtv_po_ap_api_pkg.xxrtv_fakturainfo";

	private final PlsqlProcedureRepository plsqlProcedureRepository;

	public FakturaKvitteringsService(PlsqlProcedureRepository plsqlProcedureRepository, ObjectMapper objectMapper) {
		super(objectMapper);
		this.plsqlProcedureRepository = plsqlProcedureRepository;
	}

	private FakturaInfoRequest buildRequest(Integer org_id, String po_number) {
		return FakturaInfoRequest.builder() //
				.org_id(org_id) //
				.po_number(po_number) //
				.build();
	}

	private PlsqlProcedureResult executePlsqlProcedure(FakturaInfoRequest request) {

		return plsqlProcedureRepository.executeInOutProcedure(PLSQL_PROCEDURE, toJson(request));
	}


	public String finnFakturaTransaksjoner(Integer org_id, String po_number) {

		PlsqlProcedureResult result = executePlsqlProcedure(buildRequest(org_id, po_number));

		return result.getData();

	}

}
