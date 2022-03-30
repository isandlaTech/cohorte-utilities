package org.cohorte.iot.json.validator.api;

import org.psem2m.utilities.json.JSONObject;
import org.psem2m.utilities.logging.IActivityLogger;

import com.networknt.schema.SpecVersion;

/**
 * provide method in order to validate JSON regarding a json schema that follow
 * te specification IEFT draft 6
 *
 * @author apisu
 *
 */
public interface IValidator {

	/**
	 * validate if the schema provide as a JSONObject is a valide and follow the
	 * specification
	 *
	 * @param aLogger
	 * @param aSchema
	 * @return
	 */
	public CJsonSchema getSchema(IActivityLogger aLogger, JSONObject aSchema, SpecVersion.VersionFlag aFlagVersion)
			throws Exception;

	/**
	 * validate data regarding the schema in parameter
	 *
	 * @param aLogger
	 * @param aSchema
	 * @param aJson
	 * @return
	 */
	public boolean valdate(IActivityLogger aLogger, CJsonSchema aSchema,
			JSONObject aData) throws Exception;

	/**
	 * validate the JSON data using the schema in parameter
	 *
	 * @param aLogger
	 * @param aSchema
	 * @param aJson
	 * @return
	 */
	public boolean validateJson(IActivityLogger aLogger, JSONObject aSchema,
			JSONObject aJson, SpecVersion.VersionFlag aFlagVersion) throws Exception;
}
