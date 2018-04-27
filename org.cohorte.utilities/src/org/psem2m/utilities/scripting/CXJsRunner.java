package org.psem2m.utilities.scripting;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.cohorte.utilities.CXDescriberUtil;
import org.psem2m.utilities.CXTimer;
import org.psem2m.utilities.logging.IActivityLogger;
import org.psem2m.utilities.rsrc.CXRsrcText;

/**
 * #12 Manage chains of resource providers
 *
 * @author ogattaz
 *
 */
public class CXJsRunner extends CXJsObjectBase implements IXJsRunner {

	private final static String RSRC_FORMAT = "name=[%s],timeStamp=[%s]";

	private final static SimpleDateFormat sFormat = new SimpleDateFormat(
			"yyyy/MM/dd HH:mm:ss:SS");

	private final IActivityLogger pActivityLogger;
	protected CXJsCompiledScript pCompiledScript = null;
	protected final IXjsTracer pCXjsTracer;
	protected CXJsEngine pEngine;
	protected String pId;
	protected CXJsSourceMain pMain;
	protected boolean pRunCompil;
	protected CXJsException pRunExcep;
	protected boolean pRunThread = true;
	protected CXTimer pTimer = new CXTimer();
	protected long pTimeRefNano = 0;

	/**
	 * @param aMain
	 * @param aEngine
	 * @param aId
	 * @throws CXJsException
	 */
	public CXJsRunner(final IActivityLogger aActivityLogger,
			final CXJsSourceMain aMain, final CXJsEngine aEngine,
			final String aId) throws Exception {
		super();

		pActivityLogger = aActivityLogger;

		pCXjsTracer = CXjsTracerFactory.newJsTracer(pActivityLogger);

		pMain = aMain;
		pEngine = aEngine;
		pId = aId;
		pCompiledScript = aEngine.compile(aMain, false);
	}

	/**
	 * @param aScriptResult
	 * @return
	 */
	private String buildResultInfos(final Object aScriptResult) {

		StringBuilder wSB = new StringBuilder();
		boolean wHasResult = (aScriptResult != null);
		wSB.append(String.format("hasResult=[%b]", wHasResult));
		if (wHasResult) {
			wSB.append(String.format(" kingOfResult=[%s]", aScriptResult
					.getClass().getSimpleName()));
		}
		return wSB.toString();
	}

	/**
	 * @return
	 */
	boolean checkMainTimeStamp() throws Exception {

		return pMain.checkTimeStamp();
	}

	/**
	 * @throws CXJsException
	 */
	public void checkRunException() throws CXJsException {

		if (pRunExcep != null) {
			throw pRunExcep;
		}
	}

	/**
	 * @return
	 */
	public String descrToString() {

		StringBuilder wSB = new StringBuilder();
		CXDescriberUtil.descrAddLine(wSB, getFormatedTitle());

		if (pRunExcep != null) {
			CXDescriberUtil.descrAddIndent(wSB, pRunExcep.getExcepCtx()
					.toDescription());
		}
		return wSB.toString();
	}

	/**
	 * ATTENTION : CALLED BY THE SCRIPTS
	 *
	 * @see org.psem2m.utilities.scripting.IXJsRunner#fomatTS(java.lang.Long)
	 */
	@Override
	public String fomatTS(final Long aTS) {

		return sFormat.format(new Date(aTS));
	}

	/**
	 * ATTENTION : CALLED BY THE SCRIPTS
	 *
	 * @see com.IXJsRunner.x3.bridge.bundle.extsps.restserver.script.IJsRunner#getDurationNs
	 *      ()
	 */
	@Override
	public long getDurationNs() {

		return pTimer.getDurationNs();
	}

	/**
	 * ATTENTION : CALLED BY THE SCRIPTS
	 *
	 * @see org.psem2m.utilities.scripting.IXJsRunner#getfomatedTS()
	 */
	@Override
	public String getfomatedTS() {

		return sFormat.format(new Date());
	}

	/**
	 * ATTENTION : CALLED BY THE SCRIPTS
	 *
	 * @see org.psem2m.utilities.scripting.IXJsRunner#getFormatedTitle()
	 */
	@Override
	public String getFormatedTitle() {

		return String.format("SCRIPT[%s]", pId);
	}

	/**
	 * ATTENTION : CALLED BY THE SCRIPTS
	 *
	 * @see com.IXJsRunner.x3.bridge.bundle.extsps.restserver.script.IJsRunner#getId()
	 */
	@Override
	public String getId() {

		return pId;
	}

	/**
	 * ATTENTION : CALLED BY THE SCRIPTS
	 *
	 * @see com.IXJsRunner.x3.bridge.bundle.extsps.restserver.script.IJsRunner#getSourceName
	 *      ()
	 */
	@Override
	public String getSourceName() {

		return pMain.getSourceName();
	}

	/**
	 * ATTENTION : CALLED BY THE SCRIPTS
	 *
	 * @see org.psem2m.utilities.scripting.IXJsRunner#getTimeStamps()
	 */
	@Override
	public String getTimeStamps() {

		StringBuilder wSB = new StringBuilder();
		for (CXRsrcText wRsrcText : pCompiledScript.getMainModule()
				.getResources()) {
			if (wSB.length() > 0) {
				wSB.append(';');
			}
			wSB.append(String.format(RSRC_FORMAT,
					wRsrcText.getPath().getName(),
					fomatTS(wRsrcText.getTimeStampSyst())));
		}
		return wSB.toString();
	}

	/**
	 * ATTENTION : CALLED BY THE SCRIPTS
	 *
	 * @see com.IXJsRunner.x3.bridge.bundle.extsps.restserver.script.IJsRunner#getTraceReport
	 *      ()
	 */
	@Override
	public String getTraceReport() {

		StringBuilder wSB = new StringBuilder();
		wSB.append("- Run(" + pId + ")=[" + (pRunExcep == null ? "OK" : "KO")
				+ "]");
		wSB.append('\n');
		wSB.append("- Duration=[" + pTimer.getDurationMs() + "ms]");
		wSB.append('\n');

		wSB.append("  StartAt[" + pTimer.getStartAtSecStr() + "s]");
		wSB.append('\n');
		wSB.append("- StopAt[" + pTimer.getStopAtSecStr() + "s]");
		wSB.append('\n');
		if (pRunExcep != null) {
			wSB.append(pRunExcep.getExcepCtx().toDescription());
			wSB.append('\n');
		}
		return wSB.toString();
	}

	/**
	 * @return
	 */
	private boolean hasActivityLogger() {
		return (pActivityLogger != null);
	}

	/**
	 * ATTENTION : CALLED BY THE SCRIPTS
	 *
	 * @see com.sage.x3.bridge.bundle.extsps.restserver.script.IScriptRunner#logBeginStep
	 *      (java.lang.String)
	 */
	@Override
	public void logBeginStep() {

		pActivityLogger.logInfo(this, "logBeginStep");
	}

	/**
	 * ATTENTION : CALLED BY THE SCRIPTS
	 *
	 * @see org.psem2m.utilities.scripting.IXJsRunner#logBeginStep(java.lang.String,
	 *      java.lang.Object[])
	 */
	@Override
	public void logBeginStep(final String aFormat, final Object... aArgs) {
		pActivityLogger.logInfo(this, "logBeginStep",
				String.format(aFormat, aArgs));

	}

	/**
	 * ATTENTION : CALLED BY THE SCRIPTS
	 *
	 * @see org.psem2m.utilities.scripting.IXJsRunner#logDebug(java.lang.String,
	 *      java.lang.String, java.lang.Object[])
	 */
	@Override
	public void logDebug(final String aWhat, final String aFormat,
			final Object... aArgs) {
		pActivityLogger.logInfo(this, aWhat, String.format(aFormat, aArgs));
	}

	/**
	 * ATTENTION : CALLED BY THE SCRIPTS
	 *
	 * @see com.sage.x3.bridge.bundle.extsps.restserver.script.IScriptRunner#logEndStep
	 *      ()
	 */
	@Override
	public void logEndStep() {

		pActivityLogger.logInfo(this, "logEndStep");
	}

	/**
	 * ATTENTION : CALLED BY THE SCRIPTS
	 *
	 * @see com.sage.x3.bridge.bundle.extsps.restserver.script.IScriptRunner#logEndStep
	 *      ()
	 */
	@Override
	public void logEndStep(final String aFormat, final Object... aArgs) {

		pActivityLogger.logInfo(this, "logEndStep",
				String.format(aFormat, aArgs));
	}

	/**
	 * ATTENTION : CALLED BY THE SCRIPTS
	 *
	 * @see org.psem2m.utilities.scripting.IXJsRunner#logInfo(java.lang.String,
	 *      java.lang.String, java.lang.Object[])
	 */
	@Override
	public void logInfo(final String aWhat, final String aFormat,
			final Object... aArgs) {
		pActivityLogger.logInfo(this, aWhat, String.format(aFormat, aArgs));

	}

	/**
	 * ATTENTION : CALLED BY THE SCRIPTS
	 *
	 * @see org.psem2m.utilities.scripting.IXJsRunner#logSevere(java.lang.String,
	 *      java.lang.String, java.lang.Object[])
	 */
	@Override
	public void logSevere(final String aWhat, final String aFormat,
			final Object... aArgs) {
		pActivityLogger.logSevere(this, aWhat, String.format(aFormat, aArgs));

	}

	/**
	 * ATTENTION : CALLED BY THE SCRIPTS
	 *
	 * @see org.psem2m.utilities.scripting.IXJsRunner#logSevere(java.lang.String,
	 *      java.lang.Throwable, java.lang.String, java.lang.Object[])
	 */
	@Override
	public void logSevere(final String aWhat, final Throwable e,
			final String aFormat, final Object... aArgs) {
		pActivityLogger.logSevere(this, aWhat, "ERROR: %s %s",
				String.format(aFormat, aArgs), e);

	}

	/**
	 * ATTENTION : CALLED BY THE SCRIPTS
	 *
	 * @see com.sage.x3.bridge.bundle.extsps.restserver.script.IScriptRunner#newTimer
	 *      ()
	 */
	@Override
	public CXTimer newTimer() {

		return new CXTimer().start();
	}

	/**
	 * @param aCtx
	 * @return
	 * @throws CXJsException
	 */
	IXJsRuningContext run(final IXJsRuningContext aCtx) throws CXJsException {

		aCtx.setAttrEngine(VAR_SCRIPTID_ID, pId);
		aCtx.setAttrEngine(VAR_SCRIPTTS_ID, getTimeStamps());
		aCtx.setAttrEngine(VAR_SCRIPTCTX_ID, aCtx);
		aCtx.setAttrEngine(VAR_SCRIPTRUN_ID, this);
		aCtx.setAttrEngine(VAR_SCRIPTSOURCE_ID, this.pMain);

		Object wScriptResult = null;

		// ogat - v1.4 - return handle duration and eval duration
		CXTimer wXtimer = new CXTimer();
		wXtimer.start();

		try {
			wScriptResult = pCompiledScript.eval(aCtx.start(
					CXJsRuningContext.ACT_EVAL_COMPILED, pTimeRefNano),
					pCXjsTracer);
			aCtx.setScriptResult(wScriptResult);
		} catch (CXJsException e) {
			throw e;
		} finally {
			aCtx.stop();
			if (hasActivityLogger()) {
				pActivityLogger.logDebug(this, "runEnd", String.format(
						"isEndOK=[%b] %s", aCtx.isEndOK(),
						buildResultInfos(wScriptResult)));
			}
		}

		// ogat - v1.4 - return handle duration and eval duration
		aCtx.setAttrEngine(IXJsManager.ATTR_EVAL_DURATION, wXtimer.stopStrMs());
		return aCtx;
	}

	/**
	 * ATTENTION : CALLED BY THE SCRIPTS
	 *
	 * @see org.psem2m.utilities.scripting.IXJsRunner#stringFormat(java.lang.String,
	 *      java.lang.Object[])
	 */
	@Override
	public String stringFormat(final String aFormat, final Object... aArgs) {
		return String.format(aFormat, aArgs);
	}
}
