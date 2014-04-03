package org.psem2m.utilities.system;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

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
	class CBufferReader implements Runnable {

		private final InputStream pInputStream;
		private int pNbLine = 0;
		private int pReadSize = 0;
		private final StringBuilder pStringBuilder;

		/**
		 * @param pInputStream
		 */
		CBufferReader(final InputStream aInputStream,
				final StringBuilder aStringBuilder) {
			super();
			this.pInputStream = aInputStream;
			pStringBuilder = aStringBuilder;
		}

		/**
		 * @param is
		 * @return
		 */
		private BufferedReader getBufferedReader(final InputStream is) {
			return new BufferedReader(new InputStreamReader(is));
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

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {

			pLogger.logDebug(this, "run", "Begin");
			BufferedReader br = getBufferedReader(pInputStream);
			String wLine = "";
			try {
				while ((wLine = br.readLine()) != null) {
					if (pNbLine > 0) {
						pStringBuilder.append('\n');
					}
					pStringBuilder.append(wLine);
					pReadSize = pStringBuilder.length();
					pNbLine++;
					pLogger.logDebug(this, "run", "Size=[%5d] line(%3d)=[%s] ",
							pReadSize, pNbLine, wLine);

				}
			} catch (IOException e) {
				pLogger.logSevere(this, "run", "ERROR:%s", e);
			}
			pLogger.logDebug(this, "run", "End");
		}
	}

	private final IActivityLoggerBase pLogger;

	private final IXOSCommand pOsCommand;

	/**
	 * 
	 */
	public CXOSLauncher(final IActivityLoggerBase aLogger,
			final IXOSCommand aOsCommand) {
		super();
		pLogger = (aLogger != null) ? aLogger : CActivityLoggerNull
				.getInstance();
		pOsCommand = aOsCommand;
		pLogger.logDebug(this, "<init>", "CommandLine=[%s]",
				pOsCommand.getCommandLine());
	}

	/**
	 * 
	 * http://labs.excilys.com/2012/06/26/runtime-exec-pour-les-nuls-et-
	 * processbuilder/
	 * 
	 * @param aCmdLine
	 * @param aCallBack
	 * @param aTimeOut
	 * @return the exit value of the process. By convention, 0 indicates normal
	 *         termination.
	 * @throws Exception
	 */
	public EXCommandState launch(final long aTimeOut, final File wUserDir,
			final Map<String, String> aEnv, final String... aCmdLineArgs)
			throws Exception {

		ProcessBuilder wProcessBuilder = new ProcessBuilder(aCmdLineArgs);

		wProcessBuilder.directory(wUserDir);

		if (aEnv != null && !aEnv.isEmpty()) {
			Map<String, String> wEnv = wProcessBuilder.environment();
			if (wEnv != null) {
				wEnv.putAll(aEnv);
			}
		}

		// Starts a new process using the attributes of this process builder.
		Process wProcess = wProcessBuilder.start();

		// Gets the input stream of the subprocess. The stream obtains data
		// piped from the standard output stream of the process represented by
		// this Process object.
		CBufferReader fluxSortie = new CBufferReader(wProcess.getInputStream(),
				pOsCommand.getOutBuffer());
		new Thread(fluxSortie).start();

		CBufferReader fluxErreur = new CBufferReader(wProcess.getErrorStream(),
				pOsCommand.getOutErrBuffer());
		new Thread(fluxErreur).start();

		return waitFor(wProcess, aTimeOut);
	}

	/**
	 * @param aDuration
	 * @return false if interupted, true if the sleeping is complete
	 */
	private boolean sleep(final long aDuration) {
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
	 * @return
	 */
	private EXCommandState waitFor(final Process aProcess, final long aTimeOut) {

		if (aTimeOut <= 0) {
			try {
				// causes the current thread to wait, if necessary, until the
				// process represented by this Process object has terminated.
				// This method returns immediately if the subprocess has already
				// terminated. If the subprocess has not yet terminated, the
				// calling thread will be blocked until the subprocess exits.
				return EXCommandState.exitValToState(aProcess.waitFor());
			} catch (InterruptedException e) {
				pLogger.logSevere(this, "waitFor", "InterruptedException");
				// sleep 50 milliseconds, si interrupted => STOPPED
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
			aProcess.destroy();
			return EXCommandState.CMD_RUN_TIMEOUT;
		}
	}
}
