package org.cohorte.iot.json.validator.api;

import java.util.Set;

import org.psem2m.utilities.json.JSONObject;
import org.psem2m.utilities.logging.IActivityLogger;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

/**
 *
 * @author apisu
 *
 */
public class CJsonValidatorDefault implements IValidator {



	public CJsonValidatorDefault() {
		// set private contructor
	}
	@Override
	public CJsonSchema getSchema(final IActivityLogger aLogger,
			final JSONObject aSchema) throws SchemaException {
		// create schema
		try {
			final CJsonValidatorFactory wFactory = CJsonValidatorFactory.getFactory(SpecVersion.VersionFlag.V4);
			return new CJsonSchema(wFactory.getJsonSchemaFromStringContent(aSchema.toString()), aSchema, SpecVersion.VersionFlag.V4);
		} catch (final Exception e) {
			throw new SchemaException(e, e.getMessage());
		}
	}
	@Override
	public CJsonSchema getSchema(final IActivityLogger aLogger,
			final JSONObject aSchema, final SpecVersion.VersionFlag aFlagVersion) throws SchemaException {
		// create schema
		try {
			final CJsonValidatorFactory wFactory = CJsonValidatorFactory.getFactory(aFlagVersion);
			return new CJsonSchema(wFactory.getJsonSchemaFromStringContent(aSchema.toString()), aSchema, aFlagVersion);
		} catch (final Exception e) {
			throw new SchemaException(e, e.getMessage());
		}
	}

	@Override
	public boolean valdate(final IActivityLogger aLogger,
			final CJsonSchema aSchema, final JSONObject aData) throws Exception {
		final CJsonValidatorFactory wFactory = CJsonValidatorFactory.getFactory(aSchema.getVersionFlag());
		final JsonNode node = wFactory.getJsonNodeFromStringContent(aData.toString());
		final JsonSchema wSchema = aSchema.getSchema();
		final Set<ValidationMessage> errors = wSchema.validate(node);

		return errors.size() == 0;
	}


	@Override
	public boolean validateJson(final IActivityLogger aLogger,
			final JSONObject aSchema, final JSONObject aJson)
					throws SchemaException {
		try {
			final CJsonSchema wJsSchema = getSchema(aLogger, aSchema,SpecVersion.VersionFlag.V4);
			return valdate(aLogger, wJsSchema, aJson);
		} catch (final Exception e) {
			throw new SchemaException(e, e.getMessage());
		}
	}

	@Override
	public boolean validateJson(final IActivityLogger aLogger,
			final JSONObject aSchema, final JSONObject aJson, final SpecVersion.VersionFlag aFlagVersion)
					throws SchemaException {
		try {
			final CJsonSchema wJsSchema = getSchema(aLogger, aSchema,aFlagVersion);
			return valdate(aLogger, wJsSchema, aJson);
		} catch (final Exception e) {
			throw new SchemaException(e, e.getMessage());
		}
	}
}
}