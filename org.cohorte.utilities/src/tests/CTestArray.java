/**
 * 
 */
package tests;

import org.cohorte.utilities.tests.CAbstractTest;
import org.psem2m.utilities.CXArray;

/**
 * @author ogattaz
 * 
 */
public class CTestArray extends CAbstractTest {

	/**
	 * @param args
	 */
	public static void main(final String[] args) {

		try {
			CTestArray wTest = new CTestArray(args);
			wTest.runTest();
			wTest.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	private CTestArray(final String[] args) {
		super(args);
		addOneCommand(CMD_TEST, "test the array tools");
		pLogger.logInfo(this, "<init>", "instanciated");
	}

	/**
	 * 
	 */
	@Override
	protected void destroy() {
		super.destroy();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tests.CAbstractTest#doCommandClose(java.lang.String)
	 */
	@Override
	protected void doCommandClose(final String aCmdeLine) throws Exception {
		pLogger.logInfo(this, "doCommandClose", "begin aCmdeLine=[%s]",
				aCmdeLine);

	}

	/**
	 * @param aCmdeLine
	 * @throws Exception
	 */
	private void doCommandTest(final String aCmdeLine) throws Exception {
		pLogger.logInfo(this, "doCommandTest", "begin");
		doCommandTestAppend();
		doCommandTestInsert();
		doCommandTestInsertFirst();
		pLogger.logInfo(this, "doCommandTest", "end");
	}

	/**
	 * 
	 */
	private void doCommandTestAppend() {
		String[] wStrings = newStringArray(3);
		pLogger.logInfo(this, "doCommandTestAppend", "Original : %s ",
				CXArray.arrayToString(wStrings, "|"));

		wStrings = (String[]) CXArray.appendOneObject(wStrings, "Appended");

		pLogger.logInfo(this, "doCommandTestAppend", "Appended : %s ",
				CXArray.arrayToString(wStrings, "|"));
	}

	/**
	 * 
	 */
	private void doCommandTestInsert() {

		String[] wStrings = newStringArray(0);

		pLogger.logInfo(this, "doCommandTestInsert", "Original : %s ",
				CXArray.arrayToString(wStrings, "|"));

		wStrings = (String[]) CXArray
				.insertOneObject(wStrings, "Inserted_0", 0);

		pLogger.logInfo(this, "doCommandTestInsert", "Inserted : %s ",
				CXArray.arrayToString(wStrings, "|"));

		wStrings = newStringArray(3);

		pLogger.logInfo(this, "doCommandTestInsert", "Original : %s ",
				CXArray.arrayToString(wStrings, "|"));

		wStrings = (String[]) CXArray
				.insertOneObject(wStrings, "Inserted_3", 3);

		pLogger.logInfo(this, "doCommandTestInsert", "Inserted : %s ",
				CXArray.arrayToString(wStrings, "|"));

		wStrings = (String[]) CXArray
				.insertOneObject(wStrings, "Inserted_2", 2);

		pLogger.logInfo(this, "doCommandTestInsert", "Inserted : %s ",
				CXArray.arrayToString(wStrings, "|"));

		wStrings = (String[]) CXArray
				.insertOneObject(wStrings, "Inserted_1", 1);

		pLogger.logInfo(this, "doCommandTestInsert", "Inserted : %s ",
				CXArray.arrayToString(wStrings, "|"));

		try {
			wStrings = (String[]) CXArray.insertOneObject(wStrings,
					"Inserted_1", 10);
		} catch (Exception e) {
			pLogger.logSevere(this, "doCommandTestInsert", "Inserted : %s ",
					e.getMessage());
		}
	}

	/**
	 * 
	 */
	private void doCommandTestInsertFirst() {
		String[] wStrings = newStringArray(3);
		pLogger.logInfo(this, "doCommandTestAppend", "Original : %s ",
				CXArray.arrayToString(wStrings, "|"));

		wStrings = (String[]) CXArray.insertFirstOneObject(wStrings,
				"InsertedFirst");

		pLogger.logInfo(this, "doCommandTestAppend", "Appended : %s ",
				CXArray.arrayToString(wStrings, "|"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tests.CAbstractTest#doUserCommand(java.lang.String)
	 */
	@Override
	protected void doCommandUser(final String aCmdeLine) throws Exception {
		if (isCommandX(aCmdeLine, CMD_TEST)) {
			doCommandTest(aCmdeLine);
		} else {
			wrongCommandUser(aCmdeLine);
		}
	}

	/**
	 * @param aSize
	 * @return
	 */
	private String[] newStringArray(final int aSize) {
		int wSize = (aSize > -1) ? aSize : 0;

		String[] wStrings = new String[wSize];
		for (int wIdx = 0; wIdx < wSize; wIdx++) {
			wStrings[wIdx] = "Value" + wIdx;
		}
		return wStrings;
	}

	@Override
	protected void runTest() throws Exception {
		pLogger.logInfo(this, "doTest", "begin");
		// pLogger.logInfo(this, "doTest", CXOSUtils.getEnvContext());
		// pLogger.logInfo(this, "doTest", CXJvmUtils.getJavaContext());

		if (CXArray.contains(pArgs, APPLICATION_PARAM_AUTO)) {
			doCommandTest(null);
		} else {
			waitForUserCommand();
		}

		pLogger.logInfo(this, "doTest", "end");
	}
}
