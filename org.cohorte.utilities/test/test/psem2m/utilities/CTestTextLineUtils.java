package test.psem2m.utilities;

import org.cohorte.utilities.CXMethodUtils;
import org.cohorte.utilities.CXTextLineUtils;
import org.cohorte.utilities.junit.CAbstractJunitTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * @author ogattaz
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CTestTextLineUtils extends CAbstractJunitTest {

	private static final String ONE_LINE_TEXT = "Lorem ipsum dolor sit amet, consectetur adipiscing elit";
	private static final String ONE_LINE_TEXT_TOO_LONG = ONE_LINE_TEXT + ". " + ONE_LINE_TEXT + ". " + ONE_LINE_TEXT;

	/**
	 * 
	 */
	@AfterClass
	public static void destroy() {

		// log the destroy banner containing the report
		logBannerDestroy();
	}

	/**
	 * 
	 */
	@BeforeClass
	public static void initialize() {

		// initialise the map of the test method of the current junit test class
		// de
		initializeTestsRegistry();

		// log the initialization banner
		logBannerInitialization();
	}

	/**
	 * ATTENTION: a junit test must have only one public constructor
	 */
	public CTestTextLineUtils() {
		super();
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void test10genTextLine() throws Exception {
		String wMethodName = CXMethodUtils.getMethodName(0);
		String wAction = "Generate different text lines";
		try {

			logBegin(this, wMethodName, "%s Begin...", wAction);

			int wLen = 100;
			int wTextOffset = 10;

			getLogger().logInfo(this, wMethodName,
			//
					"A  " + CXTextLineUtils.generateLine('#', wLen));

			getLogger().logInfo(this, wMethodName,
			//
					"B  " + CXTextLineUtils.generateLineLabel('#', wLen, ONE_LINE_TEXT));

			getLogger().logInfo(this, wMethodName,
			//
					"C1 " + CXTextLineUtils.generateLineBeginEnd('#', wLen));

			getLogger().logInfo(this, wMethodName,
			//
					"C2 " + CXTextLineUtils.generateLineBeginEnd('#', '.', wLen));

			getLogger().logInfo(this, wMethodName,
			//
					"D1 " + CXTextLineUtils.generateLineBeginEnd('#', wLen, ONE_LINE_TEXT));

			getLogger().logInfo(this, wMethodName,
			//
					"D2 " + CXTextLineUtils.generateLineBeginEnd('#', '.', wLen, ONE_LINE_TEXT));

			getLogger().logInfo(this, wMethodName,
			//
					"E1 " + CXTextLineUtils.generateLineBeginEnd('#', wLen, wTextOffset, ONE_LINE_TEXT));
			getLogger().logInfo(this, wMethodName,
			//
					"E2 " + CXTextLineUtils.generateLineBeginEnd('#', '.', wLen, wTextOffset, ONE_LINE_TEXT));

			getLogger().logInfo(this, wMethodName,
			//
					"F1 " + CXTextLineUtils.generateLineBeginEnd('#', wLen, wTextOffset, ONE_LINE_TEXT_TOO_LONG));
			getLogger().logInfo(this, wMethodName,
			//
					"F2 " + CXTextLineUtils.generateLineBeginEnd('#', '-', wLen, wTextOffset, ONE_LINE_TEXT_TOO_LONG));

			logEndOK(this, wMethodName, "%s End OK.", wAction);

		} catch (Throwable e) {
			logEndKO(this, wMethodName, e);
			throw e;
		}
	}

}
