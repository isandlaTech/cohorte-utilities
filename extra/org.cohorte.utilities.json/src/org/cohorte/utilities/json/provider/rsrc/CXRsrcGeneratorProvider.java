package org.cohorte.utilities.json.provider.rsrc;

import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.javatuples.Pair;
import org.psem2m.utilities.json.JSONArray;
import org.psem2m.utilities.json.JSONException;
import org.psem2m.utilities.json.JSONObject;
import org.psem2m.utilities.logging.IActivityLogger;
import org.psem2m.utilities.rsrc.CXRsrcProvider;
import org.psem2m.utilities.rsrc.CXRsrcText;
import org.psem2m.utilities.rsrc.CXRsrcTextReadInfo;
import org.psem2m.utilities.rsrc.CXRsrcUriPath;

import com.jayway.jsonpath.JsonPath;

/**
 * provider that match with $template tag. this provide transforme the content
 * of the tag $template: {....} to a new content without reading anything. it's
 * not a include file/memory or http provide.
 *
 * @author apisu
 *
 */
public class CXRsrcGeneratorProvider extends CXRsrcProvider {

	private final IActivityLogger pActivityLogger;

	private final Pattern pPatternConditionJsonPath = Pattern.compile("\\[.*=.*\\]");

	private final Pattern pPatternJsonPath = Pattern.compile(
			"(\\$(\\.(\\[?[^\\s:&,$\\/<\"'-]*\\]?))+)|(\\$(\\((\\.|\\/|\\^)+\\)(\\[?[^\\s:&,$\\/<\"'-]*\\]?))+)",
			Pattern.MULTILINE);

	public CXRsrcGeneratorProvider(final IActivityLogger aLogger) {
		super(Charset.defaultCharset());
		pActivityLogger = aLogger;
	}

	/**
	 * replace the jsonPath for a generator
	 *
	 * @return
	 * @throws JSONException
	 */
	private JSONObject applyGenerator(final JSONObject aGenerator, final List<JSONObject> aListOfFatherJson)
			throws JSONException {
		JSONObject wApplied = new JSONObject();
		for (String aProp : aGenerator.keySet()) {
			Object wValue = aGenerator.opt(aProp);
			applyGeneratorProperties(wApplied, aProp, aGenerator, wValue, aListOfFatherJson);
		}
		return wApplied;
	}

	private void applyGeneratorProperties(final JSONObject aApplied, final String aProp, final JSONObject aGenerator,
			final Object aValue, final List<JSONObject> aListOfFatherJson) throws JSONException {
		if (aValue instanceof String) {
			String wReplaceValue = (String) aValue;
			Matcher wMatcher = pPatternJsonPath.matcher(wReplaceValue);
			while (wMatcher.find()) {
				String wMatch = wMatcher.group();

				pActivityLogger.logDebug(this, "applyGenerator", "jsonPath found jsonPath=[%s]", wMatch);

				// preprocess the match to see on which father to apply the
				// jsonpath
				String wObj = "";
				if (aListOfFatherJson == null) {
					pActivityLogger.logDebug(this, "applyGenerator", "no father, apply on current object");
					// can't be applied no father setted
					wObj = applyJsonPath(aGenerator.toString(), wMatch);
				} else {
					Pair<String, JSONObject> wTuple = getFather(wMatch, aListOfFatherJson);
					wObj = applyJsonPath(wTuple.getValue1().toString(), wTuple.getValue0());

				}
				wReplaceValue = wReplaceValue.replace(wMatch, wObj);
				wMatcher = pPatternJsonPath.matcher(wReplaceValue);

			}
			aApplied.put(aProp, wReplaceValue);
		} else if (aValue instanceof JSONObject) {
			aApplied.put(aProp, applyGenerator((JSONObject) aValue, aListOfFatherJson));

		} else if (aValue instanceof JSONArray) {
			JSONArray wArrayValue = (JSONArray) aValue;
			JSONArray wReplaceArrayValue = new JSONArray();
			for (int i = 0; i < wArrayValue.length(); i++) {
				Object wElem = wArrayValue.opt(i);
				if (wElem instanceof JSONObject) {
					wReplaceArrayValue.put(applyGenerator((JSONObject) wElem, aListOfFatherJson));
				} else if (wElem instanceof JSONArray) {
					applyGeneratorProperties(aApplied, aProp, aGenerator, wElem, aListOfFatherJson);
				} else if (wElem instanceof String) {
					Matcher wMatcher = pPatternJsonPath.matcher((String) wElem);
					boolean wHasMatch = false;
					String wReplaceString = (String) wElem;
					while (wMatcher.find()) {
						wHasMatch = true;
						String wMatch = wMatcher.group();
						Pair<String, JSONObject> wTuple = getFather(wMatch, aListOfFatherJson);
						wReplaceString = wReplaceString.replace(wMatch,
								applyJsonPath(wTuple.getValue1().toString(), wTuple.getValue0()));
					}
					if (!wHasMatch) {// we add the current value
						wReplaceArrayValue.put(wElem);
					} else {
						wReplaceArrayValue.put(wReplaceString);

					}
				}
			}
			aApplied.put(aProp, wReplaceArrayValue);
		} else {
			aApplied.put(aProp, aValue);
		}
	}

	/**
	 * apply json path with a Json in input manage in addition of JSON Path the
	 * condition in [id=value] to retrieve the correct obj to apply. this
	 *
	 * @param aJson
	 * @param aJsonPath
	 * @return
	 */
	private String applyJsonPath(final String aJson, final String aJsonPath) {
		try {
			String wJsonPath = aJsonPath;
			// check if we have [id=value] and replace it by the index of the
			// array
			Matcher wMatcher = pPatternConditionJsonPath.matcher(aJsonPath);
			while (wMatcher.find()) {
				String wCond = wMatcher.group();
				int wIndexCond = wJsonPath.indexOf(wCond);
				Object wObj = JsonPath.read(aJson, aJsonPath.substring(0, wIndexCond));
				if (wObj.toString().startsWith("[")) {
					JSONArray wArr = new JSONArray(wObj.toString());
					wCond = wCond.substring(1, wCond.length() - 1);
					String[] wCondSpl = wCond.split("=");
					int wSubJsonIndex = findJSONObject(wCondSpl[0], wCondSpl[1], wArr);
					wJsonPath = wJsonPath.replace(wCond, Integer.toString(wSubJsonIndex));
				}
			}
			Object wObj = JsonPath.read(aJson, wJsonPath);

			return wObj != null ? wObj.toString() : "";

		} catch (Exception e) {
			pActivityLogger.logWarn(this, "applyJsonPath", "jsonPath can't %s be apply ! return empty string",
					aJsonPath);
			return "";
		}
	}

	@Override
	public CXRsrcProvider clone() {
		CXRsrcGeneratorProvider wRsrc = new CXRsrcGeneratorProvider(pActivityLogger);
		return wRsrc;
	}

	@Override
	protected boolean existsFulPath(final CXRsrcUriPath aPath) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * find in the JSON Array the first JSON bject that match the the key with the
	 * value in parameter e.g [{id:test},{id:toto}] with key = id and value = toto
	 * return the index of the JSONObject or 0
	 *
	 * @param aKey
	 *            : a json property name of the json
	 * @param aKey
	 *            : a value associate to the key
	 * @param aArr
	 *            : a json array
	 * @return
	 */
	private int findJSONObject(final String aKey, final String aValue, final JSONArray aArr) {
		int wFound = 0;
		for (int i = 0; aArr != null && i < aArr.length() && wFound == 0; i++) {
			JSONObject wItem = aArr.optJSONObject(i);
			if (wItem != null) {
				Object wKey = wItem.opt(aKey);
				if (wKey != null && wKey.toString().equals(aValue)) {
					wFound = i;
				}
			}
		}
		return wFound;
	}

	@Override
	protected String getDirAbsPathDirectory(final CXRsrcUriPath aPath) {

		return aPath.getFullPath();
	}

	/**
	 * return the father that match the path defined
	 *
	 * @param aJsonPathWithFather
	 *            : jsonpath with dscription of father using relative path
	 *
	 * @param aListOfFather
	 * @return a Tuple of the real JsonPath and JSONObject of the father on which it
	 *         should be applied
	 */
	private Pair<String, JSONObject> getFather(final String aJsonPathWithFather, final List<JSONObject> aListOfFather) {
		pActivityLogger.logDebug(this, "getFatherFromPath", "return root father for path=[%s]");
		Pair<String, String> wTuple = getPathFather(aJsonPathWithFather);
		String wPath = wTuple.getValue1();

		if (wPath == null) {
			// use father
			return Pair.with(wTuple.getValue0(), aListOfFather.get(aListOfFather.size() - 1));
		} else if (wPath.equals("$(^.)")) {
			// want root

			return Pair.with(wTuple.getValue0(), aListOfFather.get(0));
		} else {
			// count how many ../ is present
			int wFatherOffset = 0; // father offset from the end
			wPath = wPath.substring(2, wPath.length() - 1).replaceAll("/", "");
			wFatherOffset = wPath.length() / 2;

			if (aListOfFather.size() - wFatherOffset >= 0) {

				return Pair.with(wTuple.getValue0(), aListOfFather.get((aListOfFather.size() - 1) - wFatherOffset));
			} else {
				pActivityLogger.logWarn(this, "getFatherFromPath", "relative path too long, return the root element");
				return Pair.with(wTuple.getValue0(), aListOfFather.get(0));
			}

		}
	}

	@Override
	protected List<String> getListPathDirectory(final CXRsrcUriPath aPath, final Pattern aPattern) {
		return null;
	}

	/**
	 * return the value of the father path wanted
	 *
	 * @param aJsonPath
	 * @return a pair of JSonPath and a father path
	 */
	private Pair<String, String> getPathFather(final String aJsonPath) {
		int wIdxStartFather = aJsonPath.indexOf("$(");
		if (wIdxStartFather != -1) {
			pActivityLogger.logInfo(this, "applyGenerator", "preprocess ", aJsonPath);
			// we want a specific father
			int wIdxEndFather = aJsonPath.indexOf(".)") + 2;

			return Pair.with("$" + aJsonPath.substring(wIdxEndFather),
					aJsonPath.substring(wIdxStartFather, wIdxEndFather));

		} else {
			return Pair.with(aJsonPath, null);
		}
	}

	@Override
	public boolean isLocal() {
		return true;
	}

	/**
	 * we receive the generator object and need to generate an new object variable
	 * resolution
	 */
	@Override
	public CXRsrcText rsrcReadTxt(final String aGeneratorContent) throws Exception {
		JSONObject wGeneratorContent = new JSONObject(aGeneratorContent);
		JSONObject wAppliedContent = applyGenerator(wGeneratorContent, null);
		if (wAppliedContent.has("cond")) {
			wAppliedContent.remove("cond");
		}
		return new CXRsrcText(new CXRsrcUriPath(""),
				CXRsrcTextReadInfo.newInstanceFromString(wAppliedContent.toString()));
	}

	public CXRsrcText rsrcReadTxt(final String aGeneratorContent, final List<JSONObject> aListFather) throws Exception {
		JSONObject wGeneratorContent = new JSONObject(aGeneratorContent);
		JSONObject wAppliedContent = applyGenerator(wGeneratorContent, aListFather);
		if (wAppliedContent.has("cond")) {
			wAppliedContent.remove("cond");
		}
		return new CXRsrcText(new CXRsrcUriPath(""),
				CXRsrcTextReadInfo.newInstanceFromString(wAppliedContent.toString()));
	}

	@Override
	public String urlGetAddress() {
		// TODO Auto-generated method stub
		return null;
	}

}
