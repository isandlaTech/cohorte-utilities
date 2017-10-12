package org.cohorte.utilities.picosoc;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.psem2m.utilities.files.CXFileDir;
import org.psem2m.utilities.logging.CActivityLoggerBasic;
import org.psem2m.utilities.logging.CActivityLoggerNull;
import org.psem2m.utilities.logging.EActivityLogColumn;
import org.psem2m.utilities.logging.IActivityFormater;
import org.psem2m.utilities.logging.IActivityLogger;
import org.psem2m.utilities.logging.IActivityRequester;

/**
 * @author ogattaz
 * 
 */
public abstract class CComponentLoggerFile extends CComponentLogger {

	private final static String FILENAME_EXT = "txt";

	private final static String FILENAME_NUM = "%g";

	private static CComponentLoggerFile sMe;

	/**
	 * @param aLevel
	 * @return
	 */
	public static boolean doLog(Level aLevel) {
		if (sMe != null) {
			return sMe.isLoggable(aLevel);

		}
		return CComponentLogger.doLog(aLevel);
	}

	/**
	 * @param aLevel
	 * @param aWho
	 * @param aWhat
	 * @param aInfos
	 */
	public static void logInMain(Level aLevel, final Object aWho,
			CharSequence aWhat, Object... aInfos) {

		if (sMe != null && sMe.pFileLogger != null) {
			sMe.pFileLogger.log(aLevel, aWho, aWhat, aInfos);
			return;
		}
		CComponentLogger.logInMain(aLevel, aWho, aWhat, aInfos);
	}

	/**
	 * @param record
	 */
	public static void logInMain(final LogRecord record) {

		if (sMe != null && sMe.pFileLogger != null) {
			sMe.pFileLogger.log(record);
			return;
		}
		CComponentLogger.logInMain(record);
	}

	private final IActivityLogger pFileLogger;

	private final ISvcLoggerConfigurator pSvcLoggerConfigurator;

	/**
	 * 
	 */
	public CComponentLoggerFile() throws Exception {
		super();
		sMe = this;

		pSvcLoggerConfigurator = getService(ISvcLoggerConfigurator.class);

		pFileLogger = initFileLogger();

		pFileLogger.logInfo(this, "<init>", "OK");
	}

	/**
	 * @return
	 */
	private String buildFileNamePattern(String aLoggerName) {
		StringBuilder wSB = new StringBuilder();
		wSB.append(aLoggerName);
		wSB.append('.');
		wSB.append(FILENAME_NUM);
		wSB.append('.');
		wSB.append(FILENAME_EXT);
		return wSB.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.logging.IActivityLogger#close()
	 */
	@Override
	public void close() {

		if (pFileLogger != null) {
			pFileLogger.close();
		}

	}

	@Override
	public Level getLevel() {
		return (pFileLogger != null) ? pFileLogger.getLevel() : super
				.getLevel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cohorte.utilities.picosoc.CComponentLogger#getLogger()
	 */
	@Override
	public IActivityLogger getLogger() {
		return (pFileLogger != null) ? pFileLogger : super.getLogger();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cohorte.utilities.picosoc.CComponentLogger#getRequester()
	 */
	@Override
	public IActivityRequester getRequester() {
		return pFileLogger.getRequester();

	}

	/**
	 * @return an instance of IActivityLogger
	 */
	private IActivityLogger initFileLogger() {

		IActivityLogger wActivityLogger = CActivityLoggerNull.getInstance();
		try {

			// the three main informations
			String wLoggerName = pSvcLoggerConfigurator.getLoggerName();
			File wDirLogs = pSvcLoggerConfigurator.getDirLogs();
			String wLevel = pSvcLoggerConfigurator.getLevel().getName();

			// the specialized informatons
			int wFileLimit = 10 * 1024 * 1024;
			int wNbFile = 10;
			EActivityLogColumn[] wLineDef = IActivityFormater.LINE_SHORT;
			boolean wIsMultiLine = false;

			ISvcActivityLoggerConfigurator wActivityLoggerConfigurator = getOptionalService(ISvcActivityLoggerConfigurator.class);
			if (wActivityLoggerConfigurator != null) {
				wFileLimit = wActivityLoggerConfigurator.getFileLimit();
				wNbFile = wActivityLoggerConfigurator.getNbFile();
				wLineDef = wActivityLoggerConfigurator.getLineDef();
				wIsMultiLine = wActivityLoggerConfigurator.isMultiline();
			}

			String wLogFileNamePattern = buildFileNamePattern(wLoggerName);

			CXFileDir wLogFilePattern = new CXFileDir(wDirLogs,
					wLogFileNamePattern);

			String wAbsolutePathPattern = wLogFilePattern.getAbsolutePath();

			wActivityLogger = CActivityLoggerBasic.newLogger(wLoggerName,
					wAbsolutePathPattern, wLevel, wFileLimit, wNbFile,
					wLineDef, wIsMultiLine);

			logInfo(this, "initFileLogger", "FileLogger: %s",
					wActivityLogger.toDescription());

		} catch (Exception e) {
			logSevere(this, "initFileLogger", e);
		}
		return wActivityLogger;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cohorte.utilities.picosoc.CComponentLogger#isLogDebugOn()
	 */
	@Override
	public boolean isLogDebugOn() {

		return (pFileLogger != null) ? pFileLogger.isLogDebugOn() : super
				.isLogDebugOn();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cohorte.utilities.picosoc.CComponentLogger#isLoggable(java.util.logging
	 * .Level)
	 */
	@Override
	public boolean isLoggable(final Level aLevel) {

		return (pFileLogger != null) ? pFileLogger.isLoggable(aLevel) : super
				.isLoggable(aLevel);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cohorte.utilities.picosoc.CComponentLogger#isLogInfoOn()
	 */
	@Override
	public boolean isLogInfoOn() {

		return (pFileLogger != null) ? pFileLogger.isLogInfoOn() : super
				.isLogInfoOn();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cohorte.utilities.picosoc.CComponentLogger#isLogSevereOn()
	 */
	@Override
	public boolean isLogSevereOn() {
		return (pFileLogger != null) ? pFileLogger.isLogSevereOn() : super
				.isLogSevereOn();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cohorte.utilities.picosoc.CComponentLogger#isLogWarningOn()
	 */
	@Override
	public boolean isLogWarningOn() {

		return (pFileLogger != null) ? pFileLogger.isLogWarningOn() : super
				.isLogWarningOn();
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

		if (pFileLogger != null)
			pFileLogger.log(aLevel, aWho, aWhat, aInfos);
		else
			super.log(aLevel, aWho, aWhat, aInfos);
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

		if (pFileLogger != null)
			pFileLogger.log(record);
		else
			super.log(record);
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

		if (pFileLogger != null)
			pFileLogger.logDebug(aWho, aWhat, aInfos);
		else
			super.logDebug(aWho, aWhat, aInfos);
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

		if (pFileLogger != null)
			pFileLogger.logInfo(aWho, aWhat, aInfos);
		else
			super.logInfo(aWho, aWhat, aInfos);
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

		if (pFileLogger != null)
			pFileLogger.logSevere(aWho, aWhat, aInfos);
		else
			super.logSevere(aWho, aWhat, aInfos);
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

		if (pFileLogger != null)
			pFileLogger.logWarn(aWho, aWhat, aInfos);
		else
			super.logWarn(aWho, aWhat, aInfos);
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

		if (pFileLogger != null)
			pFileLogger.setLevel(aLevel);
		else
			super.setLevel(aLevel);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cohorte.utilities.picosoc.CComponentLogger#setLevel(java.lang.String)
	 */
	@Override
	public void setLevel(String aLevelName) {

		if (pFileLogger != null)
			pFileLogger.setLevel(aLevelName);
		else
			super.setLevel(aLevelName);
	}
}
