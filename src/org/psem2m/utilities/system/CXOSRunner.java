package org.psem2m.utilities.system;

import org.psem2m.utilities.CXDateTime;
import org.psem2m.utilities.CXStringUtils;
import org.psem2m.utilities.logging.CActivityLoggerNull;
import org.psem2m.utilities.logging.IActivityLoggerBase;

/**
 * @author ogattaz
 * 
 */
public abstract class CXOSRunner implements IXOSRunner {

	private final String[] pCmdLineArgs;

	protected final IActivityLoggerBase pLogger;

	private final StringBuilder pRunBuffStdErr = new StringBuilder();
	private final StringBuilder pRunBuffStdOut = new StringBuilder();

	protected long pRunTimeLaunch = 0;
	protected long pRunTimeStart = 0;
	protected long pRunTimeStop = 0;

	/**
	 * 
	 */
	public CXOSRunner(final IActivityLoggerBase aLogger,
			final String... aCmdLineArgs) {
		super();
		pLogger = (aLogger != null) ? aLogger : CActivityLoggerNull
				.getInstance();
		pCmdLineArgs = aCmdLineArgs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.psem2m.utilities.system.IXOSRunner#consumeStdOutputErrLine(java.lang
	 * .String)
	 */
	@Override
	public int consumeStdOutputErrLine(final String aLine) {
		pLogger.logDebug(this, "consumeStdOutputErrLine", "StdErr=[%s]", aLine);

		if (pRunBuffStdErr.length() > 0) {
			pRunBuffStdErr.append('\n');
		}
		pRunBuffStdErr.append(aLine);
		return pRunBuffStdErr.length();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.psem2m.utilities.system.IXOSRunner#consumeStdOutputLine(java.lang
	 * .String)
	 */
	@Override
	public int consumeStdOutputLine(final String aLine) {
		pLogger.logDebug(this, "consumeStdOutputLine", "StdOut=[%s]", aLine);

		if (pRunBuffStdOut.length() > 0) {
			pRunBuffStdOut.append('\n');
		}
		pRunBuffStdOut.append(aLine);
		return pRunBuffStdOut.length();
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.system.IXOSRunner#getLaunchTimeStamp()
	 */
	@Override
	public String getLaunchTimeStamp() {
		return CXDateTime.getIso8601TimeStamp(pRunTimeLaunch);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.system.IXOSCommand#getOutBuffer()
	 */
	@Override
	public StringBuilder getStdOutBuffer() {
		return pRunBuffStdOut;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.system.IXOSCommand#getOutErrBuffer()
	 */
	@Override
	public StringBuilder getStdErrBuffer() {
		return pRunBuffStdErr;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.system.IXOSRunner#getRunElapsedTime()
	 */
	@Override
	public long getRunElapsedTime() {
		if (pRunTimeStop != 0 && pRunTimeStart != 0) {
			return pRunTimeStop - pRunTimeStart;
		} else {
			return 0;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.system.IXOSRunner#hasRunStdOutput()
	 */
	@Override
	public boolean hasRunStdOutput() {
		return pRunBuffStdOut != null && pRunBuffStdOut.length() > 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.system.IXOSRunner#hasRunStdOutputErr()
	 */
	@Override
	public boolean hasRunStdOutputErr() {
		return pRunBuffStdErr != null && pRunBuffStdErr.length() > 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.system.IXOSRunner#razRunStdOutput()
	 */
	@Override
	public void razRunStdOutput() {
		pRunBuffStdOut.delete(0, pRunBuffStdOut.length());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.system.IXOSRunner#razRunStdOutputErr()
	 */
	@Override
	public void razRunStdOutputErr() {
		pRunBuffStdErr.delete(0, pRunBuffStdErr.length());
	}
}
