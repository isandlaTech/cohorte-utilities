package org.psem2m.utilities.logging;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.psem2m.utilities.CXJavaRunContext;
import org.psem2m.utilities.CXStringUtils;

/**
 * @author isandlatech (www.isandlatech.com) - ogattaz
 * 
 */
public class CActivityLoggerBasicConsole implements IActivityLogger {

	private final static CActivityLoggerBasicConsole sCActivityLoggerBasicConsole = new CActivityLoggerBasicConsole();

	/**
	 * @return
	 */
	public static IActivityLogger getInstance() {
		return sCActivityLoggerBasicConsole;
	}

	protected IActivityFormater pActivityFormater;

	private Level pLevel = Level.INFO;

	protected int pLevelValue = Level.INFO.intValue();

	protected CLogLineTextBuilder pLogLineTextBuilder;

	/**
	 * 
	 */
	protected CActivityLoggerBasicConsole() {
		super();

		pActivityFormater = CActivityFormaterBasic
				.getInstance(IActivityFormater.LINE_CONSOLE);
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
		logWarn(this, "close",
				"An instance of CActivityLoggerBasicConsole is not closable.");
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

	@Override
	public boolean isLogDebugOn() {
		return true;
	}

	@Override
	public boolean isLoggable(final Level aLevel) {
		return true;
	}

	@Override
	public boolean isLogInfoOn() {
		return true;
	}

	@Override
	public boolean isLogSevereOn() {
		return true;
	}

	/**
	 * @return
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

		if (aLevel.intValue() < pLevelValue
				|| pLevelValue == Level.OFF.intValue()) {
			return;
		}

		String wLogText = pLogLineTextBuilder.buildLogLine(aInfos);

		// System.out.println("wLogText="+((wLogText!=null)?wLogText:"null"));

		String wLogWho = pLogLineTextBuilder.buildWhoObjectId(aWho);
		// System.out.println("wLogWho="+((wLogWho!=null)?wLogWho:"null"));

		String wLogWhat = (aWhat != null) ? aWhat.toString() : CXJavaRunContext
				.getPreCallingMethod();
		// System.out.println("wLogWhat="+((wLogWhat!=null)?wLogWhat:"null"));

		String wLine = pActivityFormater.format(System.currentTimeMillis(),
				aLevel, wLogWho, wLogWhat, wLogText,
				!IActivityFormater.WITH_END_LINE);

		System.out.println((wLine != null) ? wLine : "wLine=null");
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

		System.out.print(pActivityFormater.format(record));
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
		} catch (IllegalArgumentException e) {
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
		return addDescriptionInBuffer(
				new StringBuilder(calcDescriptionLength())).toString();
	}

}
