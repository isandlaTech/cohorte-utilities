package org.psem2m.utilities.scripting;

import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.psem2m.utilities.logging.CActivityLoggerNull;
import org.psem2m.utilities.logging.IActivityLogger;
import org.psem2m.utilities.rsrc.CXRsrcProvider;
import org.psem2m.utilities.rsrc.CXRsrcUriPath;

/**
 * #12 Manage chains of resource providers
 *
 * @author ogattaz
 *
 */
public class CXJsManager extends CXJsObjectBase implements IXJsManager {

	/**
	 * @return
	 */
	public static String dumpAvailableEngines() {
		return dumpAvailableEngines(new ScriptEngineManager());
	}

	/**
	 * @param aEngineManager
	 * @return
	 */
	public static String dumpAvailableEngines(final ScriptEngineManager aEngineManager) {

		if (aEngineManager == null) {
			return "the EngineManager is null";
		}

		StringBuilder wSB = new StringBuilder();
		int wIdx = 0;
		for (ScriptEngineFactory wFactory : aEngineManager.getEngineFactories()) {
			descrAddProp(wSB, "", wIdx++);
			descrAddLine(wSB, wFactory.getLanguageName());
			descrAddIndent(wSB, new CXJsScriptFactory(wFactory).toDescription());
		}
		return wSB.toString();
	}

	private final IActivityLogger pActivityLogger;

	private final ScriptEngineManager pEngineManager;

	private final CXJsRunnerMap pJsRunnerMap = new CXJsRunnerMap();

	private boolean pMustCheckTimeStamp = true;

	// #12 Manage chains of resource providers
	private CXRsrcProvider pRsrcProviderChain = null;

	private final CXJsScriptFactory pScriptEngineFactory;

	/**
	 * @param aActivityLogger
	 * @param aScriptEngineManager
	 * @param aName
	 * @throws CXJsExcepUnknownLanguage
	 */
	public CXJsManager(final IActivityLogger aActivityLogger, final ScriptEngineManager aScriptEngineManager,
			final String aName) throws CXJsExcepUnknownLanguage {

		super();
		pActivityLogger = aActivityLogger;

		if (aScriptEngineManager == null) {
			throw new CXJsExcepUnknownLanguage("CXJsManager", "No language, the given ScriptEngineManager is null");
		}

		pEngineManager = aScriptEngineManager;
		CXJsScriptFactory wXJsScriptFactory = null;

		// search the factory of the language in the list build by the Discovery
		// mechanism of the ScriptEngineManager.
		for (ScriptEngineFactory wFact : aScriptEngineManager.getEngineFactories()) {
			if (CXJsScriptFactory.checkName(wFact, aName)) {
				wXJsScriptFactory = CXJsScriptFactory.newInstance(wFact, aName);
				break;
			}
		}

		// search the factory of the language in the name associations added
		// after the Discovery mechanism of the ScriptEngineManager.
		if (wXJsScriptFactory == null) {
			ScriptEngine wEngine = aScriptEngineManager.getEngineByName(aName);
			if (wEngine != null) {
				wXJsScriptFactory = CXJsScriptFactory.newInstance(wEngine.getFactory(), aName);
			}
		}

		if (wXJsScriptFactory == null) {
			throw new CXJsExcepUnknownLanguage("CXJsManager",
					"The scripting language [%s] is not registered in the current ScriptEngineManager [%s]", aName,
					aScriptEngineManager);
		}
		pScriptEngineFactory = wXJsScriptFactory;
	}

	/**
	 * @param aScriptLanguage
	 * @throws CXJsExcepUnknownLanguage
	 */
	public CXJsManager(final IActivityLogger aActivityLogger, final String aScriptLanguage)
			throws CXJsExcepUnknownLanguage {
		this(aActivityLogger, new ScriptEngineManager(), aScriptLanguage);
	}

	/**
	 * @param aScriptEngineManager
	 * @param aScriptLanguage
	 * @throws CXJsExcepUnknownLanguage
	 */
	public CXJsManager(final ScriptEngineManager aScriptEngineManager, final String aScriptLanguage)
			throws CXJsExcepUnknownLanguage {

		this(CActivityLoggerNull.getInstance(), aScriptEngineManager, aScriptLanguage);
	}

	/**
	 * @param aScriptLanguage
	 * @throws CXJsExcepUnknownLanguage
	 */
	public CXJsManager(final String aScriptLanguage) throws CXJsExcepUnknownLanguage {
		this(new ScriptEngineManager(), aScriptLanguage);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.scripting.CXJsObjectBase#addDescriptionInBuffer(
	 * java.lang.Appendable)
	 */
	@Override
	public Appendable addDescriptionInBuffer(final Appendable aSB) {

		descrAddLine(aSB, "Source providers");
		if (pRsrcProviderChain != null) {
			descrAddLine(aSB, "Nb providers available", pRsrcProviderChain.size());
			descrAddIndent(aSB, pRsrcProviderChain.toDescription());
		} else {
			descrAddLine(aSB, "NO PROVIDER available");

		}

		descrAddLine(aSB, "Current ScriptEngineFactory");
		if (pScriptEngineFactory != null) {
			descrAddIndent(aSB, pScriptEngineFactory.toDescription());
		} else {
			descrAddLine(aSB, "NO FACTORY available");
		}

		if (hasEngineManager()) {
			descrAddLine(aSB, "Script engine manager");
			descrAddLine(aSB, "Nb languages available", pEngineManager.getEngineFactories().size());
			descrAddIndent(aSB, getAvailableLanguages());
		}
		return aSB;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.scripting.IJsManager#addProvider(java.lang.String,
	 * org.psem2m.utilities.rsrc.CXRsrcProvider)
	 */
	@Override
	public void addProvider(final String aProviderId, final CXRsrcProvider aProvider) {
		if (pRsrcProviderChain != null) {
			pRsrcProviderChain.add(aProvider);
		} else {
			pRsrcProviderChain = aProvider;
		}
	}

	/**
	 * @param aName
	 * @return
	 */
	public boolean checkName(final String aName) {
		return pScriptEngineFactory.checkName(aName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.scripting.IJsManager#clearCache()
	 */
	@Override
	public int clearCache() {
		int wNbCleared = pJsRunnerMap.size();
		pJsRunnerMap.clear();
		return wNbCleared;
	}

	/**
	 * Compile
	 *
	 * @param aRootSrc
	 * @param aRelativePath
	 * @param aCheckTimeStamp
	 * @param tracer
	 * @return
	 * @throws CXJsException
	 */
	public CXJsCompiledScript compileFromFile(final CXRsrcProvider aRootSrc, final CXRsrcUriPath aRelativePath,
			final boolean aCheckTimeStamp, final IXjsTracer tracer) throws CXJsException {
		CXJsEngine wEngine = pScriptEngineFactory.getScriptEngine();
		return wEngine.compile(getMainSource(aRootSrc, aRelativePath, tracer), aCheckTimeStamp, tracer);
	}

	/**
	 *
	 */
	public void destroy() {
		pJsRunnerMap.destroy();
	}

	public Object evalExpression(final String aString) throws ScriptException {
		return getScriptEngine().getScriptEngine().eval(aString);
	}

	/**
	 * @return
	 */
	private IActivityLogger getActivityLogger() {
		return pActivityLogger;
	}

	/**
	 * @return
	 */
	public String getAvailableLanguages() {
		return dumpAvailableEngines(pEngineManager);
	}

	public CXJsCompiledScript getCompiledScript(CXJsSourceMain aSourceMainS) throws CXJsException {
		return getScriptEngine().compile(aSourceMainS, false);
	}

	/**
	 * @return
	 */
	public String getLanguage() {
		return pScriptEngineFactory == null ? "Null" : pScriptEngineFactory.getCallName();
	}

	/**
	 * @param aRootSrc
	 * @param aRelativePath
	 * @return
	 * @throws CXJsException
	 */
	public CXJsSourceMain getMainSource(final CXRsrcProvider aRootSrc, final CXRsrcUriPath aRelativePath)
			throws CXJsException {
		return getMainSource(aRootSrc, aRelativePath, CXjsTracerNull.getInstance());
	}

	/**
	 * @param aRootSrc
	 * @param aRelativePath
	 * @param tracer
	 * @return
	 * @throws CXJsException
	 */
	public CXJsSourceMain getMainSource(final CXRsrcProvider aRootSrc, final CXRsrcUriPath aRelativePath,
			final IXjsTracer tracer) throws CXJsException {
		return CXJsSourceMain.newInstanceFromFile(aRootSrc, aRelativePath, getLanguage(), tracer);
	}

	/**
	 * @param aRelativePath
	 * @return
	 * @throws CXJsException
	 */
	public CXJsSourceMain getMainSource(final CXRsrcUriPath aRelativePath) throws CXJsException {
		return getMainSource(aRelativePath, CXjsTracerNull.getInstance());
	}

	/**
	 * @param aRelativePath
	 * @param tracer
	 * @return
	 * @throws CXJsException
	 */
	public CXJsSourceMain getMainSource(final CXRsrcUriPath aRelativePath, final IXjsTracer tracer)
			throws CXJsException {

		try {
			return CXJsSourceMain.newInstanceFromFile(pRsrcProviderChain, aRelativePath, getLanguage(), tracer);
		} catch (CXJsExcepLoad e) {

			// can't get main source from all the providers
			throw new CXJsException(e, "Unable to find [%s] in the list of providers : %s", aRelativePath,
					pRsrcProviderChain);
		}

	}

	/**
	 * @param aSource
	 * @param aRootSrc
	 * @param tracer
	 * @return
	 * @throws CXJsException
	 */
	public CXJsSourceMain getMainSource(final String aSource, final CXRsrcProvider aRootSrc, final IXjsTracer tracer)
			throws CXJsException {
		return CXJsSourceMain.newInstanceFromSource(aRootSrc, null, aSource, getLanguage(), tracer);
	}

	/**
	 * @param aSource
	 * @param aRootSrc
	 * @param aMairSrcRelPath
	 * @return
	 * @throws CXJsException
	 */
	public CXJsSourceMain getMainSource(final String aSource, final CXRsrcProvider aRootSrc,
			final String aMairSrcRelPath) throws CXJsException {
		return CXJsSourceMain.newInstanceFromSource(aRootSrc, aMairSrcRelPath, aSource, getLanguage(),
				CXjsTracerNull.getInstance());
	}

	/**
	 * @param aSource
	 * @param aRootSrc
	 * @param aMairSrcRelPath
	 * @param tracer
	 * @return
	 * @throws CXJsException
	 */
	public CXJsSourceMain getMainSource(final String aSource, final CXRsrcProvider aRootSrc,
			final String aMairSrcRelPath, final IXjsTracer tracer) throws CXJsException {
		return CXJsSourceMain.newInstanceFromSource(aRootSrc, aMairSrcRelPath, aSource, getLanguage(), tracer);
	}

	/**
	 * @param aSource
	 * @param tracer
	 * @return
	 * @throws CXJsException
	 */
	public CXJsSourceMain getMainSource(final String aSource, final IXjsTracer tracer) throws CXJsException {
		return getMainSource(aSource, (String) null, tracer);
	}

	/**
	 * @param aSource
	 * @param aMairSrcRelPath
	 * @return
	 * @throws CXJsException
	 */
	public CXJsSourceMain getMainSource(final String aSource, final String aMairSrcRelPath) throws CXJsException {
		return getMainSource(aSource, aMairSrcRelPath, CXjsTracerNull.getInstance());
	}

	/**
	 * @param aSource
	 * @param aMairSrcRelPath
	 * @param tracer
	 * @return
	 * @throws CXJsException
	 */
	public CXJsSourceMain getMainSource(final String aSource, final String aMairSrcRelPath, final IXjsTracer tracer)
			throws CXJsException {

		try {
			return CXJsSourceMain.newInstanceFromSource(pRsrcProviderChain, aMairSrcRelPath, aSource, getLanguage(),
					tracer);
		} catch (CXJsExcepLoad e) {
			// can't get main source from all the providers
			throw new CXJsException(e, "Unable to find [%s] in the chain of providers [%s]", aSource,
					pRsrcProviderChain);
		}
	}

	/**
	 * #12 Manage chains of resource providers
	 *
	 * @return
	 */
	public CXRsrcProvider getRsrcProviderChain() {
		return pRsrcProviderChain;
	}

	/**
	 * @return
	 */
	public CXJsEngine getScriptEngine() {
		return pScriptEngineFactory.getScriptEngine();
	}

	/**
	 * @return
	 */
	public CXJsScriptFactory getScriptEngineFactory() {
		return pScriptEngineFactory;
	}

	/**
	 * @param aMainModule
	 * @return
	 */
	public CXJsEngineInvocable getScriptEngineInvocable(final CXJsSourceMain aMainModule) {
		return pScriptEngineFactory.getScriptEngineInvocable(aMainModule);
	}

	/**
	 * @return
	 */
	public boolean hasEngineManager() {
		return pEngineManager != null;
	}

	/**
	 * @return
	 */
	public boolean isMultiThreaded() {
		return pScriptEngineFactory.isMultiThreaded();
	}

	/**
	 * @return
	 */
	public boolean mustCheckTimeStamp() {

		return pMustCheckTimeStamp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.scripting.IJsManager#newRuningContext(int)
	 */
	@Override
	public IXJsRuningContext newRuningContext(final int aBufferSize) throws Exception {
		return new CXJsRuningContext(aBufferSize);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.psem2m.utilities.scripting.IJsManager#newRunner(org.psem2m.utilities.
	 * scripting.CXJsSourceMain, org.psem2m.utilities.scripting.CXJsEngine,
	 * java.lang.String)
	 */
	@Override
	public CXJsRunner newRunner(final IActivityLogger aActivityLogger, final CXJsSourceMain aJsSourceMain,
			final CXJsEngine aEngine, final String aScriptUri) throws Exception {

		return new CXJsRunner(aActivityLogger, aJsSourceMain, aEngine, aScriptUri);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.psem2m.utilities.scripting.IJsManager#removeProvider(java.lang.String )
	 */
	@Override
	public void removeProvider(final String aProviderId) {

	}

	@Override
	public IXJsRuningReply runScript(final IActivityLogger aActivityLogger, final CXJsCompiledScript aCompiledScript,
			final Map<String, Object> aVariablesMap) throws Exception {

		IXJsRuningContext wCtx = newRuningContext(-1);

		if (aVariablesMap != null && aVariablesMap.size() > 0) {
			for (Map.Entry<String, Object> wProp : aVariablesMap.entrySet()) {
				wCtx.setAttrEngine(wProp.getKey(), wProp.getValue());
			}
		}
		// Exec
		try {
			aCompiledScript.eval(wCtx.start(CXJsRuningContext.ACT_EVAL_COMPILED, 0));
			return wCtx;
		} catch (Exception e) {

			throw e;
		} finally {
			getActivityLogger().logDebug(this, "runScript", "Duration=", wCtx.getTimerInfo());
		}
	}

	@Override
	public IXJsRuningReply runScript(final IActivityLogger aActivityLogger, final CXJsSourceMain aSourceMain,
			final Map<String, Object> aVariablesMap) throws Exception {
		return runScript(aActivityLogger, null, aSourceMain, aVariablesMap);
	}

	@Override
	public IXJsRuningReply runScript(final IActivityLogger aActivityLogger, final String aProviderId,
			final CXJsSourceMain aSourceMain, final Map<String, Object> aVariablesMap) throws Exception {

		CXJsRunner wRunner = null;
		String wScriptUri = aSourceMain.getScriptUri();
		// find the runner in the cache
		if (wScriptUri != null && pJsRunnerMap.containsKey(wScriptUri)) {
			wRunner = pJsRunnerMap.get(wScriptUri);
		}
		// if the runner exists in the cache
		if (wRunner != null) {
			// CheckTimeStamp to invalidate the runner ?
			if (mustCheckTimeStamp() && !wRunner.checkMainTimeStamp()) {
				// remove the runner from the cache
				pJsRunnerMap.remove(wScriptUri);
				wRunner = null;
			}
		}
		// if the runner doesn't exist in the cache
		if (wRunner == null) {

			// use the given ActivityLogger of that associated to that manager
			IActivityLogger wActivityLogger = (aActivityLogger != null) ? aActivityLogger : pActivityLogger;

			wRunner = newRunner(wActivityLogger, aSourceMain, getScriptEngine(), aSourceMain.getSourceName());
			if (wScriptUri != null) {
				pJsRunnerMap.put(wScriptUri, wRunner);
			}
		}

		IXJsRuningContext wCtx = newRuningContext(-1);

		if (aVariablesMap != null && aVariablesMap.size() > 0) {
			for (Map.Entry<String, Object> wProp : aVariablesMap.entrySet()) {
				wCtx.setAttrEngine(wProp.getKey(), wProp.getValue());
			}
		}
		// Exec
		try {
			return wRunner.run(wCtx);

		} catch (Exception e) {
			if (wScriptUri != null) {
				wRunner = pJsRunnerMap.remove(wScriptUri);
			}
			if (wRunner != null) {
				getActivityLogger().logDebug(this, "runScript", "JsRunner=", wRunner.pId, "removed.");
			}
			throw e;
		} finally {
			getActivityLogger().logDebug(this, "runScript", "Duration=", wCtx.getTimerInfo());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.scripting.IJsManager#runScript(java.lang.String,
	 * java.util.Map)
	 */
	@Override
	public IXJsRuningReply runScript(final IActivityLogger aActivityLogger, final String aScriptUri,
			final Map<String, Object> aVariablesMap) throws Exception {
		return runScript(aActivityLogger, null, aScriptUri, aVariablesMap);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.scripting.IJsManager#runScript(java.lang.String,
	 * java.lang.String, java.util.Map)
	 */
	@Override
	public IXJsRuningReply runScript(final IActivityLogger aActivityLogger, final String aProviderId,
			final String aScriptUri, final Map<String, Object> aVariablesMap) throws Exception {
		IActivityLogger wActivityLogger = (aActivityLogger != null) ? aActivityLogger : pActivityLogger;
		CXJsSourceMain wSourceMain = getMainSource(new CXRsrcUriPath(aScriptUri),
				CXjsTracerFactory.newJsTracer(wActivityLogger));
		return runScript(aActivityLogger, aProviderId, wSourceMain, aVariablesMap);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.scripting.IXJsManager#runScript(java.lang.String)
	 */
	@Override
	public IXJsRuningReply runScript(final String aScriptUri) throws Exception {
		// runs script whithout explicit map of variables,
		return runScript(aScriptUri, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.scripting.IXJsManager#runScript(java.lang.String,
	 * java.util.Map)
	 */
	@Override
	public IXJsRuningReply runScript(final String aScriptUri, final Map<String, Object> aVariablesMap)
			throws Exception {
		// runs script whithout explicit ActivityLogger and ProviderId,
		return runScript(null, null, aScriptUri, aVariablesMap);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.scripting.IXJsManager#runScript(java.lang.String,
	 * java.lang.String, java.util.Map)
	 */
	@Override
	public IXJsRuningReply runScript(final String aProviderId, final String aScriptUri,
			final Map<String, Object> aVariablesMap) throws Exception {

		// runs script whithout explicit ActivityLogger and ProviderId,
		return runScript(null, null, aScriptUri, aVariablesMap);
	}

	/**
	 * @return
	 */
	public void setCheckTimeStamp(final boolean aFlag) {

		pMustCheckTimeStamp = aFlag;
	}

	/**
	 * #12 Manage chains of resource providers
	 *
	 * @param aRsrcProviderChain
	 */
	public void setRsrcProviderChain(final CXRsrcProvider aRsrcProviderChain) {
		pRsrcProviderChain = aRsrcProviderChain;
	}
}
