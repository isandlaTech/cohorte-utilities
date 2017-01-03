package org.cohorte.utilities.sql.pool;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.cohorte.utilities.sql.DBException;
import org.cohorte.utilities.sql.IDBConnection;
import org.cohorte.utilities.sql.IDBPool;
import org.cohorte.utilities.sql.exec.CDBConnectionFactory;
import org.cohorte.utilities.sql.exec.CDBConnectionInfos;
import org.psem2m.utilities.logging.CActivityLoggerNull;
import org.psem2m.utilities.logging.IActivityLogger;

/**
 * @author ogattaz
 *
 */
public class CDBPool implements IDBPool {

	// The max unused duration of a dbconnection (default 15 minutes =
	// 15*60*1000)
	private static long DB_POOL_MAX_UNUSED_DURATION = 15 * 60 * 1000;

	private final List<IDBConnection> pConnections = new LinkedList<IDBConnection>();

	private CDBConnectionInfos pDBConnectionInfos;

	private IActivityLogger pLogger;

	/**
	 * Gestion des connexions innutilisées dans le pool de connexion de la base
	 * agiliumdb
	 */
	private final long pMaxUnusedDuration;
	private final AtomicBoolean pOpened = new AtomicBoolean();

	private final CDBPoolMonitor pPoolMonitor;

	/**
	 *
	 */
	public CDBPool(final CDBConnectionInfos aDBConnectionInfos) {
		this(null, aDBConnectionInfos);
	}

	/**
	 *
	 */
	public CDBPool(final IActivityLogger aLogger,
			final CDBConnectionInfos aDBConnectionInfos) {

		super();
		setLogger(aLogger);
		setDBConnectionInfos(aDBConnectionInfos);
		pMaxUnusedDuration = DB_POOL_MAX_UNUSED_DURATION;

		pPoolMonitor = new CDBPoolMonitor(this);

		pLogger.logInfo(this, "<init>",
				"MaxUnusedDuration=[%d] NbConnection=[%d]", pMaxUnusedDuration,
				getNbConnection());
	}

	/**
	 * @throws Exception
	 */
	IDBConnection addNewDbConnection() throws DBException {

		return addNewDbConnection(createDbConnection());
	}

	/**
	 * @return
	 * @throws ClusterStorageException
	 */
	private IDBConnection addNewDbConnection(final IDBConnection aConnection)
			throws DBException {

		if (!aConnection.isOpened()) {
			boolean wOpened = aConnection.open();
			pLogger.logInfo(this, "addNewDbConnection",
					"NbConnection=[%d] Idx=[%d] Opened=[%b]",
					getNbConnection(), aConnection.getIdx(), wOpened);
		}

		synchronized (pConnections) {
			pConnections.add(aConnection);
		}
		return aConnection;

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see fr.agilium.ng.commons.sql.IDBPool#checkIn(fr.agilium.ng.commons.sql.
	 * IDBConnection)
	 */
	@Override
	public void checkIn(final IDBConnection aDbConn) {
		// MOD_172
		if (aDbConn != null) {
			// if no SQLException occurs during thes usage
			if (aDbConn.isValid()) {
				aDbConn.setBusyOff();
			} else {
				aDbConn.close();
				synchronized (pConnections) {
					pConnections.remove(aDbConn);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see fr.agilium.ng.commons.sql.IDBPool#checkOut()
	 */
	@Override
	public IDBConnection checkOut() throws IllegalStateException, Exception {

		if (!isConnected()) {
			throw new IllegalStateException("DBPool is not opened");
		}

		IDBConnection wConnection = findFirstFree();
		if (wConnection == null) {
			wConnection = createDbConnection();
			wConnection.setBusyOn();
			addNewDbConnection(wConnection);
			pLogger.logInfo(this, "checkOut",
					"NbConnection=[%d] NewConnectionIdx=[%d]",
					getNbConnection(), wConnection.getIdx());
		}
		return wConnection;
	}

	/**
	 *
	 */
	private void close() {

		pPoolMonitor.stopMonitor();

		pLogger.logInfo(this, "close(): NbConnection to close=[%d]",
				getNbConnection());

		synchronized (pConnections) {

			for (IDBConnection wConnection : pConnections) {
				wConnection.close();
			}
			pConnections.clear();
		}

	}

	/**
	 * @return
	 * @throws ClusterStorageException
	 */
	IDBConnection createDbConnection() throws DBException {

		return CDBConnectionFactory
				.newDbConnection(pLogger, pDBConnectionInfos);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see fr.agilium.ng.commons.sql.IDBBase#dbClose()
	 */
	@Override
	public boolean dbClose() throws IllegalStateException {

		if (!isConnected()) {
			throw new IllegalStateException("DBPool is not opened");
		}

		close();

		pOpened.set(false);

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.agilium.ng.commons.sql.IDBBase#dbOpen()
	 */
	@Override
	public boolean dbOpen() throws IllegalStateException, Exception {

		if (isConnected()) {
			throw new IllegalStateException("DBPool is already opened");
		}
		try {
			// add a new connection according the current DBConnectionInfos
			addNewDbConnection();

			pOpened.set(true);

		} catch (Exception e) {
			pLogger.logSevere(this, "<init>",
					"Unable to create a connection to open the pool: %s", e);
		}

		return false;
	}

	@Override
	public boolean dbOpen(final CDBConnectionInfos aDBConnectionInfos)
			throws Exception {

		if (isConnected()) {
			throw new Exception("Pool is already opened");
		}

		setDBConnectionInfos(aDBConnectionInfos);

		return dbOpen();
	}

	/**
	 *
	 * detect and invalidate the unused connexions since more that the max
	 * autorized unused duration
	 *
	 * @return the first "unbusy" db connexion found in the list
	 */
	private IDBConnection findFirstFree() {

		synchronized (pConnections) {

			for (IDBConnection wConnection : pConnections) {

				// if not used and always valid
				if (!wConnection.isBusy() && wConnection.isValid()) {

					// if unused since too much time
					if (wConnection.isUnusedTooLoong()) {

						wConnection.invalidate();

					} else {
						wConnection.setBusyOn();
						return wConnection;
					}
				}
			}
		}
		return null;
	}

	/**
	 * MOD_99
	 *
	 * @return
	 */
	List<IDBConnection> getConnections() {
		return pConnections;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see fr.agilium.ng.commons.sql.IDBBase#getDBConnection()
	 */
	@Override
	public IDBConnection getDBConnection() throws Exception {

		return checkOut();
	}

	/**
	 * @return
	 */
	@Override
	public CDBConnectionInfos getDBConnectionInfos() {
		return pDBConnectionInfos;
	}

	/**
	 * @return
	 */
	IActivityLogger getLogger() {
		return pLogger;
	}

	/**
	 * @return
	 */
	public int getNbConnection() {
		synchronized (pConnections) {
			return pConnections.size();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see fr.agilium.ng.commons.sql.IDBBase#isConnected()
	 */
	@Override
	public boolean isConnected() {

		return pOpened.get();
	}

	/**
	 * @param aDBConnectionInfos
	 */
	private void setDBConnectionInfos(
			final CDBConnectionInfos aDBConnectionInfos) {

		pDBConnectionInfos = aDBConnectionInfos;
	}

	/**
	 * @param aLogger
	 * @return the logger
	 */
	public void setLogger(final IActivityLogger aLogger) {
		pLogger = (aLogger != null) ? aLogger : CActivityLoggerNull
				.getInstance();
	}

}
