package org.cohorte.utilities.sql.exec;

import org.cohorte.utilities.sql.DBException;
import org.psem2m.utilities.logging.IActivityLogger;

/**
 *
 * L'archive "sqljdbc4.jar" du driver "Microsoft jdbc sqlserver driver v4" doit
 * être accessible dans le classpath
 *
 *
 * @author ogattaz
 *
 */
public class CDBConnectionSqlServer extends CDBConnection {

	/**
	 * V4
	 *
	 * @see fr.agilium.CDBDriverSqlServer.CDbDriverSqlServer
	 */

	public final static String DRIVER_CLASS_NAME = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

	/**
	 * <pre>
	 * jdbc:sqlserver://192.168.2.200:1433;databaseName=CGP;user=sa;password=pass
	 * </pre>
	 *
	 * @see fr.agilium.CDBDriverSqlServer.CDbDriverSqlServer
	 */
	public static final String DRIVER_URL_PROTOCOL = "jdbc:sqlserver:";

	private static boolean sDriverAvailableFlag = false;

	/**
	 * @param aDBConnectionInfos
	 */
	CDBConnectionSqlServer(final IActivityLogger aLogger,
			final CDBConnectionInfos aDBConnectionInfos) throws DBException {

		super(aLogger, aDBConnectionInfos);

		boolean wIsDriverValid = testDriver();

		pLogger.logInfo(this, "<init>", "instanciated. IsDriverValid=[%b]",
				wIsDriverValid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.m1i.agilium.clientcommoncode.CDBConnection#getClassName()
	 */
	@Override
	public String getClassName() {
		return DRIVER_CLASS_NAME;
	}

	/**
	 * @return
	 * @throws Exception
	 */
	@Override
	public boolean testDriver() throws DBException {

		return testDriverAvailable();
	}

	/**
	 * @return
	 */
	private boolean testDriverAvailable() throws DBException {
		if (!sDriverAvailableFlag) {
			sDriverAvailableFlag = testClassAvailability(getClassName());
		}
		return sDriverAvailableFlag;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return String.format("Driver=[%s] DriverAvailable=[%s]",
				DRIVER_CLASS_NAME, sDriverAvailableFlag)
				+ ' '
				+ super.toString();
	}
}
