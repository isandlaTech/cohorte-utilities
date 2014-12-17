package org.cohorte.utilities.picosoc;

import java.io.File;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

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

	/**
	 * 
	 */
	public CComponentLoggerFile() throws Exception {
		super();
		sMe = this;

		initJvmLoggers();

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
		return (pFileLogger != null) ? pFileLogger.getLevel() : Level.OFF;
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

	/**
	 * 
	 * 
	 * The formatting can be customized by specifying the format string in the
	 * java.util.logging.SimpleFormatter.format property.
	 * 
	 * <pre>
	 * where the arguments and their index are:
	 * 0 format  - the java.util.Formatter format string specified in the java.util.logging.SimpleFormatter.format property or the default format.
	 * 1 date    - a Date object representing event time of the log record.
	 * 2 source  - a string representing the caller, if available; otherwise, the logger's name.
	 * 3 logger  - the logger's name.
	 * 4 level   - the log level.
	 * 5 message - the formatted log message returned from the Formatter.formatMessage(LogRecord) method. It uses java.text formatting and does not use the java.util.Formatter format argument.
	 * 6 thrown  - a string representing the throwable associated with the log record and its backtrace beginning with a newline character, 
	 *            if any; otherwise, an empty string.
	 * </pre>
	 * 
	 * <ul>
	 * <li>'Y' Year, formatted as at least four digits
	 * <li>'m' Month, formatted as two digits
	 * <li>'d' Day of month, formatted as two digits
	 * <li>'H' Hour of the day for the 24-hour clock
	 * <li>'M' Minute within the hour formatted as two digits
	 * <li>'S' Seconds within the minute, formatted as two digits
	 * <li>'L' Millisecond within the second formatted as three digits
	 * </ul>
	 * 
	 * FORMAT ==> -Djava.util.logging.SimpleFormatter.format
	 * 
	 * "%1$tY/%1$tm/%1$td %1$tH-%1$tM-%1$tS.%1$tL|%3$46s|%4$14s| %5$s%6$s%n";
	 * 
	 * @see http 
	 *      ://docs.oracle.com/javase/7/docs/api/java/util/logging/SimpleFormatter
	 *      .html#formatting
	 * @see http 
	 *      ://docs.oracle.com/javase/7/docs/api/java/util/Formatter.html#syntax
	 * 
	 */
	private void initJvmLoggers() {

		boolean wConsoleHandlerConfigured = false;

		boolean wFileHandlerConfigured = false;

		Level wMainLoggerLevel = null;

		Logger wMainLogger = getMainLogger();

		boolean wHasMainLogger = wMainLogger != null;
		if (wHasMainLogger) {

			wMainLoggerLevel = wMainLogger.getLevel();

			try {
				Handler[] wHandlers = wMainLogger.getHandlers();
				if (wHandlers != null && wHandlers.length > 0) {
					for (Handler wHandler : wHandlers) {
						if (wHandler instanceof ConsoleHandler) {
							wHandler.setFormatter(sSimpleFormatter);
							wConsoleHandlerConfigured = true;

						} else if (wHandler instanceof FileHandler) {
							wHandler.setFormatter(sSimpleFormatter);
							wFileHandlerConfigured = true;
						}
					}
				}
			} catch (Exception e) {
				Exception wEx = new Exception(
						String.format(
								"TomcatLogger: unable to set the formater of the main logger [%s]",
								wMainLogger.getName()), e);
				System.err.println(sToolsException.eInString(wEx));
			}

		}

		logInfo(this,
				"initLoggers",
				"hasMainLogger=[%b] MainLoggerLevel=[%s] ConsoleHandlerConfigured=[%b] FileHandlerConfigured=[%b]",
				wHasMainLogger, wMainLoggerLevel, wConsoleHandlerConfigured,
				wFileHandlerConfigured);

	}

	@Override
	public boolean isLoggable(final Level aLevel) {
		return (pFileLogger != null) ? pFileLogger.isLoggable(aLevel) : super
				.isLoggable(aLevel);
	}
}
