package test.psem2m.utilities;

import java.io.IOException;

import org.cohorte.utilities.CXMethodUtils;
import org.cohorte.utilities.helpers.CXThrowableConverter;
import org.cohorte.utilities.junit.CAbstractJunitTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.psem2m.utilities.CXException;
import org.psem2m.utilities.CXStringUtils;

/**
 * MOD_OG_20210812
 * 
 * @author ogattaz
 *
 */
public class CJunitCXEception extends CAbstractJunitTest {

	/**
	 * 
	 */
	@AfterClass
	public static void destroy() {

		// log the destroy banner containing the report
		logBannerDestroy(CJunitCXEception.class);
	}

	/**
	 * 
	 */
	@BeforeClass
	public static void initialize() {

		// initialise the map of the test method of the current junit test class
		// de
		initializeTestsRegistry(CJunitCXEception.class);

		// log the initialization banner
		logBannerInitialization(CJunitCXEception.class);
	}

	/**
	 * 
	 */
	public CJunitCXEception() {
		super();
	}

	/**
	 * 
	 */
	private void methodLevel1(final int aLevel) throws Exception {
		String wMethodName = CXMethodUtils.getMethodName(0);
		int wCurrentLevel = aLevel + 1;
		try {
			methodLevel2(wCurrentLevel);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Catched  exception at the level [%02d] in the method=[%s]",
					wCurrentLevel, wMethodName), e);
		}
	}

	private void methodLevel2(final int aLevel) throws Exception {
		String wMethodName = CXMethodUtils.getMethodName(0);
		int wCurrentLevel = aLevel + 1;

		try {
			methodLevel3(wCurrentLevel);
		} catch (Exception e) {
			throw new UnsupportedOperationException(String.format(
					"Catched  exception at the level [%02d] in the method=[%s]", wCurrentLevel, wMethodName), e);
		}
	}

	private void methodLevel3(final int aLevel) throws Exception {
		String wMethodName = CXMethodUtils.getMethodName(0);
		int wCurrentLevel = aLevel + 1;

		throw new IOException(String.format("Orignial exception at the level [%02d] in the method=[%s]", wCurrentLevel,
				wMethodName));

	}

	@Test
	public void test05() throws Exception {
		String wMethodName = "test05";
		String wAction = "Retreive Dimensions";
		try {

			logBegin(this, wMethodName, "%s Begin...", wAction);

			try {
				// Throw an exception
				methodLevel1(0);

			} catch (Throwable e) {

				getLogger().logInfo(this, wMethodName, "XThrowableConverter:\n%s",
						new CXThrowableConverter(e).toJson().toString(2));

				getLogger().logInfo(this, wMethodName, "getCauseMessages():\n%s",
						CXStringUtils.stringListToString(CXException.getCauseMessages(e), CXStringUtils.LINE_SEP));

				getLogger().logInfo(
						this,
						wMethodName,
						"getCauseMessages():\n%s",
						CXStringUtils.stringListToString(CXException.getCauseMessages(e, CXException.MESS_WHITH_NUMBER,
								CXException.MESS_WHITH_SIMPLE_CLASS_NAME), CXStringUtils.LINE_SEP));

				getLogger().logInfo(
						this,
						wMethodName,
						"getCauseMessages():\n%s",
						CXStringUtils.stringListToString(CXException.getCauseMessages(e, CXException.MESS_WHITH_NUMBER,
								!CXException.MESS_WHITH_SIMPLE_CLASS_NAME), CXStringUtils.LINE_SEP));

			}

			logEndOK(this, wMethodName, "%s End OK. assertEquals = true", wAction);

		} catch (Throwable e) {
			logEndKO(this, wMethodName, e);
			throw e;
		}
	}
}
