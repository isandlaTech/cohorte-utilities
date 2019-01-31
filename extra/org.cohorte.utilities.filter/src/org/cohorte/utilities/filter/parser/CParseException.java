package org.cohorte.utilities.filter.parser;

import org.psem2m.utilities.CXException;

public class CParseException extends CXException {

	public CParseException(String aMessage) {
		super(aMessage);
	}

	public CParseException(Throwable aThrow, String aMessage) {
		super(aThrow, aMessage);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
