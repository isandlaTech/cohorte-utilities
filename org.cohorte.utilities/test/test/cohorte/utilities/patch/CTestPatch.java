package test.cohorte.utilities.patch;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.psem2m.utilities.CXDomUtils;
import org.psem2m.utilities.files.CXFileText;
import org.psem2m.utilities.files.CXFileTextPatch;
import org.psem2m.utilities.files.CXFileTextPatcher;
import org.psem2m.utilities.files.CXFileUtf8;
import org.psem2m.utilities.logging.CActivityLoggerBasicConsole;

import junit.framework.TestCase;

@RunWith(Theories.class)
public class CTestPatch extends TestCase {

	public static String fileTestsIn = System.getProperty("user.dir") + File.separatorChar + "files"
			+ File.separatorChar + "testPatch" + File.separatorChar + "in" + File.separatorChar;
	public static String fileTestsOut = System.getProperty("user.dir") + File.separatorChar + "files"
			+ File.separatorChar + "testPatch" + File.separatorChar + "out" + File.separatorChar;

	public static String fileTestsPatch = System.getProperty("user.dir") + File.separatorChar + "files"
			+ File.separatorChar + "testPatch" + File.separatorChar + "patch" + File.separatorChar;
	public static String fileTestsResult = System.getProperty("user.dir") + File.separatorChar + "files"
			+ File.separatorChar + "testPatch" + File.separatorChar + "result" + File.separatorChar;

	public static @DataPoints String[][] testFiles = {

	{ "patch_after.xml", "patch_after.txt" }, { "patch_before.xml", "patch_before.txt" },
			{ "patch_alter.xml", "patch_alter.txt" } };

	@BeforeClass
	public static void setup() {

	}

	@Theory
	public void testPatch(final String[] testFiles) {
		CXFileText wFileIn = new CXFileText(fileTestsIn + File.separatorChar + testFiles[1]);
		CXFileText wFileResult = new CXFileText(fileTestsResult + File.separatorChar + testFiles[1]);
		CXFileText wFileOut = new CXFileText(fileTestsOut + File.separatorChar + testFiles[1]);
		CXFileText wPathToApply = new CXFileText(fileTestsPatch + File.separatorChar + testFiles[0]);
		CXFileUtf8 wToSave = new CXFileUtf8(fileTestsIn + File.separatorChar + testFiles[1] + ".save");

		try {
			wFileResult.writeAll(wFileIn.readAll());

			CXFileTextPatcher wPath = new CXFileTextPatcher(wFileResult, wToSave,
					CActivityLoggerBasicConsole.getInstance());
			List<CXFileTextPatch> wListPatch = new ArrayList<>();

			CXDomUtils wDom;
			wDom = new CXDomUtils(wPathToApply.readAll());

			wListPatch.add(CXFileTextPatch.parse(wDom.getRootElmt(), null));
			wPath.applyPatches(wListPatch);
			wPath.saveResult();
			wFileOut.openReadLine();
			wFileResult.openReadLine();
			assertEquals(wFileOut.readAll(), wFileResult.readAll());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
