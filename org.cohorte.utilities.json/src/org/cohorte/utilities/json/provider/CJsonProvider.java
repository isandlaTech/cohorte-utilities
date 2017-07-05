package org.cohorte.utilities.json.provider;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.cohorte.utilities.json.provider.CJsonRsrcResolver.EProviderKind;
import org.psem2m.utilities.json.JSONArray;
import org.psem2m.utilities.json.JSONException;
import org.psem2m.utilities.json.JSONObject;
import org.psem2m.utilities.logging.IActivityLogger;
import org.psem2m.utilities.rsrc.CXRsrcProvider;
import org.psem2m.utilities.rsrc.CXRsrcProviderHttp;
import org.psem2m.utilities.rsrc.CXRsrcProviderMemory;
import org.psem2m.utilities.rsrc.CXRsrcText;

public class CJsonProvider implements IJsonProvider {

	public static final String INCLUDE = "$include";

	private static final String SEP_PATH = ";";

	private final String EMPTYJSON = "{}";
	/**
	 * boolean that express if we don't need to raise an exception if the
	 * content is missing. in that case the replace return an empty json Object
	 */
	private boolean pIgnoreMissingContent = true;

	// allow the api to fill the mem cache during the first file and http
	// resolving
	IHandlerInitMemoryCache pInitCacheHandler;

	private IJsonRsrcResolver pJsonResolver;

	List<Properties> pListProperties;

	private final IActivityLogger pLogger;

	private final Pattern pPatternAll = Pattern.compile(
			"(/\\*+.*(\n|.)*?\\*.*(\n|.)*?.*\\*+/)|(.*//.*$)",
			Pattern.MULTILINE);

	private final Pattern pPatternCheck = Pattern.compile(
			"(/\\*+.*(\n|.)*?\\*.*(\n|.)*?.*\\*+/)", Pattern.MULTILINE);

	private final Pattern pPatternCheckSlash = Pattern.compile("(\".*//.*\")",
			Pattern.MULTILINE);

	public CJsonProvider(final IJsonRsrcResolver aResolver,
			final IActivityLogger aLogger) {
		this(aResolver, null, aLogger, true);
	}

	public CJsonProvider(final IJsonRsrcResolver aResolver,
			final List<Properties> aListProperties,
			final IActivityLogger aLogger, final Boolean aIgnoreMissingContent) {
		pLogger = aLogger;
		pJsonResolver = aResolver;
		pIgnoreMissingContent = aIgnoreMissingContent;
		pListProperties = aListProperties;
	}

	/**
	 * check if the json stringified is ok. else raise an JSONException
	 *
	 * @param aJsonString
	 * @return
	 * @throws JSONException
	 */
	private boolean checkIsJson(final String aJsonString) throws JSONException {
		if (aJsonString == null || aJsonString.isEmpty()) {
			return true;
		} else {
			int indexSquare = aJsonString.indexOf('[');
			int indexCurly = aJsonString.indexOf('{');
			if (indexCurly == -1 && indexSquare == -1) {// not a json
				new JSONObject(aJsonString);
			} else if (indexCurly != -1 && indexSquare == -1) {
				new JSONObject(aJsonString);
			} else if (indexCurly == -1 && indexSquare != 1) {
				new JSONArray(aJsonString);
			} else if (indexCurly > indexSquare) {
				new JSONArray(aJsonString);
			} else {
				new JSONObject(aJsonString);
			}
		}

		return true;

	}

	public IHandlerInitMemoryCache getInitCache() {
		return pInitCacheHandler;
	}

	/**
	 * resolve the JSONObject to remove comment and add subcontent via $file or
	 * other tag...
	 *
	 * @param aUnresolvedJson
	 * @return
	 * @throws Exception
	 */
	@Override
	public JSONObject getJSONObject(final JSONObject aUnresolvedJson)
			throws Exception {

		return getJSONObject(null, aUnresolvedJson);
	}

	/**
	 * equivalent of calling getJSONObject("$file",aContentId).
	 *
	 * @param aContentId
	 * @return
	 * @throws Exception
	 */
	@Override
	public JSONObject getJSONObject(final String aContentId) throws Exception {
		// get content
		pLogger.logInfo(this, "getJSONObject", "get content from id %s",
				aContentId);
		return getJSONObject(INCLUDE, aContentId);
	}

	/**
	 * resolve the JSONObject to remove comment and add subcontent via $file or
	 * other tag...
	 *
	 * @param aUnresolvedJson
	 * @return
	 * @throws Exception
	 */
	public JSONObject getJSONObject(final String currentPath,
			final JSONObject aUnresolvedJson) throws Exception {

		String aContent = aUnresolvedJson.toString(2);
		// preprocess content

		// check include content that must be resolve
		pLogger.logDebug(this, "getJSONObject",
				"preprocess resolve subcontent ");

		// resolve file and http and call handle for mem cache
		String wResolvedString = resolveInclude(currentPath, aContent,
				pInitCacheHandler == null);
		checkIsJson(wResolvedString);
		if (pInitCacheHandler != null) {
			// call wit memory resolution only
			wResolvedString = resolveInclude(currentPath, wResolvedString, true);
			checkIsJson(wResolvedString);
		}

		return new JSONObject(wResolvedString);
	}

	/**
	 * get a jsonobject resolve corresponding to te contentId for the defined
	 * tag
	 *
	 * e.g : aTag = $file and aContentId : /test/toto.js this fonction will
	 * provide the toto.js file with all the resolution of include file via
	 * $file, $memory or other tags and remove the comment
	 *
	 * @param aTag
	 * @param aContentId
	 * @return
	 * @throws Exception
	 */
	@Override
	public JSONObject getJSONObject(final String aTag, final String aContentId)
			throws Exception {
		// get content
		pLogger.logInfo(this, "getJSONObject", "get content from id %s",
				aContentId);
		return getJSONObject(aTag, null, aContentId);

	}

	/**
	 * equivalent of calling getJSONObject("$file",aContentId).
	 *
	 * @param aContentId
	 * @return
	 * @throws Exception
	 */
	public JSONObject getJSONObject(final String aTag, final String aPath,
			final String aContentId) throws Exception {
		// get content
		pLogger.logInfo(this, "getJSONObject", "get content from id %s",
				aContentId);
		String wPath = aPath != null ? aPath + File.separatorChar + aContentId
				: aContentId;

		CXRsrcText wRsrc = pJsonResolver.getContent(aTag, wPath, false);
		String aContent = wRsrc.getContent();

		String wNotComment = removeComment(aContent);
		checkIsJson(wNotComment);
		// check include content that must be resolve
		return getJSONObject(aPath, new JSONObject(wNotComment));
	}

	public IJsonRsrcResolver getJsonResolver() {
		return pJsonResolver;
	}

	private String getSubPath(final String aTag, final CXRsrcText aRsrc) {
		String wSubPath = aRsrc.getFullPath();
		int wIdx = wSubPath.lastIndexOf(File.separatorChar);
		wSubPath = wIdx != -1 ? wSubPath.substring(0, wIdx + 1) : wSubPath;
		Stream<CXRsrcProvider> wProv = pJsonResolver
				.getRsrcProvider(aTag)
				.stream()
				.filter(c -> aRsrc.getFullPath().contains(
						c.getDefDirectory().getPath()));
		CXRsrcProvider wProviderUsed = wProv.findFirst().get();
		if (wProviderUsed instanceof CXRsrcProviderMemory
				|| wProviderUsed instanceof CXRsrcProviderHttp) {
			// TODO change when we move it to utilities to use polymorphisme
			return "";
		}
		// replace
		return wSubPath.replace(wProviderUsed.getDefDirectory().getPath(), "");
	}

	private String getValidContent(final CXRsrcText aRsrc) throws JSONException {
		String wSubContent = aRsrc.getContent();

		// remove comment
		wSubContent = removeComment(wSubContent);

		// check if it's json
		checkIsJson(wSubContent);

		return wSubContent;
	}

	private void initMemoryProviderCache(final String aValidContent,
			final String aTag) {
		CXRsrcProviderMemory wMemProv = pJsonResolver
				.getRsrcProviderMemory(aTag);
		if (pInitCacheHandler != null && wMemProv != null) {
			// call the initCache memory
			pInitCacheHandler.initCache(aValidContent, wMemProv);
		}
	}

	/**
	 * return a string where all subcontent that are identified by a specific id
	 * in aContent are resolved without comment
	 *
	 * @param aContent
	 * @return
	 */

	protected String removeComment(final String aContent) {

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

	/**
	 *
	 * @param currentPath
	 * @param aContent
	 * @param aMemoryProvider
	 *            : describe if can considere the memory provider or if we need
	 *            to call the handle to mme cache content for the second pass
	 * @return
	 * @throws Exception
	 */
	protected String resolveInclude(final String currentPath,
			final String aContent, final boolean aUseMemoryProvider)
			throws Exception {

		String wResolvContent = aContent;
		for (String wTag : pJsonResolver.getListTags()) {
			// regexp that allow to catch the strings like
			// {"$file":"content..."
			// }
			Pattern wPatternDollarFile = Pattern.compile("(\\{\\s*\"\\" + wTag
					+ "\"\\s*:\\s*\".*\"\\s*\\})", Pattern.MULTILINE);

			// looking for subcontent identified by a id e.g $file, $ur ,
			// $memory
			Matcher wMatcherFile = wPatternDollarFile.matcher(aContent);
			while (wMatcherFile.find()) {
				for (int i = 0; i < wMatcherFile.groupCount(); i++) {
					List<String> wSubNoCommentContent = new ArrayList<String>();
					String wStr = wMatcherFile.group(i);
					if (wStr != null) {
						JSONObject wJsonSubId = new JSONObject(wStr);
						try {

							// set absolute path
							String wlPath = wJsonSubId.optString(wTag, null);
							List<String> wListPath = Arrays.asList(wlPath
									.split(SEP_PATH));
							for (String wPath : wListPath) {
								// if file we are allowed to put relative path
								if (!wPath.startsWith(EProviderKind.FILE
										.toString() + "/")) {
									// we include te current path
									wPath = wPath.replace(
											EProviderKind.FILE.toString(),
											EProviderKind.FILE.toString()
													+ currentPath
													+ File.separatorChar);
								}

								CXRsrcText wRsrc = pJsonResolver.getContent(
										wTag, wPath, aUseMemoryProvider);
								if (wRsrc != null) {
									// resolv subcontent
									String wValidContent = getValidContent(wRsrc);
									if (!aUseMemoryProvider) {
										initMemoryProviderCache(wValidContent,
												wTag);
									}
									wSubNoCommentContent.add(resolveInclude(
											getSubPath(wTag, wRsrc),
											wValidContent, aUseMemoryProvider));
								} else {
									if (!aUseMemoryProvider) {
										// no resolution we init the cache with
										// the current content
										initMemoryProviderCache(aContent, wTag);
									}
									wSubNoCommentContent.add(wStr);
								}
							}

						} catch (Exception e) {
							// TODO Provider must return a typed exception and
							// not a global one
							if (pIgnoreMissingContent
									&& e.getCause() instanceof FileNotFoundException) {
								// continue but log warning
								pLogger.logWarn(this, "resolvInclude",
										"subfile not found {%s]",
										e.getMessage());

							} else {
								// raise e
								throw e;
							}

						}

						// replace file by empty json.
						String wRep = wSubNoCommentContent != null
								&& wSubNoCommentContent.size() != 0 ? wSubNoCommentContent
								.stream().collect(Collectors.joining(","))
								: EMPTYJSON;
						wResolvContent = wResolvContent.replace(wStr,
								wRep.isEmpty() ? EMPTYJSON : wRep);

					}
				}
			}
		}
		return wResolvContent;

	}

	public void setIgnoreMissingContent(final boolean aIgnoreMissingContent) {
		this.pIgnoreMissingContent = aIgnoreMissingContent;
	}

	public void setInitMemoryCache(final IHandlerInitMemoryCache aInitCache) {
		this.pInitCacheHandler = aInitCache;
	}

	public void setJsonResolver(final IJsonRsrcResolver aResolver) {
		pJsonResolver = aResolver;
	}

}
