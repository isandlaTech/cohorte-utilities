package org.cohorte.utilities.sql.exec;

import org.cohorte.utilities.sql.DBException;
import org.cohorte.utilities.sql.EDBType;
import org.psem2m.utilities.logging.IActivityLogger;

/**
 * @author ogattaz
 *
 */
public class CDBConnectionFactory {

	/**
	 * @param aDBConnectionInfos
	 * @return
	 * @throws Exception
	 */
	public static CDBConnection newDbConnection(final IActivityLogger aLogger,
			final CDBConnectionInfos aDBConnectionInfos) throws DBException {

		EDBType wEDBType = aDBConnectionInfos.getDbType();

		switch (wEDBType) {
		case MYSQL:
			return new CDBConnectionMySql(aLogger, aDBConnectionInfos);

		case SQLSERVER:
			return new CDBConnectionSqlServer(aLogger, aDBConnectionInfos);

		default:
			throw new DBException(String.format(
					"Unable to instanciate a connection from type [%s]",
					(wEDBType != null) ? wEDBType.name() : null));
		}

	}
}
