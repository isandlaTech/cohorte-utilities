package org.psem2m.utilities.system;

import java.io.File;
import java.util.Map;

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

	private final EXCommandState pRunExitStateOk;

	/**
	 * aExitValueOk : Valeur renvoyee par la commande si OK (0 par defaut)
	 * 
	 * @param aCommandLine
	 * @param aExitValueOk
	 */
	public CXOSCommand(final EXCommandState aExitValueOk, final String... aCommandLine) {
		this(CActivityLoggerNull.getInstance(), aExitValueOk, aCommandLine);
	}

	/**
	 * @param aLogger
	 * @param aExitValueOk
	 *            Valeur renvoyee par la commande si OK (0 par defaut)
	 * @param aCommandLine
	 */
	public CXOSCommand(final IActivityLoggerBase aLogger, final EXCommandState aExitStateOk,
			final String... aCommandLine) {
		super(aLogger, aCommandLine);

		pRunExitStateOk = aExitStateOk;
		pLogger.logDebug(this, "<init>", "CommandLine nbParts=[%s]", aCommandLine.length);

	}

	/**
	 * @param aTracer
	 * @param aCommandLine
	 */
	public CXOSCommand(final IActivityLoggerBase aTracer, final String... aCommandLine) {
		this(aTracer, EXCommandState.CMD_RUN_OK, aCommandLine);
	}

	/**
	 * @param aCommand
	 *            La ligne de commande
	 */
	public CXOSCommand(final String... aCommand) {
		this(EXCommandState.CMD_RUN_OK, aCommand);
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
		if (hasRunException()) {
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

		StringBuilder wExitInfos = new StringBuilder(2048);
		wExitInfos.append("--> isExitOk    =").append(isExitOk()).append('\n');
		wExitInfos.append("--> ExitValue   =").append(getRunExitString()).append('\n');
		return shiftTextLines(buildRepport(aParts, wExitInfos.toString()), "#OSCommand > ");
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
	@Deprecated
	public String getRunStdOutput() {
		return getRunStdOut();
	}

	/**
	 * @return
	 */
	@Deprecated
	public String getRunStdOutputErr() {
		return getRunStdErr();
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
	@Override
	public boolean isLaunched() {
		return pRunTimeStop != 0 && pRunTimeStart != 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.system.IXOSRunner#isRunOk()
	 */
	@Override
	public boolean isRunOk() {
		return isLaunched() && isExitOk() && !(hasRunException() || isRunTimeOutDetected() || hasRunStdOutputErr());
	}

	/**
	 * @return
	 */
	@Deprecated
	public boolean isRunTimeOut() {
		return isRunTimeOutDetected();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.system.IXOSRunner#isRunTimeOut()
	 */
	@Override
	public boolean isRunTimeOutDetected() {
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
	public boolean run(final long aTimeOut, final File aUserDir, final Map<String, String> aEnv) {
		// protection
		File wUserDir = (aUserDir != null) ? aUserDir : CXFileDir.getUserDir();

		if (pLogger.isLogDebugOn()) {
			boolean wChangeUserDir = !CXFileDir.getUserDir().equals(wUserDir.getAbsolutePath());
			pLogger.logDebug(this, "run", "TimeOut=[%s] wChangeUserDir=[%b]", aTimeOut, wChangeUserDir);
		}
		try {
			if (runDoBefore(aTimeOut)) {
				CXOSLauncher wCXOSLauncher = new CXOSLauncher(pLogger, this);
				pCommandState = wCXOSLauncher.launch(aTimeOut, wUserDir, aEnv, getCmdLineArgs());
				pLogger.logDebug(this, "run", "CommandState=[%s]", pCommandState.name());
			}
		} catch (Exception e) {
			setRunException(e);
		}
		return runDoAfter();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.system.IXOSCommand#run(long, java.io.File,
	 * java.util.Map)
	 */
	@Override
	public boolean runAntWait(final long aTimeOut, final File aUserDir, String aCharacterEnd,
			final Map<String, String> aEnv) {
		// protection
		File wUserDir = (aUserDir != null) ? aUserDir : CXFileDir.getUserDir();

		if (pLogger.isLogDebugOn()) {
			boolean wChangeUserDir = !CXFileDir.getUserDir().equals(wUserDir.getAbsolutePath());
			pLogger.logDebug(this, "runAntWait", "TimeOut=[%s] wChangeUserDir=[%b]", aTimeOut, wChangeUserDir);
		}
		try {
			if (runDoBefore(aTimeOut)) {
				CXOSLauncher wCXOSLauncher = new CXOSLauncher(pLogger, this);
				pCommandState = wCXOSLauncher.launch(aTimeOut, wUserDir, aEnv, getCmdLineArgs());
				if (aCharacterEnd != null) {
					// wait if characterEnd is setted
					pLogger.logDebug(this, "runAntWait", "wait characterEnd %s", aCharacterEnd);
					while (!getRunStdOut().contains(aCharacterEnd)) {
						CXOSLauncher.sleep(50);
					}
					pLogger.logDebug(this, "runAntWait", "characterEnd %s found ", aCharacterEnd);

				}
				pLogger.logDebug(this, "run", "CommandState=[%s]", pCommandState.name());
			}
		} catch (Exception e) {
			setRunException(e);
		}
		return runDoAfter();
	}

	/**
	 * @param aTimeOut
	 * @return
	 */
	@Override
	protected boolean runDoBefore(final long aTimeOut) {
		super.runDoBefore(aTimeOut);
		pCommandState = EXCommandState.CMD_RUN_NO;
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
			pCommandState = EXCommandState.CMD_RUN_EXCEPTION;
		}
	}
}
