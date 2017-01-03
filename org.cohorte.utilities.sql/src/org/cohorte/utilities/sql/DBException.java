package org.cohorte.utilities.sql;

public class DBException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 357177312260984046L;

	/**
	 *
	 */
	public DBException() {
		super();
	}

	/**
	 * @param aMessage
	 */
	public DBException(final String aMessage) {
		super(aMessage);
	}

	/**
	 * @param aMessage
	 * @param aCause
	 */
	public DBException(final String aMessage, final Throwable aCause) {
		super(aMessage, aCause);
	}

	/**
	 * @param aCause
	 */
	public DBException(final Throwable aCause) {
		super(aCause);
	}

}
