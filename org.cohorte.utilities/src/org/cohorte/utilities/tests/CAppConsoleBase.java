package org.cohorte.utilities.tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cohorte.utilities.tests.CAppCommandLineParser.OptionException;
import org.psem2m.utilities.CXArray;
import org.psem2m.utilities.CXJvmUtils;
import org.psem2m.utilities.CXOSUtils;
import org.psem2m.utilities.CXSortList;
import org.psem2m.utilities.CXStringUtils;
import org.psem2m.utilities.logging.CActivityLoggerBasicConsole;
import org.psem2m.utilities.logging.IActivityLogger;

/**
 * @author ogattaz
 * 
 */
public abstract class CAppConsoleBase {

	public final static String APPLICATION_PARAM_AUTO = "auto";

	public final static String CMD_CLOSE = "close";
	public final static String CMD_HELP = "help";
	public final static String CMD_QUIT = "quit";
	public final static String CMD_TEST = "test";
	public final static String CMD_INFO = "info";

	/**
	 * Pattern that is capable of dealing with complex command line quoting and
	 * escaping. This can recognize correctly:
	 * <ul>
	 * <li>"double quoted strings"
	 * <li>'single quoted strings'
	 * <li>"escaped \"quotes within\" quoted string"
	 * <li>C:\paths\like\this or "C:\path like\this"
	 * <li>--arguments=like_this or "--args=like this" or '--args=like this' or
	 * --args="like this" or --args='like this'
	 * <li>quoted\ whitespaces\\t (spaces & tabs)
	 * <li>and probably more :)
	 * </ul>
	 * 
	 * @see http 
	 *      ://stackoverflow.com/questions/13495449/how-to-split-a-command-line
	 *      -like-string
	 */
	private static final Pattern sCommandSplitPattern = Pattern
			.compile(
					"[^\\s]*\"(\\\\+\"|[^\"])*?\"|[^\\s]*'(\\\\+'|[^'])*?'|(\\\\\\s|[^\\s])+",
					Pattern.MULTILINE);

	protected CAppOptionsBase pAppOptions = null;

	protected final String[] pArgs;

	private String[] pCommandArgs = new String[0];

	private final Map<String, CCommand> pCommands = new HashMap<String, CCommand>();

	protected final IActivityLogger pLogger = CActivityLoggerBasicConsole
			.getInstance();

	private String pCmdeLine;

	private static final String INFO_ALL = "*";

	private static final String INFO_ENV = "env";

	private static final String INFO_JVM = "jvm";

	/**
	 * 
	 */
	public CAppConsoleBase(final String[] args) {
		super();
		pArgs = args;

		addOneCommand(CMD_CLOSE, "c", new String[] { "Close the tester" });
		addOneCommand(CMD_QUIT, "q", new String[] { "Close the tester" });
		addOneCommand(CMD_HELP, "?", new String[] { "Show help",
				"--usage for the options" });
		addOneCommand(CMD_INFO, "i", new String[] { "Show infos",
				"--kind : '*','env','jvm'" });

	}

	/**
	 * @param aCmdeVerb
	 * @param aCmdeAlias
	 * @param aCmdeHelp
	 */
	protected void addOneCommand(final String aCmdeVerb,
			final String aCmdeAlias, final String[] aCmdeHelp) {
		pCommands
				.put(aCmdeVerb, new CCommand(aCmdeVerb, aCmdeAlias, aCmdeHelp));
	}

	/**
	 * @param aCmdeVerb
	 * @param aCmdeHelp
	 */
	protected void addOneCommand(final String aCmdeVerb,
			final String[] aCmdeHelp) {
		addOneCommand(aCmdeVerb, aCmdeVerb, aCmdeHelp);
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
	protected void doCommandClose() throws Exception {
		pLogger.logInfo(this, "doCommandClose", "begin ...");

	}

	/**
	 * @param aCmdeLine
	 * @throws Exception
	 */
	protected void doCommandHelp() throws Exception {
		CXSortList<CCommand> wSL = new CXSortList<CCommand>();
		wSL.addAll(pCommands.values());

		for (CCommand wCCommand : wSL) {
			System.out.format("\n%s", wCCommand);
		}
	}

	/**
	 * @throws OptionException
	 */
	protected void doCommandInfo() throws OptionException {

		String wKind = getAppOptions().getKindValue();

		if (wKind == null || INFO_ENV.equalsIgnoreCase(wKind)
				|| INFO_ALL.equalsIgnoreCase(wKind)) {
			pLogger.logInfo(this, "doCommandInfo", CXOSUtils.getEnvContext());
		}
		if (wKind == null || INFO_JVM.equalsIgnoreCase(wKind)
				|| INFO_ALL.equalsIgnoreCase(wKind)) {
			pLogger.logInfo(this, "doCommandInfo", CXJvmUtils.getJavaContext());
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
	private boolean doCommandX() throws Exception {

		boolean wWantClose = isCommandClose();

		if (wWantClose) {
			doCommandClose();
		} else if (isCommandX(CMD_HELP)) {
			doCommandHelp();
		} else if (isCommandX(CMD_INFO)) {
			doCommandInfo();
		} else {
			doCommandUser(getCmdeLine());
		}

		return wWantClose;
	}

	/**
	 * @return
	 */
	protected CAppOptionsBase getAppOptions() {
		return pAppOptions;
	}

	/**
	 * @return
	 */
	protected String getCmdeLine() {
		return pCmdeLine;
	}

	/**
	 * @return
	 */
	protected String[] getCommandArgs() {
		return pCommandArgs;
	}

	/**
	 * @param aSeparator
	 * @return
	 */
	public String getCommands(final String aSeparator) {
		StringBuilder wSB = new StringBuilder();
		for (Entry<String, CCommand> wEntry : pCommands.entrySet()) {
			if (wSB.length() > 0) {
				wSB.append(aSeparator);
			}
			wSB.append(wEntry.getKey());
		}
		return wSB.toString();
	}

	/**
	 * @return
	 */
	protected String getFirstCommandArg() {
		return hasCommandArg() ? pCommandArgs[0] : "";
	}

	/**
	 * @return
	 */
	protected int getNbCommandArg() {
		return (pCommandArgs != null) ? pCommandArgs.length : -1;
	}

	protected boolean hasAppOptions() {
		return getAppOptions() != null;
	}

	/**
	 * @return
	 */
	protected boolean hasCommandArg() {
		return getNbCommandArg() > 0;
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
	 * @return
	 */
	protected boolean isCommandClose() {
		return isCommandX(CMD_CLOSE) || isCommandX(CMD_QUIT);
	}

	/**
	 * @param aCommand
	 * @return
	 */
	protected boolean isCommandX(final String aCommandId) {

		if (!hasCommandArg()) {
			return false;
		}

		String wFirstCommandArg = getFirstCommandArg();

		CCommand wCommand = pCommands.get(aCommandId);
		return wCommand != null
				&& (wFirstCommandArg.toLowerCase().equals(wCommand.getVerb()) || wFirstCommandArg
						.toLowerCase().equals(wCommand.getAlias()));
	}

	protected void logEndConstructor() {
		pLogger.logInfo(this, "<init>", "App [%s] instanciated. %s", this
				.getClass().getSimpleName(), toString());
	}

	/**
	 * @throws Exception
	 */
	protected abstract void runApp() throws Exception;

	/**
	 * @param aAppOptions
	 */
	protected void setAppOptions(final CAppOptionsBase aAppOptions) {
		pAppOptions = aAppOptions;
	}

	/**
	 * @param aCommandLine
	 * @return
	 * @see http 
	 *      ://stackoverflow.com/questions/13495449/how-to-split-a-command-line
	 *      -like-string
	 */
	public String[] splitCommandLine(final String aCommandLine) {
		if (aCommandLine == null || aCommandLine.length() == 0) {
			return new String[0];
		}

		Matcher matcher = sCommandSplitPattern.matcher(aCommandLine.trim());
		List<String> matches = new ArrayList<String>();
		while (matcher.find()) {
			matches.add(matcher.group());
		}
		String[] parsedCommand = matches.toArray(new String[matches.size()]);
		return parsedCommand;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("hasOption=[%b] ", hasAppOptions());
	}

	/**
	 * @return
	 * @throws Exception
	 */
	protected boolean waitForCommand() throws Exception {

		pLogger.logInfo(this, "waitForCommand",
				"begin. Stdin console. Wait for a close command to stop the server.");

		pLogger.logInfo(this, "waitForUserCommand", "=>");
		Scanner wScanConsoleIn = new Scanner(System.in);
		boolean wWantClose = false;
		do {

			// Reads a single line from the console
			pCmdeLine = wScanConsoleIn.nextLine().toLowerCase();

			pLogger.logInfo(this, "waitForCommand",
					"Stdin console command line: [%s]", getCmdeLine());

			pCommandArgs = splitCommandLine(getCmdeLine());
			if (hasAppOptions()) {
				// parse the argurmennts after the first
				pAppOptions.parse((String[]) CXArray.removeOneObject(
						pCommandArgs, 0));
				// sho the options's values
				if (pAppOptions.hasValue()) {
					pLogger.logInfo(this, "waitForCommand", "AppOptions: %s",
							pAppOptions.toString());
				}
			}

			if (hasAppOptions() && pAppOptions.hasUsageOn()) {
				pLogger.logInfo(this, "waitForCommand", "Usage:\n%s",
						pAppOptions.getUsage());
			} else {
				wWantClose = doCommandX();
			}

		} while (!wWantClose);

		wScanConsoleIn.close();

		pLogger.logInfo(this, "waitForCommand", "end");
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

	private final String[] pCmdeHelp;

	private final String pCmdeVerb;

	/**
	 * @param aCmdeVerb
	 * @param aCmdeHelp
	 */
	CCommand(final String aCmdeVerb, final String aCmdeAlias,
			final String... aCmdeHelp) {
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
	String[] getFullHelp() {
		return pCmdeHelp;
	}

	/**
	 * @return
	 */
	String getFullHelpStr() {
		return CXStringUtils.stringTableToString(getFullHelp(), "\n");
	}

	/**
	 * @return
	 */
	String getHelp() {
		return (pCmdeHelp != null && pCmdeHelp.length > 0) ? pCmdeHelp[0] : "";
	}

	/**
	 * @return
	 */
	String getVerb() {
		return pCmdeVerb;
	}

	/**
	 * <pre>
	 *                close(    c): Close the tester
	 *                 help(    ?): Show Help
	 *                 jars( jars): manage the jars of the application
	 *                 quit(    q): Close the tester
	 * </pre>
	 * 
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		String wPrefix = String.format("%20s (%5s): ", getVerb(), getAlias());
		String wHelp = getFullHelpStr().replace("\n",
				"\n" + CXStringUtils.strFromChar(' ', wPrefix.length()));
		return String.format("%s%s", wPrefix, wHelp);
	}
}
