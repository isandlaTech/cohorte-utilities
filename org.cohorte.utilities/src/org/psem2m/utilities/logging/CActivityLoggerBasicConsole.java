package org.psem2m.utilities.logging;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.psem2m.utilities.CXJavaRunContext;
import org.psem2m.utilities.CXStringUtils;

/**
 * @author isandlatech (www.isandlatech.com) - ogattaz
 *
 */
public class CActivityLoggerBasicConsole implements IActivityLoggerJul {

	private final static CActivityLoggerBasicConsole sCActivityLoggerBasicConsole = new CActivityLoggerBasicConsole();

	/**
	 * @return
	 */
	public static IActivityLoggerJul getInstance() {
		return sCActivityLoggerBasicConsole;
	}

	protected IActivityFormater pActivityFormater;

	protected final boolean pIsFormatValid = CXJulUtils.validSimpleFormaterConfig();

	protected final Logger pJulLogger = CXJulUtils.getRootLogger();

	private Level pLevel = Level.INFO;

	protected int pLevelValue = Level.INFO.intValue();

	protected CLogLineTextBuilder pLogLineTextBuilder;

	/**
	 *
	 */
	protected CActivityLoggerBasicConsole() {
		super();

		// log ALL !
		pJulLogger.setLevel(Level.ALL);

		// use the SimpleFormatter
		CXJulUtils.setSimpleFormatter(pJulLogger);

		pActivityFormater = CActivityFormaterBasic.getInstance(IActivityFormater.LINE_CONSOLE);
		pActivityFormater.acceptMultiline(IActivityFormater.MULTILINES_TEXT);
		pLogLineTextBuilder = CLogLineTextBuilder.getInstance();
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
		return CXStringUtils.appendStringsInBuff(aBuffer, getClass().getSimpleName(), String.valueOf(hashCode()));
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
		logWarn(this, "close", "An instance of CActivityLoggerBasicConsole is not closable.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.logging.IActivityLoggerJul#getJulLogger()
	 */
	@Override
	public Logger getJulLogger() {
		return pJulLogger;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.logging.IActivityLoggerBase#getLevel()
	 */
	@Override
	public Level getLevel() {
		return pLevel;
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
		return true;
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
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.logging.IActivityLoggerBase#isLogSevereOn()
	 */
	@Override
	public boolean isLogSevereOn() {
		return true;
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

	/**
	 * @return
	 */
	public boolean IsSimpleFormatterFormatValid() {
		return pIsFormatValid;
	}

	/**
	 * Modèle de ligne à formater
	 * 
	 * <pre>
	 * DATE(1)                   LEVEL(4) THREAD(3)         SOURCE(2): INSTANCE + METHOD                            LINE (5) + (6)
	 * <------- 24 car ------->..<-7c-->..<---- 16c ----->..<--------------------- 54c -------------------------->..<------------------ N characters  -------...
	 *                                                      <--------- 27c------------>..<----------25c ---------->
	 * Logger File
	 * 2019/02/12; 10:58:05:630; FINE   ;    SSEMonitor(1); SSEMachineRequestsMaps_6830;                 sendIddle; begin
	 * 2019/02/12; 10:58:05:630; FINE   ;    SSEMonitor(1); se.CSSEMachineRequests_9877;                 sendIddle; key=[(01,105)(cb6c8485-258a-496c-93a8-40aff9f997b7)] ...
	 * 2019/02/12; 10:58:05:630; FINE   ;    SSEMonitor(1); SSEMachineRequestsMaps_6830;                 sendIddle; end. NbSentIddle=[0]
	 * Logger console
	 * 2019/02/12; 15:59:28:339;   Infos;             main; apps.impl.CTestLogging_0842;                    doTest; SimpleFormatter current format=[%1$tY/%1$tm/%1$td; %1$tH:%1$tM:%1$tS:%1$tL; %4$7.7s; %3$16.016s; %2$54.54s; %5$s%6$s%n]
	 * 2019/02/12; 15:59:28:344;   Infos;             main; apps.impl.CTestLogging_0842;                    doTest; SimpleFormatter jvm property  =[%1$tY/%1$tm/%1$td; %1$tH:%1$tM:%1$tS:%1$tL; %4$7.7s; %3$16.016s; %2$54.54s; %5$s%6$s%n]
	 * 2019/02/12; 15:59:28:345;   Infos;             main; apps.impl.CTestLogging_0842;                    doTest; IsSimpleFormatterFormatValid=[true] / JulLogger: Name=[] Level=[ALL] 
	 * 2019/02/12; 15:59:28:346;   Infos;             main; apps.impl.CTestLogging_0842;                    doTest; logInfo: Ligne log info
	 * </pre>
	 * 
	 * 
	 * <pre>
	 * SimpleFormat=[%1$tY/%1$tm/%1$td; %1$tH:%1$tM:%1$tS:%1$tL; %4$7.7s; %3$16.016s; %2$54.54s; %5$s%6$s%n]
	 * </pre>
	 * 
	 * @see CXJulUtils
	 * 
	 * @see https 
	 *      ://docs.oracle.com/javase/8/docs/api/java/util/logging/SimpleFormatter
	 *      .html
	 * 
	 * @see org.psem2m.utilities.logging.IActivityLogger#log(java.util.logging.Level,
	 *      java.lang.Object, java.lang.CharSequence, java.lang.Object[])
	 */
	@Override
	public void log(final Level aLevel, final Object aWho, final CharSequence aWhat, final Object... aInfos) {

		if (aLevel.intValue() < pLevelValue || pLevelValue == Level.OFF.intValue()) {
			return;
		}

		final String wLogText = pLogLineTextBuilder.buildLogLine(aInfos);

		// System.out.println("wLogText="+((wLogText!=null)?wLogText:"null"));

		final String wLogWho = pLogLineTextBuilder.buildWhoObjectId(aWho);
		// System.out.println("wLogWho="+((wLogWho!=null)?wLogWho:"null"));

		final String wLogWhat = (aWhat != null) ? aWhat.toString() : CXJavaRunContext.getPreCallingMethod();
		// System.out.println("wLogWhat="+((wLogWhat!=null)?wLogWhat:"null"));

		final Logger wJulLogger = getJulLogger();
		if (wJulLogger.isLoggable(Level.ALL)) {
			final LogRecord wLogRecord = new LogRecord(aLevel, wLogText);

			// issue #29
			// set the logger name with the name of the current thread
			wLogRecord.setLoggerName(Thread.currentThread().getName());
			wLogRecord.setThreadID((int) Thread.currentThread().getId());

			// issue #29
			// wLogWho and wLogWhat will be concatened by the SimpleFormater in
			// the "SOURCE" (cf. idx 2)
			// @see java.util.logging.SimpleFormatter.format(LogRecord)
			wLogRecord.setSourceClassName(CXStringUtils.strAdjustRight(wLogWho, 27, ' ') + ";");
			wLogRecord.setSourceMethodName(CXStringUtils.strAdjustRight(wLogWhat, 25, ' '));

			wJulLogger.log(wLogRecord);
		} else {

			final String wLine = pActivityFormater.format(System.currentTimeMillis(), aLevel, wLogWho, wLogWhat,
					wLogText, !IActivityFormater.WITH_END_LINE);

			System.out.println((wLine != null) ? wLine : "wLine=null");
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
	public void log(final LogRecord aLogRecord) {

		final Logger wJulLogger = getJulLogger();
		if (wJulLogger.isLoggable(Level.ALL)) {
			wJulLogger.log(aLogRecord);
		} else {
			System.out.print(pActivityFormater.format(aLogRecord));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.psem2m.utilities.logging.IActivityLogger#logDebug(java.lang.Object,
	 * java.lang.CharSequence, java.lang.Object[])
	 */
	@Override
	public void logDebug(final Object aWho, final CharSequence aWhat, final Object... aInfos) {
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
	public void logInfo(final Object aWho, final CharSequence aWhat, final Object... aInfos) {
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
	public void logSevere(final Object aWho, final CharSequence aWhat, final Object... aInfos) {
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
	public void logWarn(final Object aWho, final CharSequence aWhat, final Object... aInfos) {
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
	public void setLevel(final Level aLevel) {

		pLevel = (aLevel != null) ? aLevel : Level.INFO;
		pLevelValue = pLevel.intValue();
	}

	/**
	 * @param aLevel
	 */
	@Override
	public void setLevel(final String aLevel) {

		try {
			setLevel(Level.parse(aLevel));
		} catch (final IllegalArgumentException e) {
			setLevel(Level.INFO);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.IXDescriber#toDescription()
	 */
	@Override
	public String toDescription() {
		return addDescriptionInBuffer(new StringBuilder(calcDescriptionLength())).toString();
	}

}
