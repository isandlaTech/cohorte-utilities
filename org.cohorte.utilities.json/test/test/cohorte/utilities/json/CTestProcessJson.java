package org.cohorte.utilities.json;

import java.io.File;
import java.nio.charset.Charset;

import junit.framework.TestCase;

import org.cohorte.utilities.json.provider.CJsonProvider;
import org.cohorte.utilities.json.provider.CJsonRsrcResolver;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.psem2m.utilities.files.CXFileText;
import org.psem2m.utilities.json.JSONObject;
import org.psem2m.utilities.logging.CActivityLoggerNull;
import org.psem2m.utilities.rsrc.CXRsrcProviderFile;

/**
 * unit test that valid the preprocess of a JSON file that remove comment and
 * include sub content depending of the tag
 *
 * @author apisu
 *
 */

@RunWith(Theories.class)
public class CTestProcessJson extends TestCase {

	public static String fileTestsIn = System.getProperty("user.dir")
			+ File.separatorChar + "files" + File.separatorChar
			+ "testPreprocess" + File.separatorChar + "in" + File.separatorChar;
	public static String fileTestsIn2 = System.getProperty("user.dir")
			+ File.separatorChar + "files" + File.separatorChar
			+ "testPreprocess" + File.separatorChar + "in2"
			+ File.separatorChar;

	public static String fileTestsOut = System.getProperty("user.dir")
			+ File.separatorChar + "files" + File.separatorChar
			+ "testPreprocess" + File.separatorChar + "out"
			+ File.separatorChar;

	private static CJsonProvider pProvider;

	public static @DataPoints String[][] testFiles = {
			{ "module_empty", "empty" }, { "module_noComment", "noComment" },
			{ "module_slashComment", "noComment" },
			{ "module_slashStarComment", "noComment" },
			{ "module_allComment", "noComment" },
			{ "module_testDef", "testDef" },
			{ "module_allCommentAndFile", "noComment2" },
			{ "module_allMultiPath", "noCommentMutliPath" } };

	@BeforeClass
	public static void setup() {
		try {
			CJsonRsrcResolver wResolver = new CJsonRsrcResolver();
			wResolver.addRsrcProvider("$file", new CXRsrcProviderFile(
					fileTestsIn, Charset.defaultCharset()));
			wResolver.addRsrcProvider("$file", new CXRsrcProviderFile(
					fileTestsIn2, Charset.defaultCharset()));

			pProvider = new CJsonProvider(wResolver,
					CActivityLoggerNull.getInstance());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testPreprocessCheckJson() {
		boolean wThrowable = false;
		try {

			pProvider.getJSONObject("$file", "badJson.js");

		} catch (Exception e) {
			wThrowable = true;
		}
		assertEquals(wThrowable, true);

		try {
			wThrowable = false;

			pProvider.getJSONObject("$file", "badJsonInclude.js");

		} catch (Exception e) {
			wThrowable = true;
		}
		assertEquals(wThrowable, true);
	}

	/**
	 * test preprocess json to remove all kind of comment
	 */
	@Theory
	public void testPreprocessJson(final String[] testFiles) {
		boolean wThrowable = false;
		try {

			CXFileText wFileOut = new CXFileText(fileTestsOut
					+ File.separatorChar + testFiles[1] + ".js");
			JSONObject in = pProvider.getJSONObject("$file", testFiles[0]
					+ ".js");

			JSONObject out = new JSONObject(wFileOut.readAll());
			System.out.println("------");

			System.out.println(in.toString());
			System.out.println(out.toString());

			assertEquals(in.toString(), out.toString());

		} catch (Exception e) {
			e.printStackTrace();
			wThrowable = true;
		}
		assertEquals(wThrowable, false);

	}
}
