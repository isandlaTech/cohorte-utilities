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

	protected static SimpleFormatter sSimpleFormatter = new SimpleFormatter();

	/**
	 * Build a description of a Logger
	 *
	 * <pre>
	 * LoggerName=[org.apache.felix.gogo.runtime.threadio.ThreadIOImpl                   ](51) Level =[?      ] UseParentH=[true] Parent=[root]
	 * LoggerName=[org.chohorte.isolate.logger.svc+2                                     ](33) Level =[ALL    ] UseParentH=[false] Parent=[root]
	 *
	 * <pre>
	 * @param aSB
	 * @param aLoggerName The name of the logger
	 * @param aLogger The instance of Jul logger.
	 * @return
	 *
	 *
	 */
	public static StringBuilder addDescriptionInSB(final StringBuilder aSB,
			final String aLoggerName, final Logger aLogger) {

		String wLoggerName = aLoggerName;
		if (wLoggerName == null) {
			wLoggerName = (aLogger != null) ? aLogger.getName() : "null";
		}
		final int wLoggerNameSize = wLoggerName.length();
		// if "root" logger => set label "root" after the size calculation !
		if (wLoggerName.isEmpty()) {
			wLoggerName = "root";
		}

		aSB.append(String.format(" LoggerName=[%-70s](%2d)", wLoggerName,
				wLoggerNameSize));

		if (aLogger == null) {
			aSB.append(" - LOGGER IS NULL");
		} else {

			aSB.append(String.format(" Level =[%-7s]",
					(aLogger.getLevel() != null) ? aLogger.getLevel().getName()
							: "?"));

			aSB.append(String.format(" UseParentH=[%b]",
					aLogger.getUseParentHandlers()));

			if (aLogger.getParent() != null) {
				final String wParentName = aLogger.getParent().getName();
				aSB.append(String.format(" Parent=[%s]",
						(!wParentName.isEmpty() ? wParentName : "root")));
			}

			final Handler[] wHandlers = aLogger.getHandlers();
			for (final Handler wHandler : wHandlers) {
				if (wHandler == null) {
					aSB.append("\n\t- Handler is null.");
				} else {
					String wFormatterClassName = "?";
					final Formatter wFormatter = wHandler.getFormatter();
					if (wFormatter != null) {
						wFormatterClassName = wFormatter.getClass()
								.getSimpleName();
					}
					aSB.append(String.format(
							"\n\t- Handler=[%25s] Formatter=[%s_]", wHandler
									.getClass().getSimpleName(),
							wFormatterClassName));
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
	public static StringBuilder addDumpCurrentLoggersInSB(
			final StringBuilder wSB, final String aLoggerNameFilter) {

		final List<String> wSortedwNames = getLoggerNames();

		int wLoggerIdx = 0;
		for (final String wLoggerName : wSortedwNames) {

			if (wLoggerName == null) {
				wSB.append(String.format("\n(%3d) LoggerName is null.",
						wLoggerIdx));
			}
			//
			else if (aLoggerNameFilter == null || aLoggerNameFilter.isEmpty()
					|| wLoggerName.isEmpty()
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
		return addDumpCurrentLoggersInSB(new StringBuilder(), aLoggerNameFilter)
				.toString();
	}

	/**
	 * @param aLoggerName
	 * @param aLoggerNameFilter
	 *            The filter to apply. Test the string equality by default. If
	 *            the last char is a star "*", the filter is used as prefix.
	 * @return
	 */
	private static boolean filterLogger(final String aLoggerName,
			final String aLoggerNameFilter) {

		if (aLoggerName == null || aLoggerName.isEmpty()
				|| aLoggerNameFilter == null || aLoggerNameFilter.isEmpty()) {
			return true;
		}

		if (aLoggerNameFilter.endsWith("*")) {
			final String wFilterPrefix = aLoggerNameFilter.substring(0,
					aLoggerNameFilter.length() - 2);
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
			wSB.append(String
					.format("\nMainLoggerLevel=[%s]", wMainLoggerLevel));
			try {
				wHandlers = wMainLogger.getHandlers();
				if (wHandlers != null && wHandlers.length > 0) {
					for (final Handler wHandler : wHandlers) {
						if (wHandler instanceof ConsoleHandler) {
							wHandler.setFormatter(sSimpleFormatter);
							wSB.append(String.format(
									"\n(%3d) [%s] setSimpleFormatter=[%b]",
									wHandlerIdx, "ConsoleHandler", true));

						} else if (wHandler instanceof FileHandler) {
							wHandler.setFormatter(sSimpleFormatter);
							wSB.append(String.format(
									"\n(%3d) [%s] setSimpleFormatter=[%b]",
									wHandlerIdx, "FileHandler", true));
						}
						wHandlerIdx++;
					}
				}

			} catch (final Exception e) {
				final Exception wEx = new Exception(String.format(
						"Unable to set the formater of the main logger [%s]",
						wMainLogger.getName()), e);
				wSB.append(String.format("\nERROR: %s",
						CXException.eInString(wEx)));
			}

		}

		wSB.append(String.format("\nAll handlers configured=[%b] [%d/%d]",
				(wHandlers.length == wHandlerIdx), wHandlerIdx,
				wHandlers.length));

		return wSB.toString();
	}

	/**
	 * @param aLogger
	 *            The Jul logger to set
	 * @param aFormatter
	 *            The Jul line formater to apply
	 * @return The number of modified handler
	 */
	public static int setFormatter(final Logger aLogger,
			final Formatter aFormatter) {

		int wNbSet = 0;
		final Handler[] wHandlers = aLogger.getHandlers();

		if (wHandlers != null && wHandlers.length > 0) {
			for (final Handler wHandler : wHandlers) {
				if (wHandler instanceof ConsoleHandler) {
					wHandler.setFormatter(sSimpleFormatter);
					wNbSet++;
				} else if (wHandler instanceof FileHandler) {
					wHandler.setFormatter(sSimpleFormatter);
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
		final String wLoggerName = (aLogger != null) ? aLogger.getName()
				: "logger null";
		return addDescriptionInSB(new StringBuilder(), wLoggerName, aLogger)
				.toString();
	}

}
