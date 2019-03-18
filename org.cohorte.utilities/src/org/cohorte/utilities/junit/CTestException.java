package org.cohorte.utilities.junit;

/**
 * 
 * @author ogattaz
 * @since 1.1.0 (#32)
 */
public class CTestException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1877550932939322566L;

	/**
	 * @param aCause
	 * @return
	 */
	private static String buildCauseMessage(final Throwable aCause) {
		return String.format("json exception due to %s", aCause.getClass().getSimpleName());
	}

	/**
	 * Constructs a JSONException with an explanatory message.
	 * 
	 * @param message
	 *            The message to embed in the json exception.
	 */
	public CTestException(final String message) {
		super(message);
	}

	/**
	 * Constructs a JSONException with an explanatory message.
	 * 
	 * @param aFormat
	 *            the format of the message to embed in the json exception.
	 * @param aArgs
	 *            The arguments to include in the format of the message
	 */
	public CTestException(String aFormat, final Object... aArgs) {
		super(String.format(aFormat, aArgs));
	}

	/**
	 * Constructs a JSONException with a cause.
	 * 
	 * @param aCause
	 *            The cause of the json exception.
	 */
	public CTestException(final Throwable aCause) {
		super(buildCauseMessage(aCause), aCause);
	}

	/**
	 * Constructs a JSONException with a cause and an explanatory message.
	 * 
	 * @param aCause
	 *            The cause of the json exception.
	 * @param aFormat
	 *            the format of the message to embed in the json exception.
	 * @param aArgs
	 *            The arguments to include in the format of the message.
	 */
	public CTestException(final Throwable aCause, final String aFormat, final Object... aArgs) {
		super(String.format(aFormat, aArgs), aCause);
	}

}
