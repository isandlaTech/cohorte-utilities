package org.cohorte.utilities;

import java.util.UUID;

/**
 * MOD_OG_20210427 Sharing utilities developped in Dimensions core
 * 
 * @author ogattaz
 *
 */
public class CXUuidUtils {

	/**
	 * @param string
	 * @return
	 */
	public static boolean isUUID(String aString) {

		if (aString != null && !aString.isEmpty()) {

			try {
				// Creates a UUID from the string standard representation as
				// described in the
				// toString method.
				UUID.fromString(aString);

				return true;
			}
			// IllegalArgumentException - If name does not conform to the string
			// representation as described in toString
			// NullPointExcetion if
			catch (Exception e) {
				// nithing
			}
		}
		return false;
	}

}
