package org.cohorte.iot.json.validator.api;

import java.util.concurrent.ConcurrentHashMap;

import org.psem2m.utilities.json.JSONObject;
import org.psem2m.utilities.logging.IActivityLogger;

public class CSchemasValidator {

	private final ConcurrentHashMap<String, CJsonSchema> pMapIdSchema = new ConcurrentHashMap<String,CJsonSchema>();
	private final IActivityLogger pLogger;
	public CSchemasValidator(final IActivityLogger aLogger){
		pLogger = aLogger;
	}

	public void addSchema(final String aId, final CJsonSchema aSchema) {
		pMapIdSchema.put(aId, aSchema);
	}
	public void addSchema(final String aId, final JSONObject aSchema) throws Exception {
		final CJsonSchema wSchema = CJsonValidatorFactory.getSingleton().getSchema(pLogger, aSchema);
		addSchema(aId, wSchema);
	}
	public CJsonSchema deleteSchema(final String aId) throws Exception {
		return pMapIdSchema.remove(aId);
	}
	public CJsonSchema getSchema(final String aId) {
		return pMapIdSchema.get(aId);
	}
	public boolean hasSchema(final String aId) {
		return pMapIdSchema.containsKey(aId);
	}
	public boolean validateJson(final String aIdSchema, final JSONObject aDataToValidate) throws Exception{
		if( !hasSchema(aIdSchema) ) {
			throw new Exception(String.format("can't find schema for id %s",aIdSchema));
		}
		return CJsonValidatorFactory.getSingleton().valdate(pLogger, getSchema(aIdSchema), aDataToValidate);
	}
}
