package no.nav.oebs.po_ap.api.bestillingsinfo.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.oebs.po_ap.api.common.utils.ObjektMaps;
import no.nav.oebs.po_ap.api.bestillingsinfo.v1.model.BestillingsInfoRequest;
import no.nav.oebs.po_ap.db.repository.PlsqlProcedureRepository;
import no.nav.oebs.po_ap.db.repository.PlsqlProcedureResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@Transactional(noRollbackFor = { Exception.class })
public class BestillngsInfoService extends ObjektMaps {

	private static final String PLSQL_PROCEDURE = "xxrtv_po_ap_api_pkg.xxrtv_bestillingsinfo";

	private PlsqlProcedureRepository plsqlProcedureRepository;

	public BestillngsInfoService(PlsqlProcedureRepository plsqlProcedureRepository, ObjectMapper objectMapper) {
		super(objectMapper);
		this.plsqlProcedureRepository = plsqlProcedureRepository;
	}

	/**
	 * Bygger et requestobjekt som skal konverteres til JSON.
	 */
	private BestillingsInfoRequest buildRequest(Integer org_id, String po_number) {
		return BestillingsInfoRequest.builder() //
				.org_id(org_id) //
				.po_number(po_number) //
				.build();
	}

	/**
	 * Kaller PL/SQL-prosedyren som utfører forretningslogikken til operasjonen.
	 * @param request
	 */
	private PlsqlProcedureResult executePlsqlProcedure(BestillingsInfoRequest request) {

		return plsqlProcedureRepository.executeInOutProcedure(PLSQL_PROCEDURE, toJson(request));
	}

	public String finnBestillingsTransaksjoner(Integer org_id, String po_number) {

		PlsqlProcedureResult result = executePlsqlProcedure(buildRequest(org_id, po_number));

		return result.getData();

	}
}
