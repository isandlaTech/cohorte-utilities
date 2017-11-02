package test.cohorte.utilities.testapps.impl;

import java.util.logging.Level;

import org.cohorte.utilities.tests.CAppConsoleBase;
import org.psem2m.utilities.CXDateTime;

/**
 * 
 * This class is an implmentation of a server used to test the server control
 * feature available in the package "org.psem2m.utilities.system"
 * 
 * The trace of the stdout
 * 
 * <pre>
 * -----
 * UserDir init: no argument 1 : pwd used
 * UserDir: /Users/ogattaz/workspaces/cohorte-OpenSource-git/cohorte-utilities/org.cohorte.utilities
 * -----
 * java version "1.7.0_45"
 * Java(TM) SE Runtime Environment (build 1.7.0_45-b18)
 * Java HotSpot(TM) 64-Bit Server VM (build 24.45-b08, mixed mode)
 * 
 * 055-09:08:04.800 |                         <init> | initialiszed
 * 055-09:08:04.800 |                         runApp | Begin
 * 055-09:08:04.801 |                         runApp | *** TEST SERVER STARTED
 * state
 * 055-09:08:17.758 |                         runApp | CmdeLine=[state]
 * 055-09:08:17.760 |                 doCommandState | *** TEST SERVER STATE [STARTED]
 * close
 * 055-09:08:22.029 |                         runApp | CmdeLine=[close]
 * 055-09:08:22.029 |                 doCommandClose | *** TEST SERVER STOPPED
 * 055-09:08:22.029 |                         runApp | End
 * </pre>
 * 
 * @author ogattaz
 * 
 * @see org.psem2m.utilities.system.CXOSServer
 * @see test.cohorte.utilities.testapps.impl.CTestOSServer
 * 
 */
public class CTestAppServerTest extends CAppConsoleBase {

	public static final String TEST_SERVER_RETURN_TEXT_START = CTestOSServer.TEST_SERVER_RETURN_TEXT_START;
	public static final String TEST_SERVER_RETURN_TEXT_STATE = CTestOSServer.TEST_SERVER_RETURN_TEXT_STATE;
	public static final String TEST_SERVER_RETURN_TEXT_STOP = CTestOSServer.TEST_SERVER_RETURN_TEXT_STOP;

	/**
	 * @param args
	 */
	public static void main(final String[] args) {

		try {
			CTestAppServerTest wTest = new CTestAppServerTest(args);
			wTest.runApp();
			wTest.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	private CTestAppServerTest(final String[] args) {
		super(args);
		pLogger.setLevel(Level.OFF);
		addOneCommand("state", new String[] { "get server state" });
		log("<init>", "initialiszed");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cohorte.utilities.tests.CAppConsoleBase#doCommandClose()
	 */
	@Override
	protected void doCommandClose() throws Exception {

		log("doCommandClose", TEST_SERVER_RETURN_TEXT_STOP);
	}

	/**
	 * @param aCmdeLine
	 * @throws Exceptions
	 */
	private void doCommandState(final String aCmdeLine) throws Exception {

		log("doCommandState", "%s [%s]", TEST_SERVER_RETURN_TEXT_STATE,
				"STARTED");
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

		if (isCommandX("state")) {
			doCommandState(aCmdeLine);
		} else {
			log("doCommandUser", "Unknown command [%s]", aCmdeLine);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cohorte.utilities.tests.CAppConsoleBase#echoCommandLine(java.lang
	 * .String)
	 */
	@Override
	protected void echoCommandLine(final String aCmdeLine) {

		log("runApp", "CmdeLine=[%s]", aCmdeLine);
	}

	/**
	 * @param aWhat
	 * @param aFormat
	 * @param aArgs
	 */
	private void log(final String aWhat, final String aFormat,
			final Object... aArgs) {
		System.out.println(String.format("%s | %30s | %s",
				CXDateTime.getFormatedTimeStamp(), aWhat,
				String.format(aFormat, aArgs)));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cohorte.utilities.tests.CAppConsoleBase#runApp()
	 */
	@Override
	protected void runApp() throws Exception {
		log("runApp", "Begin");

		log("runApp", TEST_SERVER_RETURN_TEXT_START);

		waitForCommand();

		log("runApp", "End");
	}
}
