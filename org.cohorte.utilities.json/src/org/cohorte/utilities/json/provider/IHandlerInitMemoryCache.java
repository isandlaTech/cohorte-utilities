package org.cohorte.utilities.json.provider;


/**
 * allow to initialize memory cache for memory provider during the reading of
 * files
 *
 * @author apisu
 *
 */
public interface IHandlerInitMemoryCache {

	public void initCache(String aContent, CXRsrcProviderMemory aMemoryProvider);
}
