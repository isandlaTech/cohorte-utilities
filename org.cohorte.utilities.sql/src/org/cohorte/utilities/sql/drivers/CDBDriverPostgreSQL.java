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
		return null;
	}

	@Override
	public String getConnectUrl(final String aHost, final int aPort,
			final String aDbName, final String aUerId, final String aPassword,
			final String aUrlExtend) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getVersion() {
		// TODO Auto-generated method stub
		return null;
	}

}
