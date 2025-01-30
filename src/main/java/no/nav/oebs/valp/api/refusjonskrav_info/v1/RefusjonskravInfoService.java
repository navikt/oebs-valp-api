package no.nav.oebs.valp.api.refusjonskrav_info.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.oebs.valp.api.refusjonskrav_info.v1.model.RefusjonskravInfoRequest;
import no.nav.oebs.valp.api.common.utils.ObjektMaps;
import no.nav.oebs.valp.db.repository.PlsqlProcedureRepository;
import no.nav.oebs.valp.db.repository.PlsqlProcedureResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(noRollbackFor = { Exception.class })
public class RefusjonskravInfoService extends ObjektMaps {

	private static final String PLSQL_PROCEDURE = "xxrtv_valp_api_pkg.xxrtv_refusjonskrav_info";

	private final PlsqlProcedureRepository plsqlProcedureRepository;

	public RefusjonskravInfoService(PlsqlProcedureRepository plsqlProcedureRepository, ObjectMapper objectMapper) {
		super(objectMapper);
		this.plsqlProcedureRepository = plsqlProcedureRepository;
	}

	private RefusjonskravInfoRequest buildRequest(Integer org_id, String po_number) {
		return RefusjonskravInfoRequest.builder() //
				.org_id(org_id) //
				.po_number(po_number) //
				.build();
	}

	private PlsqlProcedureResult executePlsqlProcedure(RefusjonskravInfoRequest request) {

		return plsqlProcedureRepository.executeInOutProcedure(PLSQL_PROCEDURE, toJson(request));
	}


	public String finnRefusjonskravtransaksjoner(Integer org_id, String po_number) {

		PlsqlProcedureResult result = executePlsqlProcedure(buildRequest(org_id, po_number));

		return result.getData();

	}

}
