package tests;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.psem2m.utilities.CXSortList;
import org.psem2m.utilities.logging.CActivityLoggerBasicConsole;
import org.psem2m.utilities.logging.IActivityLogger;

/**
 * @author ogattaz
 * 
 */
public abstract class CAbstractTest {

	public final static String APPLICATION_PARAM_AUTO = "auto";

	public final static String CMD_CLOSE = "close";
	public final static String CMD_HELP = "help";
	public final static String CMD_QUIT = "quit";
	public final static String CMD_TEST = "test";

	protected final String[] pArgs;

	private final Map<String, CCommand> pCommands = new HashMap<String, CCommand>();

	protected final IActivityLogger pLogger;

	/**
	 * 
	 */
	public CAbstractTest(final String[] args) {
		super();
		pArgs = args;
		pLogger = CActivityLoggerBasicConsole.getInstance();

		addOneCommand(CMD_CLOSE, "c", "Close the tester");
		addOneCommand(CMD_QUIT, "q", "Close the tester");
		addOneCommand(CMD_HELP, "?", "Help");

	}

	/**
	 * @param aCmdeVerb
	 * @param aCmdeHelp
	 */
	protected void addOneCommand(final String aCmdeVerb, final String aCmdeHelp) {
		addOneCommand(aCmdeVerb, aCmdeVerb, aCmdeHelp);
	}

	/**
	 * @param aCmdeVerb
	 * @param aCmdeAlias
	 * @param aCmdeHelp
	 */
	protected void addOneCommand(final String aCmdeVerb,
			final String aCmdeAlias, final String aCmdeHelp) {
		pCommands
				.put(aCmdeVerb, new CCommand(aCmdeVerb, aCmdeAlias, aCmdeHelp));
	}

	/**
	 * 
	 */
	protected void destroy() {
		pLogger.logInfo(this, "destroy", "close the logger");
		pLogger.close();
	}

	/**
	 * @param aCmdeLine
	 * @throws Exception
	 */
	protected abstract void doCommandClose(final String aCmdeLine)
			throws Exception;

	/**
	 * @param aCmdeLine
	 * @throws Exception
	 */
	protected void doCommandHelp(final String aCmdeLine) throws Exception {
		CXSortList<CCommand> wSL = new CXSortList<CCommand>();
		wSL.addAll(pCommands.values());

		for (CCommand wCCommand : wSL) {
			System.out.format("\n%s", wCCommand);
		}
	}

	/**
	 * @param aCmdeLine
	 * @throws Exception
	 */

	protected abstract void doCommandUser(final String aCmdeLine)
			throws Exception;

	/**
	 * @param aCmdeLine
	 * @return true if close or quit
	 */
	private boolean doCommandX(final String aCmdeLine) throws Exception {

		boolean wWantClose = isCommandClose(aCmdeLine);

		if (wWantClose) {
			doCommandClose(aCmdeLine);
		} else if (isCommandX(aCmdeLine, CMD_HELP)) {
			doCommandHelp(aCmdeLine);
		} else {
			doCommandUser(aCmdeLine);
		}

		return wWantClose;
	}

	/**
	 * @param aCmdeLine
	 * @param aPrefix
	 * @return
	 */
	protected boolean isCmdeLineStartsBy(final String aCmdeLine,
			final String aPrefix) {
		return (aCmdeLine != null && aCmdeLine.trim().toLowerCase()
				.startsWith(aPrefix));

	}

	/**
	 * @param aCmdeLine
	 * @return
	 */
	protected boolean isCommandClose(final String aCmdeLine) {
		return isCommandX(aCmdeLine, CMD_CLOSE)
				|| isCommandX(aCmdeLine, CMD_QUIT);
	}

	/**
	 * @param aCmdeLine
	 * @param aCommand
	 * @return
	 */
	protected boolean isCommandX(final String aCmdeLine, final String aCommandId) {

		// The \\s is equivalent to [ \\t\\n\\x0B\\f\\r]
		String[] wCmdeArgs = aCmdeLine.trim().split("\\s+");

		if (wCmdeArgs.length < 1) {
			return false;
		}

		CCommand wCommand = pCommands.get(aCommandId);
		return wCommand != null
				&& (wCmdeArgs[0].toLowerCase().equals(wCommand.getVerb()) || wCmdeArgs[0]
						.toLowerCase().equals(wCommand.getAlias()));
	}

	/**
	 * @throws Exception
	 */
	protected abstract void runTest() throws Exception;

	/**
	 * @return
	 * @throws Exception
	 */
	protected boolean waitForUserCommand() throws Exception {

		pLogger.logInfo(this, "waitForUserCommand",
				"START Stdin console. Wait for a close command to stop the server.");

		pLogger.logInfo(this, "waitForStop", "=>");
		Scanner wScanConsoleIn = new Scanner(System.in);
		String wCmdeLine = null;
		boolean wWantClose = false;
		do {

			// Reads a single line from the console
			wCmdeLine = wScanConsoleIn.nextLine().toLowerCase();

			pLogger.logInfo(this, "waitForStop",
					"Stdin console command line: [%s]", wCmdeLine);

			wWantClose = doCommandX(wCmdeLine);

		} while (!wWantClose);

		wScanConsoleIn.close();

		pLogger.logInfo(this, "waitForUserCommand", "END");
		return true;
	}

	/**
	 * @param aCmdeLine
	 * @throws Exception
	 */
	protected void wrongCommandUser(final String aCmdeLine) throws Exception {
		pLogger.logSevere(this, "wrongCommandUser", "Unknown command [%s]",
				aCmdeLine);
	}

}

/**
 * @author ogattaz
 * 
 */
class CCommand implements Comparable<CCommand> {

	private final String pCmdeAlias;

	private final String pCmdeHelp;

	private final String pCmdeVerb;

	/**
	 * @param aCmdeVerb
	 * @param aCmdeHelp
	 */
	CCommand(final String aCmdeVerb, final String aCmdeAlias,
			final String aCmdeHelp) {
		super();
		pCmdeVerb = aCmdeVerb.toLowerCase();
		pCmdeAlias = aCmdeAlias.toLowerCase();
		pCmdeHelp = aCmdeHelp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final CCommand aCommand) {
		return getVerb().compareTo(aCommand.getVerb());
	}

	/**
	 * @return
	 */
	String getAlias() {
		return pCmdeAlias;
	}

	/**
	 * @return
	 */
	String getHelp() {
		return pCmdeHelp;
	}

	/**
	 * @return
	 */
	String getVerb() {
		return pCmdeVerb;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("%20s(%3s): %s", getVerb(), getAlias(), getHelp());
	}
}
