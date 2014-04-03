package org.psem2m.utilities.system;

import java.io.File;
import java.util.Map;
import java.util.StringTokenizer;

import org.psem2m.utilities.CXDateTime;
import org.psem2m.utilities.CXOSUtils;
import org.psem2m.utilities.CXStringUtils;
import org.psem2m.utilities.CXTimer;
import org.psem2m.utilities.files.CXFileDir;
import org.psem2m.utilities.logging.CActivityLoggerNull;
import org.psem2m.utilities.logging.IActivityLoggerBase;

/**
 * Represente une commande systeme
 * 
 * Permet d'executer une commande et d'obtenir le resultat de l'execution
 * 
 */
public class CXOSCommand implements IXOSCommand {

	private final String[] pCmdLineArgs;

	private EXCommandState pCommandState;

	private final IActivityLoggerBase pLogger;
	private StringBuilder pRunBuffErr = new StringBuilder();
	private StringBuilder pRunBuffOutput = new StringBuilder();

	private Exception pRunException = null;

	private final EXCommandState pRunExitStateOk;

	private long pRunTimeLaunch = 0;
	private long pRunTimeOut = 0;
	private long pRunTimeStart = 0;
	private long pRunTimeStop = 0;

	/**
	 * aExitValueOk : Valeur renvoyee par la commande si OK (0 par defaut)
	 * 
	 * @param aCommandLine
	 * @param aExitValueOk
	 */
	public CXOSCommand(final EXCommandState aExitValueOk,
			final String... aCommandLine) {
		this(CActivityLoggerNull.getInstance(), aExitValueOk, aCommandLine);
	}

	/**
	 * @param aLogger
	 * @param aExitValueOk
	 *            Valeur renvoyee par la commande si OK (0 par defaut)
	 * @param aCmdLineArgs
	 */
	public CXOSCommand(final IActivityLoggerBase aLogger,
			final EXCommandState aExitStateOk, final String... aCmdLineArgs) {
		super();
		pLogger = (aLogger != null) ? aLogger : CActivityLoggerNull
				.getInstance();
		pCmdLineArgs = aCmdLineArgs;
		pRunExitStateOk = aExitStateOk;
		pLogger.logDebug(this, "<init>", "CommandLine=[%s]", getCommandLine());

	}

	/**
	 * @param aTracer
	 * @param aCommandLine
	 */
	public CXOSCommand(final IActivityLoggerBase aTracer,
			final String... aCommandLine) {
		this(aTracer, EXCommandState.CMD_RUN_OK, aCommandLine);
	}

	/**
	 * @param aCommand
	 *            La ligne de commande
	 */
	public CXOSCommand(final String... aCommand) {
		this(EXCommandState.CMD_RUN_OK, aCommand);
	}

	/**
	 * @param aSB
	 * @param aText
	 * @return
	 */
	private StringBuilder appenTextLinesInSB(final StringBuilder aSB,
			final String aText) {
		StringTokenizer wST = new StringTokenizer(aText, "\n");
		while (wST.hasMoreTokens()) {
			aSB.append(wST.nextToken());
			aSB.append('\n');
		}
		return aSB;
	}

	/**
	 * @return
	 */
	public String getBuffEncoding() {
		return CXOSUtils.getOsFileEncoding();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.system.IXOSCommand#getCmdLineArgs()
	 */
	@Override
	public String[] getCmdLineArgs() {
		return pCmdLineArgs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.system.IXOSCommand#getCommandLine()
	 */
	@Override
	public String getCommandLine() {
		return CXStringUtils.stringTableToString(getCmdLineArgs(), " ");
	}

	public EXCommandState getCommandState() {
		return pCommandState;
	}

	/**
	 * @return
	 */
	public String getErrMess() {
		StringBuffer wSB = new StringBuffer();
		if (hasRunStdOutputErr()) {
			wSB.append(getRunStdOutputErr());
		}
		if (isRunException()) {
			if (wSB.length() > 0) {
				wSB.append(", ");
			}
			wSB.append(getRunException().getClass().getName());
			wSB.append(", ");
			wSB.append(getRunException().getMessage());
		}

		return wSB.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.system.IXOSCommand#getOutBuffer()
	 */
	@Override
	public StringBuilder getOutBuffer() {
		return pRunBuffOutput;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.system.IXOSCommand#getOutErrBuffer()
	 */
	@Override
	public StringBuilder getOutErrBuffer() {
		return pRunBuffErr;
	}

	public String getRepport() {
		StringBuilder wResult = new StringBuilder(2048);
		wResult.append("CommandLine   : ").append(getCommandLine())
				.append('\n');
		wResult.append("OutputEncoding: ").append(getBuffEncoding())
				.append(" (").append(CXOSUtils.getOsName()).append(',')
				.append(CXOSUtils.getOsFileEncoding()).append(')').append('\n');
		wResult.append("Launched      : ");
		if (isLaunched()) {
			wResult.append(CXDateTime.getIso8601TimeStamp(pRunTimeLaunch))
					.append('\n');
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
					.append((pRunTimeOut > 0) ? pRunTimeOut : "undefined")
					.append('\n');
			wResult.append("--> isRunOk     =").append(isRunOk()).append('\n');
			wResult.append("--> isExitOk    =").append(isExitOk()).append('\n');
			wResult.append("--> ExitValue   =").append(getRunExitString())
					.append('\n');

			if (isRunException()) {
				wResult.append("--> RunException=").append(isRunException())
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
			if (isRunTimeOut()) {
				wResult.append("--> RunTimeOut  =").append(isRunTimeOut())
						.append('\n');
			}

			if (hasRunStdOutput()) {
				wResult.append("--> BUFFER OUTPUT\n");
				appenTextLinesInSB(wResult, getRunStdOutput());
			}
			if (hasRunStdOutputErr()) {
				wResult.append("--> BUFFER ERROR\n");
				appenTextLinesInSB(wResult, getRunStdOutputErr());
			}
		}
		return wResult.toString();
	}

	/**
	 * @return
	 */
	public long getRunElapsedTime() {
		if (pRunTimeStop != 0 && pRunTimeStart != 0) {
			return pRunTimeStop - pRunTimeStart;
		} else {
			return 0;
		}
	}

	/**
	 * @return
	 */
	public Exception getRunException() {
		return pRunException;
	}

	/**
	 * 
	 * @return
	 */
	public String getRunExitString() {
		return getCommandState().name();
	}

	/**
	 * @return
	 */
	public int getRunExitValue() {
		return getCommandState().getVal();
	}

	/**
	 * @return
	 */
	public String getRunStdOutput() {
		if (isLaunched() && hasRunStdOutput()) {
			return CXStringUtils.strFullTrim(pRunBuffOutput.toString());
		} else {
			return new String();
		}
	}

	/**
	 * @return
	 */
	public String getRunStdOutputErr() {
		if (isLaunched() && hasRunStdOutputErr()) {
			return CXStringUtils.strFullTrim(pRunBuffErr.toString());
		} else {
			return new String();
		}
	}

	/**
	 * @return
	 */
	public boolean hasRunStdOutput() {
		return pRunBuffOutput != null && pRunBuffOutput.length() > 0;
	}

	/**
	 * @return
	 */
	public boolean hasRunStdOutputErr() {
		return pRunBuffErr != null && pRunBuffErr.length() > 0;
	}

	/**
	 * @return
	 */
	public boolean isExitOk() {
		return getCommandState() == pRunExitStateOk;
	}

	/**
	 * @return
	 */
	public boolean isLaunched() {
		return pRunTimeStop != 0 && pRunTimeStart != 0;
	}

	/**
	 * @return
	 */
	public boolean isRunException() {
		return getRunException() != null;
	}

	/**
	 * @return
	 */
	public boolean isRunOk() {
		return isLaunched()
				&& isExitOk()
				&& !(isRunException() || isRunTimeOut() || hasRunStdOutputErr());
	}

	/**
	 * @return
	 */
	public boolean isRunTimeOut() {
		return pCommandState == EXCommandState.CMD_RUN_TIMEOUT;
	}

	/**
	 * 
	 */
	public void razRunStdOutputErr() {
		pRunBuffErr = null;
	}

	/**
	 * @return true if all is OK
	 */
	public boolean run() {
		return run(0);
	}

	/**
	 * @param aTimeOut
	 *            no timeout if <= 0
	 * @return true if all is OK
	 */
	public boolean run(final long aTimeOut) {

		return run(aTimeOut, CXFileDir.getUserDir(), null);
	}

	/**
	 * @param aTimeOut
	 *            no timeout if <= 0
	 * @param aUserDir
	 * @param aEnv
	 * @return true if all is OK
	 */
	public boolean run(final long aTimeOut, final File aUserDir,
			final Map<String, String> aEnv) {

		pLogger.logDebug(this, "run", "TimeOut=[%s]", aTimeOut);
		try {
			if (runDoBefore(aTimeOut)) {
				CXOSLauncher wCXOSLauncher = new CXOSLauncher(pLogger, this);
				pCommandState = wCXOSLauncher.launch(aTimeOut,
						CXFileDir.getUserDir(), aEnv, getCmdLineArgs());
			}
		} catch (Exception e) {
			setRunException(e);
		}
		return runDoAfter();
	}

	/**
	 * @return
	 */
	protected boolean runDoAfter() {
		pRunTimeStop = System.nanoTime();
		return isRunOk();
	}

	/**
	 * @param aTimeOut
	 * @return
	 */
	protected boolean runDoBefore(final long aTimeOut) {
		pRunTimeLaunch = System.currentTimeMillis();
		pRunTimeStart = System.nanoTime();
		pRunTimeStop = 0;
		pRunTimeOut = aTimeOut;
		pRunBuffOutput = new StringBuilder();
		pRunBuffErr = new StringBuilder();
		pCommandState = EXCommandState.CMD_RUN_NO;
		pRunException = null;
		return true;
	}

	/**
	 * @param aExep
	 */
	private void setRunException(final Exception aExep) {
		pRunException = aExep;
		pCommandState = EXCommandState.CMD_RUN_EXCEPTION;
	}
}
