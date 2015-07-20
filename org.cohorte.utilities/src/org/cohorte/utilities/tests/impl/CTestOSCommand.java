package org.cohorte.utilities.tests.impl;

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

		int wExitCode = 0;
		CTestOSCommand wTest = null;
		try {
			wTest = new CTestOSCommand();
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
	 * /bin/bash -c "echo "myPassword" | sudo -S -k -p "" apacheds status default"
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
	 * @param aPath
	 * @param aFilter
	 * @return
	 */
	private String[] buildCommandLsLa(final String aPath, final String aFilter) {

		ArrayList<String> wCmdLineArgs = new ArrayList<String>();

		wCmdLineArgs.add("/bin/bash");
		wCmdLineArgs.add("-c");
		wCmdLineArgs.add(String.format("ls -la \"%s\" | grep \"%s\"", aPath,
				aFilter));

		return wCmdLineArgs.toArray(new String[wCmdLineArgs.size()]);
	}

	/**
	 * Commande
	 * 
	 * <pre>
	 * macbookpro112:org.cohorte.utilities ogattaz$ netstat -an | grep "*.22"
	 * tcp4       0      0  *.22                   *.*                    LISTEN     
	 * tcp6       0      0  *.22                   *.*                    LISTEN
	 * </pre>
	 * 
	 * @return
	 */
	private String[] buildCommandNetstat(final String aGrepFilter) {

		ArrayList<String> wCmdLineArgs = new ArrayList<String>();

		wCmdLineArgs.add("/bin/bash");
		wCmdLineArgs.add("-c");
		wCmdLineArgs.add(String
				.format("netstat -an | grep \"%s\"", aGrepFilter));

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

		testCharacters();

		testLsLa();

		testApacheDsStatus();

		testNetstat();

		testTimeOut();

		pLogger.logInfo(this, "doTest", "END");
	}

	/**
	 * @param aTimeOut
	 * @param aCmdLineArgs
	 * @return
	 */
	private String execOsCommand(final long aTimeOut,
			final String... aCmdLineArgs) {

		CXOSCommand wCommand = new CXOSCommand(pLogger, aCmdLineArgs);
		wCommand.run(aTimeOut);
		return wCommand.getRepport();
	}

	private String execOsCommand(final String... aCmdLineArgs) {
		return execOsCommand(5000, aCmdLineArgs);
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
		return "...";
	}

	/**
	 * <pre>
	 * macbookpro112:~ ogattaz$ /bin/bash -c 'echo "Olivier38" | sudo -S -k -p "" apacheds status default'
	 * ApacheDS - default is running (6833).
	 * </pre>
	 */
	private void testApacheDsStatus() {
		pLogger.logInfo(this, "doTest", "Exec:\n%s",
				execOsCommand(buildApacheDsCommand("status")));
	}

	/**
	 * <pre>
	 * macbookpro112:~ ogattaz$ echo "abcdefghijklmnopqrstuvwxyz éèêë äâà üûù öôò €£$  @&§*% +-/\\|(){}[]"
	 * abcdefghijklmnopqrstuvwxyz éèêë äâà üûù öôò €£$  @&§*% +-/\|(){}[]
	 * </pre>
	 */
	private void testCharacters() {

		pLogger.logInfo(
				this,
				"doTest",
				"Exec:\n%s",
				execOsCommand("echo",
						"abcdefghijklmnopqrstuvwxyz éèêë äâà üûù öôò €£$  @&§*% +-/\\|(){}[]"));
	}

	/**
	 * 
	 * <pre>
	 * macbookpro112:~ ogattaz$ /bin/bash -c 'ls -la / | grep "admin" '
	 * ----------     1 root     admin             0 12 sep  2013 .file
	 * drwxrwxr-x+  141 root     admin          4794 21 mar 00:48 Applications
	 * drwxr-xr-x     7 root     admin           238 23 nov 16:22 Users
	 * drwxrwxrwt@    3 root     admin           102 21 mar 15:52 Volumes
	 * drwxrwxr-t@    2 root     admin            68 12 sep  2013 cores
	 * -rw-r--r--+    1 ogattaz  admin             0 25 avr  2011 fr
	 * drwxr-xr-x+    7 ogattaz  admin           238  5 mar 23:51 opt
	 * macbookpro112:~ ogattaz$
	 * </pre>
	 */
	private void testLsLa() {
		pLogger.logInfo(this, "doTest", "Exec:\n%s",
				execOsCommand(buildCommandLsLa("/", "admin")));
	}

	/**
	 * <pre>
	 * macbookpro112:org.cohorte.utilities ogattaz$ netstat -an | grep "*.22"
	 * tcp4       0      0  *.22                   *.*                    LISTEN     
	 * tcp6       0      0  *.22                   *.*                    LISTEN
	 * </pre>
	 */
	private void testNetstat() {
		pLogger.logInfo(this, "doTest", "Exec:\n%s",
				execOsCommand(buildCommandNetstat("*.22")));
	}

	/**
	 * timeout à 10 secondes
	 * 
	 * <pre>
	 * macbookpro112:~ ogattaz$ find / -name "signature" -print
	 * find: /.DocumentRevisions-V100: Permission denied
	 * find: /.DocumentRevisions-V100 (depuis l’ancien Mac): Permission denied
	 * find: /.fseventsd: Permission denied
	 * find: /.Spotlight-V100: Permission denied
	 * find: /.Trashes: Permission denied
	 * find: /dev/fd/3: Not a directory
	 * ...
	 * </pre>
	 */
	private void testTimeOut() {
		pLogger.logInfo(
				this,
				"doTest",
				"Exec:\n%s",
				execOsCommand(7000, "find", "/", "-name", "signature", "-print"));
	}

}