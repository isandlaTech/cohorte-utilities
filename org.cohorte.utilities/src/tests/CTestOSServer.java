package tests;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.cohorte.utilities.tests.CAbstractTest;
import org.psem2m.utilities.CXStringUtils;
import org.psem2m.utilities.system.CXOSCommand;
import org.psem2m.utilities.system.CXOSServer;
import org.psem2m.utilities.system.CXProcess;

/**
 * @author ogattaz
 * 
 */
public class CTestOSServer extends CAbstractTest {

	public final static String CMD_SERVER_GETPID = "pid";
	public final static String CMD_SERVER_KILL = "kill";
	public final static String CMD_SERVER_START = "start";
	public final static String CMD_SERVER_STOP = "stop";

	/**
	 * @param args
	 */
	public static void main(final String[] args) {

		int wExitCode = 0;
		CTestOSServer wTest = null;
		try {
			wTest = new CTestOSServer(args);
			wTest.runTest();
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

		addOneCommand(CMD_SERVER_START, "Start the server");
		addOneCommand(CMD_SERVER_STOP, "Stop the server");
		addOneCommand(CMD_SERVER_KILL, "k", "Stop the server");
		addOneCommand(CMD_SERVER_GETPID, "p", "get the pid of the server");

		pLogger.setLevel(Level.FINE);

		pLogger.logInfo(this, "<init>", "instanciated");
	}

	/**
	 * <pre>
	 * /bin/bash -c echo pwd=[$PWD];echo "Olivier38" | sudo -S -k -p "" "./bin/startup.sh";
	 * </pre>
	 * 
	 * <pre>
	 * /bin/bash -c echo pwd=[$PWD];echo "Olivier38" | sudo -S -k -p "" "./bin/shutdown.sh" -u admin - p root;
	 * </pre>
	 * 
	 * <pre>
	 * /bin/bash -c echo pwd=[$PWD];echo "Olivier38" | sudo -S -k -p "" lsof -n -i4TCP:8080
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
	private String[] buildCommandBash(final String aCommand,
			final String... aCommandArgs) {

		ArrayList<String> wCmdLineArgs = new ArrayList<String>();

		wCmdLineArgs.add("/bin/bash");
		wCmdLineArgs.add("-c");

		StringBuilder wBashCommands = new StringBuilder();

		// print user directory in stdIn of the sudo command
		wBashCommands.append("echo pwd=[$PWD];");

		// the sudo prompt is sent in stdErr ! => the value "" for the prompt
		// argument -p "" remove it
		wBashCommands
				.append(String.format("echo \"%s\" | sudo -S -k -p \"\" %s",
						getSudoPass(), aCommand));

		if (aCommandArgs != null && aCommandArgs.length > 0) {
			for (String wArg : aCommandArgs) {
				wBashCommands.append(String.format(" %s", wArg));
			}
		}
		wBashCommands.append(';');

		wCmdLineArgs.add(wBashCommands.toString());
		return wCmdLineArgs.toArray(new String[wCmdLineArgs.size()]);
	}

	/**
	 * @return
	 */
	private String[] buildCommandExitdbShutdown() {
		return buildCommandBash("./bin/shutdown.sh", "-u", "admin", "-p",
				"root");
	}

	/**
	 * @return
	 */
	private String[] buildCommandExitdbStart() {
		return buildCommandBash("./bin/startup.sh");
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

		return buildCommandBash("kill", String.format("-%s", aSignal),
				String.valueOf(aPid));
	}

	/**
	 * @param aPort
	 * @return
	 */
	private String[] buildCommandLsof(final int aPort) {

		return buildCommandBash("lsof", "-n", String.format("-i4TCP:%d", aPort));
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
	 * @see http 
	 *      ://javarevisited.blogspot.ch/2012/02/how-to-set-javahome-environment
	 *      -in.html
	 * 
	 *      <pre>
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
	 * 
	 * @return
	 */
	private Map<String, String> buildExistdbEnv() {
		Map<String, String> wEnv = new HashMap<String, String>();
		wEnv.put("JAVA_HOME",
				"/Library/Java/JavaVirtualMachines/jdk1.7.0_45.jdk/Contents/Home/jre");
		wEnv.put("EXIST_DATA_DIR",
				"/Users/ogattaz/workspaces/Cristal-eXist-db/webapp/WEB-INF/data");
		wEnv.put("EXIST_PID_DIR",
				"/Users/ogattaz/workspaces/Cristal-eXist-db/webapp/WEB-INF/data");

		return wEnv;
	}

	/**
	 * 
	 * @return
	 */
	private File buildUserDirFile() {
		return new File(
				"/Applications/eXist-db.app/Contents/Resources/eXist-db");
	}

	/**
	 * 
	 */
	@Override
	protected void destroy() {
		pLogger.logInfo(this, "destroy", "close the logger");
	}

	/**
	 * @param aCmdeLine
	 * @throws Exception
	 */
	@Override
	protected void doCommandClose(final String aCmdeLine) throws Exception {
		pLogger.logInfo(this, "doCommandClose", "begin");

		if (pCXOSServer != null) {
			doCommandStop(aCmdeLine);
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
		pLogger.logInfo(this, "doCommandGetPid", "begin aCmdeLine=[%s]",
				aCmdeLine);

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
		pLogger.logInfo(this, "doCommandKill", "begin aCmdeLine=[%s]",
				aCmdeLine);

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
	private void doCommandStart(final String aCmdeLine) throws Exception {

		pLogger.logInfo(this, "doCommandStart", "begin aCmdeLine=[%s]",
				aCmdeLine);

		if (pCXOSServer != null) {
			pLogger.logInfo(this, "doCommandStart",
					"A process server is already launched !");
		} else {

			pCXOSServer = new CXOSServer(pLogger, buildCommandExitdbStart());

			boolean wStarted = pCXOSServer.startAndWaitInStdOut(
					buildUserDirFile(), buildExistdbEnv(), 15000,
					"Server has started on ports");

			pLogger.logInfo(this, "doCommandStart", "Started=[%b]", wStarted);

			pLogger.logInfo(this, "doCommandStart", "ServerReport:\n%s",
					pCXOSServer.getRepport());
		}

		pLogger.logInfo(this, "doCommandStart", "end");
	}

	/**
	 * @param aCmdeLine
	 * @throws Exception
	 */
	private void doCommandStop(final String aCmdeLine) throws Exception {

		pLogger.logInfo(this, "doCommandStop", "begin aCmdeLine=[%s]",
				aCmdeLine);

		pLogger.logInfo(this, "doCommandStop",
				"CurrentProcessPid=[%s] CurrentProcessName=[%s]",
				CXProcess.getCurrentProcessPid(),
				CXProcess.getCurrentProcessName());

		if (pCXOSServer != null) {
			boolean wStopped = pCXOSServer.stop(buildUserDirFile(),
					buildExistdbEnv(), 10000, buildCommandExitdbShutdown());

			pLogger.logInfo(this, "doCommandStop", "Stopped=[%b]", wStopped);

			pLogger.logInfo(this, "doCommandStop", "ServerReport:\n%s",
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

		if (isCommandX(aCmdeLine, CMD_SERVER_START)) {
			doCommandStart(aCmdeLine);
		} else if (isCommandX(aCmdeLine, CMD_SERVER_STOP)) {
			doCommandStop(aCmdeLine);
		} else if (isCommandX(aCmdeLine, CMD_SERVER_KILL)) {
			doCommandKill(aCmdeLine);
		} else if (isCommandX(aCmdeLine, CMD_SERVER_GETPID)) {
			doCommandGetPid(aCmdeLine);
		}

		pLogger.logInfo(this, "doCommandUser", "END");
	}

	/**
	 * @return
	 */
	private String getSudoPass() {
		return "Olivier38";
	}

	/**
	 * @throws Exception
	 * 
	 */
	@Override
	protected void runTest() throws Exception {
		pLogger.logInfo(this, "doTest", "BEGIN");

		waitForUserCommand();

		pLogger.logInfo(this, "doTest", "END");
	}

}
