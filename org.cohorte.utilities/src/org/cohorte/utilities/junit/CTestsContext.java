package org.cohorte.utilities.junit;

import java.util.HashMap;

import org.psem2m.utilities.logging.IActivityLogger;

/**
 * @author ogattaz
 * @since 1.1.0 (#32)
 *
 */
public class CTestsContext extends HashMap<String, Object> {

	private static final long serialVersionUID = -965554339862312140L;

	private final IActivityLogger pLogger;

	/**
	 * 
	 */
	public CTestsContext(final IActivityLogger aLogger) {
		super();
		pLogger = aLogger;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.HashMap#get(java.lang.Object)
	 */
	@Override
	public Object get(final Object aKey) {

		Object wValue = super.get(aKey);

		String wDump = String.valueOf(wValue);
		if (wDump.length() > 256) {
			wDump = wDump.substring(0, 256).concat(" ... (trucated)");
		}

		pLogger.logInfo(this, "get", "Context GET : %s=[%s]", aKey, wDump);
		return wValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.HashMap#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Object put(final String aKey, Object aValue) {

		String wDump = String.valueOf(aValue);
		if (wDump.length() > 256) {
			wDump = wDump.substring(0, 256).concat(" ... (trucated)");
		}

		pLogger.logInfo(this, "put", "Context PUT : %s=[%s]", aKey, wDump);

		return super.put(aKey, aValue);
	}

}
