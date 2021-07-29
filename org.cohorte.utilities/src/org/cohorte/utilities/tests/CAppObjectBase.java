package org.cohorte.utilities.tests;

import org.psem2m.utilities.logging.CActivityLoggerBasicConsole;
import org.psem2m.utilities.logging.CXJulUtils;
import org.psem2m.utilities.logging.CXLoggerUtils;
import org.psem2m.utilities.logging.IActivityLogger;

/**
 * @author ogattaz
 * 
 */
public abstract class CAppObjectBase {

	protected IActivityLogger pLogger = CActivityLoggerBasicConsole.getInstance();

	/**
	 * 
	 */
	public CAppObjectBase() {
		super();

		// MOD_OG 1.4.3
		// if the simple formatter isn't configured with the
		// SIMPLE_FORMATTER_FORMAT
		if (!CXJulUtils.validSimpleFormaterConfig()) {
			CXLoggerUtils.logBannerSimpleFormatter(pLogger, this, "<init>");
		}
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
