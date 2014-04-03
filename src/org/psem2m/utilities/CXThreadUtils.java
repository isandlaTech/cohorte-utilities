package org.psem2m.utilities;

/**
 * @author ogattaz
 * 
 */
public class CXThreadUtils {

	/**
	 * @param aDuration
	 * @return false if interupted, true if the sleeping is complete
	 */
	public static boolean sleep(final long aDuration) {
		try {
			Thread.sleep(aDuration);
			return true;
		} catch (InterruptedException e) {
			return false;
		}
	}

}
