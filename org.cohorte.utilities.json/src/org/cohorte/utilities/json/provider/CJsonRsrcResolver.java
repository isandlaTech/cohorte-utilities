package org.cohorte.utilities.json.provider;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.psem2m.utilities.rsrc.CXRsrcProvider;
import org.psem2m.utilities.rsrc.CXRsrcProviderFile;
import org.psem2m.utilities.rsrc.CXRsrcProviderHttp;
import org.psem2m.utilities.rsrc.CXRsrcProviderMemory;
import org.psem2m.utilities.rsrc.CXRsrcText;

public class CJsonRsrcResolver implements IJsonRsrcResolver {

	public static enum EProviderKind {
		FILE("file://", CXRsrcProviderFile.class), HTTP("http://",
				CXRsrcProviderHttp.class), MEMORY("memory://",
				CXRsrcProviderMemory.class);
		private Class<? extends CXRsrcProvider> pClass;
		private String pValue;

		private EProviderKind(final String aValue,
				final Class<? extends CXRsrcProvider> aClass) {
			pValue = aValue;
			pClass = aClass;
		}

		public boolean checkKind(final String aContentid) {
			return aContentid.startsWith(pValue);
		}

		/**
		 * return the subpath if the provider is the correct one else null
		 *
		 * @param wProv
		 * @param aContentid
		 * @return
		 */
		public String getValidPathForProvider(final CXRsrcProvider wProv,
				final String aContentid) {
			if (pClass.isInstance(wProv)) {
				// same provider we return the path else we return null
				return aContentid.replace(pValue, "");
			} else {
				return null;
			}
		}

		@Override
		public String toString() {
			return pValue;
		}
	}

	// can only have none memory provider by tag
	private final Map<String, CXRsrcProviderMemory> pListMemoryProviderByTag;

	private final Map<String, Map<Integer, CXRsrcProvider>> pListProviderByTag;

	// identified directly the memory providers due to memory cache init

	public CJsonRsrcResolver() {
		pListProviderByTag = new Hashtable<String, Map<Integer, CXRsrcProvider>>();
		pListMemoryProviderByTag = new Hashtable<String, CXRsrcProviderMemory>();
	}

	/**
	 * add a provider with add order priority. the first to be add has the lower
	 * prio and is the strongest one
	 *
	 * @param aTag
	 * @param aProviders
	 * @param aPriority
	 */
	public void addRsrcProvider(final String aTag,
			final CXRsrcProvider aProviders) {
		int prio = 0;

		if (pListProviderByTag.get(aTag) != null) {
			prio = pListProviderByTag.get(aTag).size();
		}
		addRsrcProvider(aTag, aProviders, prio);

	}

	/**
	 * add a provider with a specific priority. the lower prio is the strongest
	 * one.
	 *
	 * @param aTag
	 * @param aProviders
	 * @param aPriority
	 */
	public void addRsrcProvider(final String aTag,
			final CXRsrcProvider aProviders, final int aPriority) {
		if (aProviders instanceof CXRsrcProviderMemory) {
			pListMemoryProviderByTag.put(aTag,
					(CXRsrcProviderMemory) aProviders);
		} else {
			if (pListProviderByTag.get(aTag) == null) {
				pListProviderByTag.put(aTag,
						new Hashtable<Integer, CXRsrcProvider>());
			}
			pListProviderByTag.get(aTag).put(aPriority, aProviders);
		}
	}

	private String checkValidProviderAndPath(final CXRsrcProvider aProv,
			final String aContentId) {
		if (EProviderKind.FILE.checkKind(aContentId)) {
			// it's for file provider
			return EProviderKind.FILE
					.getValidPathForProvider(aProv, aContentId);

		} else if (EProviderKind.MEMORY.checkKind(aContentId)) {
			return EProviderKind.MEMORY.getValidPathForProvider(aProv,
					aContentId);

		} else if (EProviderKind.HTTP.checkKind(aContentId)) {
			return EProviderKind.HTTP
					.getValidPathForProvider(aProv, aContentId);

		} else {
			return aContentId;
		}
	}

	@Override
	public CXRsrcText getContent(final String aTag, final String aContentId,
			final boolean aMemoryProvider) throws Exception {
		CXRsrcText wContent = null;
		List<Exception> wExcept = new ArrayList<Exception>();

		if (aMemoryProvider && pListMemoryProviderByTag.get(aTag) != null) {
			wContent = getContentByProvider(pListMemoryProviderByTag.get(aTag),
					aContentId);
		}
		if (wContent == null && pListProviderByTag.get(aTag) != null) {
			// look on all provider and return the first elem found
			Set<Integer> wKeys = pListProviderByTag.get(aTag).keySet();
			for (int wKey : wKeys) {
				CXRsrcProvider wProv = pListProviderByTag.get(aTag).get(wKey);
				// check if the content id contain file://, memory:// or http://
				// and
				// return the path without the prefix or null if it's not valid
				try {
					wContent = getContentByProvider(wProv, aContentId);
				} catch (Exception e) {
					wExcept.add(e);
				}
				if (wContent != null) {
					break;// exit the loop
				}

			}
		}
		if (wContent == null && aMemoryProvider) {
			throw new FileNotFoundException(String.format(
					"content '%s' not found in all providers\n Cause : %s",
					aContentId, wExcept));
		}
		return wContent;
	}

	private CXRsrcText getContentByProvider(final CXRsrcProvider aProvider,
			final String aContentId) throws Exception {
		String wValidContentId = checkValidProviderAndPath(aProvider,
				aContentId);
		if (wValidContentId != null) {
			return aProvider.rsrcReadTxt(wValidContentId);
		}
		return null;
	}

	@Override
	public Set<String> getListTags() {
		Set<String> wListTag = new HashSet<String>();
		wListTag.addAll(pListProviderByTag.keySet());
		wListTag.addAll(pListMemoryProviderByTag.keySet());
		return wListTag;
	}

	@Override
	public Collection<CXRsrcProvider> getRsrcProvider(final String aTag) {
		List<CXRsrcProvider> wList = new ArrayList<CXRsrcProvider>();
		if (pListProviderByTag.get(aTag) != null) {
			wList.addAll(pListProviderByTag.get(aTag).values());
		}
		wList.add(getRsrcProviderMemory(aTag));
		return wList;
	}

	@Override
	public CXRsrcProviderMemory getRsrcProviderMemory(final String aTag) {
		return pListMemoryProviderByTag.get(aTag);
	}
}