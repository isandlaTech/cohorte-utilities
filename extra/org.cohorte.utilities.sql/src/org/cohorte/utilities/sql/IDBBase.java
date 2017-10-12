package org.cohorte.utilities.sql;

import org.cohorte.utilities.sql.exec.CDBConnectionInfos;

/**
 * @author ogattaz
 *
 */
public interface IDBBase {

	/**
	 * @return
	 */
	boolean dbClose() throws IllegalStateException;

	/**
	 * @return true is the database is opened
	 * @throws IllegalStateException
	 * @throws Exception
	 */
	boolean dbOpen() throws IllegalStateException, Exception;

	/**
	 * @param aDBConnectionInfos
	 * @return
	 * @throws Exception
	 */
	boolean dbOpen(final CDBConnectionInfos aDBConnectionInfos)
			throws IllegalStateException, Exception;

	/**
	 * @return
	 */
	IDBConnection getDBConnection() throws Exception;

	/**
	 * @return
	 */
	boolean isConnected();
}
