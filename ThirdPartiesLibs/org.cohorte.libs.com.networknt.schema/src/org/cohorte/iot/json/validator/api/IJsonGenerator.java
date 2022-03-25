package org.cohorte.iot.json.validator.api;

import org.psem2m.utilities.json.JSONObject;
import org.psem2m.utilities.logging.IActivityLogger;

public interface IJsonGenerator {

	public JSONObject generateFakeJson(IActivityLogger aLogger,
			JSONObject aSchema, boolean aIsEmpty) throws Exception;

	public JSONObject generateFakeJson(JSONObject aSchema, boolean aIsEmpty)
			throws Exception;
}
