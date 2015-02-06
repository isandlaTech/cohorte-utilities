package org.cohorte.utilities.picosoc;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.psem2m.utilities.logging.CActivityLoggerBasicConsole;
import org.psem2m.utilities.logging.IActivityLogger;
import org.psem2m.utilities.logging.IActivityRequester;

/**
 * @author ogattaz
 */
public class CComponentLoggerConsole extends CComponentLogger {

	private static final IActivityLogger pLogger = CActivityLoggerBasicConsole
			.getInstance();

	/**
	 * @throws Exception
	 */
	public CComponentLoggerConsole() throws Exception {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cohorte.utilities.picosoc.CComponentLogger#close()
	 */
	@Override
	public void close() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cohorte.utilities.picosoc.CComponentLogger#getLevel()
	 */
	@Override
	public Level getLevel() {
		return pLogger.getLevel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cohorte.utilities.picosoc.CComponentLogger#getLogger()
	 */
	@Override
	public IActivityLogger getLogger() {
		return pLogger;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cohorte.utilities.picosoc.CComponentLogger#getRequester()
	 */
	@Override
	public IActivityRequester getRequester() {
		return pLogger.getRequester();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cohorte.utilities.picosoc.CComponentLogger#isLogDebugOn()
	 */
	@Override
	public boolean isLogDebugOn() {
		return pLogger.isLogDebugOn();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cohorte.utilities.picosoc.CComponentLogger#isLoggable(java.util.logging
	 * .Level)
	 */
	@Override
	public boolean isLoggable(Level aLevel) {
		return pLogger.isLoggable(aLevel);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cohorte.utilities.picosoc.CComponentLogger#isLogInfoOn()
	 */
	@Override
	public boolean isLogInfoOn() {
		return pLogger.isLogInfoOn();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cohorte.utilities.picosoc.CComponentLogger#isLogSevereOn()
	 */
	@Override
	public boolean isLogSevereOn() {
		return pLogger.isLogSevereOn();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cohorte.utilities.picosoc.CComponentLogger#isLogWarningOn()
	 */
	@Override
	public boolean isLogWarningOn() {
		return pLogger.isLogWarningOn();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cohorte.utilities.picosoc.CComponentLogger#log(java.util.logging.
	 * Level, java.lang.Object, java.lang.CharSequence, java.lang.Object[])
	 */
	@Override
	public void log(Level aLevel, Object aWho, CharSequence aWhat,
			Object... aInfos) {
		pLogger.log(aLevel, aWho, aWhat, aInfos);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cohorte.utilities.picosoc.CComponentLogger#log(java.util.logging.
	 * LogRecord)
	 */
	@Override
	public void log(LogRecord record) {
		pLogger.log(record);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cohorte.utilities.picosoc.CComponentLogger#logDebug(java.lang.Object,
	 * java.lang.CharSequence, java.lang.Object[])
	 */
	@Override
	public void logDebug(Object aWho, CharSequence aWhat, Object... aInfos) {
		pLogger.logDebug(aWho, aWhat, aInfos);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cohorte.utilities.picosoc.CComponentLogger#logInfo(java.lang.Object,
	 * java.lang.CharSequence, java.lang.Object[])
	 */
	@Override
	public void logInfo(Object aWho, CharSequence aWhat, Object... aInfos) {
		pLogger.logInfo(aWho, aWhat, aInfos);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cohorte.utilities.picosoc.CComponentLogger#logSevere(java.lang.Object
	 * , java.lang.CharSequence, java.lang.Object[])
	 */
	@Override
	public void logSevere(Object aWho, CharSequence aWhat, Object... aInfos) {
		pLogger.logSevere(aWho, aWhat, aInfos);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cohorte.utilities.picosoc.CComponentLogger#logWarn(java.lang.Object,
	 * java.lang.CharSequence, java.lang.Object[])
	 */
	@Override
	public void logWarn(Object aWho, CharSequence aWhat, Object... aInfos) {
		pLogger.logWarn(aWho, aWhat, aInfos);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cohorte.utilities.picosoc.CComponentLogger#setLevel(java.util.logging
	 * .Level)
	 */
	@Override
	public void setLevel(Level aLevel) {
		pLogger.setLevel(aLevel);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cohorte.utilities.picosoc.CComponentLogger#setLevel(java.lang.String)
	 */
	@Override
	public void setLevel(String aLevelName) {
		pLogger.setLevel(aLevelName);
	}

}
