package org.cohorte.utilities.sql.drivers;

import org.cohorte.utilities.sql.IDBDriver;
import org.psem2m.utilities.logging.IActivityLogger;

/**
 *
 * <pre>
 * jdbc:mysql://192.168.2.200:3306/agiliumdb?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true
 * jdbc:mysql://localhost:3306/HerongDB?user=Herong&password=TopSecret&useUnicode=true&characterEncoding=UTF-8&autoReconnect=true
 * </pre>
 *
 * <pre>
 * jdbc:mysql://[host][,failoverhost...][:port]/[database]
 * jdbc:mysql://[host][,failoverhost...][:port]/[database][?propertyName1][=propertyValue1][&propertyName2][=propertyValue2]...
 * </pre>
 *
 * @author ogattaz
 *
 * @see https://www.petefreitag.com/articles/jdbc_urls/
 *
 * @see http
 *      ://dev.mysql.com/doc/connector-j/en/connector-j-reference-configuration
 *      -properties.html
 *
 */
public class CDBDriverMySQL extends CDBDriverBase implements IDBDriver {

	private static final int DEFAULT_P0RT = 3306;

	private final static String DRIVER_CLASS_NAME = "com.mysql.jdbc.Driver";

	private final static String DRIVER_URL_PROTOCOL = "jdbc:mysql:";

	private final static String DRIVER_URL_SUFFIX = "&useUnicode=true&characterEncoding=UTF-8&autoReconnect=true";

	/**
	 *
	 */
	public CDBDriverMySQL() {

		this(null);
	}

	/**
	 * @param aLogger
	 * @param aV4On
	 *            the SQLServer version inicator
	 */
	public CDBDriverMySQL(final IActivityLogger aLogger) {

		super(aLogger);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.agilium.ng.commons.sql.IDbDriver#getClassName()
	 */
	@Override
	public String getClassName() {
		return DRIVER_CLASS_NAME;
	}

	/**
	 * <pre>
	 * mysql:
	 * jdbc:mysql://host:port/myDababase?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true
	 * jdbc:mysql://host:port/HerongDB?user=myUser&password=myPass&useUnicode=true&characterEncoding=UTF-8&autoReconnect=true
	 *
	 * </pre>
	 *
	 * @see org.cohorte.utilities.sql.IDBDriver#getConnectUrl(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public String getConnectUrl(final String aHost, final int aPort,
			final String aDbName, final String aUserId, final String aPassword,
			final String aUrlExtend) {

		int wPort = (aPort > 0) ? aPort : DEFAULT_P0RT;

		String wUrlExtend = DRIVER_URL_SUFFIX;
		if (aUrlExtend != null && !aUrlExtend.isEmpty()) {
			wUrlExtend += aUrlExtend;
		}

		return String.format("%s//%s:%d/%s?user=%s&password=%s%s",
				DRIVER_URL_PROTOCOL, aHost, wPort, aDbName, aUserId, aPassword,
				wUrlExtend);
	}

	@Override
	public String getVersion() {

		return null;
	}

	/**
	 * @return
	 */
	public boolean testMySqlDriverAvailable() {
		return testClassAvailability(getClassName());
	}

}
