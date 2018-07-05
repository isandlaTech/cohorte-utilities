package org.cohorte.utilities.json.provider.rsrc;

import java.nio.charset.Charset;
import java.util.Arrays;
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

	private final Pattern pPatternJsonPath = Pattern.compile(
			"\\$(\\(\\^?(\\.|\\/)*\\))?(\\.\\w*(\\[\\d\\])?)*",
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
	private JSONObject applyGenerator(final JSONObject aGenerator,
			final List<JSONObject> aListOfFatherJson) throws JSONException {
		JSONObject wApplied = new JSONObject();
		for (String aProp : aGenerator.keySet()) {
			Object wValue = aGenerator.opt(aProp);
			if (wValue instanceof String) {
				String wReplaceValue = (String) wValue;
				Matcher wMatcher = pPatternJsonPath.matcher(wReplaceValue);
				while (wMatcher.find()) {
					String wMatch = wMatcher.group();
					pActivityLogger.logInfo(this, "applyGenerator",
							"jsonPath found jsonPath=[%s]", wMatch);

					// preprocess the match to see on which father to apply the
					// jsonpath
					String wObj = "";
					if (aListOfFatherJson == null) {
						pActivityLogger.logInfo(this, "applyGenerator",
								"no father, apply on current object");
						// can't be applied no father setted
						wObj = applyJsonPath(aGenerator.toString(), wMatch);
					} else {
						Pair<String, JSONObject> wTuple = getFather(wMatch,
								aListOfFatherJson);
						wObj = applyJsonPath(wTuple.getValue1().toString(),
								wTuple.getValue0());

					}
					wReplaceValue = wReplaceValue.replace(wMatch, wObj);

				}
				wApplied.put(aProp, wReplaceValue);
			} else if (wValue instanceof JSONObject) {
				wApplied.put(aProp,
						applyGenerator((JSONObject) wValue, aListOfFatherJson));

			} else if (wValue instanceof JSONArray) {
				JSONArray wArrayValue = (JSONArray) wValue;
				JSONArray wReplaceArrayValue = new JSONArray();
				for (int i = 0; i < wArrayValue.length(); i++) {
					Object wElem = wArrayValue.opt(i);
					if (wElem instanceof JSONObject) {
						wReplaceArrayValue.put(applyGenerator(
								(JSONObject) wElem, aListOfFatherJson));
					} else if (wElem instanceof String) {
						Matcher wMatcher = pPatternJsonPath
								.matcher((String) wElem);
						boolean wHasMatch = false;
						while (wMatcher.find()) {
							wHasMatch = true;
							String wMatch = wMatcher.group();
							Pair<String, JSONObject> wTuple = getFather(wMatch,
									aListOfFatherJson);
							wReplaceArrayValue
									.put(applyJsonPath(wTuple.getValue1()
											.toString(), wTuple.getValue0()));
						}
						if (!wHasMatch) {// we add the current value
							wReplaceArrayValue.put(wElem);
						}
					}
				}
				wApplied.put(aProp, wReplaceArrayValue);
			} else {
				wApplied.put(aProp, wValue);
			}
		}
		return wApplied;
	}

	private String applyJsonPath(final String aJson, final String aJsonPath) {
		Object wObj = JsonPath.read(aJson, aJsonPath);
		return wObj.toString();
	}

	@Override
	public CXRsrcProvider clone() {
		CXRsrcGeneratorProvider wRsrc = new CXRsrcGeneratorProvider(
				pActivityLogger);
		return wRsrc;
	}

	@Override
	protected boolean existsFulPath(final CXRsrcUriPath aPath) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * return the father that match the path defined
	 *
	 * @param aJsonPathWithFather
	 *            : jsonpath with dscription of father using relative path
	 *
	 * @param aListOfFather
	 * @return a Tuple of the real JsonPath and JSONObject of the father on
	 *         which it should be applied
	 */
	private Pair<String, JSONObject> getFather(
			final String aJsonPathWithFather,
			final List<JSONObject> aListOfFather) {
		pActivityLogger.logDebug(this, "getFatherFromPath",
				"return root father for path=[%s]");
		Pair<String, String> wTuple = getPathFather(aJsonPathWithFather);
		String wPath = wTuple.getValue1();

		if (wPath == null) {
			// use father
			return Pair.with(wTuple.getValue0(),
					aListOfFather.get(aListOfFather.size() - 1));
		} else if (wPath.equals("^.")) {
			// want root

			return Pair.with(wTuple.getValue0(), aListOfFather.get(0));
		} else {
			// count how many ../ is present
			int wFatherOffset = 0; // father offset from the end

			if (wPath.contains("/")) {
				wFatherOffset = Arrays.asList(wPath.split("/")).size();
			}
			if (aListOfFather.size() - wFatherOffset >= 0) {

				return Pair.with(wTuple.getValue0(),
						aListOfFather.get(wFatherOffset));
			} else {
				pActivityLogger.logWarn(this, "getFatherFromPath",
						"relative path too long, return the root element");
				return Pair.with(wTuple.getValue0(), aListOfFather.get(0));
			}

		}
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
			pActivityLogger.logInfo(this, "applyGenerator", "preprocess ",
					aJsonPath);
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
	 * we receive the generator object and need to generate an new object
	 * variable resolution
	 */
	@Override
	public CXRsrcText rsrcReadTxt(final String aGeneratorContent)
			throws Exception {
		JSONObject wGeneratorContent = new JSONObject(aGeneratorContent);
		JSONObject wAppliedContent = applyGenerator(wGeneratorContent, null);

		return new CXRsrcText(new CXRsrcUriPath(""),
				CXRsrcTextReadInfo.newInstanceFromString(wAppliedContent
						.toString()));
	}

	public CXRsrcText rsrcReadTxt(final String aGeneratorContent,
			final List<JSONObject> aListFather) throws Exception {
		JSONObject wGeneratorContent = new JSONObject(aGeneratorContent);
		JSONObject wAppliedContent = applyGenerator(wGeneratorContent,
				aListFather);

		return new CXRsrcText(new CXRsrcUriPath(""),
				CXRsrcTextReadInfo.newInstanceFromString(wAppliedContent
						.toString()));
	}

	@Override
	public String urlGetAddress() {
		// TODO Auto-generated method stub
		return null;
	}

}
