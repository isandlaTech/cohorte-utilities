package org.cohorte.utilities.tests;

import org.psem2m.utilities.logging.CActivityLoggerBasicConsole;
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

		// MOD_OG 1.0.16
		// if the simple formatter isn't configured
		if (System.getProperty(CXLoggerUtils.SIMPLE_FORMATTER_PROP_NAME) == null) {
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
