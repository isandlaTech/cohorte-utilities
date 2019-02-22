package test.cohorte.utilities.json;

import static org.cohorte.utilities.json.CXJsonUtils.cloneJsonObj;
import static org.cohorte.utilities.json.CXJsonUtils.newJsonArray;
import static org.cohorte.utilities.json.CXJsonUtils.newJsonObj;
import static org.cohorte.utilities.json.CXJsonUtils.newKeyVal;

import org.cohorte.utilities.json.CXJsonException;
import org.junit.Test;
import org.psem2m.utilities.CXException;
import org.psem2m.utilities.json.JSONArray;
import org.psem2m.utilities.json.JSONObject;
import org.psem2m.utilities.logging.CActivityLoggerBasicConsole;
import org.psem2m.utilities.logging.IActivityLogger;

/**
 * @author ogattaz
 *
 */
public class CJunitTestCXJsonUtils {

	IActivityLogger pLogger = CActivityLoggerBasicConsole.getInstance();

	public CJunitTestCXJsonUtils() {
		super();
	}

	/**
	 * Try to put an infinit flat in a json object
	 * 
	 * EXPECTED CJsonException
	 */
	@Test(expected = CXJsonException.class)
	public void testCloneInvalid() throws Exception {
		pLogger.logInfo(this, "testCloneInvalid", "Begin");
		try {
			JSONObject wJsonObjOne = newJsonObj(newKeyVal("float", Float.POSITIVE_INFINITY));

			pLogger.logInfo(this, "testCloneInvalid", "wJsonObjOne:\n%s", wJsonObjOne.toString(2));
		} catch (CXJsonException e) {
			pLogger.logWarn(this, "testCloneInvalid", "EXPECTED ERROR: %s", CXException.eCauseMessagesInString(e));
			throw e;
		} finally {
			pLogger.logInfo(this, "testCloneInvalid", "end");
		}
	}

	/**
	 * Build a json tree and clone it
	 */
	@Test
	public void testCloneValid() throws Exception {
		pLogger.logInfo(this, "testCloneValid", "Begin");
		try {
			JSONObject wJsonObjOneA = newJsonObj(newKeyVal("aa", "valueAA"), new Object[] { "bb", 25 }, new Object[] {
					"cc", true });

			JSONArray wJsonArray = newJsonArray("text", 12, true, 45.56, newJsonObj(new Object[] { "aaa", "valueAAA" }));

			JSONObject wJsonObjOne = newJsonObj(new Object[] { "a", "valueA" }, new Object[] { "b", 25 }, new Object[] {
					"c", true }, new Object[] { "obj", wJsonObjOneA }, new Object[] { "array", wJsonArray });

			JSONObject wJsonObjTwo = cloneJsonObj(wJsonObjOne);

			pLogger.logInfo(this, "testCloneValid", "JsonObjTwo:\n%s", wJsonObjTwo.toString(2));

		} catch (Exception e) {
			pLogger.logSevere(this, "testCloneInvalid", "ERROR: %s", CXException.eCauseMessagesInString(e));
			throw e;
		} finally {
			pLogger.logInfo(this, "testCloneInvalid", "end");
		}
	}
}
