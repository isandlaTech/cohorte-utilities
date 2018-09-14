package org.cohorte.utilities.json.provider;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cohorte.utilities.json.provider.rsrc.CXRsrcGeneratorProvider;
import org.psem2m.utilities.json.JSONArray;
import org.psem2m.utilities.json.JSONObject;
import org.psem2m.utilities.rsrc.CXListRsrcText;
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

	private static final Pattern pPatternAll = Pattern.compile(
			"(/\\*+((\n|\\s|\t)*[^\\*][^/](\n|\\s|\t)*)*\\*+/)|(.*//.*$)",

			Pattern.MULTILINE);

	private static final Pattern pPatternCheck = Pattern.compile(
			"(/\\*+((\n|\\s|\t)*[^\\*][^/](\n|\\s|\t)*)*\\*+/)",
			Pattern.MULTILINE);

	private static final Pattern pPatternCheckSlash = Pattern.compile(
			"(\".*//.*\")", Pattern.MULTILINE);

	/**
	 * return a string where all subcontent that are identified by a specific id
	 * in aContent are resolved without comment
	 *
	 * @param aContent
	 * @return
	 */

	protected static String removeComment(final String aContent) {

		String wNoComment = aContent;
		if (wNoComment != null) {
			Matcher wMatcher = pPatternAll.matcher(wNoComment);

			while (wMatcher.find()) {
				for (int i = 0; i < wMatcher.groupCount(); i++) {
					String wStr = wMatcher.group(i);

					if (wStr != null
							&& wStr.indexOf("/") != -1
							&& (!pPatternCheckSlash.matcher(wStr).find() || pPatternCheck
									.matcher(wStr).find())) {
						int idx = wStr.indexOf("/");
						wNoComment = wNoComment.replace(
								wStr.substring(idx != -1 ? idx : 0), "");

					}
				}
			}

		}
		return wNoComment;
	}

	// can only have none memory provider by tag
	private final Map<String, CXRsrcProviderMemory> pListMemoryProviderByTag;

	// identified directly the memory providers due to memory cache init

	private final Map<String, Map<Integer, CXRsrcProvider>> pListProviderByTag;

	public CJsonRsrcResolver() {
		pListProviderByTag = new Hashtable<>();
		pListMemoryProviderByTag = new Hashtable<>();
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
	public CXListRsrcText getContent(final String aTag,
			final String aContentId, final boolean aMemoryProvider,
			final List<JSONObject> aFatherObject) throws Exception {
		CXListRsrcText wContents = null;
		List<Exception> wExcept = new ArrayList<>();

		if (aMemoryProvider && pListMemoryProviderByTag.get(aTag) != null) {
			wContents = getContentByProvider(
					pListMemoryProviderByTag.get(aTag), aContentId,
					aFatherObject);
		}
		if ((wContents == null || wContents.size() == 0)
				&& pListProviderByTag.get(aTag) != null) {
			// look on all provider and return the first elem found
			Set<Integer> wKeys = pListProviderByTag.get(aTag).keySet();
			for (int wKey : wKeys) {
				CXRsrcProvider wProv = pListProviderByTag.get(aTag).get(wKey);
				// check if the content id contain file://, memory:// or http://
				// and
				// return the path without the prefix or null if it's not valid
				try {
					wContents = getContentByProvider(wProv, aContentId,
							aFatherObject);
				} catch (Exception e) {
					wExcept.add(e);
				}
				if (wContents != null) {
					break;// exit the loop
				}

			}
		}
		if (wContents == null && aMemoryProvider) {
			throw new FileNotFoundException(String.format(
					"content '%s' not found in all providers\n Cause : %s",
					aContentId, wExcept));
		}
		return wContents;
	}

	private CXListRsrcText getContentByProvider(final CXRsrcProvider aProvider,
			final String aContentId, final List<JSONObject> aListFather)
			throws Exception {
		String wValidContentId = checkValidProviderAndPath(aProvider,
				aContentId);
		if (wValidContentId != null) {
			if (aProvider instanceof CXRsrcGeneratorProvider) {
				CXListRsrcText wRsrcList = new CXListRsrcText();
				wRsrcList.add(((CXRsrcGeneratorProvider) aProvider)
						.rsrcReadTxt(wValidContentId, aListFather));
				for (int i = 0; i < wRsrcList.size(); i++) {
					CXRsrcText wRsrc = wRsrcList.get(i);
					String wCommentedJSON = wRsrc.getContent();
					String wNoComment = removeComment(wCommentedJSON);
					wRsrc.setContent(wNoComment);
				}
				return wRsrcList;
			} else {
				// check if we ask for a JSON Array element
				// replace potential // in the path

				boolean wWantSubArrayElem = wValidContentId.contains("]");
				CXListRsrcText wList;
				if (wValidContentId.indexOf("?") != -1) {
					wValidContentId = wValidContentId.substring(0,
							wValidContentId.indexOf("?"));
				}
				if (wWantSubArrayElem) {
					wList = aProvider.rsrcReadTxts(wValidContentId.substring(0,
							wValidContentId.indexOf("[")));
				} else {
					wList = aProvider.rsrcReadTxts(wValidContentId);
				}
				// alter content of each RsrcText to only set the
				// subcontains asked
				for (int i = 0; i < wList.size(); i++) {
					CXRsrcText wRsrc = wList.get(i);
					String wCommentedJSON = wRsrc.getContent();
					String wNoComment = removeComment(wCommentedJSON);
					if (wWantSubArrayElem) {
						String wIndex = wValidContentId.substring(
								wValidContentId.indexOf("[") + 1,
								wValidContentId.indexOf("]"));
						JSONArray wJSon = new JSONArray(wNoComment);

						if (wIndex.equals("*")) {
							// concat all object of the array
							wNoComment = "";
							for (int k = 0; k < wJSon.length(); k++) {
								String wElem = wJSon.opt(k).toString();
								wNoComment += k > 0 ? "," + wElem : wElem;
							}
						} else {
							int wIndexInt = Integer.parseInt(wIndex);
							wNoComment = wJSon.opt(wIndexInt).toString();
						}

					}

					wRsrc.setContent(wNoComment);
				}

				return wList;
			}
		}
		return null;
	}

	@Override
	public Set<String> getListTags() {
		Set<String> wListTag = new HashSet<>();
		wListTag.addAll(pListProviderByTag.keySet());
		wListTag.addAll(pListMemoryProviderByTag.keySet());
		return wListTag;
	}

	@Override
	public Collection<CXRsrcProvider> getRsrcProvider(final String aTag) {
		List<CXRsrcProvider> wList = new ArrayList<>();
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