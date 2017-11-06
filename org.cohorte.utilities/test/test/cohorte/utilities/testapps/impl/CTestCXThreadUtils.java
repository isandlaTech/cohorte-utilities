package test.cohorte.utilities.testapps.impl;

import static org.psem2m.utilities.CXThreadUtils.sleep;

import java.util.ArrayList;
import java.util.List;

import org.cohorte.utilities.tests.CAppConsoleBase;
import org.psem2m.utilities.CXJvmUtils;
import org.psem2m.utilities.CXOSUtils;
import org.psem2m.utilities.CXThreadUtils;
import org.psem2m.utilities.CXTimer;

/**
 * @author ogattaz
 * 
 */
public class CTestCXThreadUtils extends CAppConsoleBase {

	/**
	 * @author ogattaz
	 * 
	 */
	class CRunnable implements Runnable {

		private boolean pContinue = true;
		private final String pName;
		private final long pTempo;

		/**
		 * @param aName
		 * @param aTempo
		 */
		CRunnable(final String aName, final long aTempo) {
			super();
			pName = aName;
			pTempo = aTempo;

			pLogger.logInfo(this, "<init>", "initialized [%s]", pName);
		}

		/**
		 * 
		 */
		synchronized void end() {
			pContinue = false;
		}

		/**
		 * 
		 */
		synchronized boolean mustContinue() {
			return pContinue;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			pLogger.logInfo(this, "run", "begin [%s]", pName);

			while (mustContinue()) {
				sleep(pTempo);
			}

			pLogger.logInfo(this, "run", "end [%s]", pName);
		}

	}

	private static final int NB_THREADS = 100;

	/**
	 * @param args
	 */
	public static void main(final String[] args) {

		try {
			CTestCXThreadUtils wTest = new CTestCXThreadUtils(args);
			wTest.runApp();
			wTest.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public CTestCXThreadUtils(final String[] args) {
		super(args);
		addOneCommand(CMD_TEST, new String[] { "test the threading tools" });
		pLogger.logInfo(this, "<init>", "instanciated");
	}

	/**
	 * @param aIdx
	 * @return
	 */
	private CRunnable createRunnable(final int aIdx) {

		String wName = createRunnableName(aIdx);
		CRunnable wRunnable = new CRunnable(wName, 200);
		new Thread(wRunnable, wName).start();
		return wRunnable;
	}

	/**
	 * @param aIdx
	 * @return
	 */
	private String createRunnableName(final int aIdx) {
		return String.format("Runnable_%s", aIdx);
	}

	/**
	 * @param aNb
	 * @return
	 */
	private List<CRunnable> createRunnables(final int aNb) {

		List<CRunnable> wCRunnables = new ArrayList<CRunnable>();
		for (int wIdx = 0; wIdx < aNb; wIdx++) {
			wCRunnables.add(createRunnable(wIdx));

			sleep(10);// 0,01 second
		}
		return wCRunnables;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tests.CAbstractTest#doCommandClose(java.lang.String)
	 */
	@Override
	protected void doCommandClose() throws Exception {
		pLogger.logInfo(this, "doCommandClose", "begin aCmdeLine=[%s]",
				getCmdeLine());

	}

	/**
	 * @param aCmdeLine
	 * @throws Exception
	 */
	private void doCommandTest(final String aCmdeLine) throws Exception {
		pLogger.logInfo(this, "doCommandTest", "begin aCmdeLine=[%s]",
				aCmdeLine);

		CXTimer wTimer = CXTimer.newStartedTimer();
		for (int wIdx = 0; wIdx < NB_THREADS; wIdx++) {
			Thread wThread = CXThreadUtils
					.getActiveThread(createRunnableName(wIdx));
			if (wThread == null) {
				pLogger.logSevere(this, "doCommandTest",
						"Can't retreive runable [%s]", wIdx);
			}
		}
		pLogger.logInfo(this, "doCommandTest",
				"getActiveThread duration=[%s] for [%s] search",
				wTimer.getDurationStrMicroSec(), NB_THREADS);

		wTimer = CXTimer.newStartedTimer();
		for (int wIdx = 0; wIdx < NB_THREADS; wIdx++) {
			Thread wThread = CXThreadUtils
					.getLiveThread(createRunnableName(wIdx));
			if (wThread == null) {
				pLogger.logSevere(this, "doCommandTest",
						"Can't retreive runable [%s]", wIdx);
			}
		}
		pLogger.logInfo(this, "doCommandTest",
				"getLiveThread duration=[%s] for [%s] search",
				wTimer.getDurationStrMicroSec(), NB_THREADS);

		pLogger.logInfo(this, "doCommandTest", "end");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tests.CAbstractTest#doUserCommand(java.lang.String)
	 */
	@Override
	protected void doCommandUser(final String aCmdeLine) throws Exception {
		if (isCommandX(CMD_TEST)) {
			doCommandTest(aCmdeLine);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tests.CAbstractTest#doTest()
	 */
	@Override
	protected void runApp() throws Exception {
		pLogger.logInfo(this, "doTest", "begin");
		pLogger.logInfo(this, "doTest", CXOSUtils.getEnvContext());
		pLogger.logInfo(this, "doTest", CXJvmUtils.getJavaContext());

		List<CRunnable> wRunnables = createRunnables(NB_THREADS);

		waitForCommand();

		// stop the threads
		for (CRunnable wRunnable : wRunnables) {
			wRunnable.end();
		}

		pLogger.logInfo(this, "doTest", "end");
	}
}
