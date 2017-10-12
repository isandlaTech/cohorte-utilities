package org.cohorte.utilities.sql.exec;

import java.util.ArrayList;
import java.util.List;

import org.cohorte.utilities.sql.EDBResultPart;
import org.cohorte.utilities.sql.ISqlConstants;
import org.psem2m.utilities.CXException;
import org.psem2m.utilities.CXTimer;
import org.psem2m.utilities.json.JSONArray;
import org.psem2m.utilities.json.JSONException;
import org.psem2m.utilities.json.JSONObject;

/**
 *
 * @see fr.agilium.tools.CJdbcResult
 *
 * @author ogattaz
 *
 */
public class CDBResult {

	private static final String KO = "KO";

	private static final String OK = "OK";

	/**
	 * @param aFormat
	 * @param aArgs
	 * @return
	 */
	public static CDBResult newDBResultKO(final String aFormat,
			final Object... aArgs) {

		final CDBResult wCDBResult = new CDBResult(null);
		wCDBResult.setMessageKO(aFormat, aArgs);
		return wCDBResult;
	}

	/**
	 * @param aFormat
	 * @param aArgs
	 * @return
	 */
	public static CDBResult newDBResultOK(final String aFormat,
			final Object... aArgs) {

		final CDBResult wCDBResult = new CDBResult(null);
		wCDBResult.setMessage(aFormat, aArgs);
		return wCDBResult;
	}

	int pDataManipulationCount = 0;
	private final CDBRequest pDBQuery;
	private String pDuration = "";
	List<String> pGeneratedKeysList = null;
	String pMessage = null;
	List<String[]> pResultTable = null;
	String pStatus = OK;

	/**
	 * @param aCSqlQuery
	 */
	public CDBResult(final CDBRequest aDBQuery) {
		super();
		pDBQuery = aDBQuery;
	}

	/**
	 * @param aSB
	 * @return
	 */
	public StringBuilder addDescriptionInSB(final StringBuilder wSB) {
		return addDescriptionInSB(wSB, EDBResultPart.MESSAGE);
	}

	/**
	 * @param wSB
	 * @param aResultParts
	 * @return
	 */
	public StringBuilder addDescriptionInSB(final StringBuilder wSB,
			final EDBResultPart... aResultParts) {

		wSB.append(String.format("isOK=[%b]", isOK()));
		if (!isOK()) {
			wSB.append(String.format(" jdbcmessage=[%s]", getMessage()));
		} else {
			if (hasMessage()
					&& EDBResultPart.wantPart(aResultParts, EDBResultPart.MESSAGE)) {
				wSB.append(String.format(" jdbcmessage=[%s]", getMessage()));
			}
			if (hasDataManipulationCount()) {
				wSB.append(String.format(" DataManipulationCount=[%d]",
						getDataManipulationCount()));
			}
			if (!isResultTableEmpty()
					&& EDBResultPart.wantPart(aResultParts, EDBResultPart.DATA)) {
				appendTableValuesinSB(wSB, getResultTable());
			}
			if (!isGeneratedKeysListEmpty()) {
				appendGeneratedKeysListinSB(wSB, getGeneratedKeysList());
			}
		}
		return wSB;
	}

	/**
	 * MOD_27 - Amélioration du handler "sqlquery" du httpsdk appelé par
	 * "Lister.getSqlData()"
	 *
	 * @param aSB
	 * @param aGeneratedKeysList
	 */
	private void appendGeneratedKeysListinSB(final StringBuilder aSB,
			final List<String> aGeneratedKeysList) {

		aSB.append(" {");
		for (final String wKey : aGeneratedKeysList) {
			aSB.append(String.format("generatedkey=[%s],",
					CXXmlUtils.escapeXml(wKey)));
		}
		aSB.append('}');
	}

	/**
	 * <pre>
	 * <GeneratedKeys>
	 * <GeneratedKey>15</GeneratedKey>
	 * </GeneratedKeys>
	 * </pre>
	 *
	 * @param aSB
	 * @param aGeneratedKeysList
	 */
	private void appendGeneratedKeysListInXML(final StringBuilder aSB,
			final List<String> aGeneratedKeysList) {

		aSB.append("<generatedkeys>");
		for (final String wKey : aGeneratedKeysList) {
			aSB.append(String.format("<generatedkey>%s</generatedkey>,",
					CXXmlUtils.escapeXml(wKey)));
		}
		aSB.append("</generatedkeys>");
	}

	/**
	 * @param aSB
	 * @param aResultTable
	 */
	private void appendResultTableValuesInXML(final StringBuilder aSB,
			final List<String[]> aResultTable) {

		final int wMaxLine = aResultTable.size();
		final String[] cols = aResultTable.get(0);
		for (int i = 1; i < wMaxLine; i++) {
			aSB.append("<values>");
			final String[] line = aResultTable.get(i);
			for (int j = 0; j < line.length; j++) {
				aSB.append('<');
				aSB.append(cols[j]);
				aSB.append('>');
				aSB.append(CXXmlUtils.escapeXml(line[j]));
				aSB.append("</");
				aSB.append(cols[j]);
				aSB.append('>');
			}
			aSB.append("</values>");
		}
	}

	/**
	 * MOD_27 - Amélioration du handler "sqlquery" du httpsdk appelé par
	 * "Lister.getSqlData()"
	 *
	 * @param aSB
	 * @param aResultTable
	 */
	private void appendTableValuesinSB(final StringBuilder aSB,
			final List<String[]> aResultTable) {
		aSB.append(" {");
		final int wMaxLine = aResultTable.size();
		final String[] cols = aResultTable.get(0);
		for (int i = 1; i < wMaxLine; i++) {
			aSB.append('<');
			final String[] line = aResultTable.get(i);
			for (int j = 0; j < line.length; j++) {

				aSB.append(String.format("[%s]=[%s],", cols[j], line[j]));
			}
			aSB.append(')');
		}
		aSB.append('}');
	}

	private JSONArray generatedKeysListToJson(
			final List<String> aGeneratedKeysList) {

		final JSONArray wArray = new JSONArray();
		for (final String wKey : aGeneratedKeysList) {
			wArray.put(wKey);

		}
		return wArray;
	}

	/**
	 * @return
	 */
	public int getDataManipulationCount() {
		return pDataManipulationCount;
	}

	/**
	 * @return
	 */
	public CDBRequest getDBQuery() {
		return pDBQuery;
	}

	/**
	 * @return
	 */
	public String getDuration() {
		return pDuration;
	}

	/**
	 * @param aFormat
	 * @return
	 */
	public Object getFormatedResult(final String aFormat) {

		if (ISqlConstants.FORMAT_XML.equalsIgnoreCase(aFormat)) {
			return toXmlStream();
		}

		if (ISqlConstants.FORMAT_JSON.equalsIgnoreCase(aFormat)) {
			return toXmlStream();
		}

		return this;
	}

	/**
	 * @return
	 */
	public List<String> getGeneratedKeysList() {
		return pGeneratedKeysList;
	}

	/**
	 * @return
	 */
	public String getMessage() {
		return pMessage;
	}

	/**
	 * @return
	 */
	public List<String[]> getResultTable() {
		return pResultTable;
	}

	/**
	 * @return la taille de la table moins la ligne des titres
	 */
	public int getSelectCount() {
		return (pResultTable != null) ? pResultTable.size() - 1 : -1;
	}

	/**
	 * @return
	 */
	public String getStatus() {
		return pStatus;
	}

	/**
	 * @return
	 */
	public boolean hasDataManipulationCount() {
		return getDataManipulationCount() > 0;
	}

	/**
	 * @return
	 */
	public boolean hasGeneratedKeysList() {
		return pGeneratedKeysList != null;
	}

	/**
	 * @return
	 */
	public boolean hasMessage() {
		return pMessage != null && !pMessage.isEmpty();
	}

	/**
	 * @return
	 */
	public boolean hasResultTable() {
		return pResultTable != null;
	}

	/**
	 * @return
	 */
	public boolean isGeneratedKeysListEmpty() {
		return !hasGeneratedKeysList() || pGeneratedKeysList.size() < 1;
	}

	/**
	 * @return
	 */
	public boolean isOK() {
		return OK.equals(pStatus);
	}

	/**
	 * @return
	 */
	public boolean isResultTableEmpty() {
		return !hasResultTable() || pResultTable.size() < 2;
	}

	/**
	 * @return
	 */
	List<String> newGeneratedKeysList() {
		pGeneratedKeysList = new ArrayList<String>();
		return getGeneratedKeysList();
	}

	/**
	 * MOD_44 public pour une utilisation dans ObjectStorageManager
	 *
	 * @return
	 */
	public List<String[]> newResultTable() {
		pResultTable = new ArrayList<String[]>();
		return getResultTable();
	}

	/**
	 * @param aResultTable
	 * @return
	 */
	private JSONArray resultTableValuesToJson(final List<String[]> aResultTable) {
		final JSONArray wArray = new JSONArray();

		final int wMaxLine = aResultTable.size();
		final String[] cols = aResultTable.get(0);

		JSONObject wLineObj;
		for (int i = 1; i < wMaxLine; i++) {
			wLineObj = new JSONObject();

			final String[] line = aResultTable.get(i);
			for (int j = 0; j < line.length; j++) {

				wLineObj.put(cols[j], line[j]);
			}
			wArray.put(wLineObj);
		}
		return wArray;
	}

	/**
	 * MOD_44 public pour une utilisation dans ObjectStorageManager
	 *
	 * @param aDataManipulationCount
	 */
	public void setDataManipulationCount(final int aDataManipulationCount) {
		pDataManipulationCount = aDataManipulationCount;
	}

	/**
	 * @param aTimer
	 *            a started timer
	 */
	public void setDuration(final CXTimer aTimer) {
		setDuration(aTimer.getDurationStrMicroSec());
	}

	/**
	 * @param aDuration
	 *            a formated string ("%6.3f") containing the duration in in
	 *            milliseconds with microesconds
	 */
	public void setDuration(final String aDuration) {
		pDuration = aDuration;
	}

	/**
	 * MOD_44 public pour une utilisation dans ObjectStorageManager
	 *
	 * @param aFormat
	 * @param aArgs
	 */
	public void setMessage(final String aFormat, final Object... aArgs) {
		pMessage = String.format(aFormat, aArgs);
	}

	/**
	 * MOD_44 public pour une utilisation dans ObjectStorageManager
	 *
	 * @param aFormat
	 * @param aArgs
	 */
	public void setMessageKO(final String aFormat, final Object... aArgs) {
		setStatusKO();
		setMessage(aFormat, aArgs);
	}

	/**
	 * MOD_44 public pour une utilisation dans ObjectStorageManager
	 */
	public void setStatusKO() {
		pStatus = KO;
	}

	/**
	 * @param aResultParts
	 * @return the json object witch represent the result according the wanted
	 *         parts
	 */
	public JSONObject toJson(final EDBResultPart... aResultParts) {

		final JSONObject wResultObj = new JSONObject();

		wResultObj.put("status", getStatus());
		wResultObj.put("queryduration", getDuration());
		try {
			if (getDBQuery().isSelect()) {
				wResultObj.put("selectcount", getSelectCount());

			}

			if (!isOK()) {
				wResultObj.put("jdbcmessage", getMessage());

			} else {
				if (hasMessage()
						&& EDBResultPart.wantPart(aResultParts,
								EDBResultPart.MESSAGE)) {
					wResultObj.put("jdbcmessage", getMessage());

				}
				if (hasDataManipulationCount()) {
					wResultObj.put("datamanipulationcount",
							getDataManipulationCount());

				}
				if (!isResultTableEmpty()
						&& EDBResultPart.wantPart(aResultParts, EDBResultPart.DATA)) {
					wResultObj.put("values",
							resultTableValuesToJson(getResultTable()));
				}
				if (!isGeneratedKeysListEmpty()) {
					wResultObj.put("generatedkeys",
							generatedKeysListToJson(getGeneratedKeysList()));
				}
			}
		} catch (final JSONException e) {
			wResultObj.put("ERROR", CXException.eUserMessagesInString(e));
		}
		return wResultObj;
	}

	/**
	 * @return the json stream witch represent the result
	 */
	public String toJsonStream() {

		return toJsonStream(0);
	}

	/**
	 * @param aIndentation
	 *            the nb spac charater used to indent the stream
	 * @return the json stream witch represent the result
	 */
	public String toJsonStream(final int aIndentation) {

		return toJsonStream(aIndentation, EDBResultPart.MESSAGE);
	}

	/**
	 * @param aIndentation
	 *            the nb spac charater used to indent the stream
	 * @param aResultParts
	 *            the list of parts
	 * @return the json stream witch represent the result according the wanted
	 *         parts
	 */
	public String toJsonStream(final int aIndentation,
			final EDBResultPart... aResultParts) {

		try {
			return toJson(aResultParts).toString(aIndentation);
		} catch (final JSONException e) {
			return "ERROR: " + CXException.eUserMessagesInString(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return addDescriptionInSB(new StringBuilder()).toString();
	}

	/**
	 * @param aDescriptionInfos
	 *            the infos we want to get in the description
	 * @return the description
	 */
	public String toString(final EDBResultPart... aDescriptionInfos) {
		return addDescriptionInSB(new StringBuilder(), aDescriptionInfos)
				.toString();
	}

	/**
	 *
	 *
	 *
	 * @return
	 */
	public String toXmlStream() {
		return toXmlStream(EDBResultPart.MESSAGE);
	}

	/**
	 * Les n noeuds "Valeurs" contiennent des n records résultat d'une
	 * sélection. Exemple pour la requète SELECT * FROM world.acteurs WHERE
	 * name_acteur = 'goat'
	 *
	 * <pre>
	 * <Result status="OK" queryduration="23,607">
	 * <Valeurs>
	 * <ID_acteur>1</ID_acteur>
	 * <name_acteur>ogat</name_acteur>
	 * </Valeurs>
	 * <Valeurs>
	 * <ID_acteur>15</ID_acteur>
	 * <name_acteur>ogat</name_acteur>
	 * </Valeurs>
	 * </Result>
	 * </pre>
	 *
	 * Le noeuds "GeneratedKeys" contient des n GeneratedKey résultat d'une
	 * insertion. Exemple pour la requète INSER INTO world.acteurs (name_acteur)
	 * VALUES ('olivier')|#|ReturnGeneratedKeys=true;
	 *
	 * <pre>
	 * <Result status="OK" queryduration="27,356">
	 * <DataManipulationCount>1</DataManipulationCount>
	 * <GeneratedKeys>
	 * <GeneratedKey>15</GeneratedKey>
	 * </GeneratedKeys>
	 * </Result>
	 * </pre>
	 *
	 * Lorsque l'attribut "status" vaut "KO", le noeud "Result" contient un
	 * noeud "jdbcmessage" Exemple:
	 *
	 * <pre>
	 * <Result status="KO" queryduration="28,382">
	 * <jdbcmessage>
	 * jdbcConnector error (execSql : [SELECT * FROM world.acteursxxx]) : Table 'world.acteursxxx' doesn't exist
	 * </jdbcmessage>
	 * </Result>
	 * </pre>
	 *
	 * Suite à un INSERT, UPDATE, DELETE, le noeud "Result" contient un noeud
	 * "DataManipulationCount" Exemple pour la requète UPDATE world.acteurs SET
	 * name_acteur = 'ogat' WHERE ID_acteur = 15
	 *
	 * <pre>
	 * <Result status="OK" queryduration="24,564">
	 * <DataManipulationCount>1</DataManipulationCount>
	 * </Result>
	 * </pre>
	 *
	 * @param aResultParts
	 * @return
	 */
	public String toXmlStream(final EDBResultPart... aResultParts) {
		final StringBuilder wSB = new StringBuilder();
		wSB.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		// Result element
		wSB.append(String.format("<result status=\"%s\" queryduration=\"%s\"",
				getStatus(), getDuration()));
		if (getDBQuery().isSelect()) {
			wSB.append(String.format(" selectcount=\"%d\"", getSelectCount()));
		}
		wSB.append('>');

		if (!isOK()) {
			wSB.append(String.format("<jdbcmessage>%s</jdbcmessage>",
					CXXmlUtils.escapeXml(getMessage())));
		} else {
			if (hasMessage()
					&& EDBResultPart.wantPart(aResultParts, EDBResultPart.MESSAGE)) {
				wSB.append(String.format("<jdbcmessage>%s</jdbcmessage>",
						CXXmlUtils.escapeXml(getMessage())));
			}
			if (hasDataManipulationCount()) {
				wSB.append(String.format(
						"<datamanipulationcount>%d</datamanipulationcount>",
						getDataManipulationCount()));
			}
			if (!isResultTableEmpty()
					&& EDBResultPart.wantPart(aResultParts, EDBResultPart.DATA)) {
				appendResultTableValuesInXML(wSB, getResultTable());
			}
			if (!isGeneratedKeysListEmpty()) {
				appendGeneratedKeysListInXML(wSB, getGeneratedKeysList());
			}
		}
		wSB.append("</result>");
		return wSB.toString();
	}

}
