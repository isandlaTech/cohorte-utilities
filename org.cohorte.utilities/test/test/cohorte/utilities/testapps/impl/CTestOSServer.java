package test.cohorte.utilities.testapps.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.cohorte.utilities.tests.CAppConsoleBase;
import org.psem2m.utilities.CXBytesUtils;
import org.psem2m.utilities.CXJvmUtils;
import org.psem2m.utilities.CXStringUtils;
import org.psem2m.utilities.system.CXOSCommand;
import org.psem2m.utilities.system.CXOSServer;
import org.psem2m.utilities.system.CXProcess;

/**
 * test "CXOSServer" class
 * 
 * @author ogattaz
 * 
 */
public class CTestOSServer extends CAppConsoleBase {

	public final static String CMD_SERVER_GETPID = "pid";
	public final static String CMD_SERVER_KILL = "kill";
	public final static String CMD_SERVER_SEND = "send";
	public final static String CMD_SERVER_START = "start";
	public final static String CMD_SERVER_STATE = "state";
	public final static String CMD_SERVER_STOP = "stop";

	public static final String TEST_SERVER_RETURN_TEXT_START = "*** TEST SERVER STARTED";
	public static final String TEST_SERVER_RETURN_TEXT_STATE = "*** TEST SERVER STATE";
	public static final String TEST_SERVER_RETURN_TEXT_STOP = "*** TEST SERVER STOPPED";

	private static final int TEST_SERVER_WAIT_TIMEOUT = 5000;

	private static final boolean WITH_SUDO = true;

	/**
	 * @param args
	 */
	public static void main(final String[] args) {

		int wExitCode = 0;
		CTestOSServer wTest = null;
		try {
			wTest = new CTestOSServer(args);
			wTest.runApp();
		} catch (Exception e) {
			e.printStackTrace();
			wExitCode = 1;
		} finally {
			wTest.destroy();
		}
		System.exit(wExitCode);
	}

	CXOSServer pCXOSServer = null;

	int pServerPid = -1;

	/**
	 * @param args
	 */
	public CTestOSServer(final String[] args) {
		super(args);

		addOneCommand(CMD_SERVER_START, new String[] { "Start the server" });
		addOneCommand(CMD_SERVER_STOP, new String[] { "Stop the server" });
		addOneCommand(CMD_SERVER_STATE,
				new String[] { "Get the state of the server" });
		addOneCommand(CMD_SERVER_SEND,
				new String[] { "Send a 'command line' to the server" });
		addOneCommand(CMD_SERVER_STOP, new String[] { "Stop the server" });
		addOneCommand(CMD_SERVER_KILL, "k", new String[] { "Stop the server" });
		addOneCommand(CMD_SERVER_GETPID, "p",
				new String[] { "get the pid of the server" });

		pLogger.setLevel(Level.ALL);

		pLogger.logInfo(this, "<init>", "instanciated");
	}

	/**
	 * Bash option :
	 * 
	 * -c string => If the -c option is present, then commands are read from
	 * string. If there are arguments after the string, they are assigned to the
	 * positional parameters, starting with $0.
	 * 
	 * <pre>
	 * return buildCommandBash(!WITH_SUDO, "/bin/bash","i");
	 * 
	 * ==>/bin/bash -c /bin/bash -i;
	 * </pre>
	 * 
	 * <pre>
	 * return buildCommandBash(WITH_SUDO, "./bin/startup.sh");
	 * 
	 * ==>/bin/bash -c echo pwd=[$PWD];echo "myPass" | sudo -S -k -p "" "./bin/startup.sh";
	 * </pre>
	 * 
	 * 
	 * <pre>
	 * return buildCommandBash(WITH_SUDO, "./bin/shutdown.sh", "-u", "admin","-p", "root");
	 * 
	 * ==> /bin/bash -c echo pwd=[$PWD];echo "myPass" | sudo -S -k -p "" "./bin/shutdown.sh" -u admin - p root;
	 * </pre>
	 * 
	 * <pre>
	 * return buildCommandBash(WITH_SUDO, "lsof", "-n", String.format("-i4TCP:%d", aPort));
	 * 
	 * ==>/bin/bash -c echo pwd=[$PWD];echo "myPass" | sudo -S -k -p "" lsof -n -i4TCP:8080
	 * </pre>
	 * 
	 * The sudo prompt is sent in stdErr ! => the value "" for the prompt
	 * argument -p "" remove it
	 * 
	 * The command is "bin/startup.sh;" according the user dir is
	 * "/Applications/eXist-db.app/Contents/Resources/eXist-db" (on mac os // x)
	 * 
	 * Print user directory in stdIn of the sudo command
	 * 
	 * @param aCommand
	 * @param aCommandArgs
	 * @return
	 */
	private String[] buildCommandBash(final boolean aWithSudo,
			final String aCommand, final String... aCommandArgs) {

		ArrayList<String> wCmdLineArgs = new ArrayList<String>();

		if (aWithSudo) {

			wCmdLineArgs.add("/bin/bash");
			wCmdLineArgs.add("-c");

			StringBuilder wBashCommands = new StringBuilder();

			// print user directory in stdIn of the sudo command
			wBashCommands.append("echo pwd=[$PWD];");

			// the sudo prompt is sent in stdErr ! => the value "" for the
			// prompt
			// argument -p "" remove it
			wBashCommands.append(String.format(
					"echo \"%s\" | sudo -S -k -p \"\" %s", getSudoPass(),
					aCommand));
			if (aCommandArgs != null && aCommandArgs.length > 0) {
				for (String wArg : aCommandArgs) {
					wBashCommands.append(String.format(" %s", wArg));
				}
			}
			wBashCommands.append(';');
			wCmdLineArgs.add(wBashCommands.toString());
		}
		// whithout sudo
		else {
			wCmdLineArgs.add(String.format("%s", aCommand));

			if (aCommandArgs != null && aCommandArgs.length > 0) {
				for (String wArg : aCommandArgs) {
					wCmdLineArgs.add(String.format(" \"%s\"", wArg));
				}
			}
		}

		return wCmdLineArgs.toArray(new String[wCmdLineArgs.size()]);
	}

	/**
	 * The following pids have special meanings: -1 If superuser, broadcast the
	 * signal to all processes; otherwise broadcast to all processes belonging
	 * to the user.
	 * 
	 * @param aPid
	 * 
	 * @param aSignal
	 * @return
	 * @throws Exception
	 */
	private String[] buildCommandKill(final int aPid, final String aSignal)
			throws Exception {

		if (aPid < 0) {
			throw new Exception(
					"Unable to build kill command, the pid is less than 0");
		}

		return buildCommandBash(WITH_SUDO, "kill",
				String.format("-%s", aSignal), String.valueOf(aPid));
	}

	/**
	 * @param aPort
	 * @return
	 */
	private String[] buildCommandLsof(final int aPort) {

		return buildCommandBash(WITH_SUDO, "lsof", "-n",
				String.format("-i4TCP:%d", aPort));
	}

	/**
	 * @return
	 */
	private String[] buildCommandStartTestServer() {
		return buildCommandBash(!WITH_SUDO, "./testcases/bin/startup.sh");
		// return buildCommandBash(!WITH_SUDO, null);

	}

	/**
	 * @return the command used to stop the "test server". This command is
	 *         sentto the server using its stdin
	 */
	private String buildCommandStopTestServer() {
		return "close\n";
	}

	/**
	 * 
	 * JAVA_HOME is a system environment variable which represent JDK
	 * installation directory. When you install JDK in your machine (windows,
	 * Linux or unix) it creates a home directory and puts all its binary (bin),
	 * library(lib) and other tools. In order to compile java program "javac"
	 * tool should be in your PATH and in order to get that in PATH we use
	 * JAVA_HOME environment variable. Many tools like ANT and web servers like
	 * tomcat use JAVA_HOME to find java binaries. In this article we will see
	 * how to set JAVA_HOME environment variable in different operating system
	 * including Windows (windows 7, vista, xp) and Linux (Unix).
	 * 
	 * <pre>
	 * pb-d-128-141-252-110:bin ogattaz$ pwd
	 * /Library/Java/JavaVirtualMachines/jdk1.7.0_45.jdk/Contents/Home/jre/bin
	 * 
	 * pb-d-128-141-252-110:bin ogattaz$ ./java -version
	 * java version "1.7.0_45"
	 * Java(TM) SE Runtime Environment (build 1.7.0_45-b18)
	 * Java HotSpot(TM) 64-Bit Server VM (build 24.45-b08, mixed mode)
	 * 
	 * pb-d-128-141-252-110:jre ogattaz$ ll
	 * total 624
	 * drwxrwxr-x+ 10 root  wheel     340  8 oct  2013 .
	 * drwxrwxr-x+ 15 root  wheel     510  8 oct  2013 ..
	 * -rw-rw-r--+  1 root  wheel    3339  8 oct  2013 COPYRIGHT
	 * -rw-rw-r--+  1 root  wheel      40  8 oct  2013 LICENSE
	 * -rw-rw-r--+  1 root  wheel      46  8 oct  2013 README
	 * -rw-rw-r--+  1 root  wheel  123324  8 oct  2013 THIRDPARTYLICENSEREADME-JAVAFX.txt
	 * -rw-rw-r--+  1 root  wheel  173559  8 oct  2013 THIRDPARTYLICENSEREADME.txt
	 * -rw-rw-r--+  1 root  wheel     955  8 oct  2013 Welcome.html
	 * drwxrwxr-x+ 12 root  wheel     408  8 oct  2013 bin
	 * drwxrwxr-x+ 97 root  wheel    3298  8 oct  2013 lib
	 * </pre>
	 * 
	 * System.getProperty:
	 * 
	 * <pre>
	 * java.home=[/Library/Java/JavaVirtualMachines/jdk1.7.0_45.jdk/Contents/Home/jre]
	 * </pre>
	 * 
	 * @return an instance of dictionnary (Map<String, String>)
	 * 
	 * 
	 * @see http 
	 *      ://javarevisited.blogspot.ch/2012/02/how-to-set-javahome-environment
	 *      -in.html
	 * 
	 */
	private Map<String, String> buildEnvDictionnary() {
		Map<String, String> wEnv = new HashMap<String, String>();
		wEnv.put("JAVA_HOME", System.getProperty(CXJvmUtils.SYSPROP_JAVA_HOME));
		return wEnv;
	}

	/**
	 * <pre>
	 * user.dir=[/Users/ogattaz/workspaces/cohorte-OpenSource-git/cohorte-utilities/org.cohorte.utilities]
	 * </pre>
	 * 
	 * @return
	 */
	private File buildUserDirFile() {
		return new File(System.getProperty(CXJvmUtils.SYSPROP_USER_DIR));
	}

	/**
	 * 
	 */
	@Override
	protected void destroy() {
		pLogger.logInfo(this, "destroy", "close the logger");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cohorte.utilities.tests.CAppConsoleBase#doCommandClose()
	 */
	@Override
	protected void doCommandClose() throws Exception {
		pLogger.logInfo(this, "doCommandClose", "begin");

		if (pCXOSServer != null) {
			try {
				doCommandStop(getCmdeLine());
			} catch (Exception e) {
				pLogger.logInfo(this, "doCommandClose", "ERROR: %s", e);

			}
		}

		pLogger.logInfo(this, "doCommandClose", "end");
	}

	/**
	 * <pre>
	 * 	/bin/bash -c echo pwd=[$PWD];echo "Olivier38" | sudo -S -k -p "" lsof -n -i4TCP:8080
	 * </pre>
	 * 
	 * <pre>
	 * pwd=[/Users/ogattaz/workspaces/PSEM2M_SDK_git_COHORTE2/trunk/java/isolates/org.psem2m.isolates.utilities]
	 * COMMAND   PID USER   FD   TYPE             DEVICE SIZE/OFF NODE NAME
	 * java    68090 root  214u  IPv6 0xcaaf969d4a137d59      0t0  TCP *:http-alt (LISTEN)
	 * </pre>
	 * 
	 * @see http 
	 *      ://stackoverflow.com/questions/4421633/who-is-listening-on-a-given
	 *      -tcp-port-on-mac-os-x
	 * 
	 * @param aCmdeLine
	 * @throws Exception
	 */
	private void doCommandGetPid(final String aCmdeLine) throws Exception {

		pLogger.logInfo(this, "doCommandGetPid", "begin Args=%s %s",
				getCommandArgs(), dumpProcessStates());

		String[] wCommandLsof = buildCommandLsof(8080);

		pLogger.logInfo(this, "doCommandGetPid", "CommandLsof=[%s]",
				CXStringUtils.stringTableToString(wCommandLsof));

		CXOSCommand wCommand = new CXOSCommand(pLogger, wCommandLsof);
		boolean wIsOk = wCommand.run(5000);

		if (wIsOk) {
			pLogger.logInfo(this, "doCommandGetPid", "Repport:\n%s",
					wCommand.getRepport());

			String[] wStdOutLines = wCommand.getStdOutBuffer().toString()
					.split("\\n");

			for (String wLine : wStdOutLines) {
				if (wLine != null && wLine.contains("(LISTEN)")) {

					String[] wLsofLineArgs = CXStringUtils
							.strToArguments(wLine);
					pLogger.logInfo(this, "doCommandGetPid",
							"LsofLineArgs=[%s]",
							CXStringUtils.stringTableToString(wLsofLineArgs));

					pServerPid = Integer.parseInt(wLsofLineArgs[1]);

					pLogger.logInfo(this, "doCommandGetPid", "ServerPid=[%s]",
							pServerPid);

					break;
				}
			}

		}

	}

	/**
	 * @param aCmdeLine
	 * @throws Exception
	 */
	private void doCommandKill(final String aCmdeLine) throws Exception {

		pLogger.logInfo(this, "doCommandKill", "begin Args=%s %s",
				getCommandArgs(), dumpProcessStates());

		try {
			String[] wCommandKill = buildCommandKill(pServerPid, "SIGTERM");

			pLogger.logInfo(this, "doCommandKill", "wCommandKill=[%s]",
					CXStringUtils.stringTableToString(wCommandKill));

			CXOSCommand wCommand = new CXOSCommand(pLogger, wCommandKill);
			boolean wIsOk = wCommand.run(5000);
			pLogger.logInfo(this, "doCommandKill", "IsOk=[%s]", wIsOk);

			pLogger.logInfo(this, "doCommandKill", "Repport:\n%s",
					wCommand.getRepport());

		} catch (Exception e) {
			pLogger.logSevere(this, "doCommandKill", "ERROR: %s",
					e.getMessage());
		}
	}

	/**
	 * @param aCmdeLine
	 * @throws Exception
	 */
	private void doCommandSend(final String aCmdeLine,
			final String[] aCommandArgs) throws Exception {

		pLogger.logInfo(this, "doCommandSend", "begin Args=%s %s",
				getCommandArgs(), dumpProcessStates());

		String wServerCommand = CXStringUtils.stringTableToString(aCommandArgs,
				" ", 1);

		if (wServerCommand.charAt(wServerCommand.length() - 1) != '\n') {
			wServerCommand += '\n';
		}

		pLogger.logInfo(this, "doCommandSend", "begin ServerCommand=[%s]",
				wServerCommand);

		if (pCXOSServer != null) {

			boolean wSent = pCXOSServer.write(wServerCommand,
					CXBytesUtils.ENCODING_UTF_8);

			pLogger.logInfo(this, "doCommandSend",
					"Sent=[%b] ServerReport:\n%s", wSent,
					pCXOSServer.getRepport());

		} else {
			pLogger.logSevere(this, "doCommandSend",
					"No process server available !");
		}
		pLogger.logInfo(this, "doCommandSend", "end");
	}

	/**
	 * @param aCmdeLine
	 * @throws Exception
	 */
	private void doCommandStart(final String aCmdeLine) throws Exception {

		pLogger.logInfo(this, "doCommandStart", "begin Args=[%s] %s",
				getCommandArgs(), dumpProcessStates());

		if (pCXOSServer != null) {
			pLogger.logInfo(this, "doCommandStart",
					"A process server is already launched !");
		} else {
			pCXOSServer = new CXOSServer(pLogger, buildCommandStartTestServer());

			boolean wStarted = pCXOSServer.startAndWaitInStdOut(
					buildUserDirFile(), buildEnvDictionnary(),
					TEST_SERVER_WAIT_TIMEOUT, TEST_SERVER_RETURN_TEXT_START);

			pLogger.logInfo(this, "doCommandStart",
					"Started=[%b] ServerReport:\n%s", wStarted,
					pCXOSServer.getRepport());
		}

		pLogger.logInfo(this, "doCommandStart", "end");
	}

	/**
	 * @param aCmdeLine
	 * @throws Exception
	 */
	private void doCommandState(final String aCmdeLine) throws Exception {

		pLogger.logInfo(this, "doCommandState", "begin Args=%s %s",
				getCommandArgs(), dumpProcessStates());

		if (pCXOSServer != null) {

			boolean wStateGet = pCXOSServer.writeAndWaitInStdOut("state\n",
					CXBytesUtils.ENCODING_UTF_8, TEST_SERVER_WAIT_TIMEOUT,
					TEST_SERVER_RETURN_TEXT_STATE);

			pLogger.logInfo(this, "doCommandStop",
					"State=[%b] ServerReport:\n%s", wStateGet,
					pCXOSServer.getRepport());

		} else {
			pLogger.logSevere(this, "doCommandStop",
					"No process server available !");
		}
		pLogger.logInfo(this, "doCommandStop", "end");
	}

	/**
	 * @param aCmdeLine
	 * @throws Exception
	 */
	private void doCommandStop(final String aCmdeLine) throws Exception {

		pLogger.logInfo(this, "doCommandStop", "begin Args=%s %s",
				getCommandArgs(), dumpProcessStates());

		if (pCXOSServer != null) {

			boolean wStopped = pCXOSServer.writeAndWaitInStdOut(
					buildCommandStopTestServer(), CXBytesUtils.ENCODING_UTF_8,
					TEST_SERVER_WAIT_TIMEOUT, TEST_SERVER_RETURN_TEXT_STOP);

			pLogger.logInfo(this, "doCommandStop",
					"Stopped=[%b] ServerReport:\n%s", wStopped,
					pCXOSServer.getRepport());

			pCXOSServer = null;

		} else {
			pLogger.logSevere(this, "doCommandStop",
					"No process server available !");
		}
		pLogger.logInfo(this, "doCommandStop", "end");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tests.CAbstractTest#doUserCommand(java.lang.String)
	 */
	@Override
	protected void doCommandUser(final String aCmdeLine) throws Exception {
		pLogger.logInfo(this, "doCommandUser", "BEGIN");

		if (isCommandX(CMD_SERVER_START)) {
			doCommandStart(aCmdeLine);
		} else
		//
		if (isCommandX(CMD_SERVER_STOP)) {
			doCommandStop(aCmdeLine);
		} else
		//
		if (isCommandX(CMD_SERVER_STATE)) {
			doCommandState(aCmdeLine);
		} else
		//
		if (isCommandX(CMD_SERVER_SEND)) {
			doCommandSend(aCmdeLine, getCommandArgs());
		} else
		//
		if (isCommandX(CMD_SERVER_KILL)) {
			doCommandKill(aCmdeLine);
		} else
		//
		if (isCommandX(CMD_SERVER_GETPID)) {
			doCommandGetPid(aCmdeLine);
		}

		pLogger.logInfo(this, "doCommandUser", "END");
	}

	private String dumpProcessStates() {
		return String
				.format("OsServerPid=[%s] CurrentProcessPid=[%s] CurrentProcessName=[%s] ",
						(pCXOSServer != null) ? pCXOSServer.getPid() : -1,
						CXProcess.getCurrentProcessPid(),
						CXProcess.getCurrentProcessName());
	}

	/**
	 * @return
	 */
	private String getSudoPass() {
		return "myPass";
	}

	/**
	 * @throws Exception
	 * 
	 */
	@Override
	protected void runApp() throws Exception {
		pLogger.logInfo(this, "doTest", "BEGIN");

		waitForCommand();

		pLogger.logInfo(this, "doTest", "END");
	}

}
