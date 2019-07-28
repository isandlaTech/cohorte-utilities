package test.cohorte.utilities.filter;

import java.io.File;

import org.cohorte.utilities.filter.expression.CExpression;
import org.cohorte.utilities.filter.parser.CParser;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.psem2m.utilities.files.CXFileText;
import org.psem2m.utilities.json.JSONObject;

import junit.framework.TestCase;

@RunWith(Theories.class)
public class CTestParser extends TestCase {

	public static String fileTestsIn = System.getProperty("user.dir") + File.separatorChar + "files"
			+ File.separatorChar + "testParser" + File.separatorChar + "in" + File.separatorChar;

	public static String fileTestsOut = System.getProperty("user.dir") + File.separatorChar + "files"
			+ File.separatorChar + "testParser" + File.separatorChar + "out" + File.separatorChar;

	public static @DataPoints String[][] testFiles = {
			//
			{ "filter_empty.js", "filter_empty.out" },
			//
			{ "filter_bad_operator.js", "filter_bad_operator.out" },
			//
			{ "filter_eq.js", "filter_eq.out" },
			//
			{ "filter_and.js", "filter_and.out" }, };

	/**
	 * test preprocess json to remove all kind of comment
	 */
	@Theory
	public void testParser(final String[] testFiles) {
		boolean wThrowable = false;
		try {
			CXFileText wFileIn = new CXFileText(fileTestsIn + File.separatorChar + testFiles[0]);

			CXFileText wFileOut = new CXFileText(fileTestsOut + File.separatorChar + testFiles[1]);
			String wOut = wFileOut.readAll();
			JSONObject in = new JSONObject(wFileIn.readAll());
			System.out.println("------");
			System.out.println("in = " + testFiles[0]);
			System.out.println("out = " + testFiles[1]);
			System.out.println("------");

			CExpression wExpression = CParser.parse(in);

			if (wExpression == null && wOut.isEmpty()) {
				assertTrue(true);
			} else {
				System.out.println(wExpression.toString());
				System.out.println(wOut.toString());

				assertEquals(wExpression.toString(), wOut.toString());
				System.out.println("TEST OK");
			}
		} catch (Exception e) {
			if (testFiles[0].equals("filter_bad_operator.js")) {
				System.out.println(String.format("MESSAGE: %s", e.getMessage()));
				System.out.println("TEST OK");
			}
			//
			else {

				wThrowable = true;
			}
		}
		assertEquals(wThrowable, false);

	}

}
