package org.psem2m.utilities.scripting;

import org.psem2m.utilities.logging.IActivityLogger;

/**
 * #12 Manage chains of resource providers
 * 
 * @author ogattaz
 *
 */
public class CXjsTracerFactory {

	/**
	 * @param aActivityLogger
	 * @return
	 */
	public static IXjsTracer newJsTracer(final IActivityLogger aActivityLogger) {
		return (aActivityLogger != null) ? new CXjsTracerActivity(
				aActivityLogger) : CXjsTracerNull.getInstance();
	}

}
