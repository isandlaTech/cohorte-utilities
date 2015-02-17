package org.cohorte.utilities.tests;

import java.io.File;
import java.io.IOException;
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
import org.psem2m.utilities.CXThreadUtils;
import org.psem2m.utilities.CXTimer;
import org.psem2m.utilities.files.CXFile;
import org.psem2m.utilities.files.CXFileDir;
import org.psem2m.utilities.files.CXFileFilter;
import org.psem2m.utilities.files.CXFileText;

/**
 * @author ogattaz
 * 
 */
public abstract class CAppConsoleBase extends CAppObjectBase {

	public final static String APPLICATION_PARAM_AUTO = "auto";

	public final static String CMD_CLOSE = "close";
	public final static String CMD_HELP = "help";
	public final static String CMD_QUIT = "quit";
	public final static String CMD_TEST = "test";
	public final static String CMD_INFOS = "infos";
	public final static String CMD_REDO = "redo";
	public final static String CMD_SCRIPT = "script";
	public final static String CMD_SLEEP = "sleep";

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

	// Default Options. Overrided by the extend class if needed
	protected CAppOptionsBase pAppOptions = new CAppOptionsBase(getClass()
			.getSimpleName());

	protected final String[] pAppArgs;

	// la commande courante
	private String[] pCommandArgs = new String[0];

	// les définitions des commandes acceptables
	private final Map<String, CCommand> pCommands = new HashMap<String, CCommand>();

	private String pCmdeLine;
	private String pCmdeLast;

	public static final String INFO_KIND_ALL = "*";
	public static final String INFO_KIND_ENV = "env";
	public static final String INFO_KIND_JVM = "jvm";
	public static final String INFO_KIND_DIRS = "dirs";
	public static final String INFO_KIND_ARGS = "args";

	public static final String ACTION_LIST = "list";
	public static final String ACTION_RUN = "run";
	public static final String ACTION_DUMP = "dump";

	private CXFileDir pScriptDir = CXFileDir.getUserDir();

	/**
	 * ?
	 * 
	 * @throws
	 * 
	 */
	public CAppConsoleBase(final String[] args) {
		super();
		pAppArgs = args;

		addOneCommand(CMD_CLOSE, "c", new String[] { "Close the tester" });

		addOneCommand(CMD_QUIT, "q", new String[] { "Close the tester" });

		addOneCommand(CMD_REDO, "r",
				new String[] { "Redo the last command line" });

		addOneCommand(CMD_HELP, "?", new String[] { "Show help",
				"--usage for the options" });

		addOneCommand(CMD_INFOS, "i", new String[] { "Show infos",
				"--kind : '*','env','jvm','dirs','args'" });

		addOneCommand(CMD_SCRIPT, "s", new String[] { "manage scripts",
				"--action : 'list','dump','run'", "--name : idx | name" });

		addOneCommand(CMD_SLEEP, "S", new String[] { "Sleep a duration",
				"--value n milli-seconds" });
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
	protected void doCommandInfos() throws OptionException {

		String wKind = getAppOptions().getKindValue(INFO_KIND_ALL);

		// env
		if (optionMatch(wKind, INFO_KIND_ENV)
				|| INFO_KIND_ALL.equalsIgnoreCase(wKind)) {
			pLogger.logInfo(this, "doCommandInfo", CXOSUtils.getEnvContext());
		} else
		// jvm
		if (optionMatch(wKind, INFO_KIND_JVM)
				|| INFO_KIND_ALL.equalsIgnoreCase(wKind)) {
			pLogger.logInfo(this, "doCommandInfo", CXJvmUtils.getJavaContext());
		} else
		// args
		if (optionMatch(wKind, INFO_KIND_ARGS)
				|| INFO_KIND_ALL.equalsIgnoreCase(wKind)) {
			pLogger.logInfo(this, "doCommandInfo", "Application arguments; %s",
					CXStringUtils.stringTableToString(pAppArgs));
		} else
		// dirs
		if (optionMatch(wKind, INFO_KIND_DIRS)
				|| INFO_KIND_ALL.equalsIgnoreCase(wKind)) {
			// Nothing => must be implmeented in the app
		}
	}

	/**
	 * @param aCmdeLine
	 * @return
	 * @throws Exception
	 */
	private boolean doCommandLine(final String aCmdeLine) throws Exception {
		boolean wWantClose = false;
		pCommandArgs = splitCommandLine(aCmdeLine);
		// parse the argurmennts after the first
		pAppOptions.parse((String[]) CXArray.removeOneObject(pCommandArgs, 0));
		// sho the options's values
		if (pAppOptions.hasValue()) {
			pLogger.logInfo(this, "waitForCommand", "AppOptions: %s",
					pAppOptions.toString());
		}

		if (pAppOptions.hasUsageOn()) {
			pLogger.logInfo(this, "waitForCommand", "Usage:\n%s",
					pAppOptions.getUsage());
		} else {
			try {
				wWantClose = doCommandX();
				// memo last executed command
				pCmdeLast = aCmdeLine;
			} catch (Exception e) {
				pLogger.logSevere(this, "waitForCommand", "ERROR:\n%s", e);
			}
		}
		return wWantClose;
	}

	/**
	 * @throws Exception
	 */
	protected void doCommandRedo() throws Exception {

		if (isLineRunnable(pCmdeLast)) {
			pLogger.logInfo(this, "doCommandRedo", "REDO  BEGIN ===== [%s]",
					pCmdeLast);
			CXTimer wTimer = CXTimer.newStartedTimer();
			doCommandLine(pCmdeLast);
			wTimer.stop();
			pLogger.logInfo(this, "doCommandRedo",
					"REDO END   ===== [%s] ===== Duration=[%s]", pCmdeLast,
					wTimer.getDurationStrMicroSec());
		}
	}

	/**
	 * @throws Exception
	 */
	protected void doCommandScript() throws Exception {
		pLogger.logInfo(this, "doCommandScript", "ScriptDir=[%s]",
				getScriptDir().getAbsolutePath());

		String wOptAction = getAppOptions().getActionValue(ACTION_LIST);

		if (optionMatch(wOptAction, ACTION_LIST)) {
			String wDump = dumpSciptsList();
			pLogger.logInfo(this, "doCommandScript", "List :\n%s", wDump);

		} else if (optionMatch(wOptAction, ACTION_DUMP)) {

			String wOptName = getAppOptions().getNameValue();
			CXFileText wScriptFile = (CXFileText) findScriptFile(wOptName);
			if (wScriptFile == null) {
				pLogger.logSevere(this, "doCommandScript",
						"UNKNWON SCRIPT [%s]", wOptName);
			} else {
				pLogger.logSevere(this, "doCommandScript", "SCRIPT [%s]\n%s",
						wOptName, wScriptFile.readAll());
			}

		} else if (optionMatch(wOptAction, ACTION_RUN)) {

			String wOptName = getAppOptions().getNameValue();
			CXFileText wScriptFile = (CXFileText) findScriptFile(wOptName);

			if (wScriptFile == null) {
				pLogger.logSevere(this, "doCommandScript",
						"UNKNWON SCRIPT [%s]", wOptName);
			} else {

				List<String> wLines = wScriptFile.readLines();

				pLogger.logInfo(this, "doCommandScript",
						"NameOrIdx=[%s] FileName=[%s] size=[%s]", wOptName,
						wScriptFile.getName(), wLines.size());

				for (String wLine : wLines) {
					if (isLineRunnable(wLine)) {
						pLogger.logInfo(this, "doCommandScript",
								"RUN  BEGIN ===== [%s]", wLine);
						CXTimer wTimer = CXTimer.newStartedTimer();
						doCommandLine(wLine);
						wTimer.stop();
						pLogger.logInfo(this, "doCommandScript",
								"RUN  END   ===== [%s] ===== Duration=[%s]",
								wLine, wTimer.getDurationStrMicroSec());
					}
				}
			}
		}
	}

	/**
	 * @throws Exception
	 */
	protected void doCommandSleep() throws Exception {

		String wOptValue = getAppOptions().getStringOptionValue(
				IAppOptions.OPT_VALUE, "1000");

		boolean wSleepComplete = CXThreadUtils.sleep(wOptValue);

		pLogger.logInfo(this, "doCommandScript",
				"Sleep : %s milliseconds (sleep complete=[%s])", wOptValue,
				wSleepComplete);

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
		} else if (isCommandX(CMD_INFOS)) {
			doCommandInfos();
		} else if (isCommandX(CMD_SCRIPT)) {
			doCommandScript();
		} else if (isCommandX(CMD_REDO)) {
			doCommandRedo();
		} else if (isCommandX(CMD_SLEEP)) {
			doCommandSleep();
		} else {
			doCommandUser(getCmdeLine());
		}

		return wWantClose;
	}

	/**
	 * @return
	 * @throws IOException
	 */
	protected String dumpSciptsList() throws IOException {
		StringBuilder wSB = new StringBuilder();
		List<File> wScriptFiles = getScriptFiles();

		int wIdx = 1;
		for (File wScriptFile : wScriptFiles) {
			if (wIdx > 1) {
				wSB.append('\n');
			}
			wSB.append(String.format("script(%1$2d):%2$s", wIdx,
					((CXFile) wScriptFile).getNameWithoutExtension()));
			wIdx++;
		}
		return wSB.toString();
	}

	/**
	 * @param aScriptNameOrIndex
	 * @return
	 * @throws IOException
	 */
	protected CXFile findScriptFile(final String aScriptNameOrIndex)
			throws IOException {

		List<File> wScriptFiles = getScriptFiles();
		int wIdx = -1;
		try {
			wIdx = Integer.parseInt(aScriptNameOrIndex);

		} catch (Exception e) {
		}
		if (wIdx < 1 || wIdx > wScriptFiles.size()) {
			wIdx = -1;
		}
		if (wIdx > 0) {
			return (CXFile) wScriptFiles.get(wIdx - 1);
		}
		for (File wFile : wScriptFiles) {
			CXFile wScriptFile = (CXFile) wFile;
			if (wScriptFile.getNameWithoutExtension().equalsIgnoreCase(
					aScriptNameOrIndex)) {
				return wScriptFile;
			}
		}
		return null;
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

	/**
	 * @return
	 */
	public CXFileDir getScriptDir() {
		return pScriptDir;
	}

	/**
	 * @return
	 * @throws IOException
	 */
	protected List<File> getScriptFiles() throws IOException {
		return pScriptDir.getMySortedFiles(
				CXFileFilter.getFilterExtension(CXFile.EXTENSION_TXT),
				!CXFileDir.WITH_DIR, CXFileDir.WITH_TEXTFILE);
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

		//
		if (!hasCommandArg()) {
			return false;
		}

		String wFirstCommandArg = getFirstCommandArg();

		CCommand wCommand = pCommands.get(aCommandId);
		return wCommand != null
				&& (wFirstCommandArg.toLowerCase().equals(wCommand.getVerb()) || wFirstCommandArg
						.toLowerCase().equals(wCommand.getAlias()));
	}

	/**
	 * <pre>
	 * 
	 * # create the base "BASE_TEST" and load
	 * base --name BASE_TEST --action create --from test-resources/xml/input.xml
	 * 
	 * # dump base info
	 * base --name BASE_TEST --action valid
	 * </pre>
	 * 
	 * @param aLine
	 * @return true if the line is not null and not empty and doesn't start by
	 *         '#' character and doesn't start by the command "redo"
	 */
	private boolean isLineRunnable(final String aLine) {

		return (aLine != null && !aLine.isEmpty() && aLine.charAt(0) != '#' && !aLine
				.startsWith(CMD_REDO));
	}

	protected void logEndConstructor() {
		pLogger.logInfo(this, "<init>", "App [%s] instanciated. %s", this
				.getClass().getSimpleName(), toString());
	}

	/**
	 * @param aOptAction
	 * @param aOption
	 * @return
	 */
	protected boolean optionMatch(final String aOptAction, final String aOption) {
		return aOptAction != null
				&& aOptAction != null
				&& (aOptAction.toLowerCase().contains(aOption.toLowerCase()) || optionMatchFirstChar(
						aOptAction, aOption));
	}

	/**
	 * @param aOptAction
	 * @param aOption
	 * @return
	 */
	protected boolean optionMatchFirstChar(final String aOptAction,
			final String aOption) {
		return aOptAction != null && aOptAction != null
				&& aOptAction.toLowerCase().charAt(0) == aOption.charAt(0);
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
	 * @param aScriptDir
	 */
	protected void setScriptDir(final CXFileDir aScriptDir) {
		pScriptDir = aScriptDir;
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
		return String.format("AppConsole=[%s] ", getClass().getName());
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
			pCmdeLine = wScanConsoleIn.nextLine();

			pLogger.logInfo(this, "waitForCommand",
					"Stdin console command line: [%s]", getCmdeLine());

			wWantClose = doCommandLine(getCmdeLine());

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
