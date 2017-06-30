package org.cohorte.iot.json.validator.api;

public class SchemaException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public SchemaException(final String aMessage) {
		// TODO Auto-generated constructor stub
		super(aMessage);
	}

	public SchemaException(final Throwable aCause, final String aMessage) {
		// TODO Auto-generated constructor stub
		super(aMessage, aCause);
	}
}
