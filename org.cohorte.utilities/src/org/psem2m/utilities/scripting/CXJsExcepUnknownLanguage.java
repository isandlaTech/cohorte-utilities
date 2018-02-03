package org.psem2m.utilities.scripting;

/**
 * @author ogattaz
 * 
 */
public class CXJsExcepUnknownLanguage extends CXJsException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3346971117608928154L;

	/**
	 * @param aWhat
	 * @param format
	 * @param args
	 */
	public CXJsExcepUnknownLanguage(final String aWhat, final String format, final Object... args) {
		super(null, String.format(format, args), aWhat);
	}
}
