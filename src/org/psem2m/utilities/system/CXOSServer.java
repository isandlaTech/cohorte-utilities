package org.psem2m.utilities.system;

import static org.psem2m.utilities.system.CXOSLauncher.waitForProcessEnd;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

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

	private final EXServerState pServerState;

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
	 * @see org.psem2m.utilities.system.IXOSServer#getServerState()
	 */
	@Override
	public EXServerState getServerState() {
		return pServerState;
	}

	/**
	 * @return
	 */
	protected boolean runDoAfter() {
		pRunTimeStop = System.nanoTime();
		return true;
	}

	/**
	 * @param aTimeOut
	 * @return
	 */
	protected boolean runDoBefore() {
		pRunTimeLaunch = System.currentTimeMillis();
		pRunTimeStart = System.nanoTime();
		pRunTimeStop = 0;
		razRunStdOutput();
		razRunStdOutputErr();
		pProcess = null;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.system.IXOSServer#start(java.io.File,
	 * java.util.Map)
	 */
	@Override
	public boolean start(final File aUserDir, final Map<String, String> aEnv)
			throws Exception {

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
			final String aStrInStdOut) throws Exception {
		pLogger.logDebug(this, "startAndWaitInStdOut", "UserDir=[%s]",
				aUserDir.getAbsolutePath());

		boolean wStarted = false;
		long wStartNanoTime = System.nanoTime();

		runDoBefore();

		CXOSLauncher wCXOSLauncher = new CXOSLauncher(pLogger, this);

		pProcess = wCXOSLauncher.start(aUserDir, aEnv, getCmdLineArgs());

		wStarted = (pProcess != null);

		if (wStarted && aStrInStdOut != null) {
			wStarted = waitStrInStdOut(aStartTimeout, aStrInStdOut);
		}

		pLogger.logDebug(this, "startAndWaitInStdOut",
				"Started=[%b] Duration=[%s]", wStarted, CXTimer
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
	public boolean stop(final long aTimeOut, final String... aStopCommand)
			throws IOException, InterruptedException {
		pLogger.logDebug(this, "stop", "StopCommand=[%s]", aStopCommand);

		boolean wStopped = false;
		long wStartNanoTime = System.nanoTime();

		CXOSCommand wOsCommand = execOsCommand(5000, aStopCommand);

		if (wOsCommand.isRunOk()) {
			EXCommandState wCommandState = waitForProcessEnd(pProcess, aTimeOut);
			wStopped = wCommandState.isRunOK();
		}

		pLogger.logDebug(
				this,
				"stop",
				"Stopped=[%b] Duration=[%s]",
				wStopped,
				CXTimer.nanoSecToMicroSecStr(System.nanoTime() - wStartNanoTime));

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
