package org.psem2m.utilities.scripting;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.psem2m.utilities.CXTimer;
import org.psem2m.utilities.rsrc.CXRsrcProvider;
import org.psem2m.utilities.rsrc.CXRsrcText;
import org.psem2m.utilities.rsrc.CXRsrcUriDir;
import org.psem2m.utilities.rsrc.CXRsrcUriPath;

/**
 * #12 Manage chains of resource providers
 *
 * @author ogattaz
 *
 */
public class CXJsSourceMain extends CXJsSource {

	/**
	 * @param aRsrcProviderChain
	 * @param aRelPath
	 * @param aLanguage
	 * @param tracer
	 * @return
	 * @throws CXJsException
	 */
	public static CXJsSourceMain newInstanceFromFile(final CXRsrcProvider aRsrcProviderChain,
			final CXRsrcUriPath aRelPath, final String aLanguage, final IXjsTracer tracer) throws CXJsException {
		CXJsSourceMain wResult;
		try {
			wResult = new CXJsSourceMain(aRsrcProviderChain, aRelPath.getURI().getPath(), aLanguage);

			wResult.loadFromFile(aRelPath, tracer);
			return wResult;
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			throw new CXJsException("failed create SourceMain ERROR=%s", e);
		}
	}

	/**
	 * Acces au JS qui pointe sur le repertoires des script aMairSrcRelPath : Path
	 * relatif par rapport a aSrcProvider pour les includes de aMainSrc
	 *
	 *
	 * @param aRsrcProviderChain
	 * @param aMainSrcRelPath
	 * @param aMainSrc
	 *            Root de aSrcProvider si null
	 * @param aLanguage
	 *            language du scripting
	 * @param tracer
	 * @return
	 * @throws CXJsException
	 */
	public static CXJsSourceMain newInstanceFromSource(final CXRsrcProvider aRsrcProviderChain,
			final String aMainSrcRelPath, final String aMainSrc, final String aLanguage, final IXjsTracer tracer)
			throws CXJsException {
		final CXJsSourceMain wResult = new CXJsSourceMain(aRsrcProviderChain, aMainSrc, aLanguage);
		wResult.loadFromSource(aMainSrc, new CXRsrcUriDir(aMainSrcRelPath), tracer);
		return wResult;

	}

	/**
	 * LowCase permet plus de souplesse dasn les includes
	 *
	 * @param aRelPath
	 * @return
	 */
	public static String pathToHashMapKey(final String aRelPath) {
		return aRelPath == null ? null : aRelPath.toLowerCase();
	}

	private CXRsrcUriPath pFilePath;

	private CXRsrcText pFileRsrc;
	private final String pLanguage;
	// Modules classes par path en lowCase
	private HashMap<String, CXJsModule> pListModules = new HashMap<>();
	private String pMergedCode;
	// #mymeta myvalue...
	private final Map<String, List<CXJsScriptMetaParameter>> pMetaParameters = new HashMap<>();

	private final LinkedList<CXJsModule> pOrderedIncludes = new LinkedList<>();

	private CXRsrcText[] pResources;

	// #12
	private final CXRsrcProvider pRsrcProviderChain;

	private final String pScriptUri;

	/**
	 * @param aRsrcProviderChain
	 * @param aLanguage
	 */
	protected CXJsSourceMain(final CXRsrcProvider aRsrcProviderChain, final String aScriptUri, final String aLanguage) {
		super();
		pScriptUri = aScriptUri;
		pRsrcProviderChain = aRsrcProviderChain;
		pLanguage = aLanguage;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.psem2m.utilities.scripting.CXJsSource#addDescriptionInBuffer(java
	 * .lang.Appendable)
	 */
	@Override
	public Appendable addDescriptionInBuffer(Appendable aSB) {
		aSB = aSB == null ? new StringBuilder() : aSB;
		if (isLoadedFromFile()) {
			descrAddLine(aSB, "Loaded from file", pFilePath.getFullPath());
		} else {
			descrAddLine(aSB, "Loaded from source");
		}
		descrAddLine(aSB, "Resource provider(s):");
		descrAddIndent(aSB, pRsrcProviderChain.toDescription());
		if (pResources != null) {
			descrAddLine(aSB, "Ressources");
			final StringBuilder wTmp = new StringBuilder();
			for (final CXRsrcText xRsrc : pResources) {
				descrAddLine(wTmp, xRsrc.toDescription());
			}
			descrAddIndent(aSB, wTmp);
		}
		descrAddLine(aSB, "Includes tree");
		descrAddIndent(aSB, treeToString());
		descrAddLine(aSB, "Source");
		aSB = super.addDescriptionInBuffer(aSB);
		return aSB;
	}

	/**
	 * add the meta information identified a key (#mykey) and
	 *
	 * @param aKey
	 *            : id of the marquer that starts with '#'
	 * @param aLineValue
	 *            : valu of the meta parameter
	 */
	void addMetaParameter(final String aKey, final List<String> aLineValues) {
		List<CXJsScriptMetaParameter> wListMeta = pMetaParameters.get(aKey);
		if (wListMeta == null) {
			wListMeta = new ArrayList<>();
			pMetaParameters.put(aKey, wListMeta);
		}
		CXJsScriptMetaParameter wMetaParameter = new CXJsScriptMetaParameter(aKey);
		for (String aVal : aLineValues) {
			wMetaParameter.addValues(aVal);
		}
		wListMeta.add(wMetaParameter);
	}

	/**
	 * @return
	 * @throws CXJsException
	 */
	public boolean checkTimeStamp() throws CXJsException {
		try {
			if (pResources == null) {
				return true;
			}
			for (final CXRsrcText xRsrc : pResources) {
				if (!pRsrcProviderChain.checkTimeStamp(xRsrc)) {
					return false;
				}
			}
			return true;
		} catch (final Exception e) {
			throw new CXJsException(this, "Error checking timeStamp", e, "checkTimeStamp");
		}
	}

	/**
	 * @return
	 * @throws CXJsException
	 */
	public CXRsrcText[] checkTimeStamps() throws CXJsException {
		try {
			if (pResources == null) {
				return null;
			}
			final ArrayList<CXRsrcText> wArray = new ArrayList<>(pResources.length);
			for (final CXRsrcText xRsrc : pResources) {
				if (!pRsrcProviderChain.checkTimeStamp(xRsrc)) {
					wArray.add(xRsrc);
				}
			}
			return wArray.size() == 0 ? null : wArray.toArray(new CXRsrcText[wArray.size()]);
		} catch (final Exception e) {
			throw new CXJsException(this, "Error checking timeStamp", e, "checkTimeStamp");
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.psem2m.utilities.scripting.CXJsSource#findSource(int)
	 */
	@Override
	public CXJsSourceLocalization findSource(final int aMergeLineNumber) {
		// FDB - Fiche 64829 - pLineNumber>0
		if (aMergeLineNumber < 0) {
			return null;
		}
		for (final CXJsModule xMod : pOrderedIncludes) {
			final CXJsSourceLocalization wRes = xMod.findSource(aMergeLineNumber);
			if (wRes != null) {
				return wRes;
			}
		}
		return super.findSource(aMergeLineNumber);
	}

	/**
	 * @param aLineNumber
	 * @param aColumnNumber
	 * @return
	 */
	public Object getErrReport(final int aLineNumber, final int aColumnNumber) {
		return null;
	}

	/**
	 * @return
	 */
	public String getLanguage() {
		return pLanguage;
	}

	/**
	 * MOD_OG_20170615 Use the merged sources to retreive text
	 *
	 * @return the pMergedCode ou the original sources if not loaded
	 */
	public String getMergedCode() {
		return isLoaded() ? pMergedCode : super.getSources();
	}

	/**
	 * return the list of meta parameter value line regarding akey (e.g require,
	 * include ...)
	 *
	 * @param aKey
	 * @return
	 */
	public List<CXJsScriptMetaParameter> getMetaParameter(final String aKey) {
		return pMetaParameters.get(aKey);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.psem2m.utilities.scripting.CXJsSource#getNextSibling()
	 */
	@Override
	protected CXJsModule getNextSibling() {
		return null;
	}

	/**
	 * @return
	 */
	public CXRsrcText[] getResources() {
		return pResources;
	}

	/**
	 * @return
	 */
	public CXRsrcProvider getRsrcProvider() {
		return pRsrcProviderChain;
	}

	public String getScriptUri() {
		return pScriptUri;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.psem2m.utilities.scripting.CXJsSource#getSourceName()
	 */
	@Override
	public String getSourceName() {
		return pFilePath != null ? pFilePath.getName() : "Main";
	}

	/**
	 * MOD_OG_20170615 Use the merged sources to retreive text
	 *
	 * @return
	 */
	@Override
	public String getSources() {
		return getMergedCode();
	}

	/**
	 * @return
	 */
	public boolean hasFilesDependencies() {
		return isLoadedFromFile() || (pResources != null && pResources.length != 0);
	}

	/**
	 * @return
	 */
	public boolean isLoadedFromFile() {
		return pFilePath != null;
	}

	/**
	 * @return
	 */
	public boolean isLoadedFromSource() {
		return pFilePath == null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.psem2m.utilities.scripting.CXJsSource#isMain()
	 */
	@Override
	public boolean isMain() {
		return true;
	}

	/**
	 * @param aSource
	 * @param aSrcRootDir
	 * @throws CXJsExcepLoad
	 */
	protected void load(final String aSource, final CXRsrcUriDir aSrcRootDir) throws CXJsExcepLoad {
		load(aSource, aSrcRootDir, (IXjsTracer) null);
	}

	/**
	 * @param aSource
	 * @param aSrcRootDir
	 * @param tracer
	 * @throws CXJsExcepLoad
	 */
	protected void load(final String aSource, final CXRsrcUriDir aSrcRootDir, final IXjsTracer tracer)
			throws CXJsExcepLoad {
		final boolean trace = tracer != null;
		final CXTimer wT = trace ? new CXTimer("loadMainScript", true) : null;
		try {
			setSource(aSource);
			setSrcRootDir(aSrcRootDir);
			final StringBuilder wSB = loadDoBefore();
			super.load();
			loadDoAfter(wSB);
			if (trace) {
				tracer.trace("loadOk - code[" + (pMergedCode == null ? 0 : pMergedCode.length()) + "] chars");
			}
		} catch (final Exception e) {
			if (trace) {
				tracer.trace("loadMainScriptError", e);
			}
			loadThrowExcep(this, e);
		} finally {
			if (trace) {
				wT.stop();
				tracer.trace(wT.toDescription());
			}
		}
	}

	/**
	 * @param aSB
	 * @throws CXJsExcepLoad
	 */
	protected void loadDoAfter(final StringBuilder aSB) throws CXJsExcepLoad {
		int wRsrcSize = pListModules.size();
		if (pFileRsrc != null) {
			wRsrcSize += 1;
		}
		if (wRsrcSize > 0) {
			pResources = new CXRsrcText[wRsrcSize];
			int i = 0;
			if (pFileRsrc != null) {
				pResources[i++] = pFileRsrc;
			}
			for (final CXJsModule xMod : pListModules.values()) {
				pResources[i++] = xMod.getRsrc();
			}
		}
		final StringBuilder wSB = new StringBuilder();
		processIncludes();
		int wStartLine = 1;
		for (final CXJsModule xMod : pOrderedIncludes) {
			wStartLine = xMod.merge(wSB, wStartLine);
		}
		merge(wSB, wStartLine);
		pMergedCode = wSB.toString();
		if (traceDebugOn()) {
			System.out.println("------------------ descrToString ------------------");
			System.out.println(toDescription());
			System.out.println("------------------ MergedCode ------------------");
			System.out.println(pMergedCode);
		}
	}

	/**
	 * @return
	 * @throws CXJsExcepLoad
	 */
	protected StringBuilder loadDoBefore() throws CXJsExcepLoad {
		pListModules = new HashMap<>();
		return new StringBuilder(10240);
	}

	/**
	 * Renvoie le nombre total de lignes
	 *
	 * @param aRelpath
	 * @param tracer
	 * @throws CXJsExcepLoad
	 */
	public void loadFromFile(final CXRsrcUriPath aRelpath, final IXjsTracer tracer) throws CXJsExcepLoad {
		try {
			pFilePath = aRelpath;
			pFileRsrc = pRsrcProviderChain.rsrcReadTxt(aRelpath);
			load(pFileRsrc.getContent(), pFilePath.getParent(), tracer);
		} catch (final CXJsExcepLoad e) {
			if (tracer != null) {
				tracer.trace("loadMainFleError", e);
			}
			throw (e);
		} catch (final Exception e) {
			if (tracer != null) {
				tracer.trace("loadMainFleError", e);
			}
			loadThrowExcep(this, e);
		}
	}

	/**
	 * Renvoie le nombre total de lignes
	 *
	 * @param aSource
	 * @param aDir
	 * @param tracer
	 * @throws CXJsExcepLoad
	 */
	public void loadFromSource(final String aSource, final CXRsrcUriDir aDir, final IXjsTracer tracer)
			throws CXJsExcepLoad {
		load(aSource, aDir, tracer);
	}

	/**
	 * Acces au code
	 *
	 * @param aModule
	 */
	protected void loadModuleAdd(final CXJsModule aModule) {
		pListModules.put(pathToHashMapKey(aModule.getPath()), aModule);
	}

	/**
	 * @param aRelPath
	 * @return
	 */
	protected boolean loadModuleExists(final String aRelPath) {
		return pListModules.containsKey(pathToHashMapKey(aRelPath));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.psem2m.utilities.scripting.CXJsSource#loadThrowExcep(org.psem2m.utilities
	 * .scripting.CXJsSourceMain, java.lang.Throwable)
	 */
	@Override
	protected void loadThrowExcep(final CXJsSourceMain aMain, final Throwable e) throws CXJsExcepLoad {
		if (isLoadedFromFile()) {
			throw new CXJsExcepLoad(aMain, e, "Error main module " + pFilePath.getName());
		} else {
			super.loadThrowExcep(aMain, e);
		}
	}

	/**
	 * UNiquement sir load si !pMergeIncludes
	 *
	 * @return
	 * @throws CXJsExcepLoad
	 */
	protected LinkedList<CXJsModule> processIncludes() throws CXJsExcepLoad {
		if (hasModules()) {
			for (final CXJsModule xMod : getModules()) {
				xMod.orderIncludes(pOrderedIncludes, -1);
			}
		}
		return pOrderedIncludes;
	}

	/**
	 * @param tracer
	 * @throws CXJsExcepLoad
	 */
	public void reload(final IXjsTracer tracer) throws CXJsExcepLoad {
		if (isLoaded()) {
			pOrderedIncludes.clear();
			pListModules.clear();
			pMergedCode = null;
			pResources = null;
			if (isLoadedFromFile()) {
				super.initMainReload(true);
				loadFromFile(pFilePath, tracer);
			} else {
				super.initMainReload(false);
				loadFromSource(getSources(), getSrcRootDir(), tracer);
			}
		} else {
			throw new CXJsExcepLoad(this, "Can't reload script[" + getSourceName() + "] - Script is not loaded");
		}
	}
}
