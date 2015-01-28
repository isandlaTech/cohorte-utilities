package org.cohorte.utilities.tests;

import java.util.logging.Level;

/**
 * This class is an interactive application built on top of the dedicated mini
 * framework "org.cohorte.utilities.tests".
 * 
 * 
 * The output of the "CAppDemonstrator" showing output of the "help" (?) and the
 * "close" (c) commands :
 * 
 * <pre>
 * 2015/01/28; 12:19:06:463; tests.CAppDemonstrator_4433;                    <init>; instanciated
 * 2015/01/28; 12:19:06:474; tests.CAppDemonstrator_4433;                    doTest; BEGIN
 * 2015/01/28; 12:19:06:474; tests.CAppDemonstrator_4433;            waitForCommand; begin. Stdin console. Wait for a close command to stop the server.
 * 2015/01/28; 12:19:06:474; tests.CAppDemonstrator_4433;        waitForUserCommand; =>
 * help
 * 2015/01/28; 12:19:08:852; tests.CAppDemonstrator_4433;            waitForCommand; Stdin console command line: [help]
 * 
 *                close (    c): Close the tester
 *                 help (    ?): Show help
 *                               --usage for the options
 *                 info (    i): Show infos
 *                               --kind : '*','env','jvm'
 *                 quit (    q): Close the tester
 *                 redo (    r): Redo the last command line
 *               script (    s): manage scripts
 *                               --action : 'list','run'
 *                 test ( test): exec a test
 * close
 * 2015/01/28; 12:19:13:843; tests.CAppDemonstrator_4433;            waitForCommand; Stdin console command line: [close]
 * 2015/01/28; 12:19:13:843; tests.CAppDemonstrator_4433;            doCommandClose; begin ...
 * 2015/01/28; 12:19:13:844; tests.CAppDemonstrator_4433;            waitForCommand; end
 * 2015/01/28; 12:19:13:844; tests.CAppDemonstrator_4433;                    doTest; END
 * 2015/01/28; 12:19:13:844; tests.CAppDemonstrator_4433;                   destroy; close the logger
 * 2015/01/28; 12:19:13:844; vityLoggerBasicConsole_6424;                     close; An instance of CActivityLoggerBasicConsole is not closable.
 * 
 * </pre>
 * 
 * 
 * 
 * 
 * @author ogattaz
 * 
 */
class CAppDemonstrator extends CAppConsoleBase {

	private final static String CMD_TEST = "test";

	/**
	 * @param args
	 */
	public static void main(final String[] args) {

		int wExitCode = 0;
		CAppDemonstrator wTest = null;
		try {
			wTest = new CAppDemonstrator(args);
			wTest.runApp();
		} catch (Exception e) {
			e.printStackTrace();
			wExitCode = 1;
		} finally {
			wTest.destroy();
		}
		System.exit(wExitCode);
	}

	/**
	 * @param args
	 */
	CAppDemonstrator(String[] args) {
		super(args);

		addOneCommand(CMD_TEST, new String[] { "exec a test" });

		pLogger.setLevel(Level.FINE);

		pLogger.logInfo(this, "<init>", "instanciated");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cohorte.utilities.tests.CAppConsoleBase#destroy()
	 */
	@Override
	protected void destroy() {

		// free ressources

		super.destroy();
	}

	/**
	 * @param aCmdeLine
	 * @throws Exception
	 */
	private void doCommandTest(final String aCmdeLine) throws Exception {

		pLogger.logInfo(this, "doCommandTest", "begin aCmdeLine=[%s]",
				aCmdeLine);

		// do somthing

		pLogger.logInfo(this, "doCommandTest", "end");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cohorte.utilities.tests.CAppConsoleBase#doCommandUser(java.lang.String
	 * )
	 */
	@Override
	protected void doCommandUser(String aCmdeLine) throws Exception {
		pLogger.logInfo(this, "doCommandUser", "BEGIN");

		if (isCommandX(CMD_TEST)) {
			doCommandTest(aCmdeLine);
		}

		pLogger.logInfo(this, "doCommandUser", "END");
	}

	/**
	 * @throws Exception
	 * 
	 */
	@Override
	protected void runApp() throws Exception {
		pLogger.logInfo(this, "doTest", "BEGIN");

		// init and/or open ressources

		waitForCommand();

		pLogger.logInfo(this, "doTest", "END");
	}

}
