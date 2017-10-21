package org.cohorte.utilities.sql.exec;

import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.cohorte.utilities.encode.CBase64Decoder;
import org.cohorte.utilities.encode.IBase64;
import org.cohorte.utilities.sql.EDBType;
import org.cohorte.utilities.sql.IDBDriver;
import org.cohorte.utilities.sql.ISqlConstants;
import org.cohorte.utilities.sql.drivers.CDBDriverFactory;
import org.psem2m.utilities.CXDomUtils;
import org.psem2m.utilities.CXException;
import org.psem2m.utilities.CXStringUtils;
import org.psem2m.utilities.CXTimer;
import org.psem2m.utilities.json.JSONException;
import org.psem2m.utilities.json.JSONObject;
import org.psem2m.utilities.logging.CActivityLoggerNull;
import org.psem2m.utilities.logging.IActivityLogger;
import org.w3c.dom.Element;

/**
 *
 * Descripteur d'une base de données
 *
 *
 *
 * Descripteur sérialisé en XML :
 *
 * <pre>
 *   <dbconnection Status="true" alias="world" duration="14,932">
 *     <dbConnString>jdbc:mysql://localhost:3306/world?useUnicode=true&characterEncoding=UTF-8</dbConnString>
 *     <dbType>mysql</dbType>
 *     <dbName>world</dbName>
 *     <dbUserName>root</dbUserName>
 *     <dbPassword>password</dbPassword>
 *   </dbconnection>
 * </pre>
 *
 * @author ogattaz
 *
 */
public class CDBConnectionInfos {

	private static final String XML_EXCEPTION = "<?xml  version=\"1.0\"  encoding=\"UTF-8\"?><exception><CDATA[[%s]]></exception>";

	private String dbConnDef = null;
	private String dbConnString = null;
	private String dbName = null;
	private String dbPassword = "";
	private EDBType dbType = null;
	private String dbUserName = "";
	private final String pAlias;
	private IActivityLogger pLogger;
	private String pMessage;
	private boolean pOK = false;

	/**
	 * @param aLogger
	 * @param aAlias
	 */
	public CDBConnectionInfos(final IActivityLogger aLogger, final String aAlias) {
		super();
		setLogger(aLogger);
		pAlias = aAlias;
	}

	/**
	 * @param aAlias
	 */
	public CDBConnectionInfos(final String aAlias) {
		this(null, aAlias);
	}

	/**
	 * <pre>
	 * Status=[true]
	 * alias=[world]
	 * dbConnString=[jdbc:mysql://localhost:3306/world?user=admin&password=TopSecret&useUnicode=true&characterEncoding=UTF-8]
	 * dbType=[mysql]
	 * dbName=[world]
	 * dbUserName=[admin]
	 * dbPassword=[TopSecret]
	 * </pre>
	 *
	 * @param aSB
	 * @return
	 */
	protected StringBuilder addDescriptionInSB(final StringBuilder aSB) {

		CXStringUtils.appendKeyValInBuff(aSB, ISqlConstants.ALIAS, pAlias);
		CXStringUtils.appendKeyValInBuff(aSB, ISqlConstants.STATUS,
				String.valueOf(isOk()));
		if (!isOk()) {
			CXStringUtils.appendKeyValInBuff(aSB, ISqlConstants.MESSAGE,
					getMessage());
		}

		CXStringUtils.appendKeyValInBuff(aSB, ISqlConstants.DBCONNSTRING,
				getDbConnString());
		CXStringUtils.appendKeyValInBuff(aSB, ISqlConstants.DBTYPE,
				getDbTypeStr());
		CXStringUtils
				.appendKeyValInBuff(aSB, ISqlConstants.DBNAME, getDbName());
		CXStringUtils.appendKeyValInBuff(aSB, ISqlConstants.DBUSERNAME,
				getDbUserName());
		CXStringUtils.appendKeyValInBuff(aSB, ISqlConstants.DBPASSWORD,
				getDbPassword());

		return aSB;
	}

	/**
	 * @param aParent
	 * @param aElmtName
	 * @param aText
	 */
	private void addOneElement(final Element aParent, final String aElmtName,
			final String aText) {
		final Element wElmt = aParent.getOwnerDocument().createElement(
				aElmtName);
		wElmt.setTextContent(aText);
		aParent.appendChild(wElmt);
	}

	/**
	 * @return
	 */
	public String asJsonStream() {
		return toStream(ISqlConstants.FORMAT_JSON);

	}

	/**
	 * @return
	 */
	private String buildJsonOutput(final CXTimer aTimer) {

		// TODO
		return buildXmlOutput(aTimer);
	}

	/**
	 * <pre>
	 *   <dbconnection Status="true" alias="world" duration="14,932">
	 *     <dbConnString>jdbc:mysql://localhost:3306/world?user=admin&password=TopSecret&useUnicode=true&characterEncoding=UTF-8</dbConnString>
	 *     <dbType>mysql</dbType>
	 *     <dbName>world</dbName>
	 *     <dbUserName>admin</dbUserName>
	 *     <dbPassword>TopSecret</dbPassword>
	 *   </dbconnection>
	 * </pre>
	 *
	 * @return
	 */
	private String buildXmlOutput(final CXTimer aTimer) {

		try {
			final CXDomUtils wDomUtils = new CXDomUtils();
			final Element wRootElmt = wDomUtils.setRootElmt(wDomUtils
					.createElement(ISqlConstants.DBCONNECTION));

			addOneElement(wRootElmt, ISqlConstants.DBCONNSTRING,
					getDbConnString());
			addOneElement(wRootElmt, ISqlConstants.DBTYPE, getDbTypeStr());
			addOneElement(wRootElmt, ISqlConstants.DBNAME, getDbName());
			addOneElement(wRootElmt, ISqlConstants.DBUSERNAME, getDbUserName());
			addOneElement(wRootElmt, ISqlConstants.DBPASSWORD, getDbPassword());

			wRootElmt.setAttribute(ISqlConstants.ALIAS, pAlias);
			wRootElmt
					.setAttribute(ISqlConstants.STATUS, String.valueOf(isOk()));
			if (!isOk()) {
				wRootElmt.setAttribute(ISqlConstants.MESSAGE, getMessage());
			}
			if (aTimer != null) {
				wRootElmt.setAttribute(ISqlConstants.DURATION,
						aTimer.getDurationStrMilliSec());
			}
			// avec indantation de 2
			return wDomUtils.toXml(2);
		} catch (final Exception e) {
			return String.format(XML_EXCEPTION, CXException.eInString(e, '\n'));
		}
	}

	/**
	 * @return the Agilium alias of the database
	 */
	public String getDbAlias() {
		return pAlias;
	}

	/**
	 * Returns four informations separated by comma :
	 *
	 * <pre>
	 * mysql,jdbc:mysql://localhost:3306/world?useUnicode=true&characterEncoding=UTF-8,root,password
	 * </pre>
	 *
	 * @return the dbConnDef
	 */
	public String getDbConnDef() {
		return dbConnDef;
	}

	/**
	 * Returns the jdbc connection string
	 *
	 * <pre>
	 * jdbc:mysql://localhost:3306/world?useUnicode=true&characterEncoding=UTF-8
	 * </pre>
	 *
	 * @return the dbConnString
	 */
	public String getDbConnString() {
		return dbConnString;
	}

	/**
	 * @return the dbName eg. "world"
	 */
	public String getDbName() {
		return dbName;
	}

	/**
	 * @return the dbPassword
	 */
	public String getDbPassword() {
		return dbPassword;
	}

	public EDBType getDbType() {
		return dbType;
	}

	/**
	 * @return the dbType
	 */
	public String getDbTypeStr() {
		return getDbType().name().toLowerCase();
	}

	/**
	 * @return the dbUserName
	 */
	public String getDbUserName() {
		return dbUserName;
	}

	/**
	 * @return
	 */
	public String getMessage() {
		return (pMessage != null) ? pMessage : "";
	}

	/**
	 * @return
	 */
	public boolean isMySql() {
		return isOk() && dbType.isMySql();
	}

	/**
	 * @return the ok
	 */
	public boolean isOk() {
		return pOK;
	}

	/**
	 * @return
	 */
	public boolean isPostgreSql() {
		return isOk() && dbType.isPostgreSql();
	}

	/**
	 * @return
	 */
	public boolean isSqlServer() {
		return isOk() && dbType.isSqlServer();
	}

	/**
	 * Search an entry where the key ends by the given suffix
	 *
	 * @param aProperties
	 * @param aKeySuffix
	 * @return
	 */
	private String searchValueInProperties(
			final Map<? extends Object, ? extends Object> aProperties,
			final String aKeySuffix) {

		if (aKeySuffix != null && !aKeySuffix.isEmpty()) {

			for (final Entry<? extends Object, ? extends Object> wEntry : aProperties
					.entrySet()) {

				if (wEntry.getKey().toString().toLowerCase()
						.endsWith(aKeySuffix.toLowerCase())) {
					return wEntry.getValue().toString();
				}
			}
		}
		return null;
	}

	/**
	 * initialization using a ConnectionDef
	 *
	 * <pre>
	 * mysql,jdbc:mysql://server:port/database?user=Herong&password=TopSecret&useUnicode=true&characterEncoding=UTF-8,myUser,myPass
	 * sqlserver,jdbc:sqlserver://server:port;databaseName=CGP;user=myUser;password=myPass,myUser,myPass
	 * postgresql,jdbc:postgresql://host:port/database?user=userName&password=pass&postgres&charSet=LATIN1&compatible=7.2,myUser,myPass
	 * </pre>
	 *
	 * Four parts:
	 * <ul>
	 * <li>DbType
	 * <li>DbConnString
	 * <li>DbUserName
	 * <li>DbPassword
	 * </ul>
	 *
	 * @param dbConnDef
	 * @throws Exception
	 */
	public void setbDbConnDef(final String dbConnDef) throws Exception {

		try {
			this.dbConnDef = dbConnDef;

			final StringTokenizer st = new StringTokenizer(getDbConnDef(), ",");

			setDbTypeStr(st.nextToken());

			// get the driver corresponding to the type
			final IDBDriver wDBDriver = CDBDriverFactory.newDBDriver(pLogger,
					getDbType());

			if (!st.hasMoreTokens()) {
				throw new Exception("no DbConnString in the definition");
			}
			setDbConnString(st.nextToken());

			// uses the driver to build the URL
			setDbName(wDBDriver.retrieveDbName(getDbConnString()));

			if (!st.hasMoreTokens()) {
				throw new Exception("no DbUserName in the definition");
			}
			setDbUserName(st.nextToken());

			if (!st.hasMoreTokens()) {
				throw new Exception("no DbPassword in the definition");
			}
			setDbPassword(st.nextToken());

			setOk(true);
		} catch (final Exception e) {
			final String wMessage = String.format(
					"ERROR during initialisation : %s",
					CXException.eUserMessagesInString(e));
			setNotOk(wMessage);
			pLogger.logSevere(this, "setbDbConnDef", wMessage);
			throw new Exception(wMessage, e);
		}

	}

	/**
	 * @param aXml
	 * @throws Exception
	 * @deprecated
	 */
	@Deprecated
	public void setbDbInfos(final String aXml) throws Exception {

		setbDbInfosXml(aXml);
	}

	/**
	 * @param aTDbType
	 * @param dbName
	 * @param aHost
	 * @param aPort
	 * @param dbUserName
	 * @param dbPassword
	 * @param aSpecificDbStringAdd
	 * @throws Exception
	 */
	public void setbDbInfos(final String aTDbType, final String aDbName,
			final String aHost, final int aPort, final String aUserId,
			final String aPassword, final String aUrlExtend) throws Exception {

		try {
			if (aTDbType == null || aTDbType.isEmpty()) {
				throw new Exception("the passed DbType is not null or empty");
			}
			setDbTypeStr(aTDbType);

			// get the driver corresponding to the type
			final IDBDriver wDBDriver = CDBDriverFactory.newDBDriver(pLogger,
					getDbType());

			if (aHost == null || aHost.isEmpty()) {
				throw new Exception("the passed Host is null or empty");
			}
			if (aDbName == null) {
				throw new Exception("the passed DbName is null");
			}
			if (aUserId == null || aUserId.isEmpty()) {
				throw new Exception("the passed DbUserId is not null or empty");
			}
			if (aPassword == null || aPassword.isEmpty()) {
				throw new Exception(
						"the passed DbPassword is not null or empty");
			}
			// uses the driver to build the URL
			setDbConnString(wDBDriver.getConnectUrl(aHost, aPort, aDbName,
					aUserId, aPassword, aUrlExtend));
			setDbName(aDbName);
			setDbUserName(aUserId);
			setDbPassword(aPassword);
			setOk(true);

		} catch (final Exception e) {
			final String wMessage = String.format(
					"ERROR during initialisation : %s",
					CXException.eUserMessagesInString(e));
			setNotOk(wMessage);
			pLogger.logSevere(this, "setbDbInfos", wMessage);
			throw new Exception(wMessage, e);
		}

	}

	/**
	 * <pre>
	 * {
	 * 	"dbType":"mysql",
	 * 	"dbName":"world",
	 * 	"dbHost":"localhost",
	 * 	"dbPort":3306,
	 * 	"dbUserName":"root",
	 * 	"dbPassword":"password",
	 *  ""
	 * }
	 * </pre>
	 *
	 * @param aJson
	 * @throws Exception
	 */
	public void setbDbInfosJson(final JSONObject aJson) throws Exception {

		try {

			if (!aJson.has(ISqlConstants.DBTYPE)) {
				throw new Exception(
						"The passed Json object must have a DbType string property");
			}

			final String wDbType = aJson.getString(ISqlConstants.DBTYPE);

			final String wDBName = aJson.getString(ISqlConstants.DBNAME);
			final String wDBHost = aJson.getString(ISqlConstants.DBHOST);
			final int wDBPort = aJson.getInt(ISqlConstants.DBPORT);
			final String wUsername = aJson.getString(ISqlConstants.DBUSERNAME);
			final String wPassword = aJson.getString(ISqlConstants.DBPASSWORD);
			// optional
			final String wdbUrlExtend = aJson
					.optString(ISqlConstants.DBURLEXTENDS);

			setbDbInfos(wDbType, wDBName, wDBHost, new Integer(wDBPort),
					wUsername, wPassword, wdbUrlExtend);

		} catch (final Exception e) {
			final String wMessage = String
					.format("ERROR during initialisation : Unable to read the given Json object. %s",
							CXException.eUserMessagesInString(e));
			setNotOk(wMessage);
			pLogger.logSevere(this, "setbDbInfosJson", wMessage);
			throw new Exception(wMessage, e);
		}
	}

	/**
	 * @param aJson
	 * @throws Exception
	 */
	public void setbDbInfosJson(final String aJson) throws Exception {

		try {
			setbDbInfosJson(new JSONObject(aJson));

		} catch (final JSONException e) {
			final String wMessage = String
					.format("ERROR during initialisation : Unable to parse the given Json flow. %s. %s",
							CXException.eUserMessagesInString(e), aJson);
			setNotOk(wMessage);
			pLogger.logSevere(this, "setbDbInfosJson", wMessage);
			throw new Exception(wMessage, e);
		}
	}

	/**
	 * @param aProperties
	 * @throws Exception
	 */
	public void setbDbInfosProperties(
			final Map<? extends Object, ? extends Object> aProperties)
			throws Exception {

		try {

			final String wDbType = searchValueInProperties(aProperties,
					ISqlConstants.DBTYPE);

			if (wDbType == null || wDbType.isEmpty()) {
				throw new Exception(
						"The given properties must have an entry where the key is ended by  'DbType' ");
			}

			final String wDBName = searchValueInProperties(aProperties,
					ISqlConstants.DBNAME);

			final String wDBHost = searchValueInProperties(aProperties,
					ISqlConstants.DBHOST);

			final String wDBPort = searchValueInProperties(aProperties,
					ISqlConstants.DBPORT);

			final String wUsername = searchValueInProperties(aProperties,
					ISqlConstants.DBUSERNAME);

			final String wPassword = searchValueInProperties(aProperties,
					ISqlConstants.DBPASSWORD);

			// optional
			final String wdbUrlExtend = searchValueInProperties(aProperties,
					ISqlConstants.DBURLEXTENDS);

			setbDbInfos(wDbType, wDBName, wDBHost, new Integer(wDBPort),
					wUsername, wPassword, wdbUrlExtend);

		} catch (final Exception e) {
			final String wMessage = String
					.format("ERROR during initialisation : Unable to read the given properties object. %s",
							CXException.eUserMessagesInString(e));
			setNotOk(wMessage);
			pLogger.logSevere(this, "setbDbInfosProperties", wMessage);
			throw new Exception(wMessage, e);
		}

	}

	/**
	 *
	 * <pre>
	 *   <dbconnection>
	 *     <dbType>mysql</dbType>
	 *     <dbName>world</dbName>
	 *     <dbHost>localhost</dbHost>
	 *     <dbPort>3306</dbPort>
	 *     <dbUserName>root</dbUserName>
	 *     <dbUserPassword>password</dbUserPassword>
	 *   </dbconnection>
	 * </pre>
	 *
	 *
	 * @param aXml
	 * @throws Exception
	 */
	public void setbDbInfosXml(final String aXml) throws Exception {

		try {
			final CXDomUtils wDomUtils = new CXDomUtils(aXml);

			final Element wDbTypeElmt = wDomUtils
					.getFirstChildElmtByTag(ISqlConstants.DBTYPE);

			if (wDbTypeElmt == null) {
				throw new Exception(
						"The passed xml flow must have a DbType element");
			}

			final String wDbType = wDbTypeElmt.getTextContent();

			final String wDBName = wDomUtils.getFirstChildElmtByTag(
					ISqlConstants.DBNAME).getTextContent();

			final String wDBHost = wDomUtils.getFirstChildElmtByTag(
					ISqlConstants.DBHOST).getTextContent();

			final String wDBPort = wDomUtils.getFirstChildElmtByTag(
					ISqlConstants.DBPORT).getTextContent();

			final String wUsername = wDomUtils.getFirstChildElmtByTag(
					ISqlConstants.DBUSERNAME).getTextContent();
			final String wPassword = wDomUtils.getFirstChildElmtByTag(
					ISqlConstants.DBPASSWORD).getTextContent();

			// optional
			final Element wDbUrlExtendElmt = wDomUtils
					.getFirstChildElmtByTag(ISqlConstants.DBURLEXTENDS);

			final String wdbUrlExtend = (wDbUrlExtendElmt != null) ? wDbUrlExtendElmt
					.getTextContent() : null;

			setbDbInfos(wDbType, wDBName, wDBHost, new Integer(wDBPort),
					wUsername, wPassword, wdbUrlExtend);

		} catch (final Exception e) {
			final String wMessage = String
					.format("ERROR during initialisation : Unable to parse the input xml flow. %s",
							CXException.eUserMessagesInString(e));
			setNotOk(wMessage);
			pLogger.logSevere(this, "setbDbInfosXml", wMessage);
			throw new Exception(wMessage, e);
		}
	}

	/**
	 * @param dbConnString
	 *            the dbConnString to set
	 */
	private void setDbConnString(final String dbConnString) {
		this.dbConnString = dbConnString;

	}

	/**
	 * @param dbName
	 *            the dbName to set
	 */
	private void setDbName(final String dbName) {
		this.dbName = dbName;
	}

	/**
	 * @param dbPassword
	 *            the dbPassword to set
	 */
	private void setDbPassword(final String dbPassword) {
		String wDecoded = dbPassword;

		// eg. basic:bXVpbGlnQQ==
		if (dbPassword != null
				&& !dbPassword.isEmpty()
				&& (dbPassword.startsWith(IBase64.BASIC_PREFIX) || dbPassword
						.startsWith(IBase64.BASE64_PREFIX))) {
			wDecoded = new CBase64Decoder(dbPassword).getString();
		}
		this.dbPassword = wDecoded;
	}

	/**
	 * @param dbType
	 *            the dbType to set
	 */
	private void setDbType(final EDBType dbType) {
		this.dbType = dbType;
	}

	/**
	 * @param aType
	 * @throws Exception
	 */
	private void setDbTypeStr(final String aType) throws Exception {
		final EDBType wType = EDBType.fromName(aType);
		if (wType == null) {
			throw new Exception(String.format(
					"Unable to convert [%s] to a supported DBType", aType));
		}
		setDbType(wType);
	}

	/**
	 * @param dbUserName
	 *            the dbUserName to set
	 */
	private void setDbUserName(final String dbUserName) {
		this.dbUserName = dbUserName;
	}

	/**
	 * @param aLogger
	 * @return the logger
	 */
	private void setLogger(final IActivityLogger aLogger) {
		pLogger = (aLogger != null) ? aLogger : CActivityLoggerNull
				.getInstance();
	}

	/**
	 * @param pOK
	 *            the ok to set
	 */
	private void setNotOk(final String aMessage) {
		setOk(false);
		pMessage = aMessage;
	}

	/**
	 * @param ok
	 *            the ok to set
	 */
	private void setOk(final boolean ok) {
		this.pOK = ok;
	}

	/**
	 * @param aFormat
	 * @return
	 */
	public String toStream(final String aFormat) {
		return toStream(aFormat, null);
	}

	/**
	 * @param aFormat
	 * @param aTimer
	 * @return
	 */

	public String toStream(final String aFormat, final CXTimer aTimer) {

		if (ISqlConstants.FORMAT_JSON.equalsIgnoreCase(aFormat)) {
			return buildJsonOutput(aTimer);
		} else {
			return buildXmlOutput(aTimer);
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
	 * @return
	 */
	public String toXmlStream() {
		return toStream(ISqlConstants.FORMAT_XML);
	}

}