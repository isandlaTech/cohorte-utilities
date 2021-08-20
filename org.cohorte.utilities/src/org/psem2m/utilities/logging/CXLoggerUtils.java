package org.psem2m.utilities.logging;

import java.util.logging.Level;

import org.psem2m.utilities.CXStringUtils;

/**
 * MOD_OG 1.0.16
 * 
 * @author ogattaz
 *
 */
public class CXLoggerUtils {

	private static final int BANNER_WIDTH = 140;

	// MOD_OG 1.4.3
	public static final String SIMPLE_FORMATTER_CONFIG = CXJulUtils.SIMPLE_FORMATTER_FORMAT;// "%1$tY/%1$tm/%1$td %1$tH-%1$tM-%1$tS.%1$tL|%3$30.30s|%4$8.8s| %5$s%6$s%n";

	// MOD_OG 1.4.3
	public static final String SIMPLE_FORMATTER_PROP_NAME = CXJulUtils.SIMPLE_FORMATTER_FORMAT_PROPERTY;// "java.util.logging.SimpleFormatter.format";

	/**
	 * @param aChar
	 *            the char used for the lines
	 * @param aInterline
	 *            add a line between the lines of the text
	 * @param aText
	 *            the text of the banner
	 * @return
	 */
	public static String buildBanner(final char aChar, final boolean aInterline, final String aText) {
		final StringBuilder wSB = new StringBuilder();
		wSB.append('\n');
		wSB.append('\n').append(CXStringUtils.strFromChar(aChar, BANNER_WIDTH));
		if (aText != null) {
			for (final String wLine : aText.split("\n")) {
				if (aInterline) {
					wSB.append('\n').append(aChar);
				}
				wSB.append('\n').append(aChar).append(' ').append(wLine);
			}
		}
		if (aInterline) {
			wSB.append('\n').append(aChar);
		}
		wSB.append('\n').append(CXStringUtils.strFromChar(aChar, BANNER_WIDTH));
		wSB.append('\n');
		return wSB.toString();
	}

	/**
	 * @param aText
	 *            the text of the banner
	 * @return
	 */
	public static String buildBanner(final String aText) {
		return buildBanner('#', true, aText);
	}

	/**
	 * @return
	 */
	public static boolean isSimpleFormatterConfigured() {
		String wConf = System.getProperty(CXLoggerUtils.SIMPLE_FORMATTER_PROP_NAME);
		return wConf != null && !wConf.isEmpty();
	}

	/**
	 * @param aLevel
	 * @param aWho
	 * @param aWhat
	 * @param aChar
	 *            the char used for the lines
	 * @param aInterline
	 *            add a line between the lines of the text
	 * @param aFormat
	 *            the format of the text of the banner
	 * @param aArgs
	 *            the arguments used in the format of the text of the banner
	 * @return
	 */
	public static String logBanner(final IActivityLogger aLogger, final Level aLevel, final Object aWho,
			final String aWhat, final char aChar, final boolean aInterline, final String aFormat, final Object... aArgs) {

		final String wBanner = CXLoggerUtils.buildBanner(aChar, aInterline, String.format(aFormat, aArgs));

		aLogger.log(aLevel, aWho, aWhat, wBanner);

		return wBanner;
	}

	/**
	 * @param aLogger
	 *            the target logger
	 * @param aLevel
	 * @param aWho
	 * @param aWhat
	 * @param aFormat
	 *            the format of the text of the banner
	 * @param aArgs
	 *            the arguments used in the format of the text of the banner
	 * @return
	 */
	public static String logBanner(final IActivityLogger aLogger, final Level aLevel, final Object aWho,
			final String aWhat, final String aFormat, final Object... aArgs) {

		return logBanner(aLogger, aLevel, aWho, aWhat, '#', true, aFormat, aArgs);
	}

	/**
	 * @param aLogger
	 *            the target logger
	 * @param aWho
	 * @param aWhat
	 * @param aChar
	 *            the char used for the lines
	 * @param aInterline
	 *            add a line between the lines of the text
	 * @param aFormat
	 *            the format of the text of the banner
	 * @param aArgs
	 *            the arguments used in the format of the text of the banner
	 * @return
	 */
	public static String logBannerInfo(final IActivityLogger aLogger, final Object aWho, final String aWhat,
			final char aChar, final boolean aInterline, final String aFormat, final Object... aArgs) {

		return logBanner(aLogger, Level.INFO, aWho, aWhat, aChar, aInterline, aFormat, aArgs);
	}

	/**
	 * @param aWho
	 * @param aWhat
	 * @param aFormat
	 *            the format of the text of the banner
	 * @param aArgs
	 *            the arguments used in the format of the text of the banner
	 * @return
	 */
	public static String logBannerInfo(final IActivityLogger aLogger, final Object aWho, final String aWhat,
			final String aFormat, final Object... aArgs) {

		return logBanner(aLogger, Level.INFO, aWho, aWhat, '#', true, aFormat, aArgs);
	}

	/**
	 * MOD_OG_1.4.6
	 * 
	 * <pre>
	 * 	############################################################################################################################################
	 * 	#
	 * 	# The Simpleformatter isn't configured
	 * 	#
	 * 	# The current format is       [%1$tb %1$td, %1$tY %1$tl:%1$tM:%1$tS %1$Tp %2$s%n%4$s: %5$s%6$s%n]
	 * 	#
	 * 	# The user friendly format is [%1$tY/%1$tm/%1$td; %1$tH:%1$tM:%1$tS:%1$tL; %4$7.7s; %3$16.016s; %2$54.54s; %5$s%6$s%n] 
	 * 	#
	 * 	############################################################################################################################################
	 * </pre>
	 */
	public static void logBannerSimpleFormatter(final IActivityLogger aLogger, final Object aWho, final String aWhat) {

		String wBannerLines = CXJulUtils.buildBannerLines();

		CXLoggerUtils.logBanner(aLogger, Level.SEVERE, aWho, aWhat, "%s", wBannerLines);
	}

}
