package org.cohorte.utilities.json.provider;

import org.psem2m.utilities.rsrc.CXRsrcProviderMemory;

/**
 * allow to initialize memory cache for memory provider during the reading of
 * files
 *
 * @author apisu
 *
 */
public interface IHandlerInitMemoryCache {

	public void initCache(Object aContent, CXRsrcProviderMemory aMemoryProvider);
}
