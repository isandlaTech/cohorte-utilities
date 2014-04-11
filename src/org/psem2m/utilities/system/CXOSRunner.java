package org.psem2m.utilities.system;

import static org.psem2m.utilities.system.CXOSLauncher.secureLog;

import java.util.StringTokenizer;
import java.util.logging.Level;

import org.psem2m.utilities.CXDateTime;
import org.psem2m.utilities.CXOSUtils;
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

	private Exception pRunException = null;

	protected long pRunTimeLaunch = 0;
	private long pRunTimeOut = 0;
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

	/**
	 * @param aSB
	 * @param aText
	 * @return
	 */
	protected StringBuilder appenTextLinesInSB(final StringBuilder aSB,
			final String aText) {
		StringTokenizer wST = new StringTokenizer(aText, "\n");
		while (wST.hasMoreTokens()) {
			aSB.append(wST.nextToken());
			aSB.append('\n');
		}
		return aSB;
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
		// log with secured method
		secureLogDebug("consumeStdOutputErrLine", "StdErr=[%s]", aLine);

		if (pRunBuffStdErr.length() > 0) {
			pRunBuffStdErr.append('\n');
		}
		pRunBuffStdErr.append(aLine);
		return pRunBuffStdErr.length();
	}

	@Override
	public int consumeStdOutputLine(final String aLine) {
		// log with secured method
		secureLogDebug("consumeStdOutputLine", "StdOut=[%s]", aLine);

		if (pRunBuffStdOut.length() > 0) {
			pRunBuffStdOut.append('\n');
		}
		pRunBuffStdOut.append(aLine);
		return pRunBuffStdOut.length();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.psem2m.utilities.system.IXOSRunner#consumeStdOutputLine(java.lang
	 * .String)
	 */
	@Override
	public String getBuffEncoding() {
		return CXOSUtils.getOsFileEncoding();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.system.IXOSRunner#getBuffEncoding()
	 */
	@Override
	public String[] getCmdLineArgs() {
		return pCmdLineArgs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.system.IXOSCommand#getCmdLineArgs()
	 */
	@Override
	public String getCommandLine() {
		return CXStringUtils.stringTableToString(getCmdLineArgs(), " ");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.system.IXOSCommand#getCommandLine()
	 */
	@Override
	public String getLaunchTimeStamp() {
		return CXDateTime.getIso8601TimeStamp(pRunTimeLaunch);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.system.IXOSRunner#getLaunchTimeStamp()
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
	 * @see org.psem2m.utilities.system.IXOSRunner#getRunElapsedTime()
	 */
	/**
	 * @return
	 */
	public Exception getRunException() {
		return pRunException;
	}

	@Override
	public String getRunStdErr() {
		if (isLaunched() && hasRunStdOutputErr()) {
			return CXStringUtils.strFullTrim(getStdErrBuffer().toString());
		} else {
			return new String();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.system.IXOSRunner#getRunStdErr()
	 */
	@Override
	public String getRunStdOut() {
		if (isLaunched() && hasRunStdOutput()) {
			return CXStringUtils.strFullTrim(getStdOutBuffer().toString());
		} else {
			return new String();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.system.IXOSRunner#getRunStdOut()
	 */
	/**
	 * @return
	 */
	public long getRunTimeOut() {
		return pRunTimeOut;
	}

	@Override
	public StringBuilder getStdErrBuffer() {
		return pRunBuffStdErr;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.system.IXOSCommand#getOutErrBuffer()
	 */
	@Override
	public StringBuilder getStdOutBuffer() {
		return pRunBuffStdOut;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.system.IXOSCommand#getOutBuffer()
	 */
	@Override
	public boolean hasRunException() {
		return getRunException() != null;
	}

	@Override
	public boolean hasRunStdOutput() {
		return pRunBuffStdOut != null && pRunBuffStdOut.length() > 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.system.IXOSRunner#hasRunStdOutput()
	 */
	@Override
	public boolean hasRunStdOutputErr() {
		return pRunBuffStdErr != null && pRunBuffStdErr.length() > 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.system.IXOSRunner#hasRunStdOutputErr()
	 */
	@Override
	public boolean hasRunTimeOut() {
		return pRunTimeOut > 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.system.IXOSRunner#hasRunTimeOut()
	 */
	/**
	 * @return
	 */
	@Deprecated
	public boolean isRunException() {
		return hasRunException();
	}

	/**
	 * @param aWhat
	 * @param aInfos
	 */
	private void secureLogDebug(final CharSequence aWhat, final Object... aInfos) {
		secureLog(pLogger, Level.FINE, this, aWhat, aInfos);
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
		setRunTimeOut(aTimeOut);
		razRunStdOutput();
		razRunStdOutputErr();
		setRunException(null);
		return true;
	}

	/**
	 * @param aExep
	 */
	protected void setRunException(final Exception aExep) {
		pRunException = aExep;
	}

	/**
	 * @return
	 */
	protected void setRunTimeOut(final long aRunTimeOut) {
		pRunTimeOut = aRunTimeOut;
	}

}
