package org.cohorte.iot.json.validator.api;

import org.psem2m.utilities.json.JSONObject;
import org.psem2m.utilities.logging.IActivityLogger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

/**
 *
 * @author apisu
 *
 */
public class CJsonValidator implements IJsonValidator {

	private static Object lock = new Object();

	private static IJsonValidator pSingleton;

	public static IJsonValidator getDefault() {
		synchronized (lock) {
			if (pSingleton == null) {
				pSingleton = new CJsonValidator();
			}
		}
		return pSingleton;
	}

	private CJsonValidator() {
		// set private contructor
	}

	public CJsonSchema getSchema(final IActivityLogger aLogger,
			final JSONObject aSchema) throws SchemaException {
		// create schema
		try {
			JsonSchemaFactory wFactory = JsonSchemaFactory.byDefault();
			aLogger.logDebug(this, "getSchema",
					"create JsonNode fro JSONObject for the schema");
			ObjectMapper wMapper = new ObjectMapper();
			JsonNode wSchema = wMapper.readTree(aSchema.toString());

			aLogger.logDebug(this, "getSchema", "create JsonSchema");
			return new CJsonSchema(wFactory.getJsonSchema(wSchema), aSchema);
		} catch (Exception e) {
			throw new SchemaException(e, e.getMessage());
		}
	}

	public boolean valdate(final IActivityLogger aLogger,
			final CJsonSchema aSchema, final JSONObject aData) throws Exception {
		// validate json
		try {
			aLogger.logDebug(this, "getSchema",
					"create JsonNode fro JSONObject for the data");
			ObjectMapper wMapper = new ObjectMapper();
			JsonNode wJson = wMapper.readTree(aData.toString());

			aLogger.logDebug(this, "getSchema", "validate data with schema");
			ProcessingReport wReport = aSchema.getSchema()
					.validate(wJson, true);
			if (wReport.isSuccess()) {
				return true;
			} else {
				throw new SchemaException("ERROR; failed schema validation ! ["
						+ wReport.toString() + "] ");
			}
		} catch (Exception e) {
			throw new SchemaException(e, e.getMessage());
		}
	}

	public boolean validateJson(final IActivityLogger aLogger,
			final JSONObject aSchema, final JSONObject aJson)
			throws SchemaException {
		try {
			CJsonSchema wJsSchema = getSchema(aLogger, aSchema);
			return valdate(aLogger, wJsSchema, aJson);
		} catch (Exception e) {
			throw new SchemaException(e, e.getMessage());
		}

	}
}