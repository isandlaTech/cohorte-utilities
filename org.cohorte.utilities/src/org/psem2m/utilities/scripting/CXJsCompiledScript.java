package org.psem2m.utilities.scripting;

import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptException;

import org.psem2m.utilities.CXTimer;

/**
 * #12 Manage chains of resource providers
 * 
 * @author ogattaz
 *
 */

public class CXJsCompiledScript extends CXJsObjectBase implements IXJsConstants {

	private final boolean pCheckTimeStamp;
	private CompiledScript pCompiledScript;
	private CXJsEngine pEngine;
	private CXJsSourceMain pMainModule;

	/**
	 * @param aMainModule
	 * @param aCompiledScript
	 * @param aEngine
	 * @param aCheckTimeStamp
	 */
	protected CXJsCompiledScript(CXJsSourceMain aMainModule,
			CompiledScript aCompiledScript, CXJsEngine aEngine,
			boolean aCheckTimeStamp) {
		super();
		pCompiledScript = aCompiledScript;
		pEngine = aEngine;
		pCheckTimeStamp = aCheckTimeStamp;
		pMainModule = aMainModule;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.psem2m.utilities.scripting.CXJsObjectBase#addDescriptionInBuffer(
	 * java.lang.Appendable)
	 */
	@Override
	public Appendable addDescriptionInBuffer(Appendable aSB) {
		aSB = super.addDescriptionInBuffer(aSB);
		descrAddText(aSB, "CompiledScript - ");
		descrAddLine(aSB, pEngine.toDescription());
		descrAddIndent(aSB, pMainModule.toDescription());
		return aSB;
	}

	/**
	 * @param tracer
	 * @return
	 * @throws CXJsException
	 */
	protected synchronized boolean checkTimeStamp(IXjsTracer tracer)
			throws CXJsException {
		final boolean trace = tracer != null;
		if (!pCheckTimeStamp) {
			if (trace) {
				tracer.trace("no checkTimeStamp");
			}
			return true;
		}
		final CXTimer wT = trace ? new CXTimer("checkTimeStamp", true) : null;
		try {
			return pMainModule.checkTimeStamp();
		} catch (final Exception e) {
			throwMyScriptExcep(tracer, "Error checking timeStamp", e,
					"checkTimeStamp");
		} finally {
			if (trace) {
				wT.stop();
				tracer.trace(wT.toDescription());
			}
		}
		return false;
	}

	/**
	 *
	 */
	public void destroy() {
		pEngine = null;
		pCompiledScript = null;
		pMainModule = null;
	}

	/**
	 * @return
	 * @throws CXJsException
	 */
	public Object eval() throws CXJsException {
		return eval((IXjsTracer) null);
	}

	/**
	 * @param bindings
	 * @return
	 * @throws CXJsException
	 */
	public Object eval(Bindings bindings) throws CXJsException {
		return eval(bindings, null);
	}

	/**
	 * @param aBinding
	 * @param aJsTracer
	 * @return
	 * @throws CXJsException
	 */
	public Object eval(Bindings aBinding, IXjsTracer aJsTracer)
			throws CXJsException {

		final ScriptContext wScriptContext = new CXJsRuningContext(1024,
				aBinding);

		return eval(wScriptContext, aJsTracer);
	}

	/**
	 * @param tracer
	 * @return
	 * @throws CXJsException
	 */
	public Object eval(IXjsTracer aJsTracer) throws CXJsException {

		return eval((ScriptContext) null, aJsTracer);
	}

	/**
	 * @param context
	 * @return
	 * @throws CXJsException
	 */
	public Object eval(ScriptContext aScriptContext) throws CXJsException {
		return eval(aScriptContext, null);
	}

	/**
	 * AJout tracer
	 *
	 *
	 * @param aScriptContext
	 * @param aJsTracer
	 * @return
	 * @throws CXJsException
	 */
	public Object eval(ScriptContext aScriptContext, IXjsTracer aJsTracer)
			throws CXJsException {
		Object wRes = null;
		// Test tracer!=null car on n'a pas de isTraceOn() dans ITracer
		final boolean wTraceOn = aJsTracer != null;
		final CXTimer wTimer = wTraceOn ? new CXTimer("evalCompiledContext",
				true) : null;
		try {
			if (!checkTimeStamp(aJsTracer)) {
				recompile(aJsTracer);
			}

			// MOD_OG_20170615 add the TRACER and a FORMATER in the script
			// context
			if (wTraceOn) {
				aScriptContext.setAttribute(VAR_TRACER_ID, aJsTracer,
						ScriptContext.ENGINE_SCOPE);
			}
			aScriptContext.setAttribute(VAR_FORMATTER_ID, new CXJsFormater(),
					ScriptContext.ENGINE_SCOPE);

			wRes = pCompiledScript.eval(aScriptContext);
		} catch (final ScriptException e) {
			// FDB - 64796
			CXJsExcepRhino.throwMyScriptExcep(this, pMainModule, aJsTracer, e,
					"evalContextCompiled");
		} catch (final Exception e) {
			throwMyScriptExcep(aJsTracer, "Error evaluating script", e,
					"evalContextCompiled");
		} finally {
			if (wTraceOn) {
				wTimer.stop();
				aJsTracer.trace(wTimer.toDescription());
			}
		}
		return wRes;
	}

	/**
	 * @return
	 */
	public CompiledScript getCompiledScript() {
		return pCompiledScript;
	}

	/**
	 * @return
	 */
	public CXJsEngine getEngine() {
		return pEngine;
	}

	/**
	 * @return
	 */
	public CXJsSourceMain getMainModule() {

		return pMainModule;
	}

	/**
	 * @return
	 */
	public boolean hasFilesDependencies() {
		return pMainModule.hasFilesDependencies();
	}

	/**
	 * @return
	 */
	public boolean isCheckTimeStamp() {
		return pCheckTimeStamp;
	}

	/**
	 * @param tracer
	 * @throws CXJsException
	 */
	protected void recompile(IXjsTracer tracer) throws CXJsException {
		pCompiledScript = pEngine.reCompile(pMainModule, tracer);
	}

	/**
	 * @param tracer
	 * @param aErrMsg
	 * @param aAction
	 * @throws CXJsException
	 */
	protected void throwMyScriptExcep(IXjsTracer tracer, String aErrMsg,
			String aAction) throws CXJsException {
		if (tracer != null) {
			tracer.trace(aAction + "Error[" + aErrMsg + "]");
		}
		throw new CXJsException(pMainModule, aErrMsg, null, aAction);
	}

	/**
	 * @param tracer
	 * @param aErrMsg
	 * @param e
	 * @param aAction
	 * @throws CXJsException
	 */
	protected void throwMyScriptExcep(IXjsTracer tracer, String aErrMsg,
			Throwable e, String aAction) throws CXJsException {
		if (tracer != null) {
			tracer.trace(this, aAction + "Error[" + aErrMsg + "]", e);
		}
		throw new CXJsException(pMainModule, aErrMsg, e, aAction);
	}
}
