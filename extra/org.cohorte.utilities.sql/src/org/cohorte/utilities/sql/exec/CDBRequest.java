package org.cohorte.utilities.sql.exec;

import java.util.HashMap;
import java.util.Map;

import org.cohorte.utilities.sql.DBException;
import org.cohorte.utilities.sql.EManipulationStatement;

/**
 * @see fr.agilium.tools.CJdbcResult
 * @author ogattaz
 *
 */
public class CDBRequest {

	public final static String PARAMS_RETURN_GENERATED_KEYS = "ReturnGeneratedKeys"
			.toLowerCase();

	// to keep compliance with AgiliumV3 parametrization
	public final static String PARAMS_SEPARATOR = "|#|";

	private final boolean pIsCount;
	private final boolean pIsDelete;
	private final boolean pIsInsert;
	private final boolean pIsSelect;
	private final Map<String, String> pParameters;
	private final String pSqlQuery;

	/**
	 * @param aDBStatement
	 * @throws Exception
	 */
	public CDBRequest(final String aDBStatement) throws Exception {
		this(aDBStatement, null);
	}

	/**
	 * @param aDBStatement
	 * @param aOptions
	 * @throws Exception
	 */
	public CDBRequest(final String aDBStatement,
			final Map<String, String> aOptions) throws DBException {
		super();

		if (aDBStatement == null || aDBStatement.isEmpty()) {
			throw new DBException(
					"unable to instanciate a CDBStatement with a null or empty Sql query");
		}

		Map<String, String> wParameters = null;

		/*
		 * to keep compliance with AgiliumV3 parametrization
		 *
		 * first: search parameter after the sparator in the given DBStatement
		 * eg. INSERT INTO table .... |#|ReturnGeneratedKeys=true;param2=val2;
		 */
		final int wPos = aDBStatement.indexOf(PARAMS_SEPARATOR);
		if (wPos > -1) {
			wParameters = decodeParameters(aDBStatement.substring(wPos
					+ PARAMS_SEPARATOR.length()));
			pSqlQuery = aDBStatement.substring(0, wPos);
		} else {
			pSqlQuery = aDBStatement;
		}
		// second: if the given options are not null => add and overwrite
		// already foud parameters
		if (aOptions != null) {
			if (wParameters != null) {
				wParameters.putAll(aOptions);
			} else {
				wParameters = aOptions;
			}
		}

		pParameters = wParameters;

		final String wStr = getCleanedSqlQuery().toUpperCase();
		pIsSelect = wStr.startsWith(EManipulationStatement.SELECT.name());
		pIsDelete = !pIsSelect
				&& wStr.startsWith(EManipulationStatement.DELETE.name());
		pIsInsert = !pIsSelect
				&& wStr.startsWith(EManipulationStatement.INSERT.name());
		pIsCount = pIsSelect
				&& wStr.contains(EManipulationStatement.COUNT.name());
	}

	/**
	 * eg.
	 *
	 * <pre>
	 * ReturnGeneratedKeys = true;
	 * param2 = val2;
	 * </pre>
	 *
	 * @param qSqlQueryParameters
	 */
	private Map<String, String> decodeParameters(
			final String aSqlQueryParameters) {

		if (aSqlQueryParameters == null || aSqlQueryParameters.isEmpty()) {
			return null;
		}

		final Map<String, String> wParameters = new HashMap<String, String>();

		// Split parameters
		// Note: Split never returns null string in the returned arry !
		final String[] pairs = aSqlQueryParameters.split(";");
		for (final String wPair : pairs) {
			// Split id & value
			final String[] wIdValue = wPair.split("=");
			if (wIdValue.length > 1) {
				final String name = wIdValue[0].trim().toLowerCase();
				final String value = wIdValue[1];
				wParameters.put(name, value);
			}
		}
		return wParameters;
	}

	/**
	 * @return
	 */
	private String getCleanedSqlQuery() {

		final String wStr = getSqlQuery();
		final StringBuilder wSB = new StringBuilder();
		final String[] wLines = wStr.split("\\n");
		for (String wLine : wLines) {
			wLine = wLine.trim();
			if (!wLine.isEmpty() && !wLine.startsWith("-- ")) {
				wSB.append(wLine.trim()).append('\n');
			}
		}
		return wSB.toString();
	}

	/**
	 * @return
	 */
	public String getSqlQuery() {
		return pSqlQuery;
	}

	/**
	 * @return
	 */
	public boolean hasParameters() {
		return pParameters != null && pParameters.size() > 0;
	}

	/**
	 * @param aParameterName
	 * @return true if the parameter contains true ( ignoring case)
	 */
	private boolean hasParameterTrue(final String aParameterName) {

		if (!hasParameters()) {
			return false;
		}
		// The boolean returned represents the value true if the string argument
		// is not null and is equal, ignoring case, to the string "true".
		return Boolean.parseBoolean(pParameters.get(aParameterName));
	}

	/**
	 *
	 * @return
	 */
	public boolean isCount() {
		return pIsCount;
	}

	/**
	 * @return
	 */
	public boolean isDelete() {
		return pIsDelete;
	}

	/**
	 * @return
	 */
	public boolean isInsert() {
		return pIsInsert;
	}

	/**
	 * @return
	 */
	public boolean isSelect() {
		return pIsSelect;
	}

	/**
	 * @return
	 */
	public boolean mustReturnGeneratedKeys() {
		return hasParameterTrue(PARAMS_RETURN_GENERATED_KEYS);
	}
}
