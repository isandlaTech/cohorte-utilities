package org.cohorte.utilities.json;

import java.util.Map.Entry;

import org.psem2m.utilities.json.JSONArray;
import org.psem2m.utilities.json.JSONException;
import org.psem2m.utilities.json.JSONObject;

/**
 * @author ogattaz
 *
 */
public class CXJsonUtils {

	// CXJsonObject

	/**
	 * @param aJSONObject
	 * @return
	 */
	public static JSONArray cloneJsonArray(final JSONArray aJSONArray) throws CXJsonException {
		JSONArray wNewJsonArray = new JSONArray();
		int wIdx = 0;
		for (Object wEntry : aJSONArray.getEntries(Object.class)) {
			try {
				if (wEntry instanceof JSONObject) {
					wEntry = cloneJsonObj((JSONObject) wEntry);
				} else if (wEntry instanceof JSONArray) {
					wEntry = cloneJsonArray((JSONArray) wEntry);
				}
				wNewJsonArray.put(wEntry);
				wIdx++;
			} catch (Exception e) {
				throw new CXJsonException(e, "Unable to put the [%s] value  in the cloned JsonArray", wIdx);
			}
		}
		return wNewJsonArray;
	}

	/**
	 * @param aJSONObject
	 * @return
	 */
	public static JSONObject cloneJsonObj(final JSONObject aJSONObject) throws CXJsonException {
		JSONObject wNewJsonObj = new JSONObject();
		for (Entry<String, Object> wEntry : aJSONObject.entrySet()) {
			try {
				Object wValue = wEntry.getValue();
				if (wValue instanceof JSONObject) {
					wValue = cloneJsonObj((JSONObject) wValue);
				} else if (wValue instanceof JSONArray) {
					wValue = cloneJsonArray((JSONArray) wValue);
				}
				wNewJsonObj.put(wEntry.getKey(), wValue);
			} catch (Exception e) {
				throw new CXJsonException(e, "Unable to put value of [%s] in the cloned JsonOject", wEntry.getKey());
			}
		}
		return wNewJsonObj;
	}

	/**
	 * @param aObjects
	 * @return
	 */
	public static JSONArray newJsonArray(final Object... aObjects) {
		JSONArray wJsonArray = new JSONArray();

		for (Object wObj : aObjects) {
			wJsonArray.put(wObj);
		}
		return wJsonArray;
	}

	/**
	 * @param aKeyVals
	 * @return
	 */
	public static JSONObject newJsonObj(final Object[]... aKeyVals) {

		JSONObject wJsonObj = new JSONObject();

		for (Object[] wKeyVal : aKeyVals) {

			CXJsonUtils.putInObj(wJsonObj, wKeyVal[0].toString(), wKeyVal[1]);
		}
		return wJsonObj;
	}

	/**
	 * @param aKey
	 * @param aValue
	 * @return
	 */
	public static Object[] newKeyVal(final String aKey, final Object aValue) {
		return new Object[] { aKey, aValue };
	}

	/**
	 * @param aJSONObject
	 * @param aKey
	 * @param aValue
	 * @return
	 * @throws CXJsonException
	 * @throws
	 */
	public static JSONObject putInObj(final JSONObject aJSONObject, final String aKey, final Object aValue)
			throws CXJsonException {
		try {
			return aJSONObject.put(aKey, aValue);
		} catch (JSONException e) {
			throw new CXJsonException(e, "Unable to put the key value '%s'=[%s]", aKey, aValue);
		}
	}

}
