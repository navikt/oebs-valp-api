package no.nav.oebs.po_ap.db.repository;

import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.Types;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Repository
public class PlsqlProcedureRepository {

	// Generelle parameternavn; behøver ikke å matche hva som brukes i PL/SQL.
	private static final String DATA_IN_PARAM = "data_in";
	private static final String DATA_OUT_PARAM = "data_out";
	private static final String MESSAGE_NO_PARAM = "msg_no";
	private static final String MESSAGE_PARAM = "msg";

	private final JdbcTemplate jdbcTemplate;
	private final ConcurrentMap<String, SimpleJdbcCall> jdbcCallCache = new ConcurrentHashMap<>();

	@Autowired
	public PlsqlProcedureRepository(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.setResultsMapCaseInsensitive(true);
	}

	public PlsqlProcedureResult executeInOutProcedure(String procedureName, String dataIn) {
		PlsqlProcedureResult result;

		validateProcedureName(procedureName);

		SimpleJdbcCall jdbcCall = getJdbcCall(procedureName, //
				new SqlParameter(DATA_IN_PARAM, Types.CLOB), //
				new SqlOutParameter(DATA_OUT_PARAM, Types.CLOB), //
				new SqlOutParameter(MESSAGE_NO_PARAM, Types.NUMERIC), //
				new SqlOutParameter(MESSAGE_PARAM, Types.VARCHAR));

		SqlParameterSource inParams = new MapSqlParameterSource() //
				.addValue(DATA_IN_PARAM, dataIn);

		result = executeProcedure(jdbcCall, inParams);

		return result;

	}

	private void validateProcedureName(String procedureName) {
		if (procedureName.split("\\.").length != 2) {
			throw new IllegalArgumentException(
					"Feil format på PL/SQL-prosedyrenavnet '" + procedureName + "'; skal ha format 'pakkenavn.prosedyrenavn'");
		}
	}

	private SimpleJdbcCall getJdbcCall(String procedureName, SqlParameter... declaredParameters) {
		SimpleJdbcCall jdbcCall = jdbcCallCache.get(procedureName);
		if (jdbcCall == null) {
			String[] tokens = procedureName.split("\\.");

			jdbcCall = new SimpleJdbcCall(jdbcTemplate) //
					.withCatalogName(tokens[0]) //
					.withProcedureName(tokens[1]) //
					.withoutProcedureColumnMetaDataAccess() //
					.declareParameters(declaredParameters);

			jdbcCallCache.put(procedureName, jdbcCall);

			log.debug("Oppretter og cacher SimpleJdbcCall-objekt for '" + procedureName + "'");
		} else {
			log.debug("Gjenbruker cachet SimpleJdbcCall-objekt for '" + procedureName + "'");
		}
		return jdbcCall;
	}

	private PlsqlProcedureResult executeProcedure(SimpleJdbcCall jdbcCall, SqlParameterSource inParams) {
		Map<String, Object> outParams = jdbcCall.execute(inParams);

		Clob dataOut = (Clob) outParams.get(DATA_OUT_PARAM);
		BigDecimal messageNumber = (BigDecimal) outParams.get(MESSAGE_NO_PARAM);
		String message = (String) outParams.get(MESSAGE_PARAM);

		return new PlsqlProcedureResult(dataOut, messageNumber, message);
	}

}
