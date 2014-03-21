package tests;

import java.util.ArrayList;

import org.psem2m.utilities.logging.CActivityLoggerBasicConsole;
import org.psem2m.utilities.logging.IActivityLogger;
import org.psem2m.utilities.system.CXOSCommand;

/**
 * @author ogattaz
 * 
 */
public class CTestOSCommand {

	/**
	 * @param args
	 */
	public static void main(final String[] args) {

		try {
			CTestOSCommand wTest = new CTestOSCommand();
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
	private CTestOSCommand() {
		super();
	}

	/**
	 * Commande
	 * 
	 * <pre>
	 * echo "myPassword" | sudo -S -k -p "" apacheds status default
	 * </pre>
	 * 
	 * <pre>
	 * ApacheDS - default is running (6833).
	 * </pre>
	 * 
	 * @return
	 */
	private String[] buildApacheDsCommand(final String aVerb) {

		ArrayList<String> wCmdLineArgs = new ArrayList<String>();

		wCmdLineArgs.add("/bin/bash");
		wCmdLineArgs.add("-c");
		// the sudo prompt is sent in stdErr ! => remove the prompt !
		wCmdLineArgs.add(String.format(
				"echo \"%s\" | sudo -S -k -p \"\" apacheds %s %s",
				getSudoPass(), aVerb, getInstance()));

		return wCmdLineArgs.toArray(new String[wCmdLineArgs.size()]);
	}

	/**
	 * Commande
	 * 
	 * <pre>
	 * netstat -an | grep 10389
	 * </pre>
	 * 
	 * <pre>
	 * ApacheDS - default is running (6833).
	 * </pre>
	 * 
	 * @return
	 */
	private String[] buildCommandNetstat(final int aPort) {

		ArrayList<String> wCmdLineArgs = new ArrayList<String>();

		wCmdLineArgs.add("/bin/bash");
		wCmdLineArgs.add("-c");
		wCmdLineArgs.add(String.format("netstat -an | grep %d", aPort));

		return wCmdLineArgs.toArray(new String[wCmdLineArgs.size()]);
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
		pLogger.logInfo(this, "doTest", "BEGIN");

		/**
		 * <pre>
		 * pb-d-128-141-252-154:default ogattaz$ echo "$JAVA_HOME"
		 * /System/Library/Frameworks/JavaVM.framework/Home
		 * </pre>
		 */
		pLogger.logInfo(this, "doTest", "Exec:\n%s",
				execOsCommand("echo", "abcdefghijklmnopqrstuvwxyz"));

		/**
		 * <pre>
		 * pb-d-128-141-252-154:default ogattaz$ echo "Olivier38" | sudo -S -k -p "invoker=%u runas=%U =>" apacheds status default
		 * invoker=ogattaz runas=root =>ApacheDS - default is running (6833).
		 * </pre>
		 */
		pLogger.logInfo(this, "doTest", "Exec:\n%s",
				execOsCommand(buildApacheDsCommand("status")));

		pLogger.logInfo(this, "doTest", "Exec:\n%s",
				execOsCommand(buildCommandNetstat(10389)));

		pLogger.logInfo(this, "doTest", "END");
	}

	/**
	 * @param aVerb
	 * @return
	 */
	private String execOsCommand(final String... aCmdLineArgs) {

		CXOSCommand wCommand = new CXOSCommand(pLogger, aCmdLineArgs);

		wCommand.run(5000);

		return wCommand.getRepport();
	}

	/**
	 * @return
	 */
	private String getInstance() {
		return "default";
	}

	/**
	 * @return
	 */
	private String getSudoPass() {
		return "Olivier38";
	}

}