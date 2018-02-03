package org.psem2m.utilities.scripting;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.psem2m.utilities.CXTimer;

/**
 * @author ogattaz
 *
 */
public class CXJsEngine extends CXJsObjectBase {

	private ScriptEngine pEngine;
	private CXJsScriptFactory pFactory;

	/**
	 * @param aEngine
	 * @param afactory
	 */
	protected CXJsEngine(ScriptEngine aEngine, CXJsScriptFactory afactory) {
		super();
		pEngine = aEngine;
		pFactory = afactory;
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
		descrAddProp(aSB, "ScriptEngine - Language", getLanguage());
		return aSB;
	}

	/**
	 * @param aMainModule
	 * @param aCheckTimeStamp
	 * @return
	 * @throws CXJsException
	 */
	public CXJsCompiledScript compile(CXJsSourceMain aMainModule,
			boolean aCheckTimeStamp) throws CXJsException {
		return compile(aMainModule, aCheckTimeStamp,
				CXjsTracerNull.getInstance());
	}

	public CXJsCompiledScript compile(CXJsSourceMain aMainModule,
			boolean aCheckTimeStamp, IXjsTracer tracer) throws CXJsException {
		if (tracer != null) {
			tracer.trace("Compile");
		}
		return new CXJsCompiledScript(aMainModule, doCompile(aMainModule,
				"compile", tracer), this, aCheckTimeStamp);
	}

	/**
	 *
	 */
	public void destroy() {
		pEngine = null;
		pFactory = null;
	}

	/**
	 * @param aMainModule
	 * @param aAction
	 * @param tracer
	 * @return
	 * @throws CXJsException
	 */
	private CompiledScript doCompile(CXJsSourceMain aMainModule,
			String aAction, IXjsTracer tracer) throws CXJsException {
		if (isCompilable()) {
			final boolean trace = tracer != null;
			final CXTimer wT = trace ? new CXTimer("compile", true) : null;
			try {
				return ((Compilable) pEngine).compile(aMainModule
						.getMergedCode());
			} catch (final ScriptException e) {
				CXJsExcepRhino.throwMyScriptExcep(this, aMainModule, tracer, e,
						"doCompile");
			} catch (final Exception e) {
				throwMyScriptExcep(aMainModule, tracer,
						"Error compiling script", e, "doCompile");
			} finally {
				if (trace) {
					wT.stop();
					tracer.trace(wT.toDescription());
				}
			}
		} else {
			throwMyScriptExcep(aMainModule, tracer,
					"JavaScript engine is not 'compilable' - Language["
							+ getLanguage() + "]", aAction);
		}
		return null;
	}

	/**
	 * @param aMainModule
	 * @return
	 * @throws CXJsException
	 */
	public Object eval(CXJsSourceMain aMainModule) throws CXJsException {
		return eval(aMainModule, (IXjsTracer) null);
	}

	/**
	 * @param aMainModule
	 * @param aBinding
	 * @return
	 * @throws CXJsException
	 */
	public Object eval(CXJsSourceMain aMainModule, Bindings aBinding)
			throws CXJsException {
		return eval(aMainModule, aBinding, (IXjsTracer) null);
	}

	/**
	 * MOD_OG_20170615
	 *
	 * Executes the script using the <code>Bindings</code> argument as the
	 * <code>ENGINE_SCOPE</code>
	 *
	 * @param aMainModule
	 * @param aBinding
	 *            a ENGINE_SCOPE bindings
	 * @param aJsTracer
	 * @return
	 * @throws CXJsException
	 */
	public Object eval(CXJsSourceMain aMainModule, Bindings aBinding,
			IXjsTracer aJsTracer) throws CXJsException {

		final ScriptContext wScriptContext = new CXJsRuningContext(1024,
				aBinding);

		return eval(aMainModule, wScriptContext, aJsTracer);
	}

	/**
	 * MOD_OG_20170615
	 *
	 * @param aMainModule
	 * @param aJsTracer
	 * @return
	 * @throws CXJsException
	 */
	public Object eval(CXJsSourceMain aMainModule, IXjsTracer aJsTracer)
			throws CXJsException {

		return eval(aMainModule, (ScriptContext) null, aJsTracer);
	}

	/**
	 * @param aMainModule
	 * @param aCtx
	 * @return
	 * @throws CXJsException
	 */
	public Object eval(CXJsSourceMain aMainModule, ScriptContext aScriptContext)
			throws CXJsException {
		return eval(aMainModule, aScriptContext, (IXjsTracer) null);
	}

	/**
	 * @param aMainModule
	 * @param aScriptContext
	 * @param aJsTracer
	 * @return
	 * @throws CXJsException
	 */
	public Object eval(CXJsSourceMain aMainModule,
			ScriptContext aScriptContext, IXjsTracer aJsTracer)
			throws CXJsException {
		Object wRes = null;
		final boolean wTraceOn = aJsTracer != null;
		final CXTimer wTimer = wTraceOn ? new CXTimer("evalCtx", true) : null;
		try {
			// MOD_OG_20170615 add the TRACER and a FORMATER in the script
			// context
			if (wTraceOn) {
				aScriptContext.setAttribute("TRACER", aJsTracer,
						ScriptContext.ENGINE_SCOPE);
			}
			aScriptContext.setAttribute("FORMATER", new CXJsFormater(),
					ScriptContext.ENGINE_SCOPE);

			wRes = pEngine.eval(aMainModule.getMergedCode(), aScriptContext);

		} catch (final ScriptException e) {
			CXJsExcepRhino.throwMyScriptExcep(this, aMainModule, aJsTracer, e,
					"evalCtx");
		} catch (final Exception e) {
			throwMyScriptExcep(aMainModule, aJsTracer,
					"Error evaluating script", e, "evalCtx");
		} finally {
			if (wTraceOn) {
				wTimer.stop();
				aJsTracer.trace(wTimer.toDescription());
			}
		}
		return wRes;
	}

	/**
	 * Pour eval - Ajout dans ENGINE_SCOPE
	 *
	 * @param key
	 * @param value
	 */
	public void evalPut(String key, Object value) {
		pEngine.put(key, value);
	}

	/**
	 * @return
	 */
	public CXJsScriptFactory getFactory() {
		return pFactory;
	}

	/**
	 * @return
	 */
	protected Invocable getInvocable() {
		return (Invocable) pEngine;
	}

	/**
	 * @param akey
	 * @return
	 */
	public Object getJSObject(String akey) {
		return pEngine.get(akey);
	}

	/**
	 * @return
	 */
	public String getLanguage() {
		return pFactory.getCallName();
	}

	/**
	 * @return
	 */
	public ScriptEngine getScriptEngine() {
		return pEngine;
	}

	/**
	 * @return
	 */
	public boolean isCompilable() {
		return pEngine instanceof Compilable;
	}

	/**
	 * @return
	 */
	public boolean isInvocable() {
		return pEngine instanceof Invocable;
	}

	/**
	 * pour compatibilite ascendante
	 *
	 * @param aMainModule
	 * @return
	 * @throws CXJsException
	 */
	public CompiledScript reCompile(CXJsSourceMain aMainModule)
			throws CXJsException {
		return reCompile(aMainModule, CXjsTracerNull.getInstance());
	}

	public CompiledScript reCompile(CXJsSourceMain aMainModule,
			IXjsTracer tracer) throws CXJsException {
		if (tracer != null) {
			tracer.trace("reCompile");
		}
		if (aMainModule == null) {
			throwMyScriptExcep(aMainModule, tracer,
					"Can't recompile script - Script has never been compiled - Language["
							+ getLanguage() + "]", "reCompile");
		}
		aMainModule.reload(tracer);
		return doCompile(aMainModule, "reCompile", tracer);
	}

	/**
	 * @param aMainModule
	 * @param tracer
	 * @param aErrMsg
	 * @param aAction
	 * @throws CXJsException
	 */
	protected void throwMyScriptExcep(CXJsSourceMain aMainModule,
			IXjsTracer tracer, String aErrMsg, String aAction)
			throws CXJsException {
		if (tracer != null) {
			tracer.trace(aAction + "Error[" + aErrMsg + "]");
		}
		throw new CXJsException(aMainModule, aErrMsg, null, aAction);
	}

	/**
	 * @param aMainModule
	 * @param tracer
	 * @param aErrMsg
	 * @param e
	 * @param aAction
	 * @throws CXJsException
	 */
	protected void throwMyScriptExcep(CXJsSourceMain aMainModule,
			IXjsTracer tracer, String aErrMsg, Throwable e, String aAction)
			throws CXJsException {
		if (tracer != null) {
			tracer.trace(this, aAction + "Error[" + aErrMsg + "]", e);
		}
		throw new CXJsException(aMainModule, aErrMsg, e, aAction);
	}
}
