package org.cohorte.utilities.json.provider;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptException;

import org.psem2m.utilities.CXStringUtils;
import org.psem2m.utilities.json.JSONArray;
import org.psem2m.utilities.json.JSONException;
import org.psem2m.utilities.json.JSONObject;

import de.christophkraemer.rhino.javascript.RhinoScriptEngine;

/**
 * class that resolve ternary expression. it used for the following syntax
 *
 * <pre>
 * $expression ? $result_if_true : $result_if_false;
 * e.g
 * ${test} == 1 ? mysql : postgres;
 * with ${test} is a variable.
 * </pre>
 *
 * @author apisu
 *
 */
public class CJsonResolvTernary {

	private static final Pattern sPattern = Pattern
			.compile("\\(([\\=|<|>|\\/|\\[|\\]|<=|>=|\\!|\\.|'|\\$|\\(|\\)|\\s|\\w|\\{|\\}\\-]*)\\)\\s*\\?([\\s|\\[|\\]|\\$|\\/|\\{|\\}|\\w|\\.|\\\\\\\\\"|'||\\(|\\)|\\-]*):([\\s|\\[|\\]|\\$|\\w|\\\\\\\\\"|'|\\.|\\/||\\{|\\}|\\(|\\)|\\-]*);");

	public static Object resultTernary(final Object aContent,
			final RhinoScriptEngine wRhinoScriptEngine) throws ScriptException,
			JSONException {
		String wResult = aContent.toString();
		Matcher wMatcher = sPattern.matcher(wResult);
		while (wMatcher.find()) {
			// retrieve 3 groupe that conrespond to the condition then the true
			// result and false result
			String wCondition = wMatcher.group(1);

			String wTrueResult = wMatcher.group(2);
			String wFalseResult = wMatcher.group(3);

			String wFullMatch = wMatcher.group(0);
			// apply it only if all variable are resolved
			Object wCondResult;

			wCondResult = wRhinoScriptEngine.eval(wCondition);

			if (wCondResult instanceof Boolean
					&& ((Boolean) wCondResult).booleanValue()) {
				try {
					String wTrueResolved = wRhinoScriptEngine.eval(wTrueResult)
							.toString();
					wResult = wResult.replace(wFullMatch, wTrueResolved);

				} catch (Exception e) {
					// no an expression to evaluate . it's not
					// javascript
					wResult = wResult.replace(wFullMatch, wTrueResult);

				}
			} else {
				try {
					String wFalseResolved = wRhinoScriptEngine.eval(
							wFalseResult).toString();
					wResult = wResult.replace(wFullMatch, wFalseResolved);

				} catch (Exception e) {
					wResult = wResult.replace(wFullMatch, wFalseResult);
				}
			}

		}
		if (aContent instanceof JSONObject) {
			return new JSONObject(wResult);
		} else if (aContent instanceof JSONArray) {
			return new JSONArray(wResult);
		} else {
			return wResult;

		}

	}

	/**
	 * return the new string witht e ternary resolved
	 *
	 * @param aContent
	 * @return
	 */
	public static String resultTernary(final String aContent,
			final RhinoScriptEngine wRhinoScriptEngine) throws ScriptException {
		String wResult = aContent;
		Matcher wMatcher = sPattern.matcher(aContent);
		while (wMatcher.find()) {
			// retrieve 3 groupe that conrespond to the condition then the true
			// result and false result
			String wCondition = wMatcher.group(1);

			String wTrueResult = wMatcher.group(2);
			String wFalseResult = wMatcher.group(3);

			String wFullMatch = wMatcher.group(0);
			// apply it only if all variable are resolved
			Object wCondResult;

			wCondResult = wRhinoScriptEngine.eval(wCondition);

			if (wCondResult instanceof Boolean
					&& ((Boolean) wCondResult).booleanValue()) {
				try {
					String wTrueResolved = wRhinoScriptEngine.eval(wTrueResult)
							.toString();
					if (CXStringUtils.isFloat(wTrueResult)
							|| CXStringUtils.isNumeric(wTrueResult)) {
						wResult = wResult.replace("\"" + wFullMatch + "\"",
								wTrueResult);
					} else {
						wResult = wResult.replace(wFullMatch, wTrueResolved);

					}
				} catch (Exception e) {
					// no an expression to evaluate . it's not
					// javascript
					if (CXStringUtils.isFloat(wTrueResult)
							|| CXStringUtils.isNumeric(wTrueResult)) {
						wResult = wResult.replace("\"" + wFullMatch + "\"",
								wTrueResult);

					} else {
						wResult = wResult.replace(wFullMatch, wTrueResult);

					}

				}
			} else {
				try {
					String wFalseResolved = wRhinoScriptEngine.eval(
							wFalseResult).toString();
					wResult = wResult.replace(wFullMatch, wFalseResolved);

				} catch (Exception e) {
					wResult = wResult.replace(wFullMatch, wFalseResult);
				}
			}

		}

		return wResult;
	}
}
