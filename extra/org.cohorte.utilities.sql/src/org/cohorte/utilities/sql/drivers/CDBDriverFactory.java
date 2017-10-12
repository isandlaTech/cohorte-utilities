package org.cohorte.utilities.sql.drivers;

import org.cohorte.utilities.sql.EDBType;
import org.cohorte.utilities.sql.IDBDriver;
import org.cohorte.utilities.sql.exec.CDBConnectionInfos;
import org.psem2m.utilities.logging.IActivityLogger;

/**
 * @author ogattaz
 *
 */
public class CDBDriverFactory {

	public static boolean isSqlServerV4On() {
		return true;
	}

	/**
	 * @param aLogger
	 * @param aDBConnectionInfos
	 * @return
	 * @throws Exception
	 */
	public static IDBDriver newDBDriver(final IActivityLogger aLogger,
			final CDBConnectionInfos aDBConnectionInfos) throws Exception {

		return newDBDriver(aLogger, aDBConnectionInfos.getDbType());
	}

	/**
	 * @param aLogger
	 * @param aEDBType
	 * @return
	 * @throws Exception
	 */
	public static IDBDriver newDBDriver(final IActivityLogger aLogger,
			final EDBType aEDBType) throws Exception {

		switch (aEDBType) {
		case MYSQL:
			return new CDBDriverMySQL(aLogger);

		case SQLSERVER:
			return new CDBDriverSqlServer(aLogger);

		case POSTGRESQL:
			return new CDBDriverPostgreSQL(aLogger);

		default:
			throw new Exception(String.format(
					"Unable to instanciate a driver from type [%s]",
					(aEDBType != null) ? aEDBType.name() : null));
		}

	}
}
