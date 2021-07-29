/**
 * 
 */
package test.cohorte.utilities.logging;

import org.psem2m.utilities.logging.CActivityLoggerBasicConsole;
import org.psem2m.utilities.logging.IActivityLogger;

/**
 * @author ogattaz
 *
 */
public class CTestLoggerConsole {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		IActivityLogger wLogger = CActivityLoggerBasicConsole.getInstance();

		wLogger.logInfo(CTestLoggerConsole.class, "main", "Begin");

		// ...

		wLogger.logInfo(CTestLoggerConsole.class, "main", "End");

	}

}
