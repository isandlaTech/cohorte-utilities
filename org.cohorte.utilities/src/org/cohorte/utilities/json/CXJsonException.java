package org.cohorte.utilities.json;

/**
 * @author ogattaz
 *
 */
public class CXJsonException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7640284693476141010L;

	/**
	 * @param format
	 * @param args
	 */
	public CXJsonException(final String format, final Object... args) {
		this(null, String.format(format, args));
	}

	/**
	 * @param e
	 */
	public CXJsonException(final Throwable e) {
		super(e);
	}

	/**
	 * @param aCause
	 * @param aMessage
	 */
	public CXJsonException(final Throwable aCause, final String aMessage) {
		super(aMessage, aCause);
	}

	/**
	 * @param aCause
	 * @param format
	 * @param args
	 */
	public CXJsonException(final Throwable aCause, final String format, final Object... args) {
		this(aCause, String.format(format, args));
	}

}
