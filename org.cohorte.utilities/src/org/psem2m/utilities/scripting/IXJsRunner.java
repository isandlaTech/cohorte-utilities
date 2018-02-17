package org.psem2m.utilities.scripting;

import org.psem2m.utilities.CXTimer;

/**
 * #12 Manage chains of resource providers
 * 
 * @author IsandlaTech - ogattaz
 * 
 */
public interface IXJsRunner extends IXJsConstants {

	/**
	 * @param aTS
	 *            the time stamp
	 * @return the formated timestamp as "yyyy/MM/dd HH:mm:ss:SS"
	 */
	String fomatTS(Long aTS);

	long getDurationNs();

	/**
	 * @return the current timestamp formated as "yyyy/MM/dd HH:mm:ss:SS"
	 */
	String getfomatedTS();

	/**
	 * @return a Title as "SCRIPT[ sourceId ]"
	 */
	String getFormatedTitle();

	String getId();

	String getSourceName();

	/**
	 * @return the list of the timestamps of the sources of the script
	 */
	String getTimeStamps();

	String getTraceReport();

	void logBeginStep();

	void logBeginStep(final String aFormat, final Object... aArgs);

	void logDebug(final String aWhat, final String aFormat,
			final Object... aArgs);

	void logEndStep();

	void logEndStep(final String aFormat, final Object... aArgs);

	void logInfo(final String aWhat, final String aFormat,
			final Object... aArgs);

	void logSevere(final String aWhat, final String aFormat,
			final Object... aArgs);

	void logSevere(final String aWhat, Throwable e, final String aFormat,
			final Object... aArgs);

	CXTimer newTimer();

	/**
	 * @param aFormat
	 * @param aArgs
	 * @return the result of String.format(aFormat,aArgs)
	 */
	String stringFormat(final String aFormat, final Object... aArgs);

}
