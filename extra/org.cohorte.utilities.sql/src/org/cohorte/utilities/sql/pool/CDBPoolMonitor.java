package org.cohorte.utilities.sql.pool;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.cohorte.utilities.sql.IDBConnection;
import org.psem2m.utilities.CXException;

public class CDBPoolMonitor implements Runnable {

	// la durée max pour tuer le thread
	static final long DEFAULT_JOIN_DURATION = 5000;

	// la durée d'endormissement par défaut : 1 minute
	static final long DEFAULT_SLEEP_DURATION = 60000;

	private static AtomicInteger pDBPoolMonitorIdx = new AtomicInteger();
	private final AtomicBoolean pContinue = new AtomicBoolean(false);
	private final Thread pMonitor;
	private final CDBPool pPool;

	/**
	 *
	 */
	CDBPoolMonitor(final CDBPool aPool) {
		super();

		pPool = aPool;
		pMonitor = new Thread(this);
		pMonitor.setName(calcThreadName(aPool.getDBConnectionInfos()
				.getDbAlias()));
		pMonitor.start();

		pPool.getLogger().logInfo(this, "<init>",
				"Instanciated. ThreadName=[%s] sleepDuration=[%d] ",
				pMonitor.getName(), DEFAULT_SLEEP_DURATION);
	}

	/**
	 * @param aDbName
	 * @return
	 */
	private String calcThreadName(final String aDbName) {

		return String.format("%s_%s_%d", getClass().getSimpleName(), aDbName,
				getNextMonitorIdx());
	}

	/**
	 * @return
	 */
	private boolean getContinue() {
		return pContinue.get();
	}

	/**
	 * @return the next monitor index
	 */
	private int getNextMonitorIdx() {

		return pDBPoolMonitorIdx.addAndGet(1);
	}

	/**
	 * @return
	 */
	boolean isAlive() {
		return pMonitor.isAlive();
	}

	/**
	 * @param aTimeOut
	 * @return false if we are note sure that the tread is died
	 */
	private boolean joinThread(final long aTimeOut) {

		try {
			if (Thread.currentThread() != pMonitor) {
				pMonitor.join(aTimeOut);
				pPool.getLogger().logInfo(this, "joinThread(): joinned");
			}
			return true;
		}
		// if any thread has interrupted the current thread. The interrupted
		// status of the current thread is cleared when this exception is
		// thrown.
		catch (InterruptedException e) {
			pPool.getLogger().logSevere(this, "joinThread", "ERROR: %s",
					CXException.eUserMessagesInString(e));
			return false;
		}
	}

	/**
	 * @param aTimeOut
	 * @return true is the thread id died
	 */
	private boolean killPoolThread(final long aTimeOut) {

		pPool.getLogger().logInfo(this, "killPoolThread(): TimeOut=[%d]",
				aTimeOut);

		// si le thread n'est pas vie, return immediatly
		if (!isAlive()) {
			return true;
		}
		// si le thread vie, on le tue en le faisant sortir de sa boucle
		stopRunning();
		// on attend qu'il soit mort...
		return joinThread(aTimeOut);
	}

	/**
	 *
	 */
	private void monitorPool() {
		// clean the list of connections
		List<IDBConnection> wConnections = pPool.getConnections();
		synchronized (wConnections) {
			IDBConnection wConnection;
			int wMax = wConnections.size();
			for (int wIdx = wMax - 1; wIdx >= 0; wIdx--) {
				wConnection = wConnections.get(wIdx);

				if (!wConnection.isValid() || wConnection.isUnusedTooLoong()) {
					pPool.getLogger().logInfo(this, "monitorPool",
							"ConnectionIdx=[%d] invalid or unused too Long ",
							wConnection.getIdx());

					wConnection.close();
					wConnections.remove(wIdx);
				}
			}
		}

		// create a connexion if the list is empty
		if (wConnections.size() == 0) {
			try {
				pPool.getLogger().logInfo(this, "monitorPool",
						"Pool empty: create the first connection");
				pPool.addNewDbConnection();

			} catch (Exception e) {
				pPool.getLogger()
						.logSevere(
								this,
								"monitorPool",
								"Unable to create and add a new db connection in the pool. %s ",
								e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {

		pPool.getLogger().logInfo(this, "run",
				"monitor started. ThreadName=[%s]", pMonitor.getName());

		while (getContinue()) {

			sleepTempo(DEFAULT_SLEEP_DURATION);

			try {
				if (pPool.isConnected()) {
					monitorPool();
				}
			} catch (Exception e) {
				pPool.getLogger().logSevere(this, "run", "Error: %s");
			}

		}
	}

	/**
	 * @param aContinue
	 */
	private void setContinue(final boolean aContinue) {
		pContinue.set(aContinue);
	}

	/**
	 * @param aTempo
	 */
	private void sleepTempo(final long aTempo) {
		try {
			Thread.sleep(aTempo);
		} catch (Exception e) {
		}
	}

	/**
	 *
	 */
	void stopMonitor() {

		pPool.getLogger().logInfo(this, "stopMonitor");

		boolean wKilled = killPoolThread(DEFAULT_JOIN_DURATION);

		pPool.getLogger().logInfo(this, "stopMonitor", "killed=[%sd]", wKilled);
	}

	/**
	 *
	 */
	private void stopRunning() {
		pPool.getLogger().logInfo(this, "stopRunning():");

		setContinue(false);
		try {
			if (pMonitor.isAlive()) {
				pMonitor.interrupt();
			}
		} catch (Exception e) {
		}
	}

}
