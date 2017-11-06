/**
 * 
 */
package test.cohorte.utilities.testapps.impl;

import org.cohorte.utilities.tests.CAppConsoleBase;
import org.psem2m.utilities.CXArray;

/**
 * @author ogattaz
 * 
 */
public class CTestArray extends CAppConsoleBase {

	/**
	 * @param args
	 */
	public static void main(final String[] args) {

		try {
			CTestArray wTest = new CTestArray(args);
			wTest.runApp();
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
		addOneCommand(CMD_TEST, new String[] { "test the array tools" });
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
	protected void doCommandClose() throws Exception {
		pLogger.logInfo(this, "doCommandClose", "begin aCmdeLine=[%s]",
				getCmdeLine());

	}

	/**
	 * @param aCmdeLine
	 * @throws Exception
	 */
	private void doCommandTest(final String aCmdeLine) throws Exception {
		pLogger.logInfo(this, "doCommandTest", "begin");

		pLogger.logInfo(this, "doCommandTest", "--- TestAppend");
		doCommandTestAppend();
		pLogger.logInfo(this, "doCommandTest", "--- TestInsert");
		doCommandTestInsert();
		pLogger.logInfo(this, "doCommandTest", "--- TestInsertFirst");
		doCommandTestInsertFirst();
		pLogger.logInfo(this, "doCommandTest", "--- TestAppendArray");
		doCommandTestAppendArray();
		pLogger.logInfo(this, "doCommandTest", "--- TestInsertArray");
		doCommandTestInsertArray();
		pLogger.logInfo(this, "doCommandTest", "--- TestInsertFirstArray");
		doCommandTestInsertFirstArray();

		pLogger.logInfo(this, "doCommandTest", "end");
	}

	/**
	 * <pre>
	 * doCommandTestAppend; Original : Value0|Value1|Value2 
	 * doCommandTestAppend; Appended : Value0|Value1|Value2|Appended
	 * </pre>
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
	 * <pre>
	 * doCommandTestAppendArray; Original : Value0|Value1|Value2|Value3|Value4|Value5 
	 * doCommandTestAppendArray; Appended : Value0|Value1|Value2|Value3|Value4|Value5|Appended_1|Appended_2|Appended_3
	 * </pre>
	 * 
	 */
	private void doCommandTestAppendArray() {
		String[] wStrings = newStringArray(6);
		pLogger.logInfo(this, "doCommandTestAppendArray", "Original : %s ",
				CXArray.arrayToString(wStrings, "|"));

		wStrings = (String[]) CXArray.appendObjects(wStrings, new String[] {
				"Appended_1", "Appended_2", "Appended_3" });

		pLogger.logInfo(this, "doCommandTestAppendArray", "Appended : %s ",
				CXArray.arrayToString(wStrings, "|"));
	}

	/**
	 * <pre>
	 * doCommandTestInsert; Original :  
	 * doCommandTestInsert; Inserted : Inserted_0 
	 * doCommandTestInsert; Original : Value0|Value1|Value2 
	 * doCommandTestInsert; Inserted : Value0|Value1|Value2|Inserted_3 
	 * doCommandTestInsert; Inserted : Value0|Value1|Inserted_2|Value2|Inserted_3 
	 * doCommandTestInsert; Inserted : Value0|Inserted_1|Value1|Inserted_2|Value2|Inserted_3
	 * doCommandTestInsert; Inserted : index [10] is greater than len (len=[6] )
	 * </pre>
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

		// test : Inserted : index [10] is greater than len (len=[6] )
		try {
			wStrings = (String[]) CXArray.insertOneObject(wStrings,
					"Inserted_1", 10);
		} catch (Exception e) {
			pLogger.logSevere(this, "doCommandTestInsert", "Inserted : %s ",
					e.getMessage());
		}
	}

	/**
	 * <pre>
	 * doCommandTestInsertArray; Original : Value0|Value1|Value2|Value3|Value4|Value5 
	 * doCommandTestInsertArray; Inserted : Value0|Value1|Inserted_1|Inserted_2|Inserted_3|Value2|Value3|Value4|Value5
	 * doCommandTestInsertArray; Inserted : index [99] is greater than len (len=[9] )
	 * </pre>
	 * 
	 */
	private void doCommandTestInsertArray() {

		String[] wStrings = newStringArray(6);

		pLogger.logInfo(this, "doCommandTestInsertArray", "Original : %s ",
				CXArray.arrayToString(wStrings, "|"));

		wStrings = (String[]) CXArray.insertObjects(wStrings, new String[] {
				"Inserted_1", "Inserted_2", "Inserted_3" }, 2);

		pLogger.logInfo(this, "doCommandTestInsertArray", "Inserted : %s ",
				CXArray.arrayToString(wStrings, "|"));

		// test : Inserted : index [99] is greater than len (len=[9] )
		try {
			wStrings = (String[]) CXArray.insertObjects(wStrings, new String[] {
					"Inserted_1", "Inserted_2", "Inserted_3" }, 99);
		} catch (Exception e) {
			pLogger.logSevere(this, "doCommandTestInsertArray",
					"Inserted : %s ", e.getMessage());
		}
	}

	/**
	 * <pre>
	 * doCommandTestAppend; Original : Value0|Value1|Value2 
	 * doCommandTestAppend; Appended : InsertedFirst|Value0|Value1|Value2
	 * </pre>
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

	/**
	 * <pre>
	 * mmandTestInsertFirstArray; Original : Value0|Value1|Value2|Value3|Value4|Value5 
	 * mmandTestInsertFirstArray; Inserted : Inserted_1|Inserted_2|Inserted_3|Value0|Value1|Value2|Value3|Value4|Value5
	 * </pre>
	 * 
	 */
	private void doCommandTestInsertFirstArray() {
		String[] wStrings = newStringArray(6);
		pLogger.logInfo(this, "doCommandTestInsertFirstArray",
				"Original : %s ", CXArray.arrayToString(wStrings, "|"));

		wStrings = (String[]) CXArray.insertFirstObjects(wStrings,
				new String[] { "Inserted_1", "Inserted_2", "Inserted_3" });

		pLogger.logInfo(this, "doCommandTestInsertFirstArray",
				"Inserted : %s ", CXArray.arrayToString(wStrings, "|"));
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
	protected void runApp() throws Exception {
		pLogger.logInfo(this, "doTest", "begin");
		// pLogger.logInfo(this, "doTest", CXOSUtils.getEnvContext());
		// pLogger.logInfo(this, "doTest", CXJvmUtils.getJavaContext());

		if (CXArray.contains(pAppArgs, APPLICATION_PARAM_AUTO)) {
			doCommandTest(null);
		} else {
			waitForCommand();
		}

		pLogger.logInfo(this, "doTest", "end");
	}
}
