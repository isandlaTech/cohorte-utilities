package org.cohorte.iot.json.validator.api;

import org.psem2m.utilities.json.JSONObject;

import com.networknt.schema.JsonSchema;
import com.networknt.schema.SpecVersion;


public class CJsonSchema {

	private final JSONObject pOriginJson;
	private JsonSchema pSchema;
	private final SpecVersion.VersionFlag pVersionFlag;
	public CJsonSchema(final JsonSchema aSchema, final JSONObject aJsonSchema,final SpecVersion.VersionFlag aFlag) {
		pSchema = aSchema;
		pOriginJson = aJsonSchema;
		pVersionFlag = aFlag;
	}

	public JSONObject getJsonSchema() {
		return pOriginJson;
	}
	public JsonSchema getSchema() {
		return pSchema;
	}
	public SpecVersion.VersionFlag getVersionFlag() {
		return pVersionFlag;
	}

	public void setSchema(final JsonSchema aSchema) {
		this.pSchema = aSchema;
	}

}
