package org.cohorte.utilities.sql;

import org.cohorte.utilities.sql.exec.CDBConnectionInfos;

/**
 * @author ogattaz
 *
 */
public interface IDBPool extends IDBBase {

	/**
	 * @param aDbConn
	 */
	void checkIn(final IDBConnection aDbConn);

	/**
	 * @return an opened connection to the database
	 * @throws IllegalStateException
	 * @throws Exception
	 */
	IDBConnection checkOut() throws IllegalStateException, Exception;

	/**
	 * @return the DBConnectionInfos associated to the pool
	 */
	CDBConnectionInfos getDBConnectionInfos();

}
