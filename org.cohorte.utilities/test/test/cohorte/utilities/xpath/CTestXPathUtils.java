package test.cohorte.utilities.xpath;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.psem2m.utilities.CXPathUtils;
import org.psem2m.utilities.files.CXFileUtf8;
import org.psem2m.utilities.files.CXFileUtf8WithoutBom;

import junit.framework.TestCase;

@RunWith(Theories.class)
public class CTestXPathUtils extends TestCase {

	public static String fileTestsIn = System.getProperty("user.dir") + File.separatorChar + "files"
			+ File.separatorChar + "testXPath" + File.separatorChar + "in";
	public static String fileTestsOut = System.getProperty("user.dir") + File.separatorChar + "files"
			+ File.separatorChar + "testXPath" + File.separatorChar + "check";

	public static String fileTestsResult = System.getProperty("user.dir") + File.separatorChar + "files"
			+ File.separatorChar + "testXPath" + File.separatorChar + "result";

	/**
	 * contains a array of array for each text . each quadruplet contains the
	 * name of source xml file, the xpath and the value expected and the
	 * separator
	 */
	public static @DataPoints String[][] testFiles = {

	{ "simple.xml", "//auteur[@id='second']", "Dubois", "Bertrand", "," } };

	@BeforeClass
	public static void setup() {

	}

	@Theory
	public void testPatch(final String[] testFiles) {
		String wFileInPath = fileTestsIn + File.separatorChar + testFiles[0];
		String wFileResultPath = fileTestsResult + File.separatorChar + testFiles[0];
		String wFileCheckPath = fileTestsOut + File.separatorChar + testFiles[0];

		try {
			CXFileUtf8 wIn = new CXFileUtf8WithoutBom(wFileInPath);
			CXFileUtf8 wResult = new CXFileUtf8WithoutBom(wFileResultPath);
			wResult.delete();
			wResult.writeAll(wIn.readAll());
			String wData = CXPathUtils.readTextFromXPath(wFileInPath, testFiles[1]);
			assertEquals(wData, testFiles[2]);
			CXPathUtils.appendTextInNode(wFileResultPath, testFiles[1], testFiles[3], testFiles[4],
					CXPathUtils.WITHOUT_BOM);
			CXFileUtf8 wFileResult = new CXFileUtf8(wFileResultPath);
			CXFileUtf8 wFileCheck = new CXFileUtf8(wFileCheckPath);

			assertEquals(wFileResult.readAll(), wFileCheck.readAll());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
