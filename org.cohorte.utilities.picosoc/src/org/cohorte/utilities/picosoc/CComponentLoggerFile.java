package org.cohorte.utilities.picosoc;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.psem2m.utilities.files.CXFileDir;
import org.psem2m.utilities.logging.CActivityLoggerBasic;
import org.psem2m.utilities.logging.IActivityFormater;
import org.psem2m.utilities.logging.IActivityLogger;

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

	private IActivityLogger pFileLogger = null;

	private final ISvcLoggerConfigurator pSvcLoggerConfigurator;

	/**
	 * 
	 */
	public CComponentLoggerFile() throws Exception {
		super();
		sMe = this;

		pSvcLoggerConfigurator = getService(ISvcLoggerConfigurator.class);

		initFileLogger();

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

	/**
	 * 
	 */
	private void initFileLogger() {

		try {
			// ex : X3LoadBalancerProbe
			String wLoggerName = pSvcLoggerConfigurator.getLoggerName();

			File wDirLogs = pSvcLoggerConfigurator.getDirLogs();

			String wLogFileNamePattern = buildFileNamePattern(wLoggerName);

			CXFileDir wLogFilePattern = new CXFileDir(wDirLogs,
					wLogFileNamePattern);

			String wAbsolutePathPattern = wLogFilePattern.getAbsolutePath();

			String wLevel = pSvcLoggerConfigurator.getLevel().getName();

			pFileLogger = CActivityLoggerBasic.newLogger(wLoggerName,
					wAbsolutePathPattern, wLevel, 10 * 1024 * 1024, 10,
					IActivityFormater.LINE_SHORT,
					IActivityFormater.MULTILINES_TEXT);

			logInfo(this, "initFileLogger", "FileLogger: %s",
					pFileLogger.toDescription());

		} catch (Exception e) {
			logSevere(this, "initFileLogger", e);
		}
	}

	@Override
	public boolean isLoggable(final Level aLevel) {
		return (pFileLogger != null) ? pFileLogger.isLoggable(aLevel) : super
				.isLoggable(aLevel);
	}
}
