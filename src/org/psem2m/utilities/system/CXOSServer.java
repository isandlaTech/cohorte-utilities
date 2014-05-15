package org.psem2m.utilities.system;

import static org.psem2m.utilities.system.CXOSLauncher.waitForProcessEnd;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.psem2m.utilities.CXOSUtils;
import org.psem2m.utilities.CXStringUtils;
import org.psem2m.utilities.CXTimer;
import org.psem2m.utilities.logging.IActivityLoggerBase;

/**
 * @see http://www.golesny.de/p/code/javagetpid
 * 
 * @author ogattaz
 * 
 */
public class CXOSServer extends CXOSRunner implements IXOSServer {

	public static final byte[] CTRLC = { (byte) 0x03 };

	private EXServerState pServerState;

	private CXProcess pXProcess = null;

	/**
	 * @param aLogger
	 * @param aCommandLine
	 */
	public CXOSServer(final IActivityLoggerBase aLogger,
			final String... aCommandLine) {
		super(aLogger, aCommandLine);

		pServerState = EXServerState.INSTANCIATED;
		pLogger.logDebug(this, "<init>", "CommandLine=[%s]",
				aCommandLine.length);
	}

	/**
	 * @param aTimeOut
	 * @param aCommandLine
	 * @return
	 */
	private CXOSCommand execOsCommand(final long aTimeOut,
			final String... aCommandLine) {

		CXOSCommand wCommand = new CXOSCommand(pLogger, aCommandLine);
		wCommand.run(aTimeOut);
		return wCommand;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.system.IXOSServer#getPid()
	 * 
	 * @see http://www.golesny.de/p/code/javagetpid
	 */
	@Override
	public int getPid() {
		return hasProcess() ? pXProcess.getPid() : -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.system.IXOSRunner#getRepport()
	 */
	@Override
	public String getRepport() {
		StringBuilder wResult = new StringBuilder(2048);
		wResult.append("CommandLine   : ").append(getCommandLine())
				.append('\n');
		wResult.append("OutputEncoding: ").append(getBuffEncoding())
				.append(" (").append(CXOSUtils.getOsName()).append(',')
				.append(CXOSUtils.getOsFileEncoding()).append(')').append('\n');
		wResult.append("Launched      : ");
		if (isLaunched()) {
			wResult.append(getLaunchTimeStamp()).append('\n');
		} else {
			wResult.append("Not launched.\n");
		}
		if (isLaunched()) {
			wResult.append("--> LaunchResult=")
					.append(CXStringUtils.boolToOkKo(isRunOk())).append('\n');
			wResult.append("--> ElapsedTime =")
					.append(CXTimer.nanoSecToMicroSecStr(getRunElapsedTime()))
					.append('\n');
			wResult.append("--> Timeout     =")
					.append((hasRunTimeOut()) ? getRunTimeOut() : "undefined")
					.append('\n');
			wResult.append("--> isRunOk     =").append(isRunOk()).append('\n');
			/*
			 * wResult.append("--> isExitOk    =").append(isExitOk()).append('\n'
			 * ); wResult.append("--> ExitValue   =").append(getRunExitString())
			 * .append('\n');
			 */
			if (hasRunException()) {
				wResult.append("--> RunException=").append(hasRunException())
						.append('\n');
				wResult.append("--> Name        =")
						.append(getRunException().getClass().getName())
						.append('\n');
				wResult.append("--> Message     =")
						.append(getRunException().getMessage()).append('\n');
				wResult.append(
						CXStringUtils.getExceptionStack(getRunException()))
						.append('\n');
			}
			if (isRunTimeOutDetected()) {
				wResult.append("--> RunTimeOut  =")
						.append(isRunTimeOutDetected()).append('\n');
			}

			if (hasRunStdOutput()) {
				wResult.append("--> BUFFER OUTPUT\n");
				appenTextLinesInSB(wResult, getRunStdOut());
			}
			if (hasRunStdOutputErr()) {
				wResult.append("--> BUFFER ERROR\n");
				appenTextLinesInSB(wResult, getRunStdErr());
			}
		}
		return wResult.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.system.IXOSServer#getServerState()
	 */
	@Override
	public EXServerState getServerState() {
		return pServerState;
	}

	/**
	 * @return
	 */
	private boolean hasProcess() {
		return (pXProcess != null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.system.IXOSRunner#isLaunched()
	 */
	@Override
	public boolean isLaunched() {
		return pRunTimeStart != 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.system.IXOSRunner#isRunOk()
	 */
	@Override
	public boolean isRunOk() {
		return isLaunched() && pXProcess != null && !hasRunException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.system.IXOSRunner#isRunTimeOut()
	 */
	@Override
	public boolean isRunTimeOutDetected() {
		return pServerState == EXServerState.TIMEOUT;
	}

	/**
	 * @return
	 */
	@Override
	protected boolean runDoAfter() {
		super.runDoAfter();
		pServerState = EXServerState.STOPPED;
		return true;
	}

	/**
	 * @param aTimeOut
	 * @return
	 */
	@Override
	protected boolean runDoBefore(final long aTimeOut) {
		super.runDoBefore(aTimeOut);
		pServerState = EXServerState.STARTING;
		return true;
	}

	/**
	 * @param aProcess
	 */
	private void setProcess(final CXProcess aProcess) {
		pXProcess = aProcess;
	}

	/**
	 * @param aProcess
	 */
	private void setProcess(final Process aProcess) {
		setProcess(CXProcess.newCXProcess(aProcess));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.psem2m.utilities.system.CXOSRunner#setRunException(java.lang.Exception
	 * )
	 */
	@Override
	protected void setRunException(final Exception aException) {
		super.setRunException(aException);
		if (aException != null) {
			pServerState = EXServerState.EXCEPTION;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.system.IXOSServer#start(java.io.File,
	 * java.util.Map)
	 */
	@Override
	public boolean start(final File aUserDir, final Map<String, String> aEnv) {

		return startAndWaitInStdOut(aUserDir, aEnv, -1, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.system.IXOSServer#start(java.io.File,
	 * java.util.Map)
	 */
	@Override
	public boolean startAndWaitInStdOut(final File aUserDir,
			final Map<String, String> aEnv, final long aStartTimeout,
			final String aStrInStdOut) {
		pLogger.logDebug(this, "startAndWaitInStdOut", "UserDir=[%s]",
				aUserDir.getAbsolutePath());

		boolean wStarted = false;
		long wStartNanoTime = System.nanoTime();

		try {
			runDoBefore(aStartTimeout);

			CXOSLauncher wCXOSLauncher = new CXOSLauncher(pLogger, this);

			Process wProcess = wCXOSLauncher.start(aUserDir, aEnv,
					getCmdLineArgs());

			wStarted = (wProcess != null);

			if (!wStarted) {
				throw new Exception(
						"Unable to start the server, the start method of the ProcessBuilder return null");
			}

			// wait for the passed aStrInStdOut in the stdout of the new
			// process
			if (aStrInStdOut != null) {
				wStarted = waitStrInStdOut(aStartTimeout, aStrInStdOut);
				if (!wStarted) {
					throw new Exception(
							String.format(
									"Unable to start the server, the string [%s] is not in the stdout of the process. timeout=[%s]",
									aStrInStdOut, aStartTimeout));
				}
			}

			// set the server state
			pServerState = EXServerState.STARTED;
			// set the XProcess
			setProcess(wProcess);

		} catch (Exception e) {

			setRunException(e);
		}

		pLogger.logDebug(this, "startAndWaitInStdOut",
				"Started=[%b] hasRunException=[%b] Starting duration=[%s]",
				wStarted, hasRunException(), CXTimer
						.nanoSecToMicroSecStr(System.nanoTime()
								- wStartNanoTime));
		return wStarted;
	}

	/**
	 * @param aTimeOut
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Override
	public boolean stop(final long aTimeOut, final String... aStopCommand) {
		pLogger.logDebug(this, "stop", "StopCommand=[%s]", aStopCommand);

		boolean wStopped = false;
		long wStartNanoTime = System.nanoTime();
		try {
			pServerState = EXServerState.STOPPING;

			CXOSCommand wOsCommand = execOsCommand(5000, aStopCommand);

			if (wOsCommand.isRunOk()) {
				EXCommandState wCommandState = waitForProcessEnd(
						pXProcess.getProcess(), aTimeOut);
				wStopped = wCommandState.isRunOK();
			}

			runDoAfter();

		} catch (Exception e) {

			setRunException(e);
		}

		pLogger.logDebug(this, "stop",
				"Stopped=[%b] hasRunException=[%b] Stopping duration=[%s]",
				wStopped, hasRunException(), CXTimer
						.nanoSecToMicroSecStr(System.nanoTime()
								- wStartNanoTime));

		return wStopped;
	}

	/**
	 * @param aTimeOut
	 * @param aString
	 * @return
	 */
	public boolean waitStrInStdOut(final long aTimeOut, final String aString) {

		long wStartNanoTime = System.nanoTime();

		boolean wContinue = true;
		boolean wFound = false;
		long wStartTimeout = System.currentTimeMillis();

		while (wContinue) {

			wFound = (getStdOutBuffer().indexOf(aString) > -1);
			// chaine trouvée
			if (wFound) {
				wContinue = false;
			}
			// si chaine pas trouvée => test timeout
			if (wContinue) {
				wContinue = (System.currentTimeMillis() - wStartTimeout < aTimeOut);
			}
			// si chaine pas trouvée et si pas timeout => sleep 0,1 seconde
			if (wContinue) {
				// si sleep interrompu (return false) => interruption du wait
				wContinue = CXOSLauncher.sleep(50);
			}
		}

		pLogger.logInfo(this, "waitForStrInStdOut",
				"StrFound=[%b], Duration=[%s]  strToFind=[%s] timeout=[%s]",
				wFound, CXTimer.nanoSecToMicroSecStr(System.nanoTime()
						- wStartNanoTime), aString, aTimeOut);
		return wFound;
	}
}
