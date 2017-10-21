package org.cohorte.utilities.sql.drivers;

import org.cohorte.utilities.sql.IDBDriver;
import org.psem2m.utilities.logging.IActivityLogger;

/**
 *
 * Gestion du driver Microsoft SqlServer V4
 *
 * <pre>
 * jdbc:sqlserver://server[:port];databaseName=CGP;user=sa;password=sqlserver
 * </pre>
 *
 * @see https://www.petefreitag.com/articles/jdbc_urls/
 *
 * @author ogattaz
 *
 */
public class CDBDriverSqlServer extends CDBDriverBase implements IDBDriver {

	private final static String DRIVER_CLASS_NAME_OLD = "com.microsoft.jdbc.sqlserver.SQLServerDriver";

	private final static String DRIVER_CLASS_NAME_V4 = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

	// ex:
	// jdbc:microsoft:sqlserver://192.168.2.200:1433;DatabaseName=CGP,sa,sqlserver
	private final static String DRIVER_URL_PROTOCOL_OLD = "jdbc:microsoft:sqlserver:";
	// ex:
	// jdbc:sqlserver://192.168.2.200:1433;databaseName=CGP;user=sa;password=sqlserver
	private final static String DRIVER_URL_PROTOCOL_V4 = "jdbc:sqlserver:";

	private boolean pV4On;

	/**
	 *
	 */
	public CDBDriverSqlServer() {

		this(null);
	}

	/**
	 * @param aLogger
	 * @param aV4On
	 *            the SQLServer version inicator
	 */
	public CDBDriverSqlServer(final IActivityLogger aLogger) {

		super(aLogger);
		setV4On(CDBDriverFactory.isSqlServerV4On());
	}

	/**
	 * @return
	 */
	@Override
	public String getClassName() {
		return (isV4On()) ? DRIVER_CLASS_NAME_V4 : DRIVER_CLASS_NAME_OLD;
	}

	/**
	 * <pre>
	 * OLD:
	 * jdbc:microsoft:sqlserver://192.168.2.200:1433;DatabaseName=CGP,sa,sqlserver
	 * V4:
	 * jdbc:sqlserver://192.168.2.200:1433;databaseName=CGP;user=sa;password=sqlserver
	 * </pre>
	 *
	 * @see org.cohorte.utilities.sql.IDBDriver#getConnectUrl(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public String getConnectUrl(final String aHost, final int aPort,
			final String aDbName, final String aUerId, final String aPassword,
			final String aUrlExtend) {

		String wScheme = getConnectUrlProtocol();

		if (isV4On()) {
			return String.format(
					"%s//%s:%d;DatabaseName=%s;user=%s;password=%s%s", wScheme,
					aHost, aPort, aDbName, aUerId, aPassword, aUrlExtend);
		}
		//
		else {
			return String.format("%s//%s:%d;DatabaseName=%s,%s,%s", wScheme,
					aHost, aPort, aDbName, aUerId, aPassword);
		}
	}

	/**
	 * @return
	 */
	private String getConnectUrlProtocol() {
		return (isV4On()) ? DRIVER_URL_PROTOCOL_V4 : DRIVER_URL_PROTOCOL_OLD;
	}

	@Override
	public String getVersion() {
		return (isV4On()) ? "" : "";
	}

	/**
	 * @return
	 */
	public boolean isDriverAvailable() {
		return (isV4On()) ? isDriverV4Available() : isDriverOldAvailable();
	}

	/**
	 * @return
	 */
	private boolean isDriverOldAvailable() {
		return testClassAvailability(DRIVER_CLASS_NAME_OLD);
	}

	/**
	 * @return
	 */
	private boolean isDriverV4Available() {
		return testClassAvailability(DRIVER_CLASS_NAME_V4);
	}

	/**
	 * @return
	 */
	public boolean isV4On() {
		return pV4On;
	}

	/**
	 * <pre>
	 * jdbc:sqlserver://server[:port];databaseName=CGP;user=sa;password=sqlserver
	 * jdbc:sqlserver://192.168.2.200:1433;databaseName=CGP;user=sa;password=sqlserver
	 * </pre>
	 *
	 * @param aUrl
	 * @return
	 */
	@Override
	public String retrieveDbName(final String aUrl) {

		final String wDbName = "databaseName=";

		int wPosStart = (aUrl != null) ? aUrl.indexOf(wDbName) : -1;
		if (wPosStart == -1) {
			return "unknown-database-name";
		}
		wPosStart += wDbName.length();
		int wPosEnd = aUrl.indexOf(';', wPosStart);
		if (wPosEnd == -1) {
			return aUrl.substring(wPosStart);
		} else {
			return aUrl.substring(wPosStart, wPosEnd);

		}
	}

	/**
	 * @param aV4On
	 * @return
	 */
	public void setV4On(final boolean aV4On) {

		pV4On = aV4On;
	}
}
