package org.psem2m.utilities.json;

/**
 * <pre>
 * JSONObject wJsonObjOne = newJsonObj(
 * 		//
 * 		newKeyVal(&quot;a&quot;, &quot;valueA&quot;),
 * 		//
 * 		newKeyVal(&quot;b&quot;, 25),
 * 		//
 * 		newKeyVal(&quot;c&quot;, true),
 * 		//
 * 		newKeyVal(&quot;obj&quot;, newJsonObj(
 * 				//
 * 				newKeyVal(&quot;aa&quot;, &quot;valueAA&quot;),
 * 				//
 * 				newKeyVal(&quot;bb&quot;, 25),
 * 				//
 * 				newKeyVal(&quot;cc&quot;, true),
 * 				//
 * 
 * 				newKeyVal(&quot;array2&quot;, newJsonArray(
 * 						//
 * 						&quot;string2&quot;,
 * 						//
 * 						true,
 * 						//
 * 						91.12,
 * 						//
 * 						newJsonObj(newKeyVal(&quot;aaaa&quot;, &quot;valueAAAA&quot;)))))),
 * 		//
 * 		newKeyVal(&quot;array&quot;, newJsonArray(&quot;string1&quot;,
 * 				//
 * 				true,
 * 				//
 * 				45.56,
 * 				//
 * 				newJsonObj(newKeyVal(&quot;aaa&quot;, &quot;valueAAA&quot;)))));
 * </pre>
 *
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
 *       true,
 *       91.12,
 *       {"aaaa": "valueAAAA"}
 *     ]
 *   },
 *   "array": [
 *     "string1",
 *     true,
 *     45.56,
 *     {"aaa": "valueAAA"}
 *   ]
 * }
 * </pre>
 * 
 * @author ogattaz
 * 
 * @version cojhorte utilities 1.1.0
 */
public class JSONBuilder {

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

			JSONBuilder.putKeyValInObj(wJsonObj, wKeyVal);
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
	public static JSONObject putKeyValInObj(final JSONObject aJSONObject, final Object[] aKeyVal) throws JSONException {

		if (aKeyVal == null) {
			throw new JSONException("The given keyVal is null");
		}
		if (aKeyVal.length != 2) {
			throw new JSONException("The given keyVal isn't a pair");
		}
		if (aKeyVal[0] == null) {
			throw new JSONException("The first object of the The given keyVal is null");
		}
		if (aKeyVal[0] instanceof String == false) {
			throw new JSONException("The first object of the The given keyVal isn't a String");
		}
		return aJSONObject.put(aKeyVal[0].toString(), aKeyVal[1]);

	}

}
