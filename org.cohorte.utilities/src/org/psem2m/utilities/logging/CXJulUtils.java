package org.psem2m.utilities.logging;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.cohorte.utilities.CXClassUtils;
import org.psem2m.utilities.CXException;

/**
 * @author ogattaz
 *
 */
public class CXJulUtils {

	public static final String FILTER_JUL_NAME_CATALINA = "org.apache.catalina.*";
	public static final String FILTER_JUL_NAME_JETTY = "org.eclipse.jetty*";
	public static final String FILTER_JUL_NAME_SHIRO = "org.apache.shiro*";
	public static final String NO_FILTER = null;

	/**
	 * issue #29 MOD_OG 1.4.3
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
	 */
	public static final String SIMPLE_FORMATTER_FORMAT = "%1$tY/%1$tm/%1$td; %1$tH:%1$tM:%1$tS:%1$tL; %4$7.7s; %3$16.016s; %2$54.54s; %5$s%6$s%n";

	public static final String SIMPLE_FORMATTER_FORMAT_PROPERTY = "java.util.logging.SimpleFormatter.format";

	protected static SimpleFormatter sSimpleFormatter = new SimpleFormatter();

	/**
	 * Build a description of a Logger
	 *
	 * <pre>
	 * LoggerName=[org.apache.felix.gogo.runtime.threadio.ThreadIOImpl ](51) Level
	 * =[? ] UseParentH=[true] Parent=[root]
	 * LoggerName=[org.chohorte.isolate.logger.svc+2 ](33) Level =[ALL ]
	 * UseParentH=[false] Parent=[root]
	 *
	 * <pre>
	 * 
	 * @param aSB
	 * @param aLoggerName
	 *            The name of the logger
	 * @param aLogger
	 *            The instance of Jul logger.
	 * @return
	 *
	 *
	 */
	public static StringBuilder addDescriptionInSB(final StringBuilder aSB, final String aLoggerName,
			final Logger aLogger) {

		String wLoggerName = aLoggerName;
		if (wLoggerName == null) {
			wLoggerName = (aLogger != null) ? aLogger.getName() : "null";
		}
		// if "root" logger => set label "root" after the size calculation !
		if (wLoggerName.isEmpty()) {
			wLoggerName = "root";
		}

		aSB.append(String.format(" JULogger:[%-70s]", wLoggerName));

		if (aLogger == null) {
			aSB.append(" - LOGGER IS NULL");
		} else {

			aSB.append(String.format(" Level:[%-7s]", (aLogger.getLevel() != null) ? aLogger.getLevel().getName() : "?"));

			aSB.append(String.format(" UseParentH=[%b]", aLogger.getUseParentHandlers()));

			if (aLogger.getParent() != null) {
				final String wParentName = aLogger.getParent().getName();
				aSB.append(String.format(" Parent:[%s]", (!wParentName.isEmpty() ? wParentName : "root")));
			}

			final Handler[] wHandlers = aLogger.getHandlers();
			for (final Handler wHandler : wHandlers) {
				if (wHandler == null) {
					aSB.append("\n\t- Handler is null.");
				} else {
					String wFormatterClassName = "?";
					final Formatter wFormatter = wHandler.getFormatter();
					if (wFormatter != null) {
						wFormatterClassName = wFormatter.getClass().getSimpleName();
					}
					aSB.append(String.format("\n\t- Handler=[%25s] Formatter=[%s_]", wHandler.getClass()
							.getSimpleName(), wFormatterClassName));
				}
			}
		}
		return aSB;
	}

	/**
	 * <pre>
	 * (  0) LoggerName=[root                                                                  ]( 0) UseParentHandlers=[true]
	 * 	- Level =[INFO]
	 * 	- Handler=[           ConsoleHandler] Formatter=[CActivityFormaterHuman_543609822]
	 * (  1) LoggerName=[/                                                                     ]( 1) UseParentHandlers=[true] Parent=[root]
	 * (  2) LoggerName=[cohorte.isolate.aggregator.ISOL-ATEA-GGRE-GATO-R000+2                 ](53) UseParentHandlers=[false] Parent=[root]
	 * 	- Level =[ALL]
	 * 	- Handler=[     CActivityFileHandler] Formatter=[CActivityFormaterHuman_543609822]
	 * (  3) LoggerName=[global                                                                ]( 6) UseParentHandlers=[true] Parent=[root]
	 * (  4) LoggerName=[javax.ws.rs.ext.FactoryFinder                                         ](29) UseParentHandlers=[true] Parent=[root]
	 * (  5) LoggerName=[org.apache.felix.gogo.runtime.threadio.ThreadIOImpl                   ](51) UseParentHandlers=[true] Parent=[root]
	 * (  6) LoggerName=[org.apache.shiro.util.ThreadContext                                   ](35) UseParentHandlers=[true] Parent=[root]
	 * (  7) LoggerName=[org.eclipse.jetty.http.HttpFields                                     ](33) UseParentHandlers=[true] Parent=[root]
	 * (  8) LoggerName=[org.eclipse.jetty.http.HttpGenerator                                  ](36) UseParentHandlers=[true] Parent=[root]
	 * ...
	 * ( 20) LoggerName=[org.eclipse.jetty.server.handler.ContextHandler                       ](47) UseParentHandlers=[true] Parent=[root]
	 * ( 21) LoggerName=[org.eclipse.jetty.server.handler.ContextHandlerCollection             ](57) UseParentHandlers=[true] Parent=[root]
	 * ( 22) LoggerName=[org.eclipse.jetty.server.session                                      ](32) UseParentHandlers=[true] Parent=[root]
	 * ( 23) LoggerName=[org.eclipse.jetty.server.session.AbstractSessionIdManager             ](57) UseParentHandlers=[true] Parent=[org.eclipse.jetty.server.session]
	 * ...
	 * ( 38) LoggerName=[org.eclipse.jetty.util.thread.strategy.ExecutingExecutionStrategy     ](65) UseParentHandlers=[true] Parent=[root]
	 * ( 39) LoggerName=[org.glassfish.jersey.internal.Errors                                  ](36) UseParentHandlers=[true] Parent=[root]
	 * ( 40) LoggerName=[org.glassfish.jersey.internal.OsgiRegistry                            ](42) UseParentHandlers=[true] Parent=[root]
	 * ...
	 * ( 52) LoggerName=[org.glassfish.jersey.servlet.WebComponent                             ](41) UseParentHandlers=[true] Parent=[root]
	 * ( 53) LoggerName=[org.jvnet.hk2.logger                                                  ](20) UseParentHandlers=[true] Parent=[root]
	 * ( 54) LoggerName=[sun.net.www.protocol.http.HttpURLConnection                           ](43) UseParentHandlers=[true] Parent=[root]
	 * </pre>
	 *
	 * @return
	 */
	public static StringBuilder addDumpCurrentLoggersInSB(final StringBuilder wSB, final String aLoggerNameFilter) {

		final List<String> wSortedwNames = getLoggerNames();

		int wLoggerIdx = 0;
		for (final String wLoggerName : wSortedwNames) {

			if (wLoggerName == null) {
				wSB.append(String.format("\n(%3d) LoggerName is null.", wLoggerIdx));
			}
			//
			else if (aLoggerNameFilter == null || aLoggerNameFilter.isEmpty() || wLoggerName.isEmpty()
					|| filterLogger(wLoggerName, aLoggerNameFilter)) {

				wSB.append(String.format("\n(%3d) ", wLoggerIdx));

				final Logger wLogger = getLogManager().getLogger(wLoggerName);

				addDescriptionInSB(wSB, wLoggerName, wLogger);
			}
			wLoggerIdx++;
		}
		return wSB;
	}

	/**
	 * MOD_OG_1.4.6
	 * 
	 * @return
	 */
	public static String buildBannerLines() {

		StringBuilder wBannerLines = new StringBuilder();

		wBannerLines.append("\nThe Jul SimpleFormatter isn't configured with cohorte format.");

		wBannerLines.append(String.format("\nThe current format is       [%s].",
				CXJulUtils.getSimpleFormatterCurrentFormat()));

		wBannerLines.append(String.format("\nThe user friendly format is [%s].", CXJulUtils.SIMPLE_FORMATTER_FORMAT));

		return wBannerLines.toString();
	}

	/**
	 * @return
	 */
	public static String dumpCurrentLoggers() {
		return addDumpCurrentLoggersInSB(new StringBuilder(), null).toString();
	}

	/**
	 * @param aLoggerNameFilter
	 *            The filter to apply. Test the string equality by default. If
	 *            the last char is a star "*", the filter is used as prefix.
	 * @return
	 */
	public static String dumpCurrentLoggers(final String aLoggerNameFilter) {
		return addDumpCurrentLoggersInSB(new StringBuilder(), aLoggerNameFilter).toString();
	}

	/**
	 * @param aLoggerName
	 * @param aLoggerNameFilter
	 *            The filter to apply. Test the string equality by default. If
	 *            the last char is a star "*", the filter is used as prefix.
	 * @return
	 */
	private static boolean filterLogger(final String aLoggerName, final String aLoggerNameFilter) {

		if (aLoggerName == null || aLoggerName.isEmpty() || aLoggerNameFilter == null || aLoggerNameFilter.isEmpty()) {
			return true;
		}

		if (aLoggerNameFilter.endsWith("*")) {
			final String wFilterPrefix = aLoggerNameFilter.substring(0, aLoggerNameFilter.length() - 2);
			return aLoggerName.startsWith(wFilterPrefix);
		}
		return aLoggerName.equals(aLoggerNameFilter);
	}

	/**
	 * @return The sorted list of the names of the Jul loggers
	 */
	public static List<String> getLoggerNames() {
		final Enumeration<String> wNames = getLogManager().getLoggerNames();

		final List<String> wSortedNames = Collections.list(wNames);

		Collections.sort(wSortedNames);

		return wSortedNames;

	}

	/**
	 * @param aLoggerNameFilter
	 *            The filter to apply. Test the string equality by default. If
	 *            the last char is a star "*", the filter is used as prefix.
	 * @return The sorted list of the names of the Jul loggers
	 */
	public static List<String> getLoggerNames(final String aLoggerNameFilter) {

		final List<String> wSortedNames = getLoggerNames();

		if (aLoggerNameFilter == null || aLoggerNameFilter.isEmpty()) {
			return wSortedNames;
		}

		for (final String wLoggerName : wSortedNames) {
			if (!filterLogger(wLoggerName, aLoggerNameFilter)) {
				wSortedNames.remove(wLoggerName);
			}
		}
		return wSortedNames;
	}

	/**
	 * @return the current Jul Manager
	 */
	public static LogManager getLogManager() {
		return LogManager.getLogManager();
	}

	/**
	 * @return the "main" Jul logger
	 */
	public static Logger getRootLogger() {
		return getLogManager().getLogger("");
	}

	/**
	 * issue #29
	 * 
	 * <pre>
	 * SimpleFormat=[%1$tY/%1$tm/%1$td; %1$tH:%1$tM:%1$tS:%1$tL; %4$7.7s; %3$16.016s; %2$54.54s; %5$s%6$s%n]
	 * </pre>
	 * 
	 * @return the jvm parameter declaration
	 */
	public static String getSimpleFormaterJvmParameterSample() {
		return String.format("-D%s=\"%s\"", SIMPLE_FORMATTER_FORMAT_PROPERTY, SIMPLE_FORMATTER_FORMAT);
	}

	/**
	 * MOD_OG_1.4.3
	 * 
	 * retreive the static private field "format" of the class "SimpleFormatter"
	 * 
	 * <pre>
	 * // format string for printing the log record
	 * private static final String format = LoggingSupport.getSimpleFormat();
	 * </pre>
	 * 
	 * @return
	 */
	public static String getSimpleFormatterClassCurrentFormat() {

		// format string for printing the log record
		// private static final String format
		try {
			return CXClassUtils.getPrivateStaticFinalString(SimpleFormatter.class, "format");
		}
		//
		catch (Exception e) {
			return String.format("ERROR: %s", CXException.eCauseMessagesInString(e));
		}
	}

	/**
	 * 
	 */
	public static String getSimpleFormatterCurrentFormat() {

		if (isSimpleFormatterJvmPropertyExist()) {
			return getSimpleFormatterJvmProperty();
		} else {
			return getSimpleFormatterClassCurrentFormat();
		}
	}

	/**
	 * MOD_OG 1.4.6
	 * 
	 * @return the static instance of SimpleFormatter attached to this class
	 */
	public static SimpleFormatter getSimpleFormatterInstance() {
		return sSimpleFormatter;
	}

	/**
	 * @return the value of the system property
	 *         "java.util.logging.SimpleFormatter.format"
	 */
	public static String getSimpleFormatterJvmProperty() {

		return System.getProperty(SIMPLE_FORMATTER_FORMAT_PROPERTY);
	}

	/**
	 * MOD_OG 1.4.3
	 * 
	 * set
	 *
	 * @return a repport
	 */
	public static String initializeJulLoggers() {

		final StringBuilder wSB = new StringBuilder();

		Level wMainLoggerLevel = null;
		int wHandlerIdx = 0;
		Handler[] wHandlers = new Handler[0];

		final Logger wMainLogger = getRootLogger();

		final boolean wHasMainLogger = wMainLogger != null;

		wSB.append(String.format("\nhasMainLogger=[%b]", wHasMainLogger));

		if (wHasMainLogger) {

			wMainLoggerLevel = wMainLogger.getLevel();
			wSB.append(String.format("\nMainLoggerLevel=[%s]", wMainLoggerLevel));
			try {
				wHandlers = wMainLogger.getHandlers();
				if (wHandlers != null && wHandlers.length > 0) {
					for (final Handler wHandler : wHandlers) {

						boolean wToSet = ((wHandler instanceof ConsoleHandler) || (wHandler instanceof FileHandler));

						if (wToSet) {
							wHandler.setFormatter(sSimpleFormatter);
						}

						String wHandlerClassName = wHandler.getClass().getSimpleName();

						wSB.append(String.format("\n(%3d) [%s] setSimpleFormatter=[%b]", wHandlerIdx,
								wHandlerClassName, wToSet));

						wHandlerIdx++;
					}
				}

			} catch (final Exception e) {
				final Exception wEx = new Exception(String.format("Unable to set the formater of the main logger [%s]",
						wMainLogger.getName()), e);
				wSB.append(String.format("\nERROR: %s", CXException.eInString(wEx)));
			}

		}

		wSB.append(String.format("\nAll handlers configured=[%b] [%d/%d]", (wHandlers.length == wHandlerIdx),
				wHandlerIdx, wHandlers.length));

		return wSB.toString();
	}

	/**
	 * issue #29 MOD_OG 1.4.3 MOD_OG_1.4.6
	 * 
	 * @return true if the private static final String 'format' member of the
	 *         SimpleFormatter.class is set with the format
	 *         CXJulUtils.SIMPLE_FORMATTER_FORMAT
	 */
	public static boolean isSimpleFormaterClassContainsCohorteFormat() {

		return SIMPLE_FORMATTER_FORMAT.equals(getSimpleFormatterClassCurrentFormat());
	}

	/**
	 * issue #29 MOD_OG 1.4.3 MOD_OG_1.4.6
	 * 
	 * @return true if System property
	 *         "java.util.logging.SimpleFormatter.format" is set OR if the
	 *         private static final String 'format' member of the
	 *         SimpleFormatter.class is set with the format
	 *         CXJulUtils.SIMPLE_FORMATTER_FORMAT
	 */

	public static boolean isSimpleFormaterConfiguredWithCohorteFormat() {

		return isSimpleFormatterFormatPropertyContainsCohorteFormat() || isSimpleFormaterClassContainsCohorteFormat();

	}

	/**
	 * MOD_OG 1.4.3 MOD_OG_1.4.6
	 * 
	 * @return true if the system property
	 *         "java.util.logging.SimpleFormatter.format" exists and set with
	 *         the cohorte format ?
	 */
	public static boolean isSimpleFormatterFormatPropertyContainsCohorteFormat() {

		return SIMPLE_FORMATTER_FORMAT.equals(getSimpleFormatterJvmProperty());
	}

	/**
	 * MOD_OG_1.4.6
	 * 
	 * @return true if the the system property
	 *         "java.util.logging.SimpleFormatter.format"exists
	 */
	public static boolean isSimpleFormatterJvmPropertyExist() {

		return getSimpleFormatterJvmProperty() != null;
	}

	/**
	 * MOD_OG 1.4.3
	 * 
	 * <pre>
	 * 61    // format string for printing the log record
	 * 62    private static final String format = LoggingSupport.getSimpleFormat();
	 * </pre>
	 * 
	 * @param aFormat
	 *            the format to replace the format given by the method
	 *            "LoggingSupport.getSimpleFormat() "
	 * @return the report of the setting
	 * @throws Exception
	 */
	public static String setFormatOfSimpleFormatter(final String aFormat) throws Exception {

		return CXClassUtils.setPrivateStaticFinalString(SimpleFormatter.class, "format", aFormat);
	}

	/**
	 * @param aLogger
	 *            The Jul logger to set
	 * @param aFormatter
	 *            The Jul line formater to apply
	 * @return The number of modified handler
	 */
	public static int setFormatter(final Logger aLogger, final Formatter aFormatter) {

		int wNbSet = 0;
		final Handler[] wHandlers = aLogger.getHandlers();

		if (wHandlers != null && wHandlers.length > 0) {
			for (final Handler wHandler : wHandlers) {
				if (wHandler instanceof ConsoleHandler) {
					wHandler.setFormatter(aFormatter);
					wNbSet++;
				} else if (wHandler instanceof FileHandler) {
					wHandler.setFormatter(aFormatter);
					wNbSet++;
				}
			}
		}
		return wNbSet;
	}

	/**
	 * @param aLogger
	 * @return the number of modified handler
	 */
	public static int setSimpleFormatter(final Logger aLogger) {
		return setFormatter(aLogger, sSimpleFormatter);
	}

	/**
	 * @return the number of modified handler
	 */
	public static int setSimpleFormatterOfRootLooger() {
		return setFormatter(getRootLogger(), sSimpleFormatter);
	}

	/**
	 * @param aLogger
	 * @return
	 */
	public static String toString(final Logger aLogger) {
		final String wLoggerName = (aLogger != null) ? aLogger.getName() : "logger null";
		return addDescriptionInSB(new StringBuilder(), wLoggerName, aLogger).toString();
	}
}
