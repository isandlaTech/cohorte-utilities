package org.cohorte.utilities.json.provider;

import org.psem2m.utilities.json.JSONObject;

public interface IJsonProvider {

	/**
	 *
	 * @param aUnresolvedJson
	 * @return
	 * @throws Exception
	 */
	public JSONObject getJSONObject(final JSONObject aUnresolvedJson)
			throws Exception;
	/**
	 * return a JSONObject from a specific url (memory,file or http)
	 *
	 * @param aContentId
	 * @return
	 * @throws Exception
	 */
	public JSONObject getJSONObject(final String aContentId) throws Exception;

	/**
	 * return a JSONObject from a specific url (memory,file or http) for a
	 * specific tag
	 *
	 * @param aContentId
	 * @param aTag
	 *            : a string that is used for resolving inclusion of content
	 * @return
	 * @throws Exception
	 */
	public JSONObject getJSONObject(final String aTag, final String aContentId)
			throws Exception;

	/**
	 * return a JSONObject from a specific url (memory,file or http) for a
	 * specific tag
	 *
	 * @param aContentId
	 * @param aTag
	 * @param aSubPath
	 *
	 * @return
	 * @throws Exception
	 */
	public JSONObject getJSONObject(final String aSubPath, final String aTag,
			final String aContentId) throws Exception;

	public void purgeCache();

	/**
	 * allow to don't raise exception if content are not found true : exception
	 * raise if content is not found false : a warning is log but no excetion
	 * raised
	 *
	 * @param aIgnoreMissingContent
	 */
	public void setIgnoreMissingContent(final boolean aIgnoreMissingContent);

	/**
	 * set a handler to init memory provider during the resolution of files
	 *
	 * @param aInitCache
	 */
	public void setInitMemoryCache(final IHandlerInitMemoryCache aInitCache);

}
