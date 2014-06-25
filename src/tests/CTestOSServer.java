package tests;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.psem2m.utilities.system.CXOSServer;

/**
 * @author ogattaz
 * 
 */
public class CTestOSServer extends CAbstractTest{


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

	CXOSServer pCXOSServer = null;



	/**
	 * 
	 */
	private CTestOSServer() {
		super();
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
		return wEnv;
	}

	/**
	 * 
	 * The sudo prompt is sent in stdErr ! => the value "" for the prompt //
	 * argument -p "" remove it
	 * 
	 * The command is "bin/startup.sh;" according the user dir is
	 * "/Applications/eXist-db.app/Contents/Resources/eXist-db" (on mac os // x)
	 * 
	 * Print user directory in stdIn of the sudo command
	 * 
	 * @return
	 */
	private String[] buildExitdbCommand() {

		ArrayList<String> wCmdLineArgs = new ArrayList<String>();

		wCmdLineArgs.add("/bin/bash");
		wCmdLineArgs.add("-c");

		StringBuilder wBashCommands = new StringBuilder();

		// print user directory in stdIn of the sudo command
		wBashCommands.append("echo pwd=[$PWD];");

		// the sudo prompt is sent in stdErr ! => the value "" for the prompt
		// argument -p "" remove it
		wBashCommands.append(String.format(
				"echo \"%s\" | sudo -S -k -p \"\" bin/startup.sh;",
				getSudoPass()));

		wCmdLineArgs.add(wBashCommands.toString());
		return wCmdLineArgs.toArray(new String[wCmdLineArgs.size()]);
	}

	/**
	 * @param aPid
	 * @param aSignal
	 * @return
	 */
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
		int wPid = pCXOSServer.getPid();
		pLogger.logInfo(this, "doTest", "begin aCmdeLine=[%s] Pid=[%s] ",aCmdeLine, wPid);

		boolean wStopped = false;

		// man kill on MacOsX
		// The following pids have special meanings:
		// -1 If superuser, broadcast the signal to all processes; otherwise
		// broadcast to all processes belonging to the user.

		if (wPid != -1) {
			wStopped = pCXOSServer.stop(10000,
					buildKillCommand(wPid, "SIGTERM"));
		}

		pLogger.logInfo(this, "doCommandClose", "Started=[%b]", wStopped);

		pLogger.logInfo(this, "doCommandClose", "ServerReport:\n%s",
				pCXOSServer.getRepport());
	}

	/* (non-Javadoc)
	 * @see tests.CAbstractTest#doUserCommand(java.lang.String)
	 */
	@Override
	protected  void doCommandUser(final String aCmdeLine) throws Exception{

	}

	/**
	 * @throws Exception
	 * 
	 */
	@Override
	protected void doTest() throws Exception {
		pLogger.logInfo(this, "doTest", "BEGIN");

		pCXOSServer = new CXOSServer(pLogger, buildExitdbCommand());

		boolean wStarted = pCXOSServer.startAndWaitInStdOut(buildUserDirFile(),
				buildExistdbEnv(), 15000, "Server has started on ports");

		pLogger.logInfo(this, "doTest", "Started=[%b]", wStarted);

		pLogger.logInfo(this, "doTest", "ServerReport:\n%s",
				pCXOSServer.getRepport());

		waitForUserCommand();

		pLogger.logInfo(this, "doTest", "END");
	}

	/**
	 * @return
	 */
	private String getSudoPass() {
		return "Olivier38";
	}

}
