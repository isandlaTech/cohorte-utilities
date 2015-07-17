package tests;

import org.psem2m.utilities.logging.CActivityLoggerBasicConsole;
import org.psem2m.utilities.logging.IActivityLogger;

/**
 * MOD_OG_20150717
 *
 * @author ogattaz
 *
 */
public abstract class CTest {

	final IActivityLogger pLogger = CActivityLoggerBasicConsole.getInstance();

	/**
	 *
	 */
	protected CTest() {
		super();

	}

	/**
	 * Turns array of bytes into string
	 *
	 * @param buf
	 *            Array of bytes to convert to hex string
	 * @return Generated hex string
	 */
	String asHex(byte buf[]) {
		final StringBuilder strbuf = new StringBuilder(buf.length * 2);
		int i;
		for (i = 0; i < buf.length; i++) {
			if ((buf[i] & 0xff) < 0x10) {
				strbuf.append("0");
			}
			strbuf.append(Long.toString(buf[i] & 0xff, 16));
		}
		return strbuf.toString();
	}

	/**
	 * @param aStr
	 * @param aNbCopy
	 * @return
	 */
	String buildText(final String aStr, final int aNbCopy) {
		final StringBuilder wSB = new StringBuilder();
		for (int wIdx = 0; wIdx < aNbCopy; wIdx++) {
			wSB.append(aStr);
		}
		return wSB.toString();
	}

	/**
	 *
	 */
	void destroy() {
		pLogger.logInfo(this, "destroy", "close the logger");
		pLogger.close();
	}

	/**
	 * @throws Exception
	 */
	abstract void doTest() throws Exception;

}
