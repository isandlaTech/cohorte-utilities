package test.cohorte.utilities.json;

import static org.psem2m.utilities.json.JSONBuilder.newJsonArray;
import static org.psem2m.utilities.json.JSONBuilder.newJsonObj;
import static org.psem2m.utilities.json.JSONBuilder.newKeyVal;

import org.cohorte.utilities.junit.CAbstractJunitTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.psem2m.utilities.CXException;
import org.psem2m.utilities.json.JSONException;
import org.psem2m.utilities.json.JSONObject;

/**
 * @author ogattaz
 *
 */
public class CJunitTestJSONBuiilder extends CAbstractJunitTest {

	/**
	 * 
	 */
	@AfterClass
	public static void destroy() {

		// log the destroy banner containing the report
		logBannerDestroy(CJunitTestJSONBuiilder.class);
	}

	/**
	 * 
	 */
	@BeforeClass
	public static void initialize() {

		// initialise the map of the test method of the current junit test class
		// de
		initializeTestsRegistry(CJunitTestJSONBuiilder.class);

		// log the initialization banner
		logBannerInitialization(CJunitTestJSONBuiilder.class);
	}

	/**
	 * 
	 */
	public CJunitTestJSONBuiilder() {
		super();
	}

	/**
	 * @param aMethodName
	 * @param aJsonObj
	 * @param aPath
	 * @param aExpectedValue
	 */
	private void assertInSONObject(final String aMethodName, final JSONObject aJsonObj, final String aPath,
			final Object aExpectedValue) {

		final Class<?> wExpectedClass = aExpectedValue.getClass();
		Object wValue = aJsonObj.getObject(aPath, wExpectedClass);

		String wMessage = String.format("The value of the path [%s] doesn't match", aPath);
		Assert.assertEquals(wMessage, aExpectedValue, wValue);

		getLogger().logInfo(this, aMethodName, "ASSERT OK : the value of the path [%s] is [%s]", aPath, wValue);
	}

	/**
	 * Try to put an infinit float in a json object
	 * 
	 * EXPECTED CJsonException
	 */
	@Test(expected = JSONException.class)
	public void testCloneInvalid() throws Exception {
		String wMethodName = "testCloneInvalid";

		logBegin(this, wMethodName, "Try to create a JSONObject with an invalid value");

		try {

			// try to create a JSON Object with an infinite Float =>
			// JSONException
			newJsonObj(newKeyVal("float", Float.POSITIVE_INFINITY));

			logEndKO(this, wMethodName, "The JSONObject has to throw a JSONException !");

		} catch (Exception | Error e) {
			getLogger().logWarn(this, wMethodName, "EXPECTED ERROR: %s", CXException.eCauseMessagesInString(e));

			logEndOK(this, wMethodName, "The waited JSONException is thrown");

			throw e;
		}
	}

	/**
	 * <pre>
	 * {
	 *   "a": "valueA",
	 *   "b": 25,
	 *   "c": true,
	 *   "obj": {
	 *     "aa": "valueAA",
	 *     "bb": 25,
	 *     "cc": true,
	 *     "array2": [
	 *       "string2",
	 *       24,
	 *       true,
	 *       91.12,
	 *       {"aaaa": "valueAAAA"}
	 *     ]
	 *   },
	 *   "array": [
	 *     "string1",
	 *     12,
	 *     true,
	 *     45.56,
	 *     {"aaa": "valueAAA"}
	 *   ]
	 * }
	 * </pre>
	 * 
	 * Build a json tree and clone it
	 */
	@Test
	public void testCloneValid() throws Exception {
		String wMethodName = "testCloneValid";
		logBegin(this, wMethodName, "Try to clone a complex JSONObject");
		try {

			JSONObject wJsonObjOne = newJsonObj(
			//
					newKeyVal("a", "valueA"),
					//
					newKeyVal("b", 25),
					//
					newKeyVal("c", true),
					//
					newKeyVal("obj", newJsonObj(
					//
							newKeyVal("aa", "valueAA"),
							//
							newKeyVal("bb", 25),
							//
							newKeyVal("cc", true),
							//

							newKeyVal("array2", newJsonArray(
							//
									"string2",
									//
									24,
									//
									true,
									//
									91.12,
									//
									newJsonObj(newKeyVal("aaaa", "valueAAAA")))))),
					//
					newKeyVal("array", newJsonArray("string1",
					//
							12,
							//
							true,
							//
							45.56,
							//
							newJsonObj(newKeyVal("aaa", "valueAAA")))));

			JSONObject wJsonObjTwo = wJsonObjOne.clone();

			getLogger().logInfo(this, wMethodName, "JsonObjTwo:\n%s", wJsonObjTwo.toString(2));

			// => string "valueA"
			assertInSONObject(wMethodName, wJsonObjTwo, "a", "valueA");
			// => string "valueAA"
			assertInSONObject(wMethodName, wJsonObjTwo, "obj.aa", "valueAA");
			// => string "valueAAA"
			assertInSONObject(wMethodName, wJsonObjTwo, "array[4].aaa", "valueAAA");
			// => string "valueAAAA"
			assertInSONObject(wMethodName, wJsonObjTwo, "obj.array2[4].aaaa", "valueAAAA");

			logEndOK(this, wMethodName, "The JSONObject is cloned");

		} catch (Exception | Error e) {
			getLogger().logSevere(this, wMethodName, "ERROR: %s", e);

			logEndKO(this, wMethodName, "Unexpected exception !", e);

			throw e;
		}
	}
}
