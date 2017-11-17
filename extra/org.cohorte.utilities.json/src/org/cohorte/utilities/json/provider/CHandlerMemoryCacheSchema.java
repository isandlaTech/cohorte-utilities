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

	private static String SCHEMA = "schema";
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
			JSONObject wSchema = wobj.optJSONObject(SCHEMA);

			JSONObject wDef = wobj.optJSONObject(DEFINITIONS);
			if (wDef == null) {
				// unit test case the definition is under schema because the
				// file is
				// not include.
				wDef = wSchema != null ? wSchema.optJSONObject(DEFINITIONS)
						: null;
			}
			if (wDef != null) {
				for (String wKey : wDef.keySet()) {
					String wStrToCache = wDef.getJSONObject(wKey).toString(2);
					String wFinalKey = PREFIX_DEF_KEY + wKey;
					CXRsrcText wRsrcToCache = new CXRsrcText(new CXRsrcUriPath(
							wKey),
							CXRsrcTextReadInfo
									.newInstanceFromString(wStrToCache));
					aMemoryProvider.add(wFinalKey, wRsrcToCache);
				}
				pLogger.logDebug(this, "initCache", "add %s memory cache",
						wDef.keySet());
			}
		} catch (Exception e) {
			pLogger.logSevere(this, "initCache",
					"can't init memory cache !%[s]", e);
		}
	}
}
