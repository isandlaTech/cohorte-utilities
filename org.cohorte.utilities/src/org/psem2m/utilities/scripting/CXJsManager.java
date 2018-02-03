package org.psem2m.utilities.scripting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

import org.psem2m.utilities.logging.CActivityLoggerNull;
import org.psem2m.utilities.logging.IActivityLogger;
import org.psem2m.utilities.rsrc.CXRsrcProvider;
import org.psem2m.utilities.rsrc.CXRsrcUriPath;

/**
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
	public static String dumpAvailableEngines(
			final ScriptEngineManager aEngineManager) {

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

	private final List<CXRsrcProvider> pRsrcProviders = new ArrayList<>();

	private final CXJsScriptFactory pScriptEngineFactory;

	/**
	 * @param aActivityLogger
	 * @param aScriptEngineManager
	 * @param aName
	 * @throws CXJsExcepUnknownLanguage
	 */
	public CXJsManager(final IActivityLogger aActivityLogger,
			final ScriptEngineManager aScriptEngineManager, final String aName)
			throws CXJsExcepUnknownLanguage {

		super();
		pActivityLogger = aActivityLogger;

		if (aScriptEngineManager == null) {
			throw new CXJsExcepUnknownLanguage("CXJsManager",
					"No language, the given ScriptEngineManager is null");
		}

		pEngineManager = aScriptEngineManager;
		CXJsScriptFactory wXJsScriptFactory = null;

		// search the factory of the language in the list build by the Discovery
		// mechanism of the ScriptEngineManager.
		for (ScriptEngineFactory wFact : aScriptEngineManager
				.getEngineFactories()) {
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
				wXJsScriptFactory = CXJsScriptFactory.newInstance(
						wEngine.getFactory(), aName);
			}
		}

		if (wXJsScriptFactory == null) {
			throw new CXJsExcepUnknownLanguage(
					"CXJsManager",
					"The scripting language [%s] is not registered in the current ScriptEngineManager [%s]",
					aName, aScriptEngineManager);
		}
		pScriptEngineFactory = wXJsScriptFactory;
	}

	/**
	 * @param aScriptEngineManager
	 * @param aScriptLanguage
	 * @throws CXJsExcepUnknownLanguage
	 */
	public CXJsManager(final ScriptEngineManager aScriptEngineManager,
			final String aScriptLanguage) throws CXJsExcepUnknownLanguage {

		this(CActivityLoggerNull.getInstance(), aScriptEngineManager,
				aScriptLanguage);
	}

	/**
	 * @param aScriptLanguage
	 * @throws CXJsExcepUnknownLanguage
	 */
	public CXJsManager(String aScriptLanguage) throws CXJsExcepUnknownLanguage {
		this(new ScriptEngineManager(), aScriptLanguage);
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

		descrAddLine(aSB, "Source providers");
		descrAddLine(aSB, "Nb providers available", pRsrcProviders.size());
		for (CXRsrcProvider wProvider : pRsrcProviders) {
			descrAddIndent(aSB, wProvider.toDescription());
		}

		descrAddLine(aSB, "Current ScriptEngineFactory");
		descrAddIndent(aSB, pScriptEngineFactory.toDescription());

		if (hasEngineManager()) {
			descrAddLine(aSB, "Script engine manager");
			descrAddLine(aSB, "Nb languages available", pEngineManager
					.getEngineFactories().size());
			descrAddIndent(aSB, getAvailableLanguages());
		}
		return aSB;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.psem2m.utilities.scripting.IJsManager#addProvider(java.lang.String,
	 * org.psem2m.utilities.rsrc.CXRsrcProvider)
	 */
	@Override
	public void addProvider(final String aProviderId,
			final CXRsrcProvider aProvider) {
		pRsrcProviders.add(aProvider);
	}

	/**
	 * @param aName
	 * @return
	 */
	public boolean checkName(String aName) {
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
	public CXJsCompiledScript compileFromFile(CXRsrcProvider aRootSrc,
			CXRsrcUriPath aRelativePath, boolean aCheckTimeStamp,
			IXjsTracer tracer) throws CXJsException {
		CXJsEngine wEngine = pScriptEngineFactory.getScriptEngine();
		return wEngine.compile(getMainSource(aRootSrc, aRelativePath, tracer),
				aCheckTimeStamp, tracer);
	}

	/**
	 * 
	 */
	public void destroy() {
		pJsRunnerMap.destroy();
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

	/**
	 * @return
	 */
	public String getLanguage() {
		return pScriptEngineFactory == null ? "Null" : pScriptEngineFactory
				.getCallName();
	}

	/**
	 * @param aRootSrc
	 * @param aRelativePath
	 * @return
	 * @throws CXJsException
	 */
	public CXJsSourceMain getMainSource(CXRsrcProvider aRootSrc,
			CXRsrcUriPath aRelativePath) throws CXJsException {
		return getMainSource(aRootSrc, aRelativePath,
				CXjsTracerNull.getInstance());
	}

	/**
	 * @param aRootSrc
	 * @param aRelativePath
	 * @param tracer
	 * @return
	 * @throws CXJsException
	 */
	public CXJsSourceMain getMainSource(CXRsrcProvider aRootSrc,
			CXRsrcUriPath aRelativePath, IXjsTracer tracer)
			throws CXJsException {
		return CXJsSourceMain.newInstanceFromFile(aRootSrc, aRelativePath,
				getLanguage(), tracer);
	}

	/**
	 * @param aRelativePath
	 * @return
	 * @throws CXJsException
	 */
	public CXJsSourceMain getMainSource(CXRsrcUriPath aRelativePath)
			throws CXJsException {
		return getMainSource(aRelativePath, CXjsTracerNull.getInstance());
	}

	/**
	 * @param aRelativePath
	 * @param tracer
	 * @return
	 * @throws CXJsException
	 */
	public CXJsSourceMain getMainSource(CXRsrcUriPath aRelativePath,
			IXjsTracer tracer) throws CXJsException {

		CXJsExcepLoad wCXJsExcepLoad = null;
		for (CXRsrcProvider wProvider : pRsrcProviders) {
			try {
				return CXJsSourceMain.newInstanceFromFile(wProvider,
						aRelativePath, getLanguage(), tracer);
			} catch (CXJsExcepLoad e) {
				// memo
				wCXJsExcepLoad = e;
				// try to find in next provider
			}
		}
		// can't get main source from all the providers
		throw new CXJsException(wCXJsExcepLoad,
				"Unable to find [%s] in the list of providers [%s]",
				aRelativePath, pRsrcProviders);
	}

	/**
	 * @param aSource
	 * @param aRootSrc
	 * @param tracer
	 * @return
	 * @throws CXJsException
	 */
	public CXJsSourceMain getMainSource(String aSource,
			CXRsrcProvider aRootSrc, IXjsTracer tracer) throws CXJsException {
		return CXJsSourceMain.newInstanceFromSource(aRootSrc, null, aSource,
				getLanguage(), tracer);
	}

	/**
	 * @param aSource
	 * @param aRootSrc
	 * @param aMairSrcRelPath
	 * @return
	 * @throws CXJsException
	 */
	public CXJsSourceMain getMainSource(String aSource,
			CXRsrcProvider aRootSrc, String aMairSrcRelPath)
			throws CXJsException {
		return CXJsSourceMain.newInstanceFromSource(aRootSrc, aMairSrcRelPath,
				aSource, getLanguage(), CXjsTracerNull.getInstance());
	}

	/**
	 * @param aSource
	 * @param aRootSrc
	 * @param aMairSrcRelPath
	 * @param tracer
	 * @return
	 * @throws CXJsException
	 */
	public CXJsSourceMain getMainSource(String aSource,
			CXRsrcProvider aRootSrc, String aMairSrcRelPath, IXjsTracer tracer)
			throws CXJsException {
		return CXJsSourceMain.newInstanceFromSource(aRootSrc, aMairSrcRelPath,
				aSource, getLanguage(), tracer);
	}

	/**
	 * @param aSource
	 * @param tracer
	 * @return
	 * @throws CXJsException
	 */
	public CXJsSourceMain getMainSource(String aSource, IXjsTracer tracer)
			throws CXJsException {
		return getMainSource(aSource, (String) null, tracer);
	}

	/**
	 * @param aSource
	 * @param aMairSrcRelPath
	 * @return
	 * @throws CXJsException
	 */
	public CXJsSourceMain getMainSource(String aSource, String aMairSrcRelPath)
			throws CXJsException {
		return getMainSource(aSource, aMairSrcRelPath,
				CXjsTracerNull.getInstance());
	}

	/**
	 * @param aSource
	 * @param aMairSrcRelPath
	 * @param tracer
	 * @return
	 * @throws CXJsException
	 */
	public CXJsSourceMain getMainSource(String aSource, String aMairSrcRelPath,
			IXjsTracer tracer) throws CXJsException {

		CXJsExcepLoad wCXJsExcepLoad = null;
		for (CXRsrcProvider wProvider : pRsrcProviders) {
			try {
				return CXJsSourceMain.newInstanceFromSource(wProvider,
						aMairSrcRelPath, aSource, getLanguage(), tracer);
			} catch (CXJsExcepLoad e) {
				// memo
				wCXJsExcepLoad = e;
				// try to find in next provider
			}
		}
		// can't get main source from all the providers
		throw new CXJsException(wCXJsExcepLoad,
				"Unable to find [%s] in the list of providers [%s]", aSource,
				pRsrcProviders);
	}

	public List<CXRsrcProvider> getProviders() {
		return pRsrcProviders;
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
	public CXJsEngineInvocable getScriptEngineInvocable(
			CXJsSourceMain aMainModule) {
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
	public IXJsRuningContext newRuningContext(int aBufferSize) throws Exception {
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
	public CXJsRunner newRunner(final IActivityLogger aActivityLogger,
			final CXJsSourceMain aMain, final CXJsEngine aEngine,
			final String aScriptUri) throws Exception {

		IActivityLogger wActivityLogger = (aActivityLogger != null) ? pActivityLogger
				: pActivityLogger;

		return new CXJsRunner(wActivityLogger, aMain, aEngine, aScriptUri);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.psem2m.utilities.scripting.IJsManager#removeProvider(java.lang.String
	 * )
	 */
	@Override
	public void removeProvider(String aProviderId) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.psem2m.utilities.scripting.IJsManager#runScript(java.lang.String,
	 * java.util.Map)
	 */
	@Override
	public IXJsRuningReply runScript(IActivityLogger aActivityLogger,
			final String aScriptUri, final Map<String, Object> aVariablesMap)
			throws Exception {
		return runScript(aActivityLogger, null, aScriptUri, aVariablesMap);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.psem2m.utilities.scripting.IJsManager#runScript(java.lang.String,
	 * java.lang.String, java.util.Map)
	 */
	@Override
	public IXJsRuningReply runScript(IActivityLogger aActivityLogger,
			final String aProviderId, final String aScriptUri,
			final Map<String, Object> aVariablesMap) throws Exception {

		CXJsRunner wRunner = null;

		if (pJsRunnerMap.containsKey(aScriptUri)) {
			wRunner = pJsRunnerMap.get(aScriptUri);
		}

		if (wRunner != null) {
			// CheckTimeStamp to invalidate the runner ?
			if (mustCheckTimeStamp() && !wRunner.checkMainTimeStamp()) {
				pJsRunnerMap.remove(aScriptUri);
				wRunner = null;
			}
		}
		if (wRunner == null) {

			// use the given ActivityLogger of that associated to that manager
			CXjsTracerActivity wJsTracerActivity = new CXjsTracerActivity(
					((aActivityLogger != null) ? aActivityLogger
							: pActivityLogger));

			CXJsSourceMain wSourceMain = getMainSource(new CXRsrcUriPath(
					aScriptUri), wJsTracerActivity);

			wRunner = newRunner(aActivityLogger, wSourceMain,
					getScriptEngine(), wSourceMain.getSourceName());
			pJsRunnerMap.put(aScriptUri, wRunner);
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
			wRunner = pJsRunnerMap.remove(aScriptUri);
			if (wRunner != null) {
				getActivityLogger().logDebug(this, "runScript", "JsRunner=",
						wRunner.pId, "removed.");
			}
			throw e;
		} finally {
			getActivityLogger().logDebug(this, "runScript", "Duration=",
					wCtx.getTimerInfo());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.psem2m.utilities.scripting.IXJsManager#runScript(java.lang.String,
	 * java.util.Map)
	 */
	@Override
	public IXJsRuningReply runScript(String aScriptUri,
			Map<String, Object> aVariablesMap) throws Exception {

		// runs script whithout explicit ActivityLogger and ProviderId,
		return runScript(null, null, aScriptUri, aVariablesMap);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.psem2m.utilities.scripting.IXJsManager#runScript(java.lang.String,
	 * java.lang.String, java.util.Map)
	 */
	@Override
	public IXJsRuningReply runScript(String aProviderId, String aScriptUri,
			Map<String, Object> aVariablesMap) throws Exception {

		// runs script whithout explicit ActivityLogger and ProviderId,
		return runScript(null, null, aScriptUri, aVariablesMap);
	}

	/**
	 * @return
	 */
	public void setCheckTimeStamp(boolean aFlag) {

		pMustCheckTimeStamp = aFlag;
	}
}
