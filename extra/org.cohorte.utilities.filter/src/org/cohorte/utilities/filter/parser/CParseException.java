package org.cohorte.utilities.filter.parser;

import org.psem2m.utilities.CXException;

public class CParseException extends CXException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public CParseException(final String aMessage) {
		super(aMessage);
	}

	public CParseException(final Throwable aThrow, final String aMessage) {
		super(aThrow, aMessage);
	}

}
