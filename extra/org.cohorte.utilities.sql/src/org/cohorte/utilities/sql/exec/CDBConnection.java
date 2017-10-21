package org.cohorte.utilities.sql.exec;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

import org.cohorte.utilities.sql.DBException;
import org.cohorte.utilities.sql.IDBConnection;
import org.psem2m.utilities.CXDateTime;
import org.psem2m.utilities.CXException;
import org.psem2m.utilities.CXTimer;
import org.psem2m.utilities.logging.CActivityLoggerBasicConsole;
import org.psem2m.utilities.logging.IActivityLogger;

/**
 *
 * @author ogattaz
 *
 */
public abstract class CDBConnection implements IDBConnection {

	private static final long DEFAULT_UNUSED_DURATION = 15 * 60 * 1000;

	private static Map<String, String> sReturnGeneratedKeysOption = new HashMap<String, String>();

	/**
	 * @return
	 */
	public static Map<String, String> getReturnGeneratedKeysOption() {

		synchronized (sReturnGeneratedKeysOption) {
			if (sReturnGeneratedKeysOption.size() == 0) {
				sReturnGeneratedKeysOption.put(
						CDBRequest.PARAMS_RETURN_GENERATED_KEYS, "true");
			}
		}
		return sReturnGeneratedKeysOption;
	}

	protected Connection pDbConnection;
	private final CDBConnectionInfos pDBConnectionInfos;
	private final int pIdx;
	private final AtomicBoolean pIsBusy = new AtomicBoolean(false);
	private final AtomicBoolean pIsValid = new AtomicBoolean(true);
	private final long pLastUse = System.currentTimeMillis();
	protected final IActivityLogger pLogger;

	/**
	 * Gestion des connexions innutilisées dans le pool de connexion de la base
	 * agiliumdb
	 */
	private final long pMaxUnusedDuration;

	/**
	 * @param aDBConnectionInfos
	 */
	CDBConnection(final IActivityLogger aLogger,
			final CDBConnectionInfos aDBConnectionInfos) {

		super();
		pLogger = (aLogger != null) ? aLogger : CActivityLoggerBasicConsole
				.getInstance();
		pDBConnectionInfos = aDBConnectionInfos;
		pIdx = CDBConnectionIdx.getIdx();
		pMaxUnusedDuration = DEFAULT_UNUSED_DURATION;
	}

	/**
	 * @return
	 * @throws Exception
	 */
	@Override
	public boolean close() {
		if (pDbConnection != null) {
			try {
				if (!pDbConnection.isClosed()) {
					pDbConnection.close();
				}
			} catch (SQLException e) {
				String wMessage = String.format(
						"unable to close the db connection : %s",
						CXException.eCauseMessagesInString(e));
				pLogger.logSevere(this, "testClassAvailability", wMessage);
				return false;
			}
			pDbConnection = null;
		}
		return true;
	}

	/**
	 * @param aSqlRequest
	 * @return
	 * @throws Exception
	 */
	@Override
	public CDBResult execSqlRequest(final String aSqlRequest)
			throws DBException {
		return execSqlRequest(aSqlRequest, null);
	}

	/**
	 * @param aSqlStatement
	 * @param aOptions
	 * @return
	 */
	@Override
	public CDBResult execSqlRequest(final String aSqlRequest,
			final Map<String, String> aOptions) throws DBException {

		// MOD_OG 20161020
		if (aSqlRequest == null || aSqlRequest.isEmpty()) {
			pLogger.logWarn(this, "execSqlRequest", "No SqlRequest available");
		}

		CDBRequest wDBRequest = new CDBRequest(aSqlRequest, aOptions);
		CDBResult wDBResult = new CDBResult(wDBRequest);

		java.sql.Statement wStatement = null;

		try {
			if (!isOpened()) {
				throw new DBException(
						"execSqlRequest: The database connection isn't opened. Look at the logged exception in the server log file. Verify the connections parameters.");
			}

			wStatement = pDbConnection.createStatement();

			// If STATEMENT is SELECT
			if (wDBRequest.isSelect()) {

				execSqlRequestSelect(wDBRequest, wStatement, wDBResult);
			}
			// If STATEMENT is INSERT
			else if (wDBRequest.isInsert()) {

				execSqlRequestInsert(wDBRequest, wStatement, wDBResult);
			}
			// If STATEMENT is an other one
			else {
				execSqlRequestOther(wDBRequest, wStatement, wDBResult);
			}

		} catch (Exception e) {
			wDBResult.setMessageKO("jdbcConnector error (execSql : [%s]) : %s",
					wDBResult.getDBQuery(),
					CXException.eCauseMessagesInString(e));
			pLogger.logSevere(this, "execSqlStatement", wDBResult.getMessage());
			if (pLogger.isLoggable(Level.FINEST)) {
				pLogger.logSevere(this, "execSqlStatement", "ERROR:%s", e);
			}

		} finally {
			if (wStatement != null) {
				try {
					// Releases this Statement object's database and JDBC
					// resources immediately
					wStatement.close();
				} catch (SQLException e) {
					if (pLogger.isLoggable(Level.FINEST)) {
						pLogger.logSevere(this, "execSqlStatement", "ERROR:%s",
								e);
					}
				}
			}
		}
		return wDBResult;
	}

	/**
	 * @param aDBRequest
	 * @param wStatement
	 * @param aDBResult
	 * @return
	 * @throws Exception
	 */
	private CDBResult execSqlRequestInsert(final CDBRequest aDBRequest,
			final java.sql.Statement wStatement, final CDBResult aDBResult)
			throws Exception {

		CXTimer wTimer = CXTimer.newStartedTimer();

		int wDataManipulationCount = 0;

		if (aDBRequest.mustReturnGeneratedKeys()) {
			try {
				/*
				 * executeUpdate wait for sql an SQL Data Manipulation Language
				 * (DML) statement, such as INSERT, UPDATE or DELETE; or an SQL
				 * statement that returns nothing, such as a DDL statement.
				 * 
				 * autoGeneratedKeys a flag indicating whether auto-generated
				 * keys should be made available for retrieval; one of the
				 * following constants: Statement.RETURN_GENERATED_KEYS
				 * Statement.NO_GENERATED_KEYS
				 */
				wDataManipulationCount = wStatement.executeUpdate(
						aDBRequest.getSqlQuery(),
						java.sql.Statement.RETURN_GENERATED_KEYS);
			} catch (java.lang.AbstractMethodError e) {
				throw new Exception(
						"the jdbc driver doesn't implement the method : executeUpdate(Strint slq, int autoGeneratedKeys)",
						e);
			}
		} else {
			wDataManipulationCount = wStatement.executeUpdate(aDBRequest
					.getSqlQuery());
		}

		if (wDataManipulationCount == 0) {
			aDBResult.setMessageKO(
					"Inserting failed, no rows affected. (execSql : [%s])",
					aDBRequest.getSqlQuery());
		} else {
			aDBResult.setDataManipulationCount(wDataManipulationCount);

			// if the generated keys (eg. autoindent) must be returned
			if (aDBRequest.mustReturnGeneratedKeys()) {
				java.sql.ResultSet wResultSet = null;
				try {
					/*
					 * Retrieves any auto-generated keys created as a result of
					 * executing this Statement object. If this Statement object
					 * did not generate any keys, an empty ResultSet object is
					 * returned.
					 */
					wResultSet = wStatement.getGeneratedKeys();

					List<String> wKeys = aDBResult.newGeneratedKeysList();
					while (wResultSet.next()) {
						wKeys.add(String.valueOf(wResultSet.getLong(1)));
					}
					if (wKeys.size() == 0) {
						aDBResult
								.setMessageKO("Inserting failed, no generated key obtained.");
					}
				} finally {
					if (wResultSet != null) {
						try {
							// Releases this ResultSet object's database and
							// JDBC resources immediately.
							wResultSet.close();
						} catch (SQLException e) {
							if (pLogger.isLoggable(Level.FINEST)) {
								pLogger.logSevere(this, "execSqlRequestInsert",
										"ERROR:%s", e);
							}
						}
					}
				}
			}
		}
		aDBResult.setDuration(wTimer);
		return aDBResult;
	}

	/**
	 * @param aDBRequest
	 * @param wStatement
	 * @param aDBResult
	 * @return the contain of the resultset or the count of the impacted row
	 * @throws Exception
	 */
	private CDBResult execSqlRequestOther(final CDBRequest aDBRequest,
			final java.sql.Statement wStatement, final CDBResult aDBResult)
			throws Exception {

		CXTimer wTimer = CXTimer.newStartedTimer();
		java.sql.ResultSet wResultSet = null;
		try {
			// return true if the first result is a ResultSet object; false
			// if it is an update count or there are no results
			boolean hasResultSet = wStatement.execute(aDBRequest.getSqlQuery());

			if (hasResultSet) {
				wResultSet = wStatement.getResultSet();
				putResultSetInDBResult(wResultSet, aDBResult);
			} else {
				aDBResult.setDataManipulationCount(wStatement.getUpdateCount());
			}
		} finally {
			if (wResultSet != null) {
				try {
					// Releases this ResultSet object's database and
					// JDBC resources immediately.
					wResultSet.close();
				} catch (SQLException e) {
					if (pLogger.isLoggable(Level.FINEST)) {
						pLogger.logSevere(this, "execSqlRequestOther",
								"ERROR:%s", e);
					}
				}
			}
		}

		aDBResult.setDuration(wTimer);
		return aDBResult;
	}

	/**
	 * @param aDBRequest
	 * @param wStatement
	 * @param aDBResult
	 * @return
	 * @throws Exception
	 */
	private CDBResult execSqlRequestSelect(final CDBRequest aDBRequest,
			final java.sql.Statement wStatement, final CDBResult aDBResult)
			throws Exception {

		CXTimer wTimer = CXTimer.newStartedTimer();
		java.sql.ResultSet wResultSet = null;
		try {

			wResultSet = wStatement.executeQuery(aDBRequest.getSqlQuery());

			putResultSetInDBResult(wResultSet, aDBResult);

		} finally {
			if (wResultSet != null) {
				try {
					// Releases this ResultSet object's database and
					// JDBC resources immediately.
					wResultSet.close();
				} catch (SQLException e) {
					if (pLogger.isLoggable(Level.FINEST)) {
						pLogger.logSevere(this, "execSqlRequestSelect",
								"ERROR:%s", e);
					}
				}
			}
		}
		aDBResult.setDuration(wTimer);
		return aDBResult;
	}

	/**
	 * @return
	 */
	public abstract String getClassName();

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.agilium.ng.commons.sql.IDBConnection#getDBConnectionInfos()
	 */
	@Override
	public CDBConnectionInfos getDBConnectionInfos() {
		return pDBConnectionInfos;
	}

	/**
	 *
	 * @return
	 */
	public String getFormatedMaxUnusedDuration() {
		return CXDateTime.getFormatedDuration(getmaxUnusedDuration());
	}

	/**
	 * @return
	 */
	public String getFormatedUnusedDuration() {
		return CXDateTime.getFormatedDuration(getUnusedDuration());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.agilium.ng.commons.sql.IBdConnection#getIdx()
	 */
	@Override
	public int getIdx() {
		return pIdx;
	}

	/**
	 *
	 * @return
	 */
	public long getmaxUnusedDuration() {
		return pMaxUnusedDuration;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.agilium.ng.commons.sql.IBdConnection#isUnusedTooLoong()
	 */

	/**
	 * @return
	 */
	public long getUnusedDuration() {
		return System.currentTimeMillis() - pLastUse;
	}

	/**
	 * @param aClassName
	 * @return
	 * @throws Exception
	 */
	boolean instanciateDriver(final String aClassName) throws DBException {
		Object wDriver = null;
		try {
			wDriver = Class.forName(aClassName).newInstance();
		} catch (Exception e) {

			throw new DBException(String.format(
					"Unable to instanciate the driver [%s]. %s",
					getClassName(), CXException.eCauseMessagesInString(e)), e);
		}
		return (wDriver != null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.agilium.ng.commons.sql.IBdConnection#invalidate()
	 */
	@Override
	public synchronized void invalidate() {
		pIsValid.set(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.agilium.ng.commons.sql.IBdConnection#isBusy()
	 */
	@Override
	public synchronized boolean isBusy() {
		return pIsBusy.get();

	}

	/**
	 * @return
	 */
	@Override
	public boolean isOpened() {
		return pDbConnection != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.agilium.ng.commons.sql.IDBConnection#isUnusedTooLoong()
	 */
	@Override
	public boolean isUnusedTooLoong() {
		boolean wTooLong = getUnusedDuration() > getmaxUnusedDuration();
		if (wTooLong) {
			pLogger.logInfo(
					this,
					"invalidate",
					"Connection [%d] no longer used since=[%s] > Max=[%s] => must be invalidated",
					getIdx(), getFormatedUnusedDuration(),
					getFormatedMaxUnusedDuration());
		}
		return wTooLong;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.agilium.ng.commons.sql.IBdConnection#isValid()
	 */
	@Override
	public boolean isValid() {
		return pIsValid.get();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.agilium.ng.commons.sql.IDBConnection#open()
	 */
	@Override
	public boolean open() throws DBException {

		try {
			pDbConnection = DriverManager.getConnection(
					pDBConnectionInfos.getDbConnString(),
					pDBConnectionInfos.getDbUserName(),
					pDBConnectionInfos.getDbPassword());

			return true;
		} catch (SQLException e) {
			throw new DBException(String.format(
					"Unable to open connection [%s]",
					pDBConnectionInfos.getDbConnString()), e);
		}
	}

	/**
	 * @param aResultSet
	 * @param aDBResult
	 * @throws SQLException
	 */
	private void putResultSetInDBResult(final java.sql.ResultSet aResultSet,
			final CDBResult aDBResult) throws SQLException {

		java.sql.ResultSetMetaData wMetaData = aResultSet.getMetaData();

		int wNbColums = wMetaData.getColumnCount();
		String[] titles = new String[wNbColums];
		for (int i = 1; i <= wMetaData.getColumnCount(); i++) {
			titles[i - 1] = wMetaData.getColumnLabel(i);
		}

		List<String[]> wResultTable = aDBResult.newResultTable();
		wResultTable.add(titles);

		while (aResultSet.next()) {
			String[] line = new String[wNbColums];
			for (int i = 1; i <= wNbColums; i++) {
				line[i - 1] = aResultSet.getString(i);
			}
			wResultTable.add(line);
		}
	}

	/**
	 * @param aIsBusy
	 */
	private synchronized void setBusy(final boolean aIsBusy) {
		pIsBusy.set(aIsBusy);
	}

	/**
	 *
	 */
	@Override
	public void setBusyOff() {
		setBusy(false);
	}

	/**
	 *
	 */
	@Override
	public void setBusyOn() {
		setBusy(true);
	}

	/**
	 * @param aClassName
	 * @return
	 */
	boolean testClassAvailability(final String aClassName) throws DBException {
		Class<?> wClass = null;
		try {
			wClass = Class.forName(aClassName);
		} catch (ClassNotFoundException e) {
			String wMessage = String.format(
					"unable to find the class [%s]. %s", aClassName,
					CXException.eCauseMessagesInString(e));
			pLogger.logSevere(this, "testClassAvailability", wMessage);
			throw new DBException(wMessage, e);
		}
		return (wClass != null);
	}

	/**
	 * @return true is the driver is valid
	 * @throws Exception
	 */
	public abstract boolean testDriver() throws Exception;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return String.format("isOpened=[%b] isValid=[%b] isBusy=[%b]",
				isOpened(), isValid(), isBusy());
	}

}

/**
 * @author ogattaz
 *
 */
class CDBConnectionIdx {

	private static CDBConnectionIdx sSingleton = new CDBConnectionIdx();

	/**
	 * @return
	 */
	static int getIdx() {
		return sSingleton.calcIdx();
	}

	private int pConnexionIdx = 0;

	/**
	 *
	 */
	private CDBConnectionIdx() {
		super();
	}

	/**
	 * @return
	 */
	private synchronized int calcIdx() {
		pConnexionIdx++;
		return pConnexionIdx;
	}
}
