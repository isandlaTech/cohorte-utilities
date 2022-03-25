package org.cohorte.iot.json.validator.api;

import org.psem2m.utilities.json.JSONObject;

import com.networknt.schema.JsonSchema;


public class CJsonSchema {

	private final JSONObject pOriginJson;
	private JsonSchema pSchema;

	public CJsonSchema(final JsonSchema aSchema, final JSONObject aJsonSchema) {
		pSchema = aSchema;
		pOriginJson = aJsonSchema;
	}

	public JSONObject getJsonSchema() {
		return pOriginJson;
	}

	public JsonSchema getSchema() {
		return pSchema;
	}

	public void setSchema(final JsonSchema aSchema) {
		this.pSchema = aSchema;
	}

}
