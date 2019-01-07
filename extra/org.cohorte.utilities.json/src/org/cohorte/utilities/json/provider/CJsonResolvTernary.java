package org.cohorte.utilities.json.provider;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptException;

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
			.compile("(.*\\s*\\=.*\\s*)\\?(.*\\s*)\\:(.*\\s*);");

	/**
	 * return the new string witht e ternary resolved
	 *
	 * @param aContent
	 * @return
	 */
	public static String resultTernary(final String aContent,
			final RhinoScriptEngine wRhinoScriptEngine) {
		String wResult = aContent;
		Matcher wMatcher = sPattern.matcher(aContent);
		while (wMatcher.find()) {
			// retrieve 3 groupe that conrespond to the condition then the true
			// result and false result
			String wCondition = wMatcher.group(0);

			String wTrueResult = wMatcher.group(0);
			String wFalseResult = wMatcher.group(0);

			String wFullMatch = wCondition + "?" + wTrueResult + ":"
					+ wFalseResult;
			if (!wFullMatch.contains("${")) {
				// apply it only if all variable are resolved
				Object wCondResult;
				try {
					wCondResult = wRhinoScriptEngine.eval(wCondition);

					if (wCondResult instanceof Boolean) {
						String wTrueResolved = wRhinoScriptEngine.eval(
								wTrueResult).toString();
						wResult.replace(wFullMatch, wTrueResolved);
					} else {
						String wFalseResolved = wRhinoScriptEngine.eval(
								wFalseResult).toString();
						wResult.replace(wFullMatch, wFalseResolved);
					}
				} catch (ScriptException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return wResult;
	}
}
