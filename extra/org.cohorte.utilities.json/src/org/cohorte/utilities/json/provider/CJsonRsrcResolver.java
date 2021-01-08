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
import org.cohorte.utilities.json.provider.rsrc.CXRsrcMergeFileProvider;
import org.cohorte.utilities.json.provider.rsrc.CXRsrcTextFileProvider;
import org.psem2m.utilities.json.JSONArray;
import org.psem2m.utilities.json.JSONObject;
import org.psem2m.utilities.logging.CActivityLoggerNull;
import org.psem2m.utilities.logging.IActivityLogger;
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
			final Matcher wMatcher = pPatternAll.matcher(wNoComment);

			while (wMatcher.find()) {
				for (int i = 0; i < wMatcher.groupCount(); i++) {
					final String wStr = wMatcher.group(i);

					if (wStr != null
							&& wStr.indexOf("/") != -1
							&& (!pPatternCheckSlash.matcher(wStr).find() || pPatternCheck
									.matcher(wStr).find())) {
						final int idx = wStr.indexOf("/");
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

	private IActivityLogger pLogger = CActivityLoggerNull.getInstance();

	public CJsonRsrcResolver() {
		pListProviderByTag = new Hashtable<>();
		pListMemoryProviderByTag = new Hashtable<>();
	}

	public void setLogger(IActivityLogger aLogger) {
		pLogger = aLogger;
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
			return aContentId;

		} else if (EProviderKind.MEMORY.checkKind(aContentId)) {
			return aContentId.replace(EProviderKind.MEMORY.toString(),"");


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
			final List<JSONObject> aFatherObject, final Map<String,String > aMapString) throws Exception {
		CXListRsrcText wContents = new CXListRsrcText();
		final List<Exception> wExcept = new ArrayList<>();

		if (aMemoryProvider && pListMemoryProviderByTag.get(aTag) != null) {
			wContents = getContentByProvider(
					pListMemoryProviderByTag.get(aTag), aContentId,
					aFatherObject,aMapString);
		}
		if ((wContents == null || wContents.size() == 0)
				&& pListProviderByTag.get(aTag) != null) {
			// look on all provider and return the first elem found
			final Set<Integer> wKeys = pListProviderByTag.get(aTag).keySet();
			for (final int wKey : wKeys) {
				final CXRsrcProvider wProv = pListProviderByTag.get(aTag).get(wKey);
				// check if the content id contain file://, memory:// or http://
				// and
				// return the path without the prefix or null if it's not valid
				try {
					pLogger.logInfo(this, "getContent", "get content from id %s",aContentId);
					if( wProv instanceof CXRsrcMergeFileProvider ){
						// need merge every content by provider
						final CXListRsrcText wContentCurrent =  getContentByProvider(wProv, aContentId,
								aFatherObject,aMapString);
						if( wContents.size()==0) {
							wContents=wContentCurrent;
						}else if( wContentCurrent.size()>0) {
							JSONObject wNew = new JSONObject(wContentCurrent.get(0).getContent());
							final JSONObject wOld = new JSONObject(wContents.get(0).getContent());
							wNew = CXRsrcMergeFileProvider.merge(wNew, wOld);
							wContents.get(0).setContent(wNew.toString());
						}
					}else {
						wContents.addAll(getContentByProvider(wProv, aContentId,
								aFatherObject,aMapString));
					}
				} catch (final Exception e) {
					wExcept.add(e);
				}
				/*if (wContents != null && wContents.size()>0) {
					break;// exit the loop
				}*/

			}
		}
		if (wContents == null && aMemoryProvider) {
			throw new FileNotFoundException(String.format(
					"content '%s' not found in all providers\n Cause : %s",
					aContentId, wExcept));
		}
		return wContents;
	}
	private CXListRsrcText getContentByProviderFile(CXRsrcProviderFile aProvider, String aValidContentId, Map<String,String> aMapString) throws Exception {

		// check if we ask for a JSON Array element
		// replace potential // in the path
		final boolean wWantSubTagJson = aValidContentId.contains("#");

		final boolean wWantSubArrayElem = aValidContentId.contains("]");
		CXListRsrcText wList;
		if (aValidContentId.indexOf("?") != -1) {

			aValidContentId = aValidContentId.substring(0,
					aValidContentId.indexOf("?"));
		}
		String wFilePath = aValidContentId;
		if (wWantSubArrayElem && wFilePath.contains("]")) {
			wFilePath = wFilePath.substring(0,
					aValidContentId.indexOf("["));
		}
		if (wWantSubTagJson && wFilePath.contains("#")) {
			wFilePath = wFilePath.substring(0,
					aValidContentId.indexOf("#"));
		}
		wList = aProvider.rsrcReadTxts(wFilePath,aMapString);

		// alter content of each RsrcText to only set the
		// subcontains asked
		for (int i = 0; i < wList.size(); i++) {
			final CXRsrcText wRsrc = wList.get(i);
			final String wCommentedJSON = wRsrc.getContent();
			pLogger.logInfo(this, "getContentByProvider", "get content from id %s",wRsrc.getFullPath());

			String wNoComment = removeComment(wCommentedJSON);
			if (wWantSubArrayElem) {
				final String wIndex = aValidContentId.substring(
						aValidContentId.indexOf("[") + 1,
						aValidContentId.indexOf("]"));
				final JSONArray wJSon = new JSONArray(wNoComment);

				if (wIndex.equals("*")) {
					// concat all object of the array
					wNoComment = "";
					for (int k = 0; k < wJSon.length(); k++) {
						final String wElem = wJSon.opt(k).toString();
						wNoComment += k > 0 ? "," + wElem : wElem;
					}
				} else {
					final int wIndexInt = Integer.parseInt(wIndex);
					wNoComment = wJSon.opt(wIndexInt).toString();
				}

			}
			if( wWantSubTagJson &&  wNoComment.contains("{")  && wNoComment.indexOf("{") < wNoComment.indexOf("[") ) {
				final String wTagField = aValidContentId.split("#")[1];

				final JSONObject wSubContent = new JSONObject(wNoComment);
				if( wTagField.contains(".") ) {
					Object wSubJsonElem = wSubContent;
					for(final String wTagPart:wTagField.split("\\.")) {
						if(wSubJsonElem instanceof JSONObject ) {
							wSubJsonElem = ((JSONObject)wSubJsonElem).opt(wTagPart);
						}
					}
					wNoComment = wSubJsonElem.toString();

				}else {
					wNoComment = wSubContent.opt(wTagField).toString();
				}
			}
			wRsrc.setContent(wNoComment);
		}

		return wList;
	}


	private CXListRsrcText getContentByProviderMerge(CXRsrcMergeFileProvider aProvider, String aValidContentId, Map<String,String> aMapString) throws Exception {

		// check if we ask for a JSON Array element

		final String wFilePath = aValidContentId;
		CXListRsrcText wList;

		wList = aProvider.rsrcReadTxts(wFilePath,aMapString);

		// alter content of each RsrcText to only set the
		// subcontains asked
		for (int i = 0; i < wList.size(); i++) {
			final CXRsrcText wRsrc = wList.get(i);
			final String wCommentedJSON = wRsrc.getContent();
			pLogger.logInfo(this, "getContentByProvider", "get content from id %s",wRsrc.getFullPath());

			final String wNoComment = removeComment(wCommentedJSON);


			wRsrc.setContent(wNoComment);
		}

		return wList;
	}

	private CXListRsrcText getContentByProvider(final CXRsrcProvider aProvider,
			final String aContentId, final List<JSONObject> aListFather, final Map<String,String> aMapString)
					throws Exception {
		String wValidContentId = checkValidProviderAndPath(aProvider,
				aContentId);

		final String wQueryParam = null;

		if (wValidContentId != null) {
			if (aProvider instanceof CXRsrcMergeFileProvider) {

				return getContentByProviderMerge((CXRsrcMergeFileProvider)aProvider, wValidContentId, aMapString);
			}else if (aProvider instanceof CXRsrcGeneratorProvider) {
				final CXListRsrcText wRsrcList = new CXListRsrcText();
				wRsrcList.add(((CXRsrcGeneratorProvider) aProvider)
						.rsrcReadTxt(wValidContentId, aListFather));
				for (int i = 0; i < wRsrcList.size(); i++) {
					final CXRsrcText wRsrc = wRsrcList.get(i);
					final String wCommentedJSON = wRsrc.getContent();
					final String wNoComment = removeComment(wCommentedJSON);
					wRsrc.setContent(wNoComment);
				}
				return wRsrcList;
			} else if (aProvider instanceof CXRsrcTextFileProvider) {
				if (wValidContentId.indexOf("?") != -1) {

					wValidContentId = wValidContentId.substring(0,
							wValidContentId.indexOf("?"));

				}
				final CXListRsrcText wRsrcList = new CXListRsrcText();
				wRsrcList.add(aProvider.rsrcReadTxt(wValidContentId,aMapString));

				return wRsrcList;

			}else if (aProvider instanceof CXRsrcProviderMemory) {

				final CXListRsrcText wRsrcList = new CXListRsrcText();
				wRsrcList.add(aProvider.rsrcReadTxt(wValidContentId,aMapString));

				return wRsrcList;

			} else {
				return getContentByProviderFile((CXRsrcProviderFile)aProvider, wValidContentId, aMapString);

			}
		}
		return null;
	}

	@Override
	public Set<String> getListTags() {
		final Set<String> wListTag = new HashSet<>();
		wListTag.addAll(pListProviderByTag.keySet());
		wListTag.addAll(pListMemoryProviderByTag.keySet());
		return wListTag;
	}
	@Override
	public Collection<CXRsrcProvider> getRsrcProvider() {
		final List<CXRsrcProvider> wList = new ArrayList<>();
		for(final String wKey:pListProviderByTag.keySet()) {
			if (pListProviderByTag.get(wKey) != null) {
				wList.addAll(pListProviderByTag.get(wKey).values());
			}
		}

		return wList;
	}


	@Override
	public Collection<CXRsrcProvider> getRsrcProvider(final String aTag) {
		final List<CXRsrcProvider> wList = new ArrayList<>();
		if (pListProviderByTag.get(aTag) != null) {
			wList.addAll(pListProviderByTag.get(aTag).values());
		}
		final CXRsrcProvider wMemory = getRsrcProviderMemory(aTag);
		if (wMemory != null) {
			wList.add(wMemory);
		}
		return wList;
	}

	@Override
	public CXRsrcProviderMemory getRsrcProviderMemory(final String aTag) {
		return pListMemoryProviderByTag.get(aTag);
	}
}