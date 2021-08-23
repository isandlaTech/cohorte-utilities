package test.psem2m.utilities;

import java.awt.Font;

import org.cohorte.utilities.CXMethodUtils;
import org.cohorte.utilities.asciiart.CXArtSetting;
import org.cohorte.utilities.asciiart.CXAsciiArt;
import org.cohorte.utilities.junit.CAbstractJunitTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.psem2m.utilities.CXTimer;
import org.psem2m.utilities.logging.CXLoggerUtils;

/**
 * MOD_OG_20210812
 * 
 * @author ogattaz
 *
 */
public class CTestAsciiArt extends CAbstractJunitTest {

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
	 * 
	 */
	public CTestAsciiArt() {
		super();
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void test05() throws Exception {
		String wMethodName = CXMethodUtils.getMethodName(0);
		String wAction = "Ascii Art";
		try {

			logBegin(this, wMethodName, "%s Begin...", wAction);

			// String wText = "Olivier Gattaz";
			// String wText = "Phillipe De Cesatis";
			String wText = "QS Monitoring";
			char wBlackChar = '@';
			// char wBlackChar = '*';
			char wWhiteChar = '-';
			// char wWhiteChar = ' ';

			String wFontFamily = "SansSerif";
			// String wFontFamily = "Courier";
			// int wFontStyle = Font.ITALIC + Font.BOLD;
			int wFontStyle = Font.BOLD;
			int wFontSize = 24;

			CXArtSetting wArtSetting = new CXArtSetting(new Font(wFontFamily, wFontStyle, wFontSize), wWhiteChar,
					wBlackChar);

			CXAsciiArt wCXAsciiArt = new CXAsciiArt(wArtSetting);

			int wNbTestMax = 10;
			String wResult = null;
			CXTimer wTimer = CXTimer.newStartedTimer();

			for (int wNbTest = 0; wNbTest < wNbTestMax; wNbTest++) {
				wResult = wCXAsciiArt.drawString(wText);
			}
			wTimer.stop();

			getLogger().logInfo(this, wMethodName, "Result:\n%s", wResult);

			getLogger().logInfo(this, wMethodName, "Duration=[%s]", wTimer.getDurationStrMicroSec(wNbTestMax));

			CXLoggerUtils.logBannerInfo(getLogger(), this, wMethodName, '#', false, wResult);

			logEndOK(this, wMethodName, "%s End OK.", wAction);

		} catch (Throwable e) {
			logEndKO(this, wMethodName, e);
			throw e;
		}
	}
}
