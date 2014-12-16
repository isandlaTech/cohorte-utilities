package org.cohorte.utilities.picosoc;

import java.io.File;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.psem2m.utilities.CXJavaRunContext;
import org.psem2m.utilities.CXStringUtils;
import org.psem2m.utilities.files.CXFileDir;
import org.psem2m.utilities.logging.CActivityFormaterBasic;
import org.psem2m.utilities.logging.CActivityLoggerBasic;
import org.psem2m.utilities.logging.CLogLineTextBuilder;
import org.psem2m.utilities.logging.CLogToolsException;
import org.psem2m.utilities.logging.IActivityFormater;
import org.psem2m.utilities.logging.IActivityLogger;
import org.psem2m.utilities.logging.IActivityRequester;


public abstract class CSocLogger  extends CAbstractComponentBase implements IActivityLogger {

	private final static String FILENAME_EXT = "txt";

	private final static String FILENAME_NUM = "%g";

	public static final String PROP_MAINLOGGER_LEVEL = "mainlogger.level";

	private static final IActivityFormater sActivityFormater = CActivityFormaterBasic
			.getInstance(IActivityFormater.LINE_SIMPLEFORMATER);


	private static CSocLogger sLogger;

	private static final CLogLineTextBuilder sLogLineTextBuilder = CLogLineTextBuilder
			.getInstance();

	private static SimpleFormatter sSimpleFormatter = new SimpleFormatter();

	// /**
	// * @return
	// */
	// public static IActivityLogger getInstance() {
	// return sActivityLogger;
	// }

	private static final CLogToolsException sToolsException = CLogToolsException
			.getInstance();

	static {
		sActivityFormater.acceptMultiline(IActivityFormater.MULTILINES_TEXT);
	}

	/**
	 * @param aLevel
	 * @return
	 */
	public static boolean doLog(Level aLevel) {
		if (sLogger == null) {
			return true;
		}

		return sLogger.isLoggable(aLevel);

	}

	/**
	 * @return
	 */
	public static LogManager getLogManager() {
		return LogManager.getLogManager();
	}

	/**
	 * @return
	 */
	public static Logger getMainLogger() {
		return getLogManager().getLogger("");
	}

	/**
	 * @param aLevel
	 * @param aWho
	 * @param aWhat
	 * @param aInfos
	 */
	public static void logInMain(Level aLevel, final Object aWho,
			CharSequence aWhat, Object... aInfos) {

		if (sLogger != null && sLogger.pFileLogger != null) {
			sLogger.pFileLogger.log(aLevel, aWho, aWhat, aInfos);
			return;
		}

		Logger wMainLogger = getMainLogger();

		// si on ne doit pas logger !
		if (wMainLogger != null) {
			Level wMainLevel = wMainLogger.getLevel();
			// si pas de level sur le main logger ou si le level du main logger
			// est supérieur au level de la demande de log => no log
			if (wMainLevel == null || aLevel.intValue() < wMainLevel.intValue())
				return;
		}

		String wLogText = sLogLineTextBuilder.buildLogLine(aInfos);
		// System.out.println("wLogText="+((wLogText!=null)?wLogText:"null"));

		String wLogWho = sLogLineTextBuilder.buildWhoObjectId(aWho);
		// System.out.println("wLogWho="+((wLogWho!=null)?wLogWho:"null"));

		String wLogWhat = (aWhat != null) ? aWhat.toString() : CXJavaRunContext
				.getPreCallingMethod();
		// System.out.println("wLogWhat="+((wLogWhat!=null)?wLogWhat:"null"));

		String wLine = sActivityFormater.format(System.currentTimeMillis(),
				aLevel, wLogWho, wLogWhat, wLogText,
				!IActivityFormater.WITH_END_LINE);

		try {
			wMainLogger.log(aLevel, wLine);
		} catch (Throwable e) {
			System.out.print(sSimpleFormatter.format(new LogRecord(aLevel,
					wLine)));
		}
	}

	/**
	 * @param record
	 */
	public static void logInMain(final LogRecord record) {

		if (sLogger != null && sLogger.pFileLogger != null) {
			sLogger.pFileLogger.log(record);
			return;
		}

		Logger wMainLogger = getMainLogger();

		// si on ne doit pas logger !
		if (wMainLogger != null) {
			Level wMainLevel = wMainLogger.getLevel();
			// si pas de level sur le main logger ou si le level du main logger
			// est supérieur au level de la demande de log => no log
			if (wMainLevel == null
					|| record.getLevel().intValue() < wMainLevel.intValue())
				return;
		}
		wMainLogger.log(record);
	}

	/**
	 * 
	 * @param aLevel
	 */
	public static void setMainLoggerLevel(Level aLevel) {
		logInMain(Level.INFO, CSocLogger.class, "setMainLoggerLevel",
				"Set the level of the main logger to [%s]", aLevel);

		try {
			Logger wMainLogger = getMainLogger();
			if (wMainLogger == null)
				new Exception("Unable to find the MainLogger");

			wMainLogger.setLevel(aLevel);
			Handler[] wHandlers = wMainLogger.getHandlers();
			for (Handler wHandler : wHandlers) {
				wHandler.setLevel(aLevel);
				logInMain(Level.INFO, CSocLogger.class, "setMainLoggerLevel",
						"level=[%s] handler: %s", aLevel, wHandler);
			}
		} catch (Exception e) {
			Exception wEx = new Exception(
					String.format(
							"TomcatLogger.setMainLoggerLevel(): unable to set the level of the main logger to  [%s]",
							aLevel), e);
			System.err.println(sToolsException.eInString(wEx));
		}
	}

	/**
	 * 
	 * @param aLevelName
	 */
	public static void setMainLoggerLevel(String aLevelName) {
		logInMain(Level.INFO, CSocLogger.class, "setMainLoggerLevel",
				" Set the level of the main logger with level name [%s]",
				aLevelName);

		try {
			setMainLoggerLevel(Level.parse(aLevelName));

		} catch (Exception e) {
			Exception wEx = new Exception(
					String.format(
							"TomcatLogger.setMainLoggerLevel(): unable to set the level of the main logger with level name [%s]",
							aLevelName), e);
			System.err.println(sToolsException.eInString(wEx));
		}
	}

	private IActivityLogger pFileLogger = null;
	
	private final ISvcLoggerConfigurator pSvcLoggerConfigurator;

	/**
	 * 
	 */
	public CSocLogger() throws Exception {
		super();
		sLogger = this;

		pSvcLoggerConfigurator = getService(ISvcLoggerConfigurator.class);

		initJvmLoggers();

		initFileLogger();

		registerMeAsService(IActivityLogger.class);

		logInfo(this, "<init>", "instanciated: lineDef=[%s]",
				sActivityFormater.getLineDefInString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.psem2m.utilities.IXDescriber#addDescriptionInBuffer(java.lang.Appendable
	 * )
	 */
	@Override
	public Appendable addDescriptionInBuffer(final Appendable aBuffer) {
		return CXStringUtils.appendStringsInBuff(aBuffer, getClass()
				.getSimpleName(), String.valueOf(hashCode()));
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

	/**
	 * @return
	 */
	public int calcDescriptionLength() {
		return 128;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.logging.IActivityLogger#getRequester()
	 */
	@Override
	public IActivityRequester getRequester() {
		return null;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.logging.IActivityLoggerBase#isLogDebugOn()
	 */
	@Override
	public boolean isLogDebugOn() {
		return isLoggable(Level.FINE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.psem2m.utilities.logging.IActivityLoggerBase#isLoggable(java.util
	 * .logging.Level)
	 */
	@Override
	public boolean isLoggable(final Level aLevel) {
		return (pFileLogger != null) ? pFileLogger.isLoggable(aLevel) : true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.logging.IActivityLoggerBase#isLogInfoOn()
	 */
	@Override
	public boolean isLogInfoOn() {
		return isLoggable(Level.INFO);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.logging.IActivityLoggerBase#isLogSevereOn()
	 */
	@Override
	public boolean isLogSevereOn() {
		return isLoggable(Level.SEVERE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.logging.IActivityLoggerBase#isLogWarningOn()
	 */
	@Override
	public boolean isLogWarningOn() {
		return true;
	}

	/**
	 * @return
	 */
	protected boolean isOpened() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.psem2m.utilities.logging.IActivityLogger#log(java.util.logging.Level,
	 * java.lang.Object, java.lang.CharSequence, java.lang.Object[])
	 */
	@Override
	public void log(final Level aLevel, final Object aWho,
			final CharSequence aWhat, final Object... aInfos) {

		if (!isLoggable(aLevel))
			return;

		if (pFileLogger != null) {
			pFileLogger.log(aLevel, aWho, aWhat, aInfos);
		} else {
			logInMain(aLevel, aWho, aWhat, aInfos);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.psem2m.utilities.logging.IActivityLoggerBase#log(java.util.logging
	 * .LogRecord)
	 */
	@Override
	public void log(final LogRecord record) {
		logInMain(record);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.psem2m.utilities.logging.IActivityLogger#logDebug(java.lang.Object,
	 * java.lang.CharSequence, java.lang.Object[])
	 */
	@Override
	public void logDebug(final Object aWho, final CharSequence aWhat,
			final Object... aInfos) {
		log(Level.FINE, aWho, aWhat, aInfos);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.psem2m.utilities.logging.IActivityLogger#logInfo(java.lang.Object,
	 * java.lang.CharSequence, java.lang.Object[])
	 */
	@Override
	public void logInfo(final Object aWho, final CharSequence aWhat,
			final Object... aInfos) {
		log(Level.INFO, aWho, aWhat, aInfos);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.psem2m.utilities.logging.IActivityLogger#logSevere(java.lang.Object,
	 * java.lang.CharSequence, java.lang.Object[])
	 */
	@Override
	public void logSevere(final Object aWho, final CharSequence aWhat,
			final Object... aInfos) {
		log(Level.SEVERE, aWho, aWhat, aInfos);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.psem2m.utilities.logging.IActivityLogger#logWarn(java.lang.Object,
	 * java.lang.CharSequence, java.lang.Object[])
	 */
	@Override
	public void logWarn(final Object aWho, final CharSequence aWhat,
			final Object... aInfos) {
		log(Level.WARNING, aWho, aWhat, aInfos);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.psem2m.utilities.logging.IActivityLogger#setLevel(java.util.logging
	 * .Level)
	 */
	@Override
	public void setLevel(Level aLevel) {
		if (pFileLogger != null) {
			pFileLogger.setLevel(aLevel);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.psem2m.utilities.logging.IActivityLogger#setLevel(java.lang.String)
	 */
	@Override
	public void setLevel(String aLevelName) {
		if (pFileLogger != null) {
			pFileLogger.setLevel(aLevelName);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.IXDescriber#toDescription()
	 */
	@Override
	public String toDescription() {
		return addDescriptionInBuffer(
				new StringBuilder(calcDescriptionLength())).toString();
	}
}
