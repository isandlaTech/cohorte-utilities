package org.cohorte.utilities.junit;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;

import org.cohorte.utilities.CXClassUtils;
import org.psem2m.utilities.CXException;
import org.psem2m.utilities.CXStringUtils;
import org.psem2m.utilities.CXTimer;
import org.psem2m.utilities.logging.CActivityLoggerBasicConsole;
import org.psem2m.utilities.logging.CXLoggerUtils;
import org.psem2m.utilities.logging.IActivityLogger;
import org.psem2m.utilities.logging.IActivityLoggerJul;

/**
 * @author ogattaz
 * @since 1.1.0 (#32)
 */
public class CAbstractJunitTest {

	private static final String END_TEST_LINE = CXStringUtils.strFromChar('.', 140);

	private static final String ID_PARAM_SIMPLE_FORMATTER = "java.util.logging.SimpleFormatter.format";

	private static final String LINE_FORMAT_VALUE = "%1$tY/%1$tm/%1$td; %1$tH:%1$tM:%1$tS:%1$tL; %4$7.7s; %3$16.016s; %2$54.54s; %5$s%6$s%n"
			.replaceAll("%", "%%");

	private static final String PARAM_SIMPLE_FORMATTER_DEF = String.format("-D%s=\"%s\"", ID_PARAM_SIMPLE_FORMATTER,
			LINE_FORMAT_VALUE);

	protected static final IActivityLogger sLogger = CActivityLoggerBasicConsole.getInstance();

	protected static final CTestsContext sTestsContext = new CTestsContext(sLogger);

	private static final Map<String, CTestsRegistry> sTestsRegistrysMap = new TreeMap<>();

	/**
	 * @return
	 */
	public static String dumpTestsMaps() {
		StringBuilder wSB = new StringBuilder();
		for (Entry<String, CTestsRegistry> wEntry : sTestsRegistrysMap.entrySet()) {
			if (wSB.length() > 0) {
				wSB.append('\n');
			}
			wEntry.getValue().dumpIn(wSB);
		}
		return wSB.toString();
	}

	/**
	 * @return
	 */
	public static int getNbtestsMap(final Class<? extends CAbstractJunitTest> aClass) {
		return sTestsRegistrysMap.get(getTestsMapKey(aClass)).size();
	}

	/**
	 * @param aClass
	 * @return
	 */
	private static String getTestsMapKey(final Class<? extends CAbstractJunitTest> aClass) {

		return aClass.getName();
	}

	/**
	 * @return
	 */
	public static CTestsRegistry initializeTestsRegistry() {
		return initializeTestsRegistry(CXClassUtils.findClass(CAbstractJunitTest.class));
	}
	
	/**
	 * @return
	 */
	public static CTestsRegistry initializeTestsRegistry(final Class<? extends CAbstractJunitTest> aClass) {

		CTestsRegistry wTestsRegistry = new CTestsRegistry(aClass);

		sTestsRegistrysMap.put(getTestsMapKey(aClass), wTestsRegistry);

		return wTestsRegistry;
	}
	/**
	 * 
	 */
	public static void logBannerDestroy() {
		logBannerDestroy(CXClassUtils.findClass(CAbstractJunitTest.class));
	}
	
	/**
	 * @param aClassTestSimpleName
	 */
	public static void logBannerDestroy(final Class<? extends CAbstractJunitTest> aClassTest) {

		CXLoggerUtils.logBannerInfo(sLogger, aClassTest, "destroy", "END OF TEST %s \n%s", aClassTest.getSimpleName(),
				dumpTestsMaps());
	}
	
	/**
	 * 
	 */
	public static void logBannerInitialization() {
		logBannerInitialization(CXClassUtils.findClass(CAbstractJunitTest.class));
	}
	
	/**
	 * <pre>
	 * -Djava.util.logging.SimpleFormatter.format="%1$tY/%1$tm/%1$td; %1$tH:%1$tM:%1$tS:%1$tL; %4$7.7s; %3$16.016s; %2$54.54s; %5$s%6$s%n"
	 * </pre>
	 * 
	 * @param aClassTestSimpleName
	 */

	public static void logBannerInitialization(final Class<? extends CAbstractJunitTest> aClassTest) {

		StringBuilder wBannerLines = new StringBuilder();

		wBannerLines.append(
				String.format("BEGIN OF TEST %s . NbTest=[%s]", aClassTest.getSimpleName(), getNbtestsMap(aClassTest)));

		boolean wIsSimpleFormatterConfigured = (System.getProperty(ID_PARAM_SIMPLE_FORMATTER) != null);

		if (!wIsSimpleFormatterConfigured) {
			wBannerLines
					.append(String.format("\nThe Simpleformatter must be configured using the property[%s] .\neg. %s",
							ID_PARAM_SIMPLE_FORMATTER, PARAM_SIMPLE_FORMATTER_DEF));
		}

		CXLoggerUtils.logBannerInfo(sLogger, aClassTest, "initialize", wBannerLines.toString());

		sLogger.logInfo(aClassTest, "initialize", "CAbstractJunitTest initialized.");

		sLogger.setLevel(Level.ALL);

		((IActivityLoggerJul) sLogger).getJulLogger().setLevel(Level.ALL);

		sLogger.logInfo(aClassTest, "initialize", "Logger=[%s] Level=[%s]", sLogger.toDescription(),
				sLogger.getLevel().getName());

	}

	private final CXTimer pTimer;

	/**
	 * 
	 */
	public CAbstractJunitTest() {
		super();
		pTimer = CXTimer.newStartedTimer();
		getLogger().logInfo(this, "<init>", "Test [%s] extends [CAbstractJunitTest] instanciated OK",
				this.getClass().getSimpleName());
	}

	/**
	 * Sample
	 * 
	 * <pre>
	 * 	2019/03/17; 15:43:45:834;   INFOS;             main; s.calendar.CTestEaster_3070; ildValidationDataRimouski; --- END TEST OK : nb loaded date=[2518]
	 * 	                                                                                                             --- Duration=[118.679 ms]
	 * 	............................................................................................................................................
	 * </pre>
	 * 
	 * @return
	 */
	private String buildTestFooter() {
		return String.format("\n%108s --- Duration=[%s ms]\n%s", "", pTimer.getDurationStrMicroSec(), END_TEST_LINE);
	}

	/**
	 * @return
	 */
	public IActivityLogger getLogger() {
		return sLogger;
	}

	/**
	 * @return
	 */
	public CTestsContext getTestsContext() {
		return sTestsContext;
	}

	/**
	 * @return
	 */
	public CTestsRegistry getTestsRegistry() {
		return sTestsRegistrysMap.get(getTestsRegistryKey());
	}

	/**
	 * @return
	 */
	private String getTestsRegistryKey() {
		return getTestsMapKey(this.getClass());
	}

	/**
	 * @param aTest
	 * @param aMethodName
	 * @param aLine
	 * @throws Exception
	 */
	protected void logBegin(final CAbstractJunitTest aTest, final String aMethodName, final String aLineFormat,
			final Object... aArgs) throws Exception {

		logBeginMultiple(aTest, aMethodName, null, aLineFormat, aArgs);
	}

	/**
	 * @param aTest
	 * @param aMethodName
	 * @param aRunningId
	 * @param aLineFormat
	 * @param aArgs
	 * @throws Exception
	 */
	protected void logBeginMultiple(final CAbstractJunitTest aTest, final String aMethodName, final String aRunningId,
			final String aLineFormat, final Object... aArgs) throws Exception {

		CTestDefinition wTestDefinition = getTestsRegistry().setTestBegin(aMethodName, aRunningId);

		String wRunningInfos = (aRunningId != null) ? String.format("runningId=[%s]", aRunningId) : "";

		CXLoggerUtils.logBanner(getLogger(), Level.INFO, this, aMethodName, '-', false,
				"BEGIN TEST [%d/%d]   method=[%s]   %s", wTestDefinition.getTestNumber(), getTestsRegistry().size(),
				aMethodName, wRunningInfos);

		getLogger().logInfo(this, aMethodName, "--- BEGIN TEST : %s", String.format(aLineFormat, aArgs));
	}

	/**
	 * @param aTest
	 * @param aMethodName
	 * @param aRunningId
	 * @param aLineFormat
	 * @param aArgs
	 * @throws Exception
	 */
	protected void logBeginMultipleKO(final CAbstractJunitTest aTest, final String aMethodName, final String aRunningId,
			final String aMessage, final Throwable aException) throws Exception {

		getTestsRegistry().setTestKO(aMethodName, aRunningId, aMessage, aException);

		getLogger().logSevere(this, aMethodName, "--- END TEST ON ERROR : %s" + buildTestFooter(),
				CXException.eCauseMessagesInString(aException));
	}

	/**
	 * @param aTest
	 * @param aMethodName
	 * @param aRunningId
	 * @param aLineFormat
	 * @param aArgs
	 * @throws Exception
	 */
	protected void logBeginMultipleOK(final CAbstractJunitTest aTest, final String aMethodName, final String aRunningId,
			final String aLineFormat, final Object... aArgs) throws Exception {

		getTestsRegistry().setTestOK(aMethodName, aRunningId);

		getLogger().logInfo(this, aMethodName, "--- END TEST OK : %s" + buildTestFooter(),
				String.format(aLineFormat, aArgs));
	}

	/**
	 * @param aTest
	 * @param aMethodName
	 * @param aMessage
	 * @throws Exception
	 */
	protected void logEndKO(final CAbstractJunitTest aTest, final String aMethodName, final String aMessage)
			throws Exception {
		logEndKO(aTest, aMethodName, aMessage, null);
	}

	/**
	 * @param aTest
	 * @param aMethodName
	 * @param aMessage
	 * @param aException
	 * @throws Exception
	 */
	protected void logEndKO(final CAbstractJunitTest aTest, final String aMethodName, final String aMessage,
			final Throwable aException) throws Exception {
		logBeginMultipleKO(aTest, aMethodName, null, aMessage, aException);
	}

	/**
	 * @param aTest
	 * @param aMethodName
	 * @param aLineFormat
	 * @param aException
	 * @throws Exception
	 */
	protected void logEndKO(final CAbstractJunitTest aTest, final String aMethodName, final Throwable aException)
			throws Exception {
		logEndKO(aTest, aMethodName, null, aException);
	}

	/**
	 * @param aTest
	 * @param aMethodName
	 * @param aLineFormat
	 * @param aArgs
	 * @throws Exception
	 */
	protected void logEndOK(final CAbstractJunitTest aTest, final String aMethodName, final String aLineFormat,
			final Object... aArgs) throws Exception {
		logBeginMultipleOK(aTest, aMethodName, null, aLineFormat, aArgs);
	}
}
