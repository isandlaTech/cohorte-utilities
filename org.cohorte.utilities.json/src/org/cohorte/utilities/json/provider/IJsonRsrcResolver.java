package org.cohorte.utilities.json.provider;


import java.util.Collection;
import java.util.Set;

import org.psem2m.utilities.rsrc.CXRsrcProvider;
import org.psem2m.utilities.rsrc.CXRsrcText;

/**
 * defined the interface that have to be implemented to resolv subContent
 *
 * @author apisu
 *
 */
public interface IJsonRsrcResolver {

	/**
	 * return the String content identified by te aContentid. this id can be a
	 * httpurl, file or memory
	 *
	 * @param aContentId
	 * @return
	 */
	CXRsrcText getContent(String aTag, String aContentId,
			boolean aMemoryProvider) throws Exception;

	/**
	 * return the list of the content id that handle the resolve like $file,
	 * $http, include
	 *
	 * @return
	 */
	Set<String> getListTags();

	public Collection<CXRsrcProvider> getRsrcProvider(final String aTag);

	public CXRsrcProviderMemory getRsrcProviderMemory(final String aTag);

}