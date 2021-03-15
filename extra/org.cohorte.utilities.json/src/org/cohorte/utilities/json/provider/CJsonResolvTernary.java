package org.cohorte.utilities.json.provider;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.psem2m.utilities.CXStringUtils;
import org.psem2m.utilities.json.JSONException;
import org.psem2m.utilities.logging.IActivityLogger;

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

	private static final Pattern sPattern = Pattern.compile(
			"\\(([\\=|<|>|\\/|\\[|\\]|<=|>=|\\!|\\.|:|'|\\$|\\(|\\)|\\s|\\w|\\{|\\}\\-]*)\\)\\s*\\?([\\s|\\[|\\]|:|\\$|\\/|\\{|\\}|\\w|\\.|\\\\\\\\\\\"|'||\\(|\\)|\\-]*):([\\s|\\[|\\]|\\$|\\w|\\\\\\\\\\\"|'|\\.|\\/||\\{|\\}|\\(|\\)|\\-]*);");

	public static Object resultTernary(final IActivityLogger aLogger, final Object aContent,
			final RhinoScriptEngine wRhinoScriptEngine) throws JSONException {
		return resultTernary(aLogger, aContent.toString(), wRhinoScriptEngine);

	}

	/**
	 * return the new string witht e ternary resolved
	 *
	 * @param aContent
	 * @return
	 */
	public static String resultTernary(final IActivityLogger aLogger, final String aContent,
			final RhinoScriptEngine wRhinoScriptEngine) throws JSONException {
		String wResult = aContent;
		final Matcher wMatcher = sPattern.matcher(aContent);
		while (wMatcher.find()) {
			// retrieve 3 groupe that conrespond to the condition then the true
			// result and false result
			final String wCondition = wMatcher.group(1);

			final String wTrueResult = wMatcher.group(2);
			final String wFalseResult = wMatcher.group(3);

			final String wFullMatch = wMatcher.group(0);
			// apply it only if all variable are resolved
			Object wCondResult;
			try {
				wCondResult = wRhinoScriptEngine.eval(wCondition);

				if (wCondResult instanceof Boolean && ((Boolean) wCondResult).booleanValue()) {
					try {
						final String wTrueResolved = wRhinoScriptEngine.eval(wTrueResult).toString();
						if (CXStringUtils.isFloat(wTrueResult) || CXStringUtils.isNumeric(wTrueResult)) {
							wResult = wResult.replace("\"" + wFullMatch + "\"", wTrueResult);
						} else {
							wResult = wResult.replace(wFullMatch, wTrueResolved);

						}
					} catch (final Exception e) {
						// no an expression to evaluate . it's not
						// javascript
						if (CXStringUtils.isFloat(wTrueResult) || CXStringUtils.isNumeric(wTrueResult)) {
							wResult = wResult.replace("\"" + wFullMatch + "\"", wTrueResult);

						} else {
							wResult = wResult.replace(wFullMatch, wTrueResult);

						}

					}
				} else {
					try {
						final String wFalseResolved = wRhinoScriptEngine.eval(wFalseResult).toString();
						if (CXStringUtils.isFloat(wFalseResult) || CXStringUtils.isNumeric(wFalseResult)) {
							wResult = wResult.replace("\"" + wFullMatch + "\"", wFalseResult);
						} else {
							wResult = wResult.replace(wFullMatch, wFalseResolved);
						}
					} catch (final Exception e) {
						if (CXStringUtils.isFloat(wFalseResult) || CXStringUtils.isNumeric(wFalseResult)) {
							wResult = wResult.replace("\"" + wFullMatch + "\"", wFalseResult);
						} else {
							wResult = wResult.replace(wFullMatch, wFalseResult);
						}
					}
				}
			} catch (final Exception e) {
				// can't resolve ternary expression do nothin
				aLogger.logInfo(CJsonResolvTernary.class, "resultTernary", "fail to evaluate condition %s", wCondition);

			}
		}

		return wResult;
	}
}
