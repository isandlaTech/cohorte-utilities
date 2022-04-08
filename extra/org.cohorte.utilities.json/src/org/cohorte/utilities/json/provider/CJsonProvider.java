package org.cohorte.utilities.json.provider;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.cohorte.utilities.json.provider.CJsonRsrcResolver.EProviderKind;
import org.psem2m.utilities.CXException;
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

import de.christophkraemer.rhino.javascript.RhinoScriptEngine;

public class CJsonProvider implements IJsonProvider {

	private static String COND = "cond";

	public static final String INCLUDE = "$include";

	private static String PATH = "path";
	private static final String SEP_PATH = ";file";

	private final String EMPTYJSON = "{}";

	private final ExecutorService pExecutors = Executors.newCachedThreadPool();

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

	// use for evaluate condition
	RhinoScriptEngine pRhinoScriptEngine;

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

		pRhinoScriptEngine = new RhinoScriptEngine();

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
			final JSONObject wObj = (JSONObject) aContent;
			if (aReplaceVars != null && wObj.keySet().contains(aTag)) {

				String wSubIncludeStr = wObj.optString(aTag);
				final int wIndexParameter = wSubIncludeStr.indexOf("?");
				if (wSubIncludeStr.contains("?")) {
					final Map<String, String> wReplaceVarsSubInclude = CXQueryString
							.splitQueryFirst(wSubIncludeStr.substring(
									wIndexParameter + 1,
									wSubIncludeStr.length()));
					aReplaceVars.putAll(wReplaceVarsSubInclude);
					wSubIncludeStr = wSubIncludeStr.substring(0,
							wIndexParameter);
				}

				final String wParameterUrl = CXQueryString
						.urlEncodeUTF8(aReplaceVars);
				wSubIncludeStr = wSubIncludeStr + "?" + wParameterUrl;
				wObj.put(aTag, wSubIncludeStr);
			}
		} else if (aContent instanceof JSONArray) {
			final JSONArray wArr = (JSONArray) aContent;
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
			final int indexSquare = aJsonString.indexOf('[');
			final int indexCurly = aJsonString.indexOf('{');
			if (indexCurly == -1 && indexSquare == -1) {// not a json
				return new JSONObject(aJsonString);
			} else if (indexCurly != -1 && indexSquare == -1) {
				return new JSONObject(aJsonString);
			} else if (indexCurly == -1 && indexSquare != -1) {
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
			pLogger.logInfo(this, "evaluateCondition", "eval condition %s ",
					aCondition);

			final Object wReply = pRhinoScriptEngine.eval(aCondition);
			pLogger.logInfo(this, "evaluateCondition",
					"eval condition %s , result=%s", aCondition, wReply);

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
		} catch (final Exception e) {
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
		final List<JSONObject> wListTagJson = new ArrayList<>();
		if (aObject instanceof JSONObject) {
			final JSONObject wJsonObject = (JSONObject) aObject;
			if (wJsonObject.keySet().contains(aTag)) {
				wListTagJson.add(wJsonObject);
			} else {
				for (final Object wValue : wJsonObject.values()) {
					wListTagJson.addAll(foundMatchTags(aTag, wValue));
				}
			}
		} else if (aObject instanceof JSONArray) {
			final JSONArray wJsonArray = (JSONArray) aObject;
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
			final JSONArray aUnresolvedJson,
			final Map<String, String> aReplaceVars) throws Exception {

		// preprocess content

		// check include content that must be resolve
		pLogger.logDebug(this, "getJSONArray", "preprocess resolve subcontent ");

		// resolve file and http and call handle for mem cache
		Object wResolvedString = resolveInclude(currentPath, aUnresolvedJson,
				pInitCacheHandler == null, new ArrayList<JSONObject>(),
				aReplaceVars);
		if (pInitCacheHandler != null) {
			// call wit memory resolution only
			wResolvedString = resolveInclude(currentPath, wResolvedString,
					true, new ArrayList<JSONObject>(), aReplaceVars);
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
		final String wPath = aPath != null ? aPath + "/" + aContentId : aContentId;

		final CXListRsrcText wRsrcs = pJsonResolver.getContent(aTag, wPath, false,
				null,null);
		if (wRsrcs != null && wRsrcs.size() > 0) {
			final JSONArray wArr = new JSONArray();
			for (final CXRsrcText wRsrc : wRsrcs) {
				final String aContent = wRsrc.getContent();

				final Object wNotCommentJson = checkIsJson(aContent);
				// check include content that must be resolve
				wArr.put(wNotCommentJson);
			}
			return getJSONArray(aPath, wArr, null);
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

		return getJSONObject(null, aUnresolvedJson, null);
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
			final JSONObject aUnresolvedJson, final Map<String, String> wVars)
					throws Exception {

		// preprocess content

		// check include content that must be resolve
		pLogger.logDebug(this, "getJSONObject",
				"preprocess resolve subcontent ");

		// resolve file and http and call handle for mem cache
		final List<JSONObject> wListFather = new ArrayList<>();
		wListFather.add(aUnresolvedJson);
		Object wResolvedObj = resolveInclude(currentPath, aUnresolvedJson,
				pInitCacheHandler == null, wListFather, wVars);
		if (pInitCacheHandler != null) {
			// call wit memory resolution only
			wResolvedObj = resolveInclude(currentPath, wResolvedObj, true,
					null, wVars);
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
		return getJSONObject(aTag, aFatherPath, aContentId, false);
	}

	public JSONObject getJSONObject(final String aTag,
			final String aFatherPath, final String aContentId,
			final boolean noIncludeResolution) throws Exception {
		// get content
		pLogger.logInfo(this, "getJSONObject", "get content from id %s",
				aContentId);
		final String wPath = aFatherPath != null ? aFatherPath + "/" + aContentId
				: aContentId;
		final Map<String, String> wVars = getVariableFromPath(wPath);

		final CXListRsrcText wRsrcs = pJsonResolver.getContent(aTag, wPath, false,
				null,wVars);
		if (wRsrcs != null && wRsrcs.size() > 0) {
			// we get only the first one
			final CXRsrcText wRsrc = wRsrcs.get(0);
			String wNotComment = wRsrc.getContent();

			// replace vars regarding the variable set in the path

			wNotComment = CXStringUtils.replaceVariables(wNotComment, wVars);
			wNotComment = CJsonResolvTernary.resultTernary(pLogger,
					wNotComment, pRhinoScriptEngine);
			final Object wNotCommentJson = checkIsJson(wNotComment);
			// check include content that must be resolve
			if (!noIncludeResolution) {
				return getJSONObject(aFatherPath, (JSONObject) wNotCommentJson,
						wVars);
			}
			return (JSONObject) wNotCommentJson;
		}
		return null;
	}

	public IJsonRsrcResolver getJsonResolver() {
		return pJsonResolver;
	}

	private List<JSONObject> getListFather(
			final List<JSONObject> aListOfFather, final Object aContent) {
		final List<JSONObject> wFathersContent = new ArrayList<>();
		if (aListOfFather != null && aListOfFather.size() > 0) {
			wFathersContent.addAll(aListOfFather);
		}
		if (aContent instanceof JSONObject) {
			wFathersContent.add((JSONObject) aContent);
		}
		return wFathersContent;
	}

	/**
	 * return the path of the directory for the current resource aRsrc
	 *
	 * @param aTag
	 * @param aRsrc
	 * @return
	 */
	private String getSubPath(final String aTag, final CXRsrcText aRsrc) {
		String wSubPath = aRsrc.getFullPath();
		// always used / even on windows. the path are always define with /
		final int wIdx = wSubPath.lastIndexOf("/");
		wSubPath = wIdx != -1 ? wSubPath.substring(0, wIdx + 1) : wSubPath;
		CXRsrcProvider wProviderUsed = null;
		for (final CXRsrcProvider aProvider : pJsonResolver.getRsrcProvider(aTag)) {
			if (wProviderUsed == null
					&& aProvider != null
					&& aRsrc.getFullPath().contains(
							aProvider.getDefDirectory().getPath())) {
				wProviderUsed = aProvider;
			}
		}

		if (wProviderUsed instanceof CXRsrcProviderMemory
				|| wProviderUsed instanceof CXRsrcProviderHttp
				|| wProviderUsed == null) {
			// TODO change when we move it to utilities to use polymorphisme
			return "";
		}
		// replace
		return wSubPath.replace(wProviderUsed.getDefDirectory().getPath(), "");
	}

	private Object getValidContent(final CXRsrcText aRsrc) throws JSONException {
		final String wSubContent = aRsrc.getContent();

		// remove comment
		Object wSubContentObj = null;
		// check if it's json
		try {
			wSubContentObj = checkIsJson(wSubContent);
		} catch (final Exception e) {
			throw new JSONException(String.format(
					"bad JSON content Exception=[%s] , content=[%S]", e,
					wSubContent));
		}
		return wSubContentObj;
	}

	private Map<String, String> getVariableFromPath(String aPath)
			throws UnsupportedEncodingException {
		if (aPath != null) {
			final int wIdx = aPath.indexOf("?");
			if (wIdx != -1) {
				// need to parse to extract the memory key
				final String wParam = aPath.substring(wIdx + 1);
				aPath = aPath.substring(0, wIdx);
				return CXQueryString.splitQueryFirst(wParam);
			}
		}

		return null;
	}

	private void initMemoryProviderCache(final Object aValidContent,
			final String aTag) {
		final CXRsrcProviderMemory wMemProv = pJsonResolver
				.getRsrcProviderMemory(aTag);
		if (pInitCacheHandler != null && wMemProv != null) {
			// call the initCache memory
			pInitCacheHandler.initCache(aValidContent, wMemProv);
		}
	}


	@Override
	public void purgeCache() {
		for(final CXRsrcProvider wProv:pJsonResolver.getRsrcProvider()) {
			wProv.purgeCache();
		}
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
			final List<JSONObject> aFathersContent,
			final Map<String, String> aReplaceVars) throws Exception {

		Object wResolvContent = aContent;

		for (final String wTag : pJsonResolver.getListTags()) {
			// regexp that allow to catch the strings like

			/*
			 * Pattern wPatternDollarFile = Pattern.compile(
			 * "((\\n)*\\{(\\n)*\\s*\"\\" + wTag +
			 * "\"\\s*:(\\s*\".*\"\\s*)\\})", Pattern.MULTILINE);
			 */

			// looking for subcontent identified by a id e.g $file, $ur ,
			// $memory
			// Matcher wMatcherFile = wPatternDollarFile.matcher(aContent);
			final List<String> wSubNoCommentContent = new ArrayList<>();

			for (final JSONObject wMatch : foundMatchTags(wTag, wResolvContent)) {
				final JSONObject wJsonSubId = wMatch;
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
					final Object wlTag = wJsonSubId.opt(wTag);
					String wlPath = null;
					boolean wMustBeInclude = true; // condition if the tag
					// should be taken in
					// account
					if (wlTag instanceof JSONObject) {
						final JSONObject wlTagJson = new JSONObject(wlTag.toString());
						wlPath = wlTagJson.optString(PATH);
						wMustBeInclude = evaluateCondition(wlTagJson
								.optString(COND));

						wlTagJson.remove(COND);
					} else {
						pLogger.logInfo(this, "resolveInclude",
								"not a file include Object but another tag. we keep the jsonObject");
						wlPath = wlTag.toString();
						try {
							final JSONObject wOtherTag = new JSONObject(wlTag);
							wMustBeInclude = evaluateCondition(wOtherTag
									.optString(COND));
						} catch (final Exception e) {
							// do nothing

						}
					}

					if (wMustBeInclude) {
						final List<String> wListPath = Arrays.asList(wlPath
								.split(SEP_PATH));
						final List<Future<?>> wListFuture = new ArrayList<>();
						for (final String wPath : wListPath) {
							wListFuture.add(pExecutors.submit(new Runnable() {
								@Override
								public void run() {
									try {
										final List<JSONObject> wListFather = new ArrayList<>();
										wListFather.addAll(getListFather(aFathersContent,
												aContent));
										final Map<String,String> wCloneReplaceVars = new HashMap<>();
										if( aReplaceVars != null ) {
											wCloneReplaceVars.putAll(aReplaceVars);
										}
										resolveIncludePath(aContent,wMatch,wPath,currentPath,aUseMemoryProvider,wlTag,wTag,wListFather,wCloneReplaceVars,wSubNoCommentContent);

									}catch(final Exception e) {
										pLogger.logSevere(this, "resolveInclude", "Fail to resolve include Path Error=[%s]",e);
									}
								}
							}));

						}
						boolean wFinish = false;
						while(!wFinish) {
							wFinish=true;
							for(final Future<?> wFuture:wListFuture) {
								wFinish=wFinish && wFuture.isDone();
							}
						}

					}

				} catch (final Exception e) {
					// TODO Provider must return a typed exception and
					// not a global one
					if (pIgnoreMissingContent
							&& e.getCause() instanceof FileNotFoundException) {
						// continue but log warning
						pLogger.logWarn(this, "resolvInclude",
								"subfile not found {%s]", e.getMessage());

					} else {
						if (e instanceof IOException) {
							throw new JSONException(String.format(
									"can't resolve JSON=[%s]\n cause=[%s]",
									wResolvContent, CXException.eInString(e)));
						} else {
							throw e;
						}

					}

				}

				// replace file by empty json.
				final String wResolvContentStr = wResolvContent.toString();
				if (wSubNoCommentContent.size() == 1) {
					wResolvContent = wResolvContentStr.replace(
							wMatch.toString(), wSubNoCommentContent.get(0));
				} else if (wSubNoCommentContent.size() == 0) {
					wResolvContent = wResolvContentStr.replace(
							wMatch.toString(), EMPTYJSON);
				} else {
					String wMerge = "";
					for (final String wSubContent : wSubNoCommentContent) {
						if (!wMerge.isEmpty()) {
							wMerge = wMerge + ",";
						}
						wMerge = wMerge + wSubContent;
					}
					wResolvContent = wResolvContentStr.replace(
							wMatch.toString(), "[" + wMerge + "]");
				}

			}

		}
		wResolvContent = CJsonResolvTernary.resultTernary(pLogger,
				wResolvContent, pRhinoScriptEngine);
		wResolvContent = checkIsJson(wResolvContent.toString());
		return wResolvContent;

	}
	private void resolveIncludePath(final Object aContent,final JSONObject aMatch,final String aPath, final String currentPath, final boolean aUseMemoryProvider, final Object alTag, final String aTag, final List<JSONObject> aFathersContent, final Map<String,String> aReplaceVars, final List<String> aSubNoCommentContent) throws Exception {
		String wPath = aPath;
		String wFatherPath = "";
		Map<String, String> replaceVars = null;

		if( !wPath.isEmpty() ){
			if( wPath.startsWith("[") ) {
				// conver to string path with file:// and ";" as separator
				final JSONArray wPathMerge = new JSONArray(wPath);
				wPath = "";
				for(int i=0;i<wPathMerge.length();i++) {
					wPath+="file://"+wPathMerge.getString(i)+";";
				}
			}else {
				if( !wPath.startsWith(EProviderKind.FILE.toString()) && !wPath.startsWith(EProviderKind.MEMORY.toString()) && !wPath.startsWith(EProviderKind.HTTP.toString())) {
					wPath="file"+wPath;
				}
				if( wPath.indexOf("?") != -1 ) {
					wFatherPath = wPath.substring(0,wPath.substring(0,wPath.indexOf("?")).lastIndexOf("/"));

				}else {
					if( wPath.lastIndexOf("/") >=0) {
						wFatherPath = wPath.substring(0,wPath.lastIndexOf("/"));
					}else {
						wFatherPath ="";
					}
				}
				if( wFatherPath.equals("file:/") ){
					wFatherPath = "";
				}
			}
		}

		// if file we are allowed to put relative path
		// we include te current path
		if (currentPath != null && !currentPath.isEmpty()) {
			if (!wPath.startsWith(EProviderKind.FILE
					.toString() + "/")) {
				if( currentPath.endsWith("/")) {
					wPath = wPath.replace(
							EProviderKind.FILE.toString(),
							EProviderKind.FILE.toString()
							+ currentPath );
				}else {
					wPath = wPath.replace(
							EProviderKind.FILE.toString(),
							EProviderKind.FILE.toString()
							+ currentPath + "/");
				}
			}

		}
		pLogger.logInfo(this, "resolveInclude",
				"retrieve variable to replace from path");
		replaceVars = aReplaceVars;
		final Map<String, String> wCurrentReplaceVars = getVariableFromPath(wPath);
		if (wCurrentReplaceVars != null) {
			if (replaceVars != null) {
				replaceVars.putAll(wCurrentReplaceVars);
			} else {
				replaceVars = wCurrentReplaceVars;
			}
		}

		// read the current object . we set the list of the
		// father
		final CXListRsrcText wRsrcs = pJsonResolver.getContent(
				aTag, wPath.isEmpty() ? alTag.toString()
						: wPath, aUseMemoryProvider,
						aFathersContent,replaceVars);
		if (wRsrcs != null && wRsrcs.size() > 0) {
			for (final CXRsrcText wRsrc : wRsrcs) {
				// resolv subcontent
				Object wValidContent = getValidContent(wRsrc);
				// must be a JSONArray or JSONObject
				// replace vars in the resolve content
				final String wResolvVariable = CXStringUtils
						.replaceVariables(
								wValidContent.toString(),
								replaceVars);

				wValidContent = checkIsJson(wResolvVariable);

				// call ternary resolve
				// resolve json path from the father json
				// object
				// with the json to include

				if (!aUseMemoryProvider) {
					initMemoryProviderCache(wValidContent,
							aTag);
				}
				addInheritParameter(wValidContent,
						replaceVars, aTag);

				// we resolve an include so we need to pass
				// the
				// list
				// of father plus the current one that is
				// his
				// father
				// add to manage the sub current path when
				// we hae include in genreator (TODO enhance
				// this mechanism)
				wFatherPath = (wFatherPath.startsWith(EProviderKind.FILE.toString()) ? wFatherPath.substring(7):wFatherPath);
				String wCurrentPathInclude=null;
				if (currentPath != null && !wFatherPath.startsWith("/") ) {
					if( currentPath.endsWith("/") ) {
						wCurrentPathInclude = currentPath+wFatherPath;

					}else{
						wCurrentPathInclude = currentPath+"/"+wFatherPath;

					}
				}else {
					wCurrentPathInclude =wFatherPath;
				}


				aSubNoCommentContent.add(resolveInclude(
						wCurrentPathInclude,
						wValidContent, aUseMemoryProvider,
						aFathersContent, replaceVars)
						.toString());
			}

		} else {
			if (!aUseMemoryProvider) {
				// no resolution we init the cache with
				// the current content
				initMemoryProviderCache(aContent, aTag);
			}
			aSubNoCommentContent.add(aMatch.toString());
		}
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
