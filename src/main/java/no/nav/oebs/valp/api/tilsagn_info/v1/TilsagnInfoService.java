package no.nav.oebs.valp.api.tilsagn_info.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.oebs.valp.api.tilsagn.v1.model.TilsagnRequest;
import no.nav.oebs.valp.api.common.utils.ObjektMaps;
import no.nav.oebs.valp.api.tilsagn_info.v1.model.TilsagnInfoRequest;
import no.nav.oebs.valp.db.repository.PlsqlProcedureRepository;
import no.nav.oebs.valp.db.repository.PlsqlProcedureResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@Transactional(noRollbackFor = { Exception.class })
public class TilsagnInfoService extends ObjektMaps {

	private static final String PLSQL_PROCEDURE = "xxrtv_valp_api_pkg.xxrtv_tilsagn_info";

	private PlsqlProcedureRepository plsqlProcedureRepository;

	public TilsagnInfoService(PlsqlProcedureRepository plsqlProcedureRepository, ObjectMapper objectMapper) {
		super(objectMapper);
		this.plsqlProcedureRepository = plsqlProcedureRepository;
	}

	/**
	 * Bygger et requestobjekt som skal konverteres til JSON.
	 */
	private TilsagnInfoRequest buildRequest(Integer org_id, String po_number) {
		return TilsagnInfoRequest.builder() //
				.org_id(org_id) //
				.po_number(po_number) //
				.build();
	}

	/**
	 * Kaller PL/SQL-prosedyren som utfører forretningslogikken til operasjonen.
	 * @param request
	 */
	private PlsqlProcedureResult executePlsqlProcedure(TilsagnInfoRequest request) {

		return plsqlProcedureRepository.executeInOutProcedure(PLSQL_PROCEDURE, toJson(request));
	}

	public String finnTilsagn_transaksjoner(Integer org_id, String po_number) {

		PlsqlProcedureResult result = executePlsqlProcedure(buildRequest(org_id, po_number));

		return result.getData();

	}
}
