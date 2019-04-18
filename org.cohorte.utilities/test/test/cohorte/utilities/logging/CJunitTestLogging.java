/**
 * 
 */
package test.cohorte.utilities.logging;

import java.util.logging.Level;

import org.cohorte.utilities.junit.CAbstractJunitTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.psem2m.utilities.CXException;
import org.psem2m.utilities.logging.CActivityLoggerBasicConsole;
import org.psem2m.utilities.logging.CXJulUtils;

/**
 * @author ogattaz
 * 
 */
public class CJunitTestLogging extends CAbstractJunitTest {

	/**
	 * 
	 */
	@AfterClass
	public static void destroy() {

		// log the destroy banner containing the report
		logBannerDestroy(CJunitTestLogging.class);
	}

	/**
	 * 
	 */
	@BeforeClass
	public static void initialize() {

		// initialise the map of the test method of the current junit test class
		// de
		initializeTestsRegistry(CJunitTestLogging.class);

		// log the initialization banner
		logBannerInitialization(CJunitTestLogging.class);
	}

	/**
	 * 
	 */
	public CJunitTestLogging() {
		super();
	}

	/**
	 * 
	 */
	@Test
	public void doTest10() throws Exception {
		String wMethodName = "doTest10";

		logBegin(this, wMethodName, "Test of the different logging methods");

		try {

			getLogger().logInfo(this, wMethodName,
					"--------------- Test of the the configuration of the SimpleFormatter");

			getLogger().logInfo(this, wMethodName, "SimpleFormatter current format=[%s]",
					CXJulUtils.getSimpleFormatterCurrentFormat());

			getLogger().logInfo(this, wMethodName, "SimpleFormatter jvm property  =[%s]",
					CXJulUtils.getSimpleFormatterJvmProperty());

			getLogger().logInfo(this, wMethodName, "SimpleFormatter Jvm property def sample: %s",
					CXJulUtils.getSimpleFormaterJvmParameterSample());

			CActivityLoggerBasicConsole wLoggerBasicConsole = (CActivityLoggerBasicConsole) CActivityLoggerBasicConsole
					.getInstance();

			java.util.logging.Logger wJulLogger = wLoggerBasicConsole.getJulLogger();

			getLogger().logInfo(this, wMethodName,
					"IsSimpleFormatterFormatValid=[%b] / JulLogger: Name=[%s] Level=[%s] ",
					wLoggerBasicConsole.IsSimpleFormatterFormatValid(), wJulLogger.getName(),
					wJulLogger.getLevel().getName());

			getLogger().logInfo(this, wMethodName, "--------------- Test of the level");
			getLogger().logInfo(this, wMethodName, "logInfo: log line with level INFO");
			getLogger().setLevel(Level.FINE);
			getLogger().logDebug(this, wMethodName, "logDebug: log line with level FINE");
			getLogger().logWarn(this, wMethodName, "logWarn: log line with level WARNING");
			getLogger().logSevere(this, wMethodName,
					"logSevere: log line with level SEVERE >>> See below an expected exception with its stack : %s",
					new Exception("Message de l'exception"));
			getLogger().logInfo(this, wMethodName, "logInfo: Ligne log info");

			logEndOK(this, wMethodName, "Test of the different logging methods done");

		} catch (Exception | Error e) {
			getLogger().logSevere(this, wMethodName, "ERROR: %s", e);

			logEndKO(this, wMethodName, "Unexpected exception !", e);

			throw e;
		}
	}

	/**
			 * 
			 */
	@Test
	public void doTestExceptionLevel1() throws Exception {
		String wMethodName = "doTestExceptionLevel1";

		logBegin(this, wMethodName, "Test the logging of the exceptions");

		try {

			try {
				doTestExceptionLevel2();
			} catch (Exception e) {
				getLogger().logSevere(this, wMethodName, "ERROR: %s", e);

				// valid the availability of the 2 causes
				String wMessgae = "The exception have 2 causes";
				Assert.assertEquals(wMessgae, 2, CXException.getNbCause(e));
				getLogger().logInfo(this, wMethodName, "ASSERTION OK : %s", wMessgae);
			}

			logEndOK(this, wMethodName, "Test of the logging methods of exceptions is done");

		} catch (Exception | Error e) {
			getLogger().logSevere(this, wMethodName, "ERROR: %s", e);

			logEndKO(this, wMethodName, "Unexpected exception !", e);

			throw e;
		}
	}

	private void doTestExceptionLevel2() throws Exception {
		String wMethodName = "doTestExceptionLevel2";
		getLogger().logInfo(this, wMethodName, "Ligne log info level 2");
		try {
			doTestExceptionLevel3();
		} catch (Exception e) {
			getLogger().logSevere(this, wMethodName, "ERROR: %s", e);
			throw new Exception("Message Exception Level2", e);
		} finally {
			getLogger().logInfo(this, wMethodName, "finally");
		}
	}

	private void doTestExceptionLevel3() throws Exception {
		String wMethodName = "doTestExceptionLevel3";

		getLogger().logInfo(this, wMethodName, "Ligne log info level 3");
		try {
			doTestExceptionLevel4();
		} catch (Exception e) {
			getLogger().logSevere(this, wMethodName, "ERROR: %s", e);
			throw new IllegalArgumentException("Message Exception Level3", e);
		} finally {
			getLogger().logInfo(this, wMethodName, "finally");
		}
	}

	private void doTestExceptionLevel4() throws Exception {
		String wMethodName = "doTestExceptionLevel4";
		getLogger().logInfo(this, wMethodName, "Ligne log info level 4");
		try {
			throw new NullPointerException("Message Exception Level4");
		} catch (Exception e) {
			getLogger().logSevere(this, wMethodName, "ERROR: %s", e);
			throw e;
		} finally {
			getLogger().logInfo(this, wMethodName, "finally");
		}

	}
}
