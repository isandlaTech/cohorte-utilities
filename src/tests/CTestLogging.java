/**
 * 
 */
package tests;

import org.psem2m.utilities.CXJvmUtils;
import org.psem2m.utilities.CXOSUtils;
import org.psem2m.utilities.logging.CActivityLoggerBasicConsole;
import org.psem2m.utilities.logging.IActivityLogger;

/**
 * @author ogattaz
 * 
 */
public class CTestLogging {

	/**
	 * @param args
	 */
	public static void main(final String[] args) {

		try {
			CTestLogging wTest = new CTestLogging();
			wTest.doTest();
			wTest.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private final IActivityLogger pLogger = CActivityLoggerBasicConsole
			.getInstance();

	/**
	 * 
	 */
	private CTestLogging() {
		super();
	}

	/**
	 * 
	 */
	private void destroy() {
		pLogger.logInfo(this, "destroy", "close the logger");
		pLogger.close();
	}

	/**
	 * 
	 */
	private void doTest() {

		Throwable wThrowable = new Exception("Message de l'exception");

		IActivityLogger wLogger = CActivityLoggerBasicConsole.getInstance();

		wLogger.logInfo(this, "doTest", "Ligne log info");
		wLogger.logDebug(this, "doTest", "Ligne log debug");
		wLogger.logWarn(this, "doTest", "Ligne log warning");
		wLogger.logSevere(this, "doTest", "Message d'erreur", wThrowable);
		wLogger.logInfo(this, "doTest", "Ligne log info");

		wLogger.logInfo(this, "doTest", CXOSUtils.getEnvContext());

		wLogger.logInfo(this, "doTest", CXJvmUtils.getJavaContext());

		wLogger.close();
	}
}
