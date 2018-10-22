package test.cohorte.utilities.json;

import java.io.File;
import java.nio.charset.Charset;

import org.cohorte.utilities.json.provider.CJsonProvider;
import org.cohorte.utilities.json.provider.CJsonRsrcResolver;
import org.cohorte.utilities.json.provider.rsrc.CXRsrcGeneratorProvider;
import org.cohorte.utilities.json.provider.rsrc.CXRsrcTextFileProvider;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.psem2m.utilities.files.CXFileText;
import org.psem2m.utilities.json.JSONObject;
import org.psem2m.utilities.logging.CActivityLoggerBasicConsole;
import org.psem2m.utilities.logging.CActivityLoggerNull;
import org.psem2m.utilities.rsrc.CXRsrcProviderFile;

import junit.framework.TestCase;

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

			{ "module_empty.js", "empty.js" },
			{ "module_noComment.js", "noComment.js" },
			{ "module_slashComment.js", "noComment.js" },
			{ "module_slashStarComment.js", "noComment.js" },
			{ "module_allComment.js", "noComment.js" },
			{ "module_testDef.js", "testDef.js" },

			{ "module_allCommentAndFile.js", "noComment2.js" },
			{ "module_allCommentAndFileWithPath.js", "noComment2.js" },

			{ "module_allMultiPath.js", "noCommentMutliPath.js" },

			{ "test_condition.js?var=test", "test_condition_true.js" },

			{ "test_condition.js?var=other", "test_condition_false.js" },

			{ "test_jsonpath_grandfather.js", "test_jsonpath_grandfather.js" },

			{ "deploy_world.js", "deploy_world.js" },
			{
					"deploy_world.js?deploy.subdomain=grandest&deploy.ip=80.80.80.80",
					"deploy_world_with_properties.js" },
			{ "test_replace_vars.js?var=test", "test_replace_vars.js" },

			{ "test_array_of_array.js", "test_array_of_array.js" },
			{ "test_include_file_text.js", "test_include_file_text.js" },

			{ "test_jsonpath_condition.js", "test_jsonpath_condition.js" } };

	@BeforeClass
	public static void setup() {
		try {

			CJsonRsrcResolver wResolver = new CJsonRsrcResolver();
			wResolver.addRsrcProvider(
					"$generator",
					new CXRsrcGeneratorProvider(CActivityLoggerNull
							.getInstance()));
			wResolver.addRsrcProvider("$textFile", new CXRsrcTextFileProvider(
					fileTestsIn, CActivityLoggerNull.getInstance()));
			wResolver.addRsrcProvider("$file", new CXRsrcProviderFile(
					fileTestsIn, Charset.defaultCharset()));
			wResolver.addRsrcProvider("$file", new CXRsrcProviderFile(
					fileTestsIn2, Charset.defaultCharset()));

			pProvider = new CJsonProvider(wResolver,
					CActivityLoggerBasicConsole.getInstance());
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
					+ File.separatorChar + testFiles[1]);
			JSONObject in = pProvider.getJSONObject("$file", testFiles[0]);

			JSONObject out = new JSONObject(wFileOut.readAll());
			System.out.println("------");
			System.out.println("in = " + testFiles[0]);
			System.out.println("out = " + testFiles[1]);

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
