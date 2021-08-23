package test.psem2m.utilities;

import java.awt.Font;

import org.cohorte.utilities.CXBannerUtils;
import org.cohorte.utilities.CXMethodUtils;
import org.cohorte.utilities.asciiart.CXArtSetting;
import org.cohorte.utilities.asciiart.CXAsciiArt;
import org.cohorte.utilities.junit.CAbstractJunitTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.psem2m.utilities.CXLoremIpsum;
import org.psem2m.utilities.CXThreadUtils;

/**
 * @author ogattaz
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CTestBannerUtils extends CAbstractJunitTest {

	private static final String[] DIMAUTH1 = new String[] { "DIM Auth", "@", "-", "courier", "BOLD,ITALIC", "18", "#",
			".", "200", "10" };

	private static final String[] DIMAUTH2 = new String[] { "DIM Auth", "@", "-", "courier", "BOLD", "18", "#", "-",
			"200", "10" };

	private static final String[] DIMAUTH3 = new String[] { "DIM Auth", "@", " ", "courier", "", "18", "#", " ", "200",
			"10" };

	private static final String[] QSFAB1 = new String[] { "QS Fab", "*", " ", "SansSerif", "BOLD,ITALIC", "30", "#",
			".", "200", "10" };

	private static final String[] QSFAB2 = new String[] { "QS Fab", "*", " ", "SansSerif", "BOLD", "30", "#", " ",
			"200", "10" };

	private static final String[] QSFAB3 = new String[] { "QS Fab", "*", " ", "SansSerif", "", "30", "#", " ", "200",
			"10" };

	private static final String[] QSPLANNING1 = new String[] { "QS Planning", "@", "-", "SansSerif", "BOLD,ITALIC",
			"24", "#", ".", "-1", "0" };

	private static final String[] QSPLANNING2 = new String[] { "QS Planning", "@", "-", "SansSerif", "BOLD", "24", "#",
			"-", "-1", "0" };

	private static final String[] QSPLANNING3 = new String[] { "QS Planning", "@", "-", "SansSerif", "", "24", "#",
			"-", "-1", "0" };

	private static final String[][] TESTS_CONFIGS = new String[][] { QSFAB1, QSFAB2, QSFAB3, QSPLANNING1, QSPLANNING2,
			QSPLANNING3, DIMAUTH1, DIMAUTH2, DIMAUTH3 };

	private static final String[] WORDS = CXLoremIpsum.LOREM_IPSUM.split("\\s");

	/**
	 * 
	 */
	@AfterClass
	public static void destroy() {

		// log the destroy banner containing the report
		logBannerDestroy();
	}

	/**
	 * 
	 */
	@BeforeClass
	public static void initialize() {

		// initialise the map of the test method of the current junit test class
		// de
		initializeTestsRegistry();

		// log the initialization banner
		logBannerInitialization();
	}

	/**
	 * ATTENTION: a junit test must have only one public constructor
	 */
	public CTestBannerUtils() {
		super();
	}

	/**
	 * @param aConfiguration
	 * @return
	 */
	private String buildBanner(final String[] aConfiguration) {

		String wText = aConfiguration[0];
		char wBlackChar = aConfiguration[1].charAt(0);
		char wWhiteChar = aConfiguration[2].charAt(0);

		String wFontFamily = aConfiguration[3];

		// Font.BOLD; //1
		// Font.ITALIC; //2
		int wFontStyle = ((aConfiguration[4].contains("BOLD")) ? 1 : 0)
				+ ((aConfiguration[4].contains("ITALIC")) ? 2 : 0);

		int wFontSize = Integer.parseInt(aConfiguration[5]);

		CXArtSetting wArtSetting = new CXArtSetting(new Font(wFontFamily, wFontStyle, wFontSize), wWhiteChar,
				wBlackChar);

		CXAsciiArt wCXAsciiArt = new CXAsciiArt(wArtSetting);

		String wAsciiArtContent = wCXAsciiArt.drawString(wText);

		// getLogger().logInfo(this, "buildBanner", "wAsciiArtContent:\n%s",
		// wAsciiArtContent);
		// getLogger().logInfo(this, "buildBanner", "lenMaxOfLines=[%d]",
		// CXBannerUtils.lenMaxOfLines(wAsciiArtContent));

		char wBorderChar = aConfiguration[6].charAt(0);

		char wWhiteChar2 = aConfiguration[7].charAt(0);

		int wWidth = Integer.parseInt(aConfiguration[8]);

		int wTextOffset = Integer.parseInt(aConfiguration[9]);

		int wModifiers = CXBannerUtils.WITH_BLANK_LINES;

		String wBanner = CXBannerUtils.build(wBorderChar, wWhiteChar2, wWidth, wTextOffset, wModifiers,
				wAsciiArtContent);

		return wBanner;

	}

	/**
	 * @return a random int between 10 and 200
	 */
	private int randomNbWord() {
		return 10 + new Double(Math.random() * 190).intValue();
	}

	/**
	 * @return
	 */
	private String randomWord() {
		int wRandomIdx = new Double(Math.random() * WORDS.length).intValue();
		return WORDS[wRandomIdx];
	}

	/**
	 * @param aLenMaxOfLine
	 * @return
	 */
	private String randomWords(final int aLenMaxOfLine) {

		final StringBuilder wWords = new StringBuilder();

		int wNbMax = randomNbWord();
		String wWord;
		int wWordIdx = 0;

		StringBuilder wLine = new StringBuilder();

		for (int i = 0; i < wNbMax; i++) {
			if (wWordIdx == CXLoremIpsum.LOREM_IPSUM_WORDS.length) {
				wWordIdx = 0;
			}
			wWord = CXLoremIpsum.LOREM_IPSUM_WORDS[wWordIdx];

			if (wLine.length() + wWord.length() > aLenMaxOfLine) {
				wWords.append('\n').append(wLine);
				wLine = new StringBuilder();
			}
			wLine.append(wWord);

			if (i < wNbMax - 1) {
				wLine.append(' ');
			}

			wWordIdx++;
		}
		if (wLine.length() > 0) {
			wWords.append('\n').append(wLine);
		}

		return wWords.toString();
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void test10genBanner() throws Exception {
		String wMethodName = CXMethodUtils.getMethodName(0);
		String wAction = "Generate different banner ";
		try {

			logBegin(this, wMethodName, "%s Begin...", wAction);

			for (String[] wTestConfig : TESTS_CONFIGS) {
				getLogger().logInfo(this, wMethodName, "BANNER\n%s", buildBanner(wTestConfig));
			}

			logEndOK(this, wMethodName, "%s End OK.", wAction);

		} catch (Throwable e) {
			logEndKO(this, wMethodName, e);
			throw e;
		}
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void test20genBanner() throws Exception {
		String wMethodName = CXMethodUtils.getMethodName(0);
		String wAction = "Generate banner using different fonts ";
		try {

			logBegin(this, wMethodName, "%s Begin...", wAction);

			String[] wTestConfig = new String[] { "???", "@", "-", "???", "", "24", "#", ".", "-1", "0" };

			int wMax = CXBannerUtils.FONTS.length;

			int wIdx = 0;
			for (String wFont : CXAsciiArt.FONTS) {
				wIdx++;
				wTestConfig[0] = String.format("%d/%d:abcdef  %s", wIdx, wMax, randomWord());
				wTestConfig[3] = wFont;
				String wBuiltBanner = buildBanner(wTestConfig);
				getLogger()
						.logInfo(this, wMethodName, "BANNER [%d/%d] font=[%s]:\n%s", wIdx, wMax, wFont, wBuiltBanner);
				CXThreadUtils.sleep(100);
			}

			logEndOK(this, wMethodName, "%s End OK.", wAction);

		} catch (Throwable e) {
			logEndKO(this, wMethodName, e);
			throw e;
		}
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void test30genBanner() throws Exception {
		String wMethodName = CXMethodUtils.getMethodName(0);
		String wAction = "Generate banner using different fonts ";
		try {

			logBegin(this, wMethodName, "%s Begin...", wAction);

			CXArtSetting wArtSetting = new CXArtSetting(new Font(CXAsciiArt.FONT_MONOSPACED, Font.PLAIN, 24), ' ', '@');

			CXAsciiArt wCXAsciiArt = new CXAsciiArt(wArtSetting);

			String wAsciiArtContent = wCXAsciiArt.drawString(randomWord() + ' ' + randomWord());

			int wLenMaxOfLines = CXBannerUtils.lenMaxOfLines(wAsciiArtContent);

			StringBuilder wText = new StringBuilder();
			wText.append(wAsciiArtContent);
			wText.append('\n');
			wText.append(randomWords(wLenMaxOfLines));
			wText.append('\n');
			wText.append(randomWords(wLenMaxOfLines));
			wText.append('\n');
			wText.append(randomWords(wLenMaxOfLines));
			wText.append('\n');
			wText.append(randomWords(wLenMaxOfLines));

			String wBanner = CXBannerUtils.build('#', ' ', -1, 0, CXBannerUtils.WITH_BLANK_LINES, wText.toString());

			getLogger().logInfo(this, wMethodName, "BANNER: \n%s", wBanner);

			logEndOK(this, wMethodName, "%s End OK.", wAction);

		} catch (Throwable e) {
			logEndKO(this, wMethodName, e);
			throw e;
		}
	}
}
