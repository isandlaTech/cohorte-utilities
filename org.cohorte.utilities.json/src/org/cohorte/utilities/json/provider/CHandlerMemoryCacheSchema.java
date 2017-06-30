package org.cohorte.utilities.json.provider;


import org.psem2m.utilities.json.JSONObject;
import org.psem2m.utilities.logging.IActivityLogger;
import org.psem2m.utilities.rsrc.CXRsrcProviderMemory;
import org.psem2m.utilities.rsrc.CXRsrcText;
import org.psem2m.utilities.rsrc.CXRsrcTextReadInfo;
import org.psem2m.utilities.rsrc.CXRsrcUriPath;

public class CHandlerMemoryCacheSchema implements IHandlerInitMemoryCache {
	private static String DEFINITIONS = "definitions";
	private static String PREFIX_DEF_KEY = "#/definitions/";
	private final IActivityLogger pLogger;

	public CHandlerMemoryCacheSchema(final IActivityLogger aLogger) {
		pLogger = aLogger;
	}

	// set handler to cache content in memory
	@Override
	public void initCache(final String aContent,
			final CXRsrcProviderMemory aMemoryProvider) {
		try {
			JSONObject wobj = new JSONObject(aContent);
			JSONObject wDef = wobj.optJSONObject(DEFINITIONS);
			if (wDef != null) {
				for (String wKey : wDef.keySet()) {
					String wStrToCache = wDef.getJSONObject(wKey).toString(2);
					CXRsrcText wRsrcToCache = new CXRsrcText(new CXRsrcUriPath(
							wKey),
							CXRsrcTextReadInfo
									.newInstanceFromString(wStrToCache));
					aMemoryProvider.add(PREFIX_DEF_KEY + wKey, wRsrcToCache);
				}
			}
		} catch (Exception e) {
			pLogger.logSevere(this, "initCache",
					"can't init memory cache !%[s]", e);
		}
	}
}
