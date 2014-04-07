package tests;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.psem2m.utilities.logging.CActivityLoggerBasicConsole;
import org.psem2m.utilities.logging.IActivityLogger;
import org.psem2m.utilities.system.CXOSServer;

/**
 * @author ogattaz
 * 
 */
public class CTestOSServer {

	private final static String CMD_CLOSE = "close";

	/**
	 * @param args
	 */
	public static void main(final String[] args) {

		int wExitCode = 0;
		CTestOSServer wTest = null;
		try {
			wTest = new CTestOSServer();
			wTest.doTest();
		} catch (Exception e) {
			e.printStackTrace();
			wExitCode = 1;
		} finally {

			wTest.destroy();
		}
		System.exit(wExitCode);
	}

	private final IActivityLogger pLogger = CActivityLoggerBasicConsole
			.getInstance();

	CXOSServer wCXOSServer = null;

	/**
	 * 
	 */
	private CTestOSServer() {
		super();
		pLogger.logInfo(this, "<init>", "initialized");
	}

	/**
	 * @return
	 */
	private Map<String, String> buildExistdbEnv() {
		Map<String, String> wEnv = new HashMap<String, String>();
		wEnv.put("JAVA_HOME",
				"/Library/Java/JavaVirtualMachines/jdk1.7.0_45.jdk/Contents/Home/jre");
		return wEnv;
	}

	/**
	 * @return
	 */
	private String[] buildExitdbCommand() {

		ArrayList<String> wCmdLineArgs = new ArrayList<String>();

		wCmdLineArgs.add("/bin/bash");
		wCmdLineArgs.add("-c");

		StringBuilder wBashCommands = new StringBuilder();

		// show user directory
		wBashCommands.append("echo pwd=[$PWD];");

		// the sudo prompt is sent in stdErr ! => remove the prompt !
		wBashCommands.append(String.format(
				"echo \"%s\" | sudo -S -k -p \"\" bin/startup.sh;",
				getSudoPass()));

		wCmdLineArgs.add(wBashCommands.toString());
		return wCmdLineArgs.toArray(new String[wCmdLineArgs.size()]);
	}

	private String[] buildKillCommand(final int aPid, final String aSignal) {

		ArrayList<String> wCmdLineArgs = new ArrayList<String>();

		wCmdLineArgs.add("/bin/bash");
		wCmdLineArgs.add("-c");

		StringBuilder wBashCommands = new StringBuilder();

		// show user directory
		wBashCommands.append("echo pwd=[$PWD];");

		// the sudo prompt is sent in stdErr ! => remove the prompt !
		wBashCommands
				.append(String
						.format("echo \"%s\" | sudo -S -k -p \"\" kill -%s %s; echo \"after kill\"",
								getSudoPass(), aSignal.toUpperCase(), aPid));

		wCmdLineArgs.add(wBashCommands.toString());
		return wCmdLineArgs.toArray(new String[wCmdLineArgs.size()]);
	}

	private File buildUserDirFile() {
		return new File(
				"/Applications/eXist-db.app/Contents/Resources/eXist-db");
	}

	/**
	 * 
	 */
	private void destroy() {
		pLogger.logInfo(this, "destroy", "close the logger");
	}

	/**
	 * @throws Exception
	 */
	private void doCommandClose() throws Exception {
		int wPid = wCXOSServer.getPid();
		pLogger.logInfo(this, "doTest", "Pid=[%s]", wPid);
		wCXOSServer.stop(10000, buildKillCommand(wPid, "SIGTERM"));
	}

	/**
	 * @throws Exception
	 * 
	 */
	private void doTest() throws Exception {
		pLogger.logInfo(this, "doTest", "BEGIN");

		wCXOSServer = new CXOSServer(pLogger, buildExitdbCommand());

		wCXOSServer.startAndWaitInStdOut(buildUserDirFile(), buildExistdbEnv(),
				15000, "Server has started on ports");

		waitForUserCommand();

		pLogger.logInfo(this, "doTest", "END");
	}

	/**
	 * @return
	 */
	private String getSudoPass() {
		return "Olivier38";
	}

	/**
	 * @return
	 * @throws Exception
	 */
	private boolean waitForUserCommand() throws Exception {

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

			if (CMD_CLOSE.equalsIgnoreCase(wCmde)) {
				doCommandClose();
			}

		} while (!CMD_CLOSE.equalsIgnoreCase(wCmde));

		wScanConsoleIn.close();

		pLogger.logInfo(this, "waitForUserCommand", "END");
		return true;
	}

}
