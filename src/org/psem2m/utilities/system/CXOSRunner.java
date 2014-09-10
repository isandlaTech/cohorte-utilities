package org.psem2m.utilities.system;

import static org.psem2m.utilities.system.CXOSLauncher.secureLog;

import java.util.StringTokenizer;
import java.util.logging.Level;

import org.psem2m.utilities.CXDateTime;
import org.psem2m.utilities.CXOSUtils;
import org.psem2m.utilities.CXStringUtils;
import org.psem2m.utilities.CXTimer;
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

	public String buildRepport(final String aExitInfos) {
		StringBuilder wResult = new StringBuilder(2048);
		wResult.append("---------- Command repport ----------").append('\n');
		wResult.append("CommandLine        : ").append(getCommandLine())
				.append('\n');
		wResult.append("OutputEncoding     : ").append(getBuffEncoding())
				.append(" (").append(CXOSUtils.getOsName()).append(',')
				.append(CXOSUtils.getOsFileEncoding()).append(')').append('\n');
		wResult.append("Launching TimeStamp : ");
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
			if (aExitInfos != null) {
				wResult.append(aExitInfos);
			}
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
	 * @see
	 * org.psem2m.utilities.system.IXOSRunner#consumeStdOutputErrLine(java.lang
	 * .String)
	 */
	@Override
	public int consumeStdOutputErrLine(final String aLine) {
		// log with secured method
		secureLogFinest("consumeStdOutputErrLine", "StdErr=[%s]", aLine);

		if (pRunBuffStdErr.length() > 0) {
			pRunBuffStdErr.append('\n');
		}
		pRunBuffStdErr.append(aLine);
		return pRunBuffStdErr.length();
	}

	@Override
	public int consumeStdOutputLine(final String aLine) {
		// log with secured method
		secureLogFinest("consumeStdOutputLine", "StdOut=[%s]", aLine);

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
	 * @param aWhat
	 * @param aInfos
	 */
	private void secureLogFinest(final CharSequence aWhat,
			final Object... aInfos) {
		secureLog(pLogger, Level.FINEST, this, aWhat, aInfos);
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

	/**
	 * @param aText
	 * @param aPrefix
	 * @return
	 */
	protected String shiftTextLines(final String aText, final String aPrefix) {

		if (aText == null || aText.isEmpty()) {
			return aText;
		}

		StringBuilder wSB = new StringBuilder();
		String[] wLines = aText.split("\\n");
		for (String wLine : wLines) {
			wSB.append(aPrefix).append(wLine).append('\n');
		}
		return wSB.toString();
	}

}
