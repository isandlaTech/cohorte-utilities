package tests;

import java.util.Scanner;

import org.psem2m.utilities.logging.CActivityLoggerBasicConsole;
import org.psem2m.utilities.logging.IActivityLogger;

/**
 * @author ogattaz
 * 
 */
public abstract class CAbstractTest {

	public final static String CMD_CLOSE = "close";

	protected final IActivityLogger pLogger;

	/**
	 * 
	 */
	public CAbstractTest() {
		super();
		pLogger = CActivityLoggerBasicConsole.getInstance();
	}

	/**
	 * 
	 */
	protected void destroy() {
		pLogger.logInfo(this, "destroy", "close the logger");
		pLogger.close();
	}

	protected abstract void doTest() throws Exception;

	protected abstract void doUserCommand(final String wCmde) throws Exception;

	/**
	 * @return
	 * @throws Exception
	 */
	protected boolean waitForUserCommand() throws Exception {

		pLogger.logInfo(this, "waitForUserCommand",
				"START Stdin console. Wait for a close command to stop the server.");

		pLogger.logInfo(this, "waitForStop", "=>");
		Scanner wScanConsoleIn = new Scanner(System.in);
		String wCmde = null;
		do {

			// Reads a single line from the console
			wCmde = wScanConsoleIn.nextLine().toLowerCase();

			pLogger.logInfo(this, "waitForStop",
					"Stdin console command : [%s]", wCmde);

			doUserCommand(wCmde);

		} while (!CMD_CLOSE.equalsIgnoreCase(wCmde));

		wScanConsoleIn.close();

		pLogger.logInfo(this, "waitForUserCommand", "END");
		return true;
	}

}
