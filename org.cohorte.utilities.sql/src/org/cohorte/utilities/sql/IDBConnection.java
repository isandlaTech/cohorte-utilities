package org.cohorte.utilities.sql;

import java.util.Map;

import org.cohorte.utilities.sql.exec.CDBConnectionInfos;
import org.cohorte.utilities.sql.exec.CDBResult;

public interface IDBConnection {

	/**
	 * @return
	 */
	boolean close();

	/**
	 * @param aSqlRequest
	 * @return
	 * @throws Exception
	 */
	CDBResult execSqlRequest(final String aSqlRequest) throws DBException;

	/**
	 * @param aSqlRequest
	 * @param aOptions
	 * @return
	 * @throws Exception
	 */
	CDBResult execSqlRequest(final String aSqlRequest,
			final Map<String, String> aOptions) throws DBException;

	/**
	 * @return
	 */
	CDBConnectionInfos getDBConnectionInfos();

	/**
	 * @return
	 */
	int getIdx();

	/**
	 *
	 */
	void invalidate();

	/**
	 * @return
	 */
	boolean isBusy();

	/**
	 * @return
	 */
	boolean isOpened();

	/**
	 * @return true if the unused duration is longer than the max unused
	 *         authorized duration
	 */
	boolean isUnusedTooLoong();

	/**
	 * @return
	 */
	boolean isValid();

	/**
	 * @return true if the connection is opened
	 * @throws Exception
	 */
	boolean open() throws DBException;

	/**
	 *
	 */
	void setBusyOff();

	/**
	 *
	 */
	void setBusyOn();
}
