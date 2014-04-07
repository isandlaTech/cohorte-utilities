package org.psem2m.utilities.system;

import java.io.File;
import java.util.Map;
import java.util.StringTokenizer;

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
public class CXOSCommand extends CXOSRunner implements IXOSCommand {

	private EXCommandState pCommandState;

	private Exception pRunException = null;

	private final EXCommandState pRunExitStateOk;

	private long pRunTimeOut = 0;

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
	 * @param aCommandLine
	 */
	public CXOSCommand(final IActivityLoggerBase aLogger,
			final EXCommandState aExitStateOk, final String... aCommandLine) {
		super(aLogger, aCommandLine);

		pRunExitStateOk = aExitStateOk;
		pLogger.logDebug(this, "<init>", "CommandLine nbParts=[%s]",
				aCommandLine.length);

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
	 * @see org.psem2m.utilities.system.IXOSCommand#getCommandState()
	 */
	@Override
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

	/**
	 * @return
	 */
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
			return CXStringUtils.strFullTrim(getStdOutBuffer().toString());
		} else {
			return new String();
		}
	}

	/**
	 * @return
	 */
	public String getRunStdOutputErr() {
		if (isLaunched() && hasRunStdOutputErr()) {
			return CXStringUtils.strFullTrim(getStdErrBuffer().toString());
		} else {
			return new String();
		}
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.system.IXOSCommand#run()
	 */
	@Override
	public boolean run() {
		return run(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.system.IXOSCommand#run(long)
	 */
	@Override
	public boolean run(final long aTimeOut) {

		return run(aTimeOut, CXFileDir.getUserDir(), null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.system.IXOSCommand#run(long)
	 */
	@Override
	public boolean run(final long aTimeOut, final File aUserDir) {

		return run(aTimeOut, aUserDir, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.system.IXOSCommand#run(long, java.io.File,
	 * java.util.Map)
	 */
	@Override
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
		razRunStdOutput();
		razRunStdOutputErr();
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
