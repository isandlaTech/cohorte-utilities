package org.cohorte.utilities.installer;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

import org.cohorte.utilities.picosoc.CAbstractComponentBase;
import org.psem2m.utilities.CXDateTime;
import org.psem2m.utilities.CXJavaRunContext;
import org.psem2m.utilities.CXStringUtils;
import org.psem2m.utilities.files.CXFile;
import org.psem2m.utilities.files.CXFileDir;
import org.psem2m.utilities.logging.CActivityFormaterBasic;
import org.psem2m.utilities.logging.CActivityLoggerBasic;
import org.psem2m.utilities.logging.CLogLineTextBuilder;
import org.psem2m.utilities.logging.IActivityFormater;
import org.psem2m.utilities.logging.IActivityLogger;
import org.psem2m.utilities.logging.IActivityRequester;

public class CInstallerLogger extends CAbstractComponentBase implements
		IActivityLogger {

	private final static String FILENAME_EXT = "log";

	private final static String FILENAME_NUM = "%g";

	private static final IActivityFormater sActivityFormater = CActivityFormaterBasic
			.getInstance(IActivityFormater.LINE_SIMPLEFORMATER);

	private static CActivityLoggerBasic sFileLogger = null;

	private static final CLogLineTextBuilder sLogLineTextBuilder = CLogLineTextBuilder
			.getInstance();

	private static SimpleFormatter sSimpleFormatter = new SimpleFormatter();

	/**
	 * @param aLevel
	 * @return
	 */
	public static boolean isLogOn(Level aLevel) {
		return (sFileLogger != null) ? sFileLogger.isLoggable(aLevel) : true;
	}

	/**
	 * @param aLevel
	 * @param aWho
	 * @param aWhat
	 * @param aInfos
	 */
	public static void logInConsole(Level aLevel, final Object aWho,
			CharSequence aWhat, Object... aInfos) {

		if (!isLogOn(aLevel)) {
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

		LogRecord wLogRecord = new LogRecord(aLevel, wLine);
		wLogRecord.setLoggerName("console CInstallerLogger:logInConsole()");
		System.out.print(sSimpleFormatter.format(wLogRecord));
	}

	/**
	 * @param aLevel
	 * @param aWho
	 * @param aWhat
	 * @param aInfos
	 */
	public static void logInFile(Level aLevel, final Object aWho,
			CharSequence aWhat, Object... aInfos) {

		if (!isLogOn(aLevel)) {
			return;
		}
		sFileLogger.log(aLevel, aWho, aWhat, aInfos);

		logInConsole(aLevel, aWho, aWhat, aInfos);
	}

	/**
	 * @param record
	 */
	public static void logInFile(final LogRecord record) {

		if (!isLogOn(record.getLevel())) {
			return;
		}
		sFileLogger.log(record);
	}

	private String pAbsolutePathPattern = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.logging.IActivityLogger#getRequester()
	 */
	/**
	 * @param aLog
	 */
	public CInstallerLogger(final String aName) {
		super();

		registerMeAsService(IActivityLogger.class);

		initFileLogger(aName);

		logInfo(this, "<init>", "instanciated: lineDef=[%s]",
				sActivityFormater.getLineDefInString());
	}

	@Override
	public Appendable addDescriptionInBuffer(Appendable aBuffer) {
		return CXStringUtils.appendStringsInBuff(aBuffer, getClass()
				.getSimpleName(), String.valueOf(hashCode()));
	}

	/**
	 * @return
	 */
	private String buildFileNamePattern(String aLoggerName) {
		StringBuilder wSB = new StringBuilder();
		wSB.append(aLoggerName);
		wSB.append('_');
		wSB.append(CXDateTime.time2StrAAAAMMJJHHMMSS());
		wSB.append('_');
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
		logInfo(this, "close", "hasFileLogger=[%s]", (sFileLogger != null));

		if (sFileLogger != null) {
			sFileLogger.close();
			sFileLogger = null;
		}
	}

	/**
	 * 
	 */
	public void closeAndMove(final String aDestPath) {

		CXFileDir wDestDir = destPathToFileDir(aDestPath);

		boolean wMoveLogFiles = (wDestDir != null);

		if (!wMoveLogFiles) {
			logInfo(this,
					"closeAndMove",
					"MoveLog=[%s] aDestPath [%s] is null or isn't an existing directory",
					wMoveLogFiles, aDestPath);
		} else {
			logInfo(this, "closeAndMove", "MoveLog=[%s] Destination=[%s] ",
					wMoveLogFiles, wDestDir.getAbsolutePath());
		}

		close();

		if (wMoveLogFiles) {
			try {
				int wFileIdx = 0;

				CXFile wDestFile;
				CXFile wSourceFile = new CXFile(pAbsolutePathPattern.replace(
						FILENAME_NUM, String.valueOf(wFileIdx)));

				while (wSourceFile.exists()) {

					wDestFile = new CXFile(wDestDir, wSourceFile.getName());

					wDestFile = wSourceFile.copyTo(wDestFile,
							CXFile.DELETE_IF_EXIST);

					logInConsole(Level.INFO, this, "closeAndMove",
							"copied=[%s] dest=[%s]", wDestFile.exists(),
							wDestFile.getAbsolutePath());

					if (wDestFile.exists()) {

						boolean wDeleted = wSourceFile.delete();
						logInConsole(Level.INFO, this, "closeAndMove",
								"deleted=[%s] source=[%s]", wDeleted,
								wSourceFile.getAbsolutePath());
					}
					wFileIdx++;
					wSourceFile = new CXFile(pAbsolutePathPattern.replace(
							FILENAME_NUM, String.valueOf(wFileIdx)));
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param aDestPath
	 * @return
	 */
	private CXFileDir destPathToFileDir(final String aDestPath) {
		if (aDestPath == null || aDestPath.isEmpty()) {
			return null;
		}
		CXFileDir wCXFileDir = new CXFileDir(aDestPath);
		if (!wCXFileDir.exists() || !wCXFileDir.isDirectory()) {
			return null;
		}
		return wCXFileDir;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.logging.IActivityLoggerBase#getLevel()
	 */
	@Override
	public Level getLevel() {
		return (sFileLogger != null) ? sFileLogger.getLevel() : Level.OFF;
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
	 * @param aLoggerName
	 *            eg. "X3CryptedExchange_CInstaller" or "iCT4X3_CUninstaller"
	 */
	private void initFileLogger(final String aLoggerName) {

		try {
			String wLevel = "FINE";

			String wLogFileNamePattern = buildFileNamePattern(aLoggerName);

			File wDirLogs = CXFileDir.getTempDir();
			CXFileDir wLogFilePattern = new CXFileDir(wDirLogs,
					wLogFileNamePattern);

			pAbsolutePathPattern = wLogFilePattern.getAbsolutePath();

			sFileLogger = (CActivityLoggerBasic) CActivityLoggerBasic
					.newLogger(aLoggerName, pAbsolutePathPattern, wLevel,
							10 * 1024 * 1024, 10, IActivityFormater.LINE_SHORT,
							IActivityFormater.MULTILINES_TEXT);

			logInfo(this, "initFileLogger", "FileLogger: %s",
					sFileLogger.toDescription());

		} catch (Exception e) {
			logSevere(this, "initFileLogger", e);
		}
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
		return isLogOn(aLevel);
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

		logInFile(aLevel, aWho, aWhat, aInfos);
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
		logInFile(record);
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
		if (sFileLogger != null) {
			sFileLogger.setLevel(aLevel);
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
		if (sFileLogger != null) {
			sFileLogger.setLevel(aLevelName);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.IXDescriber#toDescription()
	 */
	@Override
	public String toDescription() {
		return addDescriptionInBuffer(new StringBuilder(128)).toString();
	}
}
