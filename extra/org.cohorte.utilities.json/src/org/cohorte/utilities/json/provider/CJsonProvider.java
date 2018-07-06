package org.cohorte.utilities.json.provider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cohorte.utilities.json.provider.CJsonRsrcResolver.EProviderKind;
import org.psem2m.utilities.CXQueryString;
import org.psem2m.utilities.CXStringUtils;
import org.psem2m.utilities.json.JSONArray;
import org.psem2m.utilities.json.JSONException;
import org.psem2m.utilities.json.JSONObject;
import org.psem2m.utilities.logging.IActivityLogger;
import org.psem2m.utilities.rsrc.CXListRsrcText;
import org.psem2m.utilities.rsrc.CXRsrcProvider;
import org.psem2m.utilities.rsrc.CXRsrcProviderHttp;
import org.psem2m.utilities.rsrc.CXRsrcProviderMemory;
import org.psem2m.utilities.rsrc.CXRsrcText;
import org.psem2m.utilities.scripting.CXJsExcepUnknownLanguage;
import org.psem2m.utilities.scripting.CXJsManager;

public class CJsonProvider implements IJsonProvider {

	private static String COND = "cond";

	public static final String INCLUDE = "$include";

	private static String PATH = "path";
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
			"(/\\*+((\n|\\s|\t)*[^\\*][^/](\n|\\s|\t)*)*\\*+/)|(.*//.*$)",

			Pattern.MULTILINE);

	private final Pattern pPatternCheck = Pattern.compile(
			"(/\\*+((\n|\\s|\t)*[^\\*][^/](\n|\\s|\t)*)*\\*+/)",
			Pattern.MULTILINE);

	private final Pattern pPatternCheckSlash = Pattern.compile("(\".*//.*\")",
			Pattern.MULTILINE);

	// use for evaluate condition
	CXJsManager pScriptRunner;

	public CJsonProvider(final IJsonRsrcResolver aResolver,
			final IActivityLogger aLogger) {
		this(aResolver, null, aLogger, true);
	}

	public CJsonProvider(final IJsonRsrcResolver aResolver,
			final IActivityLogger aLogger, final boolean aIgnoreMissingFile) {
		this(aResolver, null, aLogger, aIgnoreMissingFile);
	}

	public CJsonProvider(final IJsonRsrcResolver aResolver,
			final List<Properties> aListProperties,
			final IActivityLogger aLogger, final Boolean aIgnoreMissingContent) {
		pLogger = aLogger;
		pJsonResolver = aResolver;
		pIgnoreMissingContent = aIgnoreMissingContent;
		pListProperties = aListProperties;

		try {
			pScriptRunner = new CXJsManager(pLogger, "JavaScript");
		} catch (CXJsExcepUnknownLanguage e) {
			// TODO Auto-generated catch block
			pLogger.logSevere(
					this,
					"CJsonProvider",
					"can't instanciate JSEngine, no condition will be taken Error=[%s]",
					e);
		}
	}

	/**
	 * add parameter for the next includes to be able to replace on all include
	 * variable that has been define on the first include
	 *
	 * @throws UnsupportedEncodingException
	 */
	private void addInheritParameter(final Object aContent,
			final Map<String, String> aReplaceVars, final String aTag)
			throws UnsupportedEncodingException {

		if (aContent instanceof JSONObject) {
			JSONObject wObj = (JSONObject) aContent;
			if (aReplaceVars != null && wObj.keySet().contains(aTag)) {
				String wParameterUrl = CXQueryString
						.urlEncodeUTF8(aReplaceVars);
				String wSubIncludeStr = wObj.optString(aTag);
				String wSubIncludeWithParemter = wSubIncludeStr.contains("?") ? wSubIncludeStr
						+ "&" + wParameterUrl
						: wSubIncludeStr + "?" + wParameterUrl;
				wObj.put(aTag, wSubIncludeWithParemter);
			}
		} else if (aContent instanceof JSONArray) {
			JSONArray wArr = (JSONArray) aContent;
			for (int i = 0; i < wArr.length(); i++) {
				addInheritParameter(wArr.opt(i), aReplaceVars, aTag);
			}
		}
		// add query string to subpath in order to
		// get
		// the replace variable on each level

	}

	/**
	 * check if the json stringified is ok. else raise an JSONException
	 *
	 * @param aJsonString
	 * @return
	 * @throws JSONException
	 */
	private Object checkIsJson(final String aJsonString) throws JSONException {
		if (aJsonString == null || aJsonString.isEmpty()) {
			return null;
		} else {
			int indexSquare = aJsonString.indexOf('[');
			int indexCurly = aJsonString.indexOf('{');
			if (indexCurly == -1 && indexSquare == -1) {// not a json
				return new JSONObject(aJsonString);
			} else if (indexCurly != -1 && indexSquare == -1) {
				return new JSONObject(aJsonString);
			} else if (indexCurly == -1 && indexSquare != 1) {
				return new JSONArray(aJsonString);
			} else if (indexCurly > indexSquare) {
				return new JSONArray(aJsonString);
			} else {
				return new JSONObject(aJsonString);
			}
		}

	}

	/**
	 * a expression that should return true of false
	 *
	 * @param aCondition
	 * @return
	 */
	private boolean evaluateCondition(final String aCondition)
			throws JSONException {
		try {
			if (aCondition == null || aCondition.isEmpty()) {
				return true;
			}

			Object wReply = pScriptRunner.evalExpression(aCondition);

			if (wReply instanceof Boolean) {
				return ((Boolean) wReply).booleanValue();
			} else {
				pLogger.logSevere(
						this,
						"evaluateCondition",
						"bad condition! boolean value is expected !,  eval failed condition=[%s], resp=[%s]",
						aCondition, wReply);
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			pLogger.logSevere(
					this,
					"evaluateCondition",
					"bad condition! return false!,  eval failed condition=[%s], error=[%s]",
					aCondition, e);
			throw new JSONException(
					String.format(
							"bad condition! return false!,  eval failed condition=[%s], error=[%s]",
							aCondition, e));
		}
	}

	/**
	 * return the list of JSONObject that match the $tag in parameter aTag :
	 * value of the tag to match aJSONObject : value of a JSONObject where a tag
	 * can be defined
	 *
	 * @param aJsonObject
	 * @return
	 */
	List<JSONObject> foundMatchTags(final String aTag, final Object aObject) {
		List<JSONObject> wListTagJson = new ArrayList<>();
		if (aObject instanceof JSONObject) {
			JSONObject wJsonObject = (JSONObject) aObject;
			if (wJsonObject.keySet().contains(aTag)) {
				wListTagJson.add(wJsonObject);
			} else {
				for (Object wValue : wJsonObject.values()) {
					wListTagJson.addAll(foundMatchTags(aTag, wValue));
				}
			}
		} else if (aObject instanceof JSONArray) {
			JSONArray wJsonArray = (JSONArray) aObject;
			for (int i = 0; i < wJsonArray.length(); i++) {
				wListTagJson.addAll(foundMatchTags(aTag, wJsonArray.opt(i)));
			}
		}
		return wListTagJson;
	}

	public IHandlerInitMemoryCache getInitCache() {
		return pInitCacheHandler;
	}

	public JSONArray getJSONArray(final String currentPath,
			final JSONArray aUnresolvedJson) throws Exception {

		// preprocess content

		// check include content that must be resolve
		pLogger.logDebug(this, "getJSONArray", "preprocess resolve subcontent ");

		// resolve file and http and call handle for mem cache
		Object wResolvedString = resolveInclude(currentPath, aUnresolvedJson,
				pInitCacheHandler == null, new ArrayList<JSONObject>());
		if (pInitCacheHandler != null) {
			// call wit memory resolution only
			wResolvedString = resolveInclude(currentPath, wResolvedString,
					true, new ArrayList<JSONObject>());
		}
		if (wResolvedString instanceof JSONArray) {
			return (JSONArray) wResolvedString;
		}
		return null;
	}

	public JSONArray getJSONArray(final String aTag, final String aPath,
			final String aContentId) throws Exception {
		// get content
		pLogger.logInfo(this, "getJSONObject", "get content from id %s",
				aContentId);
		String wPath = aPath != null ? aPath + File.separatorChar + aContentId
				: aContentId;

		CXListRsrcText wRsrcs = pJsonResolver.getContent(aTag, wPath, false,
				null);
		if (wRsrcs != null && wRsrcs.size() > 0) {
			JSONArray wArr = new JSONArray();
			for (CXRsrcText wRsrc : wRsrcs) {
				String aContent = wRsrc.getContent();

				String wNotComment = removeComment(aContent);
				Object wNotCommentJson = checkIsJson(wNotComment);
				// check include content that must be resolve
				wArr.put(wNotCommentJson);
			}
			return getJSONArray(aPath, wArr);
		}
		return null;
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

		// preprocess content

		// check include content that must be resolve
		pLogger.logDebug(this, "getJSONObject",
				"preprocess resolve subcontent ");

		// resolve file and http and call handle for mem cache
		Object wResolvedObj = resolveInclude(currentPath, aUnresolvedJson,
				pInitCacheHandler == null, null);
		if (pInitCacheHandler != null) {
			// call wit memory resolution only
			wResolvedObj = resolveInclude(currentPath, wResolvedObj, true, null);
		}
		if (wResolvedObj instanceof JSONObject) {
			return (JSONObject) wResolvedObj;
		}
		return null;
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
	@Override
	public JSONObject getJSONObject(final String aTag,
			final String aFatherPath, final String aContentId) throws Exception {
		// get content
		pLogger.logInfo(this, "getJSONObject", "get content from id %s",
				aContentId);
		String wPath = aFatherPath != null ? aFatherPath + File.separatorChar
				+ aContentId : aContentId;

		CXListRsrcText wRsrcs = pJsonResolver.getContent(aTag, wPath, false,
				null);
		if (wRsrcs != null && wRsrcs.size() > 0) {
			// we get only the first one
			CXRsrcText wRsrc = wRsrcs.get(0);
			String aContent = wRsrc.getContent();

			String wNotComment = removeComment(aContent);
			// replace vars regarding the variable set in the path
			Map<String, String> wVars = getVariableFromPath(wPath);
			if (wVars != null) {
				wNotComment = CXStringUtils.replaceVariables(wNotComment,
						wVars, "");
			}
			Object wNotCommentJson = checkIsJson(wNotComment);
			// check include content that must be resolve
			return getJSONObject(aFatherPath, (JSONObject) wNotCommentJson);
		}
		return null;
	}

	public IJsonRsrcResolver getJsonResolver() {
		return pJsonResolver;
	}

	private List<JSONObject> getListFather(
			final List<JSONObject> aListOfFather, final Object aContent) {
		List<JSONObject> wFathersContent = new ArrayList<>();
		if (aListOfFather != null && aListOfFather.size() > 0) {
			wFathersContent.addAll(aListOfFather);
		}
		if (aContent instanceof JSONObject) {
			wFathersContent.add((JSONObject) aContent);
		}
		return wFathersContent;
	}

	private String getSubPath(final String aTag, final CXRsrcText aRsrc) {
		String wSubPath = aRsrc.getFullPath();
		int wIdx = wSubPath.lastIndexOf(File.separatorChar);
		wSubPath = wIdx != -1 ? wSubPath.substring(0, wIdx + 1) : wSubPath;
		CXRsrcProvider wProviderUsed = null;
		for (CXRsrcProvider aProvider : pJsonResolver.getRsrcProvider(aTag)) {
			if (wProviderUsed == null
					&& aRsrc.getFullPath().contains(
							aProvider.getDefDirectory().getPath())) {
				wProviderUsed = aProvider;
			}
		}

		if (wProviderUsed instanceof CXRsrcProviderMemory
				|| wProviderUsed instanceof CXRsrcProviderHttp) {
			// TODO change when we move it to utilities to use polymorphisme
			return "";
		}
		// replace
		return wSubPath.replace(wProviderUsed.getDefDirectory().getPath(), "");
	}

	private Object getValidContent(final CXRsrcText aRsrc) throws JSONException {
		String wSubContent = aRsrc.getContent();

		// remove comment
		wSubContent = removeComment(wSubContent);
		Object wSubContentObj = null;
		// check if it's json
		try {
			wSubContentObj = checkIsJson(wSubContent);
		} catch (Exception e) {
			throw new JSONException(String.format(
					"bad JSON content Exception=[%s] , content=[%S]", e,
					wSubContent));
		}
		return wSubContentObj;
	}

	private Map<String, String> getVariableFromPath(String aPath)
			throws UnsupportedEncodingException {
		if (aPath != null) {
			int wIdx = aPath.indexOf("?");
			if (wIdx != -1) {
				// need to parse to extract the memory key
				String wParam = aPath.substring(wIdx + 1);
				aPath = aPath.substring(0, wIdx);
				return CXQueryString.splitQueryFirst(wParam);
			}
		}

		return null;
	}

	private void initMemoryProviderCache(final Object aValidContent,
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
	protected Object resolveInclude(final String currentPath,
			final Object aContent, final boolean aUseMemoryProvider,
			final List<JSONObject> aFathersContent) throws Exception {

		List<JSONObject> wFathersContent = getListFather(aFathersContent,
				aContent);
		Object wResolvContent = aContent;

		for (String wTag : pJsonResolver.getListTags()) {
			// regexp that allow to catch the strings like

			/*
			 * Pattern wPatternDollarFile = Pattern.compile(
			 * "((\\n)*\\{(\\n)*\\s*\"\\" + wTag +
			 * "\"\\s*:(\\s*\".*\"\\s*)\\})", Pattern.MULTILINE);
			 */

			// looking for subcontent identified by a id e.g $file, $ur ,
			// $memory
			// Matcher wMatcherFile = wPatternDollarFile.matcher(aContent);
			List<String> wSubNoCommentContent = new ArrayList<>();

			for (JSONObject wMatch : foundMatchTags(wTag, wResolvContent)) {
				JSONObject wJsonSubId = wMatch;
				try {
					wSubNoCommentContent.clear();
					// set absolute path
					/*
					 * the content of the tag can be a string that is the path
					 * or an object that contain path property and properties
					 * property that are value to apply to the sub content ex :
					 * { "$tag":"file://myRelativeOrFullPath" } or { "$tag":{
					 * "path" : "file://myRelativeOrFullPath", "properties":{
					 * "key1":"val1", ... } } } for the e.g 2 with properties
					 * and path the subcontent that contain string value like
					 * {key1} will be replace by val1
					 */
					Object wlTag = wJsonSubId.opt(wTag);
					String wlPath = null;
					Map<String, String> replaceVars = null;
					boolean wMustBeInclude = true; // condition if the tag
													// should be taken in
													// account
					if (wlTag instanceof JSONObject) {
						JSONObject wlTagJson = (JSONObject) wlTag;
						wlPath = wlTagJson.optString(PATH);
						wMustBeInclude = evaluateCondition(wlTagJson
								.optString(COND));
					}
					if (wlPath == null || wlPath.isEmpty()) {
						pLogger.logInfo(this, "resolveInclude",
								"not a file include but another tag. we keep the jsonObject");
						wlPath = wlTag.toString();
					}

					if (wMustBeInclude) {
						List<String> wListPath = Arrays.asList(wlPath
								.split(SEP_PATH));
						for (String wPath : wListPath) {
							// if file we are allowed to put relative path
							if (!wPath.startsWith(EProviderKind.FILE.toString()
									+ "/")) {
								// we include te current path
								if (currentPath != null) {
									wPath = wPath.replace(
											EProviderKind.FILE.toString(),
											EProviderKind.FILE.toString()
													+ currentPath
													+ File.separatorChar);
								}
								pLogger.logInfo(this, "resolveInclude",
										"retrieve variable to replace from path");
								replaceVars = getVariableFromPath(wPath);
							}

							// read the current object . we set the list of the
							// father
							CXListRsrcText wRsrcs = pJsonResolver.getContent(
									wTag, wPath, aUseMemoryProvider,
									aFathersContent);
							if (wRsrcs != null && wRsrcs.size() > 0) {
								for (CXRsrcText wRsrc : wRsrcs) {
									// resolv subcontent
									Object wValidContent = getValidContent(wRsrc);
									// must be a JSONArray or JSONObject
									// replace vars in the resolve content
									wValidContent = checkIsJson(CXStringUtils
											.replaceVariables(
													wValidContent.toString(),
													replaceVars, ""));

									// resolve json path from the father json
									// object
									// with the json to include

									if (!aUseMemoryProvider) {
										initMemoryProviderCache(wValidContent,
												wTag);
									}
									addInheritParameter(wValidContent,
											replaceVars, wTag);

									// we resolve an include so we need to pass
									// the
									// list
									// of father plus the current one that is
									// his
									// father
									wSubNoCommentContent.add(resolveInclude(
											getSubPath(wTag, wRsrc),
											wValidContent, aUseMemoryProvider,
											wFathersContent).toString());
								}

							} else {
								if (!aUseMemoryProvider) {
									// no resolution we init the cache with
									// the current content
									initMemoryProviderCache(aContent, wTag);
								}
								wSubNoCommentContent.add(wMatch.toString());
							}

						}
					}

				} catch (Exception e) {
					// TODO Provider must return a typed exception and
					// not a global one
					if (pIgnoreMissingContent
							&& e.getCause() instanceof FileNotFoundException) {
						// continue but log warning
						pLogger.logWarn(this, "resolvInclude",
								"subfile not found {%s]", e.getMessage());

					} else {
						// raise e
						throw e;
					}

				}

				// replace file by empty json.
				String wResolvContentStr = wResolvContent.toString();
				if (wSubNoCommentContent.size() == 1) {
					wResolvContent = checkIsJson(wResolvContentStr.replace(
							wMatch.toString(), wSubNoCommentContent.get(0)));
				} else if (wSubNoCommentContent.size() == 0) {
					wResolvContent = checkIsJson(wResolvContentStr.replace(
							wMatch.toString(), EMPTYJSON));
				} else {
					String wMerge = "";
					for (String wSubContent : wSubNoCommentContent) {
						if (!wMerge.isEmpty()) {
							wMerge = wMerge + ",";
						}
						wMerge = wMerge + wSubContent;
					}
					wResolvContent = checkIsJson(wResolvContentStr.replace(
							wMatch.toString(), "[" + wMerge + "]"));
				}

			}

		}
		return wResolvContent;

	}

	@Override
	public void setIgnoreMissingContent(final boolean aIgnoreMissingContent) {
		this.pIgnoreMissingContent = aIgnoreMissingContent;
	}

	@Override
	public void setInitMemoryCache(final IHandlerInitMemoryCache aInitCache) {
		this.pInitCacheHandler = aInitCache;
	}

	public void setJsonResolver(final IJsonRsrcResolver aResolver) {
		pJsonResolver = aResolver;
	}

}
