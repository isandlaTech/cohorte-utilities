/**
 * 
 */
package test.cohorte.utilities.testapps.impl;

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

	private final IActivityLogger pLogger;

	/**
	 * 
	 */
	private CTestLogging() {
		super();
		pLogger = CActivityLoggerBasicConsole.getInstance();
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

		pLogger.logInfo(this, "doTest", CXOSUtils.getEnvContext());
		pLogger.logInfo(this, "doTest", CXJvmUtils.getJavaContext());

		pLogger.logInfo(this, "doTest", "Ligne log info");
		pLogger.logDebug(this, "doTest", "Ligne log debug");
		pLogger.logWarn(this, "doTest", "Ligne log warning");
		pLogger.logSevere(this, "doTest", "Ligne log erreur: %s",
				new Exception("Message de l'exception"));
		pLogger.logInfo(this, "doTest", "Ligne log info");

		try {
			doTestLevel2();
		} catch (Exception e) {
			pLogger.logSevere(this, "doTest", "Ligne log erreur: %s", e);
		}
	}

	private void doTestLevel2() throws Exception {
		pLogger.logInfo(this, "doTestLevel2", "Ligne log info");
		try {
			doTestLevel3();
		} catch (Exception e) {
			pLogger.logSevere(this, "doTestLevel2", "ERROR: %s", e);
			throw new Exception("Message Exception Level2", e);
		} finally {
			pLogger.logInfo(this, "doTestLevel2", "finally");
		}
	}

	private void doTestLevel3() throws Exception {
		pLogger.logInfo(this, "doTestLevel3", "Ligne log info");
		try {
			doTestLevel4();
		} catch (Exception e) {
			pLogger.logSevere(this, "doTestLevel3", "ERROR: %s", e);
			throw new IllegalArgumentException("Message Exception Level3", e);
		} finally {
			pLogger.logInfo(this, "doTestLevel3", "finally");
		}
	}

	private void doTestLevel4() throws Exception {
		pLogger.logInfo(this, "doTestLevel4", "Ligne log info");
		try {
			throw new NullPointerException("Message Exception Level4");
		} catch (Exception e) {
			pLogger.logSevere(this, "doTest", "ERROR: %s", e);
			throw e;
		} finally {
			pLogger.logInfo(this, "doTestLevel4", "finally");
		}

	}
}
