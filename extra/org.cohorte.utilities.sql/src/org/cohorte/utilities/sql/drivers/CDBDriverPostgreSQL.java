package org.cohorte.utilities.sql.drivers;

import org.cohorte.utilities.sql.IDBDriver;
import org.psem2m.utilities.logging.IActivityLogger;

/**
 *
 *
 * <pre>
 *    jdbc:postgresql:database
 *     jdbc:postgresql://host/database
 *     jdbc:postgresql://host:port/database
 *     jdbc:postgresql://host:port/database?user=userName&password=pass
 *     jdbc:postgresql://host:port/database?charSet=LATIN1&compatible=7.2
 * </pre>
 *
 * @author ogattaz
 *
 * @see https://www.petefreitag.com/articles/jdbc_urls/
 *
 */
public class CDBDriverPostgreSQL extends CDBDriverBase implements IDBDriver {

	private static final int DEFAULT_P0RT = 5432;

	private final static String DRIVER_CLASS_NAME = "org.postgresql.Driver";

	private final static String DRIVER_URL_PROTOCOL = "jdbc:postgresql:";

	private final static String DRIVER_URL_SUFFIX = "";

	/**
	 *
	 */
	public CDBDriverPostgreSQL() {

		this(null);
	}

	/**
	 * @param aLogger
	 */
	protected CDBDriverPostgreSQL(final IActivityLogger aLogger) {
		super(aLogger);
	}

	@Override
	public String getClassName() {
		// TODO Auto-generated method stub
		return DRIVER_CLASS_NAME;
	}

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
		// TODO Auto-generated method stub
		return null;
	}

}
