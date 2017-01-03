package org.cohorte.utilities.sql.drivers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.psem2m.utilities.CXException;
import org.psem2m.utilities.logging.CActivityLoggerNull;
import org.psem2m.utilities.logging.IActivityLogger;

/**
 * @author ogattaz
 *
 */
public abstract class CDBDriverBase {

	private IActivityLogger pLogger;

	/**
	 * @param aLogger
	 */
	protected CDBDriverBase(final IActivityLogger aLogger) {
		super();
		setLogger(aLogger);
	}

	/**
	 * @return
	 */
	public abstract String getClassName();

	/**
	 * @param aHostNamePort
	 * @param aDbName
	 * @param aUerId
	 * @param aPassword
	 * @return
	 */
	public abstract String getConnectUrl(final String aHost, final int aPort,
			final String aDbName, final String aUerId, final String aPassword,
			final String aUrlExtend);

	/**
	 * to be overriden in iPOJO component
	 *
	 * @return
	 */
	protected IActivityLogger getLogger() {
		return pLogger;
	}

	/**
	 * @return
	 */
	public boolean isDriverAvailabile() {

		return testClassAvailability(getClassName());
	}

	/**
	 * @param aHostNamePort
	 * @param aDbName
	 * @param aDbUserId
	 * @param aDbPassword
	 * @return an instance of mySql Connection
	 * @throws ClusterStorageException
	 */
	public Connection newDbConnection(final String aHost, final int aPort,
			final String aDbName, final String aDbUserId,
			final String aDbPassword, final String aUrlExtend) throws Exception {

		String wDbUrl = getConnectUrl(aHost, aPort, aDbName, aDbUserId,
				aDbPassword, aUrlExtend);
		try {
			return DriverManager.getConnection(wDbUrl, aDbUserId, aDbPassword);
		} catch (SQLException e) {
			String wMessage = String
					.format("newDbConnection: unable to open database connection. URL=[%s] Login=[%s][%s]",
							wDbUrl, aDbUserId, aDbPassword);
			throw new Exception(wMessage, e);
		}
	}

	/**
	 * <pre>
	 * jdbc:mysql://localhost:3306/HerongDB?user=Herong&password=TopSecret&useUnicode=true&characterEncoding=UTF-8&autoReconnect=true
	 * </pre>
	 *
	 * @param aUrl
	 * @return
	 */
	public String retrieveDbName(final String aUrl) {

		int wPosStart = (aUrl != null) ? aUrl.lastIndexOf('/') : -1;
		if (wPosStart == -1) {
			return "unknown-database-name";
		}
		wPosStart++;
		int wPosEnd = aUrl.indexOf('?', wPosStart);
		if (wPosEnd == -1) {
			return aUrl.substring(wPosStart);
		} else {
			return aUrl.substring(wPosStart, wPosEnd);

		}
	}

	/**
	 * @param aLogger
	 * @return the logger
	 */
	public void setLogger(final IActivityLogger aLogger) {
		pLogger = (aLogger != null) ? aLogger : CActivityLoggerNull
				.getInstance();
	}

	/**
	 * @param aClassName
	 * @return
	 */
	protected boolean testClassAvailability(final String aClassName) {
		Class<?> wClass = null;
		try {
			wClass = Class.forName(aClassName);
			Object wDriver = Class.forName(aClassName).newInstance();

		} catch (Exception e) {
			getLogger().logSevere(this, "testClassAvailability",
					"unable to load the driver [%s]. %s", aClassName,
					CXException.eCauseMessagesInString(e));
		}
		return (wClass != null);
	}
}
