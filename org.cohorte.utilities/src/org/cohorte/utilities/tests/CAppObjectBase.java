package org.cohorte.utilities.tests;

import org.psem2m.utilities.logging.CActivityLoggerBasicConsole;
import org.psem2m.utilities.logging.IActivityLogger;

/**
 * @author ogattaz
 * 
 */
public abstract class CAppObjectBase {

	protected IActivityLogger pLogger = CActivityLoggerBasicConsole
			.getInstance();

	/**
	 * 
	 */
	public CAppObjectBase() {
		super();
	}

	/**
	 * @return
	 */
	protected IActivityLogger getLogger() {
		return pLogger;
	}

	/**
	 * @param aLogger
	 */
	protected void setLogger(final IActivityLogger aLogger) {
		pLogger = aLogger;
	}

}
