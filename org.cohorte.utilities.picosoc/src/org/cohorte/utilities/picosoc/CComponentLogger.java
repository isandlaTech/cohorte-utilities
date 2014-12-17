package org.cohorte.utilities.picosoc;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.psem2m.utilities.CXJavaRunContext;
import org.psem2m.utilities.CXStringUtils;
import org.psem2m.utilities.logging.CActivityFormaterBasic;
import org.psem2m.utilities.logging.CLogLineTextBuilder;
import org.psem2m.utilities.logging.CLogToolsException;
import org.psem2m.utilities.logging.IActivityFormater;
import org.psem2m.utilities.logging.IActivityLogger;
import org.psem2m.utilities.logging.IActivityRequester;

abstract class CComponentLogger extends CAbstractComponentBase implements
		IActivityLogger {

	public static final String PROP_MAINLOGGER_LEVEL = "mainlogger.level";

	protected static final IActivityFormater sActivityFormater = CActivityFormaterBasic
			.getInstance(IActivityFormater.LINE_SIMPLEFORMATER);


	protected static final CLogLineTextBuilder sLogLineTextBuilder = CLogLineTextBuilder
			.getInstance();

	protected static SimpleFormatter sSimpleFormatter = new SimpleFormatter();

	protected static final CLogToolsException sToolsException = CLogToolsException
			.getInstance();

	static {
		sActivityFormater.acceptMultiline(IActivityFormater.MULTILINES_TEXT);
	}

	/**
	 * @param aLevel
	 * @return
	 */
	public static boolean doLog(Level aLevel) {
			return true;
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
		logInMain(Level.INFO, CComponentLogger.class, "setMainLoggerLevel",
				"Set the level of the main logger to [%s]", aLevel);

		try {
			Logger wMainLogger = getMainLogger();
			if (wMainLogger == null)
				new Exception("Unable to find the MainLogger");

			wMainLogger.setLevel(aLevel);
			Handler[] wHandlers = wMainLogger.getHandlers();
			for (Handler wHandler : wHandlers) {
				wHandler.setLevel(aLevel);
				logInMain(Level.INFO, CComponentLogger.class, "setMainLoggerLevel",
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
		logInMain(Level.INFO, CComponentLogger.class, "setMainLoggerLevel",
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

	protected final ISvcLoggerConfigurator pSvcLoggerConfigurator;

	/**
	 * @throws Exception 
	 * 
	 */
	public CComponentLogger() throws Exception  {
		super();

		pSvcLoggerConfigurator = getService(ISvcLoggerConfigurator.class);

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

	}

	@Override
	public Level getLevel() {
		return Level.OFF;
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
		return true;
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

		logInMain(aLevel, aWho, aWhat, aInfos);

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

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.psem2m.utilities.logging.IActivityLogger#setLevel(java.lang.String)
	 */
	@Override
	public void setLevel(String aLevelName) {

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
