package test.org.cohorte.utilities.picosoc.loggers;

import org.cohorte.utilities.CXMethodUtils;
import org.cohorte.utilities.junit.CAbstractJunitTest;
import org.cohorte.utilities.picosoc.CComponentLogger;
import org.cohorte.utilities.picosoc.CServiceProperties;
import org.cohorte.utilities.picosoc.CServicesRegistry;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.psem2m.utilities.logging.CXJulUtils;
import org.psem2m.utilities.logging.IActivityLogger;

/**
 * #48
 * 
 * @author ogattaz
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CTestComponentsLoggerFile extends CAbstractJunitTest {

	static final String LOGGER_ONE_NAME = "LOGGER_ONE";

	static final String LOGGER_THREE_NAME = "LOGGER_THREE";

	static final String LOGGER_TWO_NAME = "LOGGER_TWO";

	/**
	 *
	 */
	@AfterClass
	public static void destroy() {

		// log the destroy banner containing the report
		logBannerDestroy();
	}

	/**
	 *
	 */
	@BeforeClass
	public static void initialize() throws Exception {

		// initialise the map of the test method of the current junit test class

		initializeTestsRegistry();

		// log the initialization banner
		logBannerInitialization();

		CServicesRegistry.newRegistry();
	}

	/**
	 * 
	 */
	public CTestComponentsLoggerFile() {
		super();
		getLogger().logInfo(this, "<init>", "instaciated");
	}

	/**
	 * @param wMethodName
	 * @param wLoggerName
	 * @throws Exception
	 */
	private void doNewLogger(final String wMethodName, final String wLoggerName)
			throws Exception {

		CLoggerConfigurator wLoggerConfig = new CLoggerConfigurator(wLoggerName);

		getLogger().logInfo(this, wMethodName, "LoggerConfig:\n%s",
				wLoggerConfig.toDescription());

		IActivityLogger wLogger = new CLoggerFile(wLoggerName);

		wLogger.logInfo(this, "wMethodName", "test");

		getLogger().logInfo(this, wMethodName, "Logger:\n%s",
				wLogger.toDescription());

		// getLogger().logInfo(this, wMethodName, "LOGGERS:\n%s",
		// CXJulUtils.dumpCurrentLoggers());
	}

	/**
	 * @param wMethodName
	 * @param wLoggerName
	 * @param wAction
	 * @throws Exception
	 */
	private void doUseLogger(final String wMethodName,
			final String wLoggerName, final String wAction) throws Exception {

		CServiceProperties wPropsTwo = (wLoggerName == null) ? null
				: CServiceProperties.newProps(CComponentLogger.LOGGER_ALIAS,
						wLoggerName);

		IActivityLogger wLoggerTwo = CServicesRegistry.getRegistry()
				.getService(IActivityLogger.class, wPropsTwo);

		wLoggerTwo.logInfo(this, wMethodName, "%s [%s] OK", wAction,
				wLoggerName);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void test01DumpLoggers() throws Exception {
		String wMethodName = CXMethodUtils.getMethodName(0);
		String wAction = "Dump the loggers";
		try {

			logBegin(this, wMethodName, "%s Begin...", wAction);

			getLogger().logInfo(this, wMethodName, "LOGGERS:\n%s",
					CXJulUtils.dumpCurrentLoggers());

			logEndOK(this, wMethodName, "%s End OK.", wAction);
		} catch (Throwable e) {
			logEndKO(this, wMethodName, e);
			throw e;
		}
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void test10NewLogger() throws Exception {
		String wMethodName = CXMethodUtils.getMethodName(0);
		String wAction = "New logger";
		try {

			logBegin(this, wMethodName, "%s Begin...  loggerName=[%s]",
					wAction, CComponentLogger.NO_LOGGER_ALIAS);

			doNewLogger(wMethodName, CComponentLogger.NO_LOGGER_ALIAS);

			logEndOK(this, wMethodName, "%s End OK.", wAction);
		} catch (Throwable e) {
			logEndKO(this, wMethodName, e);
			throw e;
		}
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void test20NewLoggerOne() throws Exception {
		String wMethodName = CXMethodUtils.getMethodName(0);
		String wAction = "New logger";
		try {

			logBegin(this, wMethodName, "%s Begin...  loggerName=[%s]",
					wAction, LOGGER_ONE_NAME);

			doNewLogger(wMethodName, LOGGER_ONE_NAME);

			logEndOK(this, wMethodName, "%s End OK.", wAction);
		} catch (Throwable e) {
			logEndKO(this, wMethodName, e);
			throw e;
		}
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void test30NewLoggerTwo() throws Exception {
		String wMethodName = CXMethodUtils.getMethodName(0);
		String wAction = "New logger";
		try {

			logBegin(this, wMethodName, "%s Begin...  loggerName=[%s]",
					wAction, LOGGER_TWO_NAME);

			doNewLogger(wMethodName, LOGGER_TWO_NAME);

			logEndOK(this, wMethodName, "%s End OK.", wAction);
		} catch (Throwable e) {
			logEndKO(this, wMethodName, e);
			throw e;
		}
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void test40NewLoggerThree() throws Exception {
		String wMethodName = CXMethodUtils.getMethodName(0);
		String wAction = "New logger";
		try {

			logBegin(this, wMethodName, "%s Begin...  loggerName=[%s]",
					wAction, LOGGER_THREE_NAME);

			doNewLogger(wMethodName, LOGGER_THREE_NAME);

			logEndOK(this, wMethodName, "%s End OK.", wAction);
		} catch (Throwable e) {
			logEndKO(this, wMethodName, e);
			throw e;
		}
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void test60UseLoggers() throws Exception {
		String wMethodName = CXMethodUtils.getMethodName(0);

		String wAction = "User loggers";
		try {

			logBegin(this, wMethodName, "%s Begin...  loggerName=[%s,%s,%s]",
					wAction, LOGGER_ONE_NAME, LOGGER_TWO_NAME,
					LOGGER_THREE_NAME);

			/*
			 * ONE
			 */
			doUseLogger(wMethodName, LOGGER_ONE_NAME, wAction);
			/*
			 * TWO
			 */
			doUseLogger(wMethodName, LOGGER_TWO_NAME, wAction);
			/*
			 * THREE
			 */
			doUseLogger(wMethodName, LOGGER_THREE_NAME, wAction);

			logEndOK(this, wMethodName, "%s End OK.", wAction);
		} catch (Throwable e) {
			logEndKO(this, wMethodName, e);
			throw e;
		}
	}

}
