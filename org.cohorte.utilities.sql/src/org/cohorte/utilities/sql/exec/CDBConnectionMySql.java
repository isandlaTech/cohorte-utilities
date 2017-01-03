package org.cohorte.utilities.sql.exec;

import org.cohorte.utilities.sql.DBException;
import org.psem2m.utilities.logging.IActivityLogger;

/**
 *
 *
 * L'archive "mysql.jar" du driver "Oracle jdbc mysql driver" doit être
 * accessible dans le classpath
 *
 * @author ogattaz
 *
 */
public class CDBConnectionMySql extends CDBConnection {

	public final static String DRIVER_CLASS_NAME = "com.mysql.jdbc.Driver";

	/**
	 * @see fr.agilium.CDBDriverMySQL.CDbDriverMySQL
	 */
	public static final String DRIVER_URL_PROTOCOL = "jdbc:mysql:";

	private static boolean sDriverAvailableFlag = false;

	private static boolean sDriverLoadedFlag = false;

	/**
	 * @param aDBConnectionInfos
	 */
	CDBConnectionMySql(final IActivityLogger aLogger,
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

		return testDriverAvailable() && testLoadDriver();
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

	/**
	 * @return
	 */
	private boolean testLoadDriver() throws DBException {

		if (!sDriverLoadedFlag) {
			sDriverLoadedFlag = instanciateDriver(getClassName());
		}
		return sDriverLoadedFlag;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return String.format(
				"Driver=[%s] DriverAvailable=[%s] DriverLoaded=[%s]",
				DRIVER_CLASS_NAME, sDriverAvailableFlag, sDriverLoadedFlag)
				+ ' ' + super.toString();
	}

}
