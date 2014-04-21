package org.psem2m.utilities.system;

import static org.psem2m.utilities.system.CXOSLauncher.waitForProcessEnd;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

import org.psem2m.utilities.CXOSUtils;
import org.psem2m.utilities.CXStringUtils;
import org.psem2m.utilities.CXTimer;
import org.psem2m.utilities.logging.IActivityLoggerBase;
import org.psem2m.utilities.system.win32.Kernel32;
import org.psem2m.utilities.system.win32.W32API;

import com.sun.jna.Pointer;

/**
 * @see http://www.golesny.de/p/code/javagetpid
 * 
 * @author ogattaz
 * 
 */
public class CXOSServer extends CXOSRunner implements IXOSServer {

	public static final byte[] CTRLC = { (byte) 0x03 };
	
	Process pProcess = null;

	private EXServerState pServerState;

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
		int wPid = -1;
		// on Unix
		if (pProcess.getClass().getName().equals("java.lang.UNIXProcess")) {
			/* get the PID on unix/linux systems */
			try {
				Field f = pProcess.getClass().getDeclaredField("pid");
				f.setAccessible(true);
				// décalage de 20 , sur Mac Os X ?
				wPid = f.getInt(pProcess) + 20;
			} catch (Throwable e) {
			}
		}
		// on Windows
		// Download the jna.jar on Suns JNA Site
		else if (pProcess.getClass().getName().equals("java.lang.Win32Process")
				|| pProcess.getClass().getName()
						.equals("java.lang.ProcessImpl")) {
			/* determine the pid on windows plattforms */
			try {
				Field f = pProcess.getClass().getDeclaredField("handle");
				f.setAccessible(true);
				long handl = f.getLong(pProcess);

				Kernel32 kernel = Kernel32.INSTANCE;
				W32API.HANDLE handle = new W32API.HANDLE();
				handle.setPointer(Pointer.createConstant(handl));
				wPid = kernel.GetProcessId(handle);
			} catch (Throwable e) {
			}
		}
		return wPid;
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
		return isLaunched() && pProcess != null && !hasRunException();
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

			pProcess = wCXOSLauncher.start(aUserDir, aEnv, getCmdLineArgs());

			wStarted = (pProcess != null);

			if (wStarted && aStrInStdOut != null) {
				wStarted = waitStrInStdOut(aStartTimeout, aStrInStdOut);
			}

			pServerState = EXServerState.STARTED;

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
				EXCommandState wCommandState = waitForProcessEnd(pProcess,
						aTimeOut);
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
				"StrFound=[%b], Duration=[%s]", wFound, CXTimer
						.nanoSecToMicroSecStr(System.nanoTime()
								- wStartNanoTime));
		return wFound;
	}
}
