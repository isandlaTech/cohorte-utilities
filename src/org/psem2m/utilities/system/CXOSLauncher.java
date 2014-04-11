package org.psem2m.utilities.system;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.logging.Level;

import org.psem2m.utilities.logging.CActivityLoggerNull;
import org.psem2m.utilities.logging.IActivityLoggerBase;

/**
 * @author ogattaz
 * 
 */
public class CXOSLauncher {

	/**
	 * @author ogattaz
	 * 
	 */
	class COutputBufferConsumer implements Runnable {

		private final InputStream pInputStream;
		private final boolean pIsStdErr;
		private int pNbLine = 0;
		private final IXOSRunner pOSRunner;

		private int pReadSize = 0;

		/**
		 * @param pInputStream
		 */
		COutputBufferConsumer(final InputStream aInputStream,
				final IXOSRunner aOSRunner, final boolean aIsStdErr) {
			super();
			pInputStream = aInputStream;
			pOSRunner = aOSRunner;
			pIsStdErr = aIsStdErr;
		}

		/**
		 * @param is
		 * @return
		 */
		BufferedReader getBufferedReader() {
			return new BufferedReader(new InputStreamReader(pInputStream));
		}

		/**
		 * @return
		 */
		int getNbLine() {
			return pNbLine;
		}

		/**
		 * @return
		 */
		int getReadSize() {
			return pReadSize;
		}

		/**
		 * @param aWhat
		 * @param aInfos
		 */
		private void secureLogDebug(final CharSequence aWhat, final Object... aInfos) {
			secureLog(pLogger, Level.FINE, this, aWhat, aInfos);
		}

		/**
		 * @param aWhat
		 * @param aInfos
		 */
		private void secureLogSevere(final CharSequence aWhat, final Object... aInfos) {
			secureLog(pLogger, Level.SEVERE, this, aWhat, aInfos);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {

			secureLogDebug("run", "%s consumer thread begin", (pIsStdErr) ? "StdErr"
					: "StdOut");
			BufferedReader br = getBufferedReader();
			String wLine = "";
			try {
				while ((wLine = br.readLine()) != null) {
					int wBuffSize;
					if (pIsStdErr) {
						wBuffSize = pOSRunner.consumeStdOutputErrLine(wLine);
					} else {
						wBuffSize = pOSRunner.consumeStdOutputLine(wLine);
					}
					pReadSize += wLine.length();
					pNbLine++;
					secureLogDebug("run", "line(%3d) lineSize=[%5d] buffSize=[%5d] ",
							pNbLine, pReadSize, wBuffSize);
				}
			} catch (IOException e) {
				secureLogSevere("run", "ERROR:%s", e);
			}
			secureLogDebug("run", "%s consumer thread end", (pIsStdErr) ? "StdErr"
					: "StdOut");
		}
	}

	/**
	 * @param aLevel
	 * @param aWho
	 * @param aWhat
	 * @param aInfos
	 */
	static void secureLog(final IActivityLoggerBase aLogger, final Level aLevel,
			final Object aWho, final CharSequence aWhat, final Object... aInfos) {
		try {
			aLogger.log(aLevel, aWho, aWhat, aInfos);
		} catch (RuntimeException e) {
			System.err.println(String.format(
					"pLogger unavailable [%s][%s][%s]", aLevel.getName(), aWho,
					aWhat));
		}
	}

	/**
	 * @param aDuration
	 *            in milliseconds
	 * @return false if interupted, true if the sleeping is complete
	 */
	static boolean sleep(final long aDuration) {
		try {
			Thread.sleep(aDuration);
			return true;
		} catch (InterruptedException e) {
			return false;
		}
	}

	/**
	 * @param aProcess
	 * @param aTimeOut
	 * @return aCommandState (if interrupted => CMD_RUN_STOPED) (if exit 0 =>
	 *         CMD_RUN_OK) (if exit > 0 => CMD_RUN_KO) (if timeout =>
	 *         CMD_RUN_TIMEOUT)
	 */
	static EXCommandState waitForProcessEnd(final Process aProcess,
			final long aTimeOut) {

		if (aTimeOut <= 0) {
			try {
				// causes the current thread to wait, if necessary, until the
				// process represented by this Process object has terminated.
				// This method returns immediately if the subprocess has already
				// terminated. If the subprocess has not yet terminated, the
				// calling thread will be blocked until the subprocess exits.
				return EXCommandState.exitValToState(aProcess.waitFor());
			} catch (InterruptedException e) {
				return EXCommandState.CMD_RUN_STOPED;
			}
		} else {
			long wStart = System.currentTimeMillis();

			do {
				try {
					// Returns the exit value for the subprocess.
					// Throws: IllegalThreadStateException - if the subprocess
					// represented
					// by this Process object has not yet terminated.
					return EXCommandState.exitValToState(aProcess.exitValue());
				} catch (IllegalThreadStateException e) {
					// sleep 50 milliseconds, si interrupted => STOPPED
					if (!sleep(50)) {
						return EXCommandState.CMD_RUN_STOPED;
					}
				}

			} while (System.currentTimeMillis() - wStart < aTimeOut);

			// uniquement si timeout !
			aProcess.destroy();
			return EXCommandState.CMD_RUN_TIMEOUT;
		}
	}

	private final IActivityLoggerBase pLogger;

	private final IXOSRunner pOsRunnner;

	/**
	 * @param aLogger
	 * @param aOsCommand
	 */
	public CXOSLauncher(final IActivityLoggerBase aLogger,
			final IXOSRunner aOsCommand) {
		super();
		pLogger = (aLogger != null) ? aLogger : CActivityLoggerNull
				.getInstance();
		pOsRunnner = aOsCommand;
		pLogger.logDebug(this, "<init>", "CommandLine=[%s]",
				pOsRunnner.getCommandLine());
	}

	/**
	 * http://labs.excilys.com/2012/06/26/runtime-exec-pour-les-nuls-et-
	 * processbuilder/
	 * 
	 * @param aTimeOut
	 * @param aUserDir
	 * @param aEnv
	 * @param aCommandLine
	 * @return the exit State of the process.
	 * @throws Exception
	 */
	public EXCommandState launch(final long aTimeOut, final File aUserDir,
			final Map<String, String> aEnv, final String... aCommandLine)
			throws Exception {

		Process wProcess = start(aUserDir, aEnv, aCommandLine);

		return waitForProcessEnd(wProcess, aTimeOut);
	}

	/**
	 * Gets the input stream of the subprocess. The stream obtains data piped
	 * from the standard output stream of the process represented by this
	 * Process object.
	 * 
	 * @param aStartedProcess
	 */
	private void setBufferConsumers(final Process aStartedProcess) {

		String wThreadId = String.valueOf(pOsRunnner.hashCode());
		wThreadId = wThreadId.substring(wThreadId.length() - 4);

		COutputBufferConsumer fluxSortie = new COutputBufferConsumer(
				aStartedProcess.getInputStream(), pOsRunnner, false);
		String wThreadNameOut = String.format("%s_stdout", wThreadId);
		new Thread(fluxSortie, wThreadNameOut).start();

		COutputBufferConsumer fluxErreur = new COutputBufferConsumer(
				aStartedProcess.getErrorStream(), pOsRunnner, true);
		String wThreadNameErr = String.format("%s_stderr", wThreadId);
		new Thread(fluxErreur, wThreadNameErr).start();
	}

	/**
	 * @param aUserDir
	 * @param aEnv
	 * @param aCommandLine
	 * @return the started process
	 * @throws Exception
	 */
	public Process start(final File aUserDir, final Map<String, String> aEnv,
			final String... aCommandLine) throws Exception {
		ProcessBuilder wProcessBuilder = new ProcessBuilder(aCommandLine);

		wProcessBuilder.directory(aUserDir);

		if (aEnv != null && !aEnv.isEmpty()) {
			Map<String, String> wEnv = wProcessBuilder.environment();
			if (wEnv != null) {
				wEnv.putAll(aEnv);
			}
		}

		// Starts a new process using the attributes of this process builder.
		Process wProcess = wProcessBuilder.start();

		setBufferConsumers(wProcess);

		return wProcess;
	}
}
