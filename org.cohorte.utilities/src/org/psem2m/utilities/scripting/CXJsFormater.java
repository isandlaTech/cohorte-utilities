package org.psem2m.utilities.scripting;

/**
 * MOD_OG_20170615 add current tracer as an atribute in the ENGINE_SCOPE
 *
 * @author ogattaz
 *
 */
public class CXJsFormater {

	/**
	 * @param aFormat
	 * @param aArgs
	 * @return
	 */
	public String format(final String aFormat, final Object... aArgs) {
		return String.format(aFormat, aArgs);
	}

	/**
	 * @param aObject
	 * @return
	 */
	public String valueOf(Object aObject) {
		return String.valueOf(aObject);
	}
}
