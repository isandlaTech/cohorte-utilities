package org.psem2m.utilities.scripting;

import org.psem2m.utilities.logging.IActivityLogger;

/**
 * @author ogattaz
 * 
 */
public class CXjsTracerActivity implements IXjsTracer {

	private final IActivityLogger pActivityLogger;

	/**
	 * @return
	 */
	public CXjsTracerActivity(final IActivityLogger aActivityLogger) {
		super();
		pActivityLogger = aActivityLogger;
	}

	/**
	 * @return
	 */
	IActivityLogger getActivityLogger() {
		return pActivityLogger;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.scripting.IXjsTracer#isTraceDebugOn()
	 */
	@Override
	public boolean isTraceDebugOn() {
		return pActivityLogger.isLogDebugOn();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.scripting.IXjsTracer#isTraceInfosOn()
	 */
	@Override
	public boolean isTraceInfosOn() {
		return pActivityLogger.isLogInfoOn();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.scripting.IXjsTracer#trace(java.lang.CharSequence)
	 */
	@Override
	public void trace(CharSequence aSB) {
		pActivityLogger.logInfo(this, "trace", aSB);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.scripting.IXjsTracer#trace(java.lang.Object,
	 * java.lang.CharSequence)
	 */
	@Override
	public void trace(Object aObj, CharSequence aS) {
		pActivityLogger.logInfo(aObj, "trace", aS);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.scripting.IXjsTracer#trace(java.lang.Object,
	 * java.lang.CharSequence, java.lang.Throwable)
	 */
	@Override
	public void trace(Object aObj, CharSequence aSB, Throwable e) {
		pActivityLogger.logSevere(aObj, "trace", "ERROR: %s %s", aSB, e);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.scripting.IXjsTracer#trace(java.lang.Object,
	 * java.lang.Throwable)
	 */
	@Override
	public void trace(Object aObj, Throwable e) {
		pActivityLogger.logSevere(aObj, "trace", "ERROR: %s", e);
	}

}
