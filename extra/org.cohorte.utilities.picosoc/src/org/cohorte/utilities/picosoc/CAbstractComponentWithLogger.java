package org.cohorte.utilities.picosoc;

import java.util.logging.Level;

import org.psem2m.utilities.logging.CActivityLoggerBasicConsole;
import org.psem2m.utilities.logging.IActivityLogger;

/**
 * @author ogattaz
 * 
 */
public class CAbstractComponentWithLogger extends CAbstractComponentBase {

	/**
	 * The activity logger of the webapp
	 */
	private final IActivityLogger pLogger;

	/**
	 * 
	 */
	public CAbstractComponentWithLogger() {
		super();
		pLogger = getServiceLogger();
	}

	/**
	 * @return
	 */
	protected IActivityLogger getLogger() {
		return pLogger;
	}

	/**
	 * @return the registered Logger service or an instance of
	 *         CActivityLoggerBasicConsole
	 */
	protected IActivityLogger getServiceLogger() {
		IActivityLogger wLogger = CActivityLoggerBasicConsole.getInstance();
		try {
			wLogger = getService(IActivityLogger.class);

		} catch (Exception e) {
			CComponentLogger
					.logInMain(
							Level.WARNING,
							this,
							"getServiceLogger",
							"unable to retreive the service IActivityLogger: Use an ActivityLoggerBasicConsole");
		}
		return wLogger;
	}

	/**
	 * @return
	 */
	protected boolean isLogDebugOn() {
		return pLogger.isLogDebugOn();
	}

	/**
	 * @return
	 */
	protected boolean isLogInfoOn() {
		return pLogger.isLogInfoOn();
	}

	/**
	 * @return
	 */
	protected boolean isLogSevereOn() {
		return pLogger.isLogSevereOn();
	}

	/**
	 * @return
	 */
	protected boolean isLogWarningOn() {
		return pLogger.isLogWarningOn();
	}

}
