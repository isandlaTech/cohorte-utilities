package org.cohorte.utilities.security;

/**
 * @author ogattaz
 * 
 */
public class CXPassphraseSchemeException extends Exception {

	private static final long serialVersionUID = 6752107713723698962L;

	/**
	 * @param aFormat
	 * @param aArgs
	 */
	public CXPassphraseSchemeException(final String aFormat, final Object... aArgs) {
		super(String.format(aFormat, aArgs));
	}

	/**
	 * @param aCause
	 * @param aFormat
	 * @param aArgs
	 */
	public CXPassphraseSchemeException(final Throwable aCause, final String aFormat, final Object... aArgs) {
		super(String.format(aFormat, aArgs), aCause);
	}

}
