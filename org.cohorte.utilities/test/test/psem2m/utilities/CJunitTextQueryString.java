package test.psem2m.utilities;

import org.cohorte.utilities.junit.CAbstractJunitTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.psem2m.utilities.CXQueryString;
import org.psem2m.utilities.CXStringUtils;
import org.psem2m.utilities.files.CXFileDir;

/**
 * MOD_OG_20210812
 * 
 * @author ogattaz
 *
 */
public class CJunitTextQueryString extends CAbstractJunitTest {

	/**
	 * 
	 */
	@AfterClass
	public static void destroy() {

		// log the destroy banner containing the report
		logBannerDestroy(CJunitTextQueryString.class);
	}

	/**
	 * 
	 */
	@BeforeClass
	public static void initialize() {

		// initialise the map of the test method of the current junit test class
		// de
		initializeTestsRegistry(CJunitTextQueryString.class);

		// log the initialization banner
		logBannerInitialization(CJunitTextQueryString.class);
	}

	/**
	 * 
	 */
	public CJunitTextQueryString() {
		super();
	}

	@Test
	public void test05() throws Exception {
		String wMethodName = "test05";
		String wAction = "Retreive Dimensions";
		try {

			logBegin(this, wMethodName, "%s Begin...", wAction);

			CXQueryString wQS1 = new CXQueryString();

			wQS1.put("name", "myWebAppName");

			wQS1.put("resourceNamePrefix", "myWebAppName".toLowerCase());

			wQS1.put("hasDatabase", new Boolean(true).toString());

			wQS1.put("webAppDataDir", CXFileDir.getUserDir().getAbsolutePath());

			// to stream
			String wQS1Stream = wQS1.toQueryString();

			getLogger().logInfo(this, wMethodName, "QS1Stream=[%s]", wQS1Stream);

			// parse the stream
			CXQueryString wQS2 = new CXQueryString(wQS1Stream);

			String wQS2Stream = wQS2.toQueryString();

			getLogger().logInfo(this, wMethodName, "QS2Stream=[%s]", wQS2Stream);

			// asserion the two stream are equals
			Assert.assertEquals("the two stream are not equals", wQS1Stream, wQS2Stream);

			// to human representation
			String wQS1HumanRepresentation = CXStringUtils.stringMapToString(wQS1.toMapOfString());

			getLogger().logInfo(this, wMethodName, "QS1 human representation: %s", wQS1HumanRepresentation);

			String wQS2HumanRepresentation = CXStringUtils.stringMapToString(wQS2.toMapOfString());

			getLogger().logInfo(this, wMethodName, "QS2 human representation: %s", wQS2HumanRepresentation);

			Assert.assertEquals("the two human representations are not equals", wQS1HumanRepresentation,
					wQS2HumanRepresentation);

			getLogger().logInfo(this, wMethodName, "QS1: %s",
					CXStringUtils.stringMapToString(wQS1.toSortedMapOfString()));

			// to sorted human representation
			String wQS1SortedHumanRepresentation = CXStringUtils.stringMapToString(wQS1.toSortedMapOfString());

			getLogger()
					.logInfo(this, wMethodName, "QS1 sorted human representation: %s", wQS1SortedHumanRepresentation);

			String wQS2SortedHumanRepresentation = CXStringUtils.stringMapToString(wQS2.toSortedMapOfString());

			getLogger()
					.logInfo(this, wMethodName, "QS2 sorted human representation: %s", wQS2SortedHumanRepresentation);

			Assert.assertEquals("the two sorted human representations are not equals", wQS1SortedHumanRepresentation,
					wQS2SortedHumanRepresentation);

			logEndOK(this, wMethodName, "%s End OK. assertEquals = true", wAction);

		} catch (Throwable e) {
			logEndKO(this, wMethodName, e);
			throw e;
		}
	}
}
