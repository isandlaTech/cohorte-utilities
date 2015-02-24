package org.psem2m.utilities.system;

import java.io.File;
import java.io.OutputStream;
import java.util.Map;

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

	private final String FORMAT_CANT_WAIT_STRING = "%s, the waited string [%s] is not in the stdout of the process. timeout=[%s]";

	private final String MESS_UNABLE_TO_START = "Unable to start the server";

	private final String MESS_UNABLE_TO_WRITE = "Unable to write to the server";

	private EXServerState pServerState;

	private CXOSCommand pStopCommand = null;

	private long pStopDuration = -1;

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
	private CXOSCommand execOsCommand(final File aUserDir,
			final Map<String, String> aEnv, final long aTimeOut,
			final String... aCommandLine) {

		CXOSCommand wCommand = new CXOSCommand(pLogger, aCommandLine);
		wCommand.run(aTimeOut, aUserDir, aEnv);
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
		return getRepport(ERepportPart.ALL_PARTS);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.psem2m.utilities.system.IXOSRunner#getRepport(org.psem2m.utilities
	 * .system.EReprortPart[])
	 */
	@Override
	public String getRepport(final ERepportPart[] aParts) {

		StringBuilder wStopInfos = new StringBuilder(2048);

		wStopInfos.append(String.format("--> LauncherPid   =[%s]\n",
				(pXProcess != null) ? pXProcess.getPid() : -1));
		wStopInfos.append(String.format("--> ServerState   =[%s]\n",
				pServerState.name()));

		if (pStopCommand != null) {
			wStopInfos.append("--> StopCommand  =")
					.append(pStopCommand.getCommandLine()).append('\n');
			wStopInfos.append("--> StopDuration =")
					.append(CXTimer.nanoSecToMicroSecStr(pStopDuration))
					.append('\n');
		}

		StringBuilder wRepport = new StringBuilder(2048);

		wRepport.append(buildRepport(aParts, wStopInfos.toString()));
		if (pStopCommand != null) {
			wRepport.append(pStopCommand.getRepport());
		}
		return shiftTextLines(wRepport.toString(), "#OSServer > ");
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
	public boolean isStopped() {
		return pServerState == EXServerState.STOPPED;
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
					throw new Exception(String.format(FORMAT_CANT_WAIT_STRING,
							MESS_UNABLE_TO_START, aStrInStdOut, aStartTimeout));
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.system.IXOSServer#stop(java.io.File,
	 * java.util.Map, long, java.lang.String[])
	 */
	@Override
	public boolean stop(final File aUserDir, final Map<String, String> aEnv,
			final long aTimeOut, final String... aStopCommand) {
		pLogger.logDebug(this, "stop", "StopCommand=[%s]", aStopCommand);

		boolean wServerStopped = false;
		long wStartNanoTime = System.nanoTime();
		try {
			pServerState = EXServerState.STOPPING;

			pStopCommand = execOsCommand(aUserDir, aEnv, 10000, aStopCommand);

			wServerStopped = (pStopCommand.isRunOk());

			runDoAfter();

		} catch (Exception e) {

			setRunException(e);
		}

		pStopDuration = System.nanoTime() - wStartNanoTime;

		if (wServerStopped) {
			pServerState = EXServerState.STOPPED;
		}

		pLogger.logDebug(this, "stop",
				"Stopped=[%b] hasRunException=[%b] Stopping duration=[%s]",
				wServerStopped, hasRunException(),
				CXTimer.nanoSecToMicroSecStr(pStopDuration));

		return wServerStopped;
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

	/**
	 * @param aServerCommand
	 * @return
	 * @throws Exception
	 */
	public boolean write(final String aServerCommand, final String aCharSet)
			throws Exception {
		return writeAndWaitInStdOut(aServerCommand, aCharSet, -1, null);
	}

	/**
	 * @param aServerCommand
	 * @param aStartTimeout
	 * @param aStrInStdOu
	 * @return
	 * @throws Exception
	 */
	public boolean writeAndWaitInStdOut(final String aServerCommand,
			final String aCharSet, final long aStartTimeout,
			final String aStrInStdOut) throws Exception {

		if (!hasProcess()) {
			throw new Exception(
					"Can't write to the server. Process not available");
		}
		if (!getServerState().isStarted()) {
			throw new Exception(
					"Can't write to the server. Process not started");
		}

		boolean wWrote = false;
		long wStartNanoTime = System.nanoTime();
		try {
			// Returns the output stream connected to the normal input of the
			// subprocess. Output to the stream is piped into the standard input
			// of the process represented by this Process object.
			OutputStream wTargetStdIn = pXProcess.getProcess()
					.getOutputStream();
			byte[] wBuffer = aServerCommand.getBytes(aCharSet);
			wTargetStdIn.write(wBuffer);
			wTargetStdIn.flush();

			// wait for the passed aStrInStdOut in the stdout of the new
			// process
			if (aStrInStdOut != null) {
				wWrote = waitStrInStdOut(aStartTimeout, aStrInStdOut);
				if (!wWrote) {
					throw new Exception(String.format(FORMAT_CANT_WAIT_STRING,
							MESS_UNABLE_TO_WRITE, aStrInStdOut, aStartTimeout));
				}
			}

		} catch (Exception e) {

			setRunException(e);
		}

		pLogger.logDebug(this, "writeAndWaitInStdOut",
				"Wrote=[%b] hasRunException=[%b] Starting duration=[%s]",
				wWrote, hasRunException(), CXTimer.nanoSecToMicroSecStr(System
						.nanoTime() - wStartNanoTime));

		return wWrote;
	}
}
