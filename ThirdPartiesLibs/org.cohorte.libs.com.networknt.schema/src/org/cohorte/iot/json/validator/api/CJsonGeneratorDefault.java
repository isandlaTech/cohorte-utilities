package org.cohorte.iot.json.validator.api;

import org.psem2m.utilities.json.JSONObject;
import org.psem2m.utilities.logging.CActivityLoggerBasicConsole;
import org.psem2m.utilities.logging.IActivityLogger;

import io.apptik.json.JsonElement;
import io.apptik.json.JsonObject;
import io.apptik.json.generator.JsonGenerator;
import io.apptik.json.generator.JsonGeneratorConfig;
import io.apptik.json.schema.SchemaV4;

public class CJsonGeneratorDefault implements IJsonGenerator {

	@Override
	public JSONObject generateFakeJson(final IActivityLogger aLogger,
			final JSONObject aSchema, final boolean aIsEmpty) throws Exception {
		SchemaV4 schema = new SchemaV4().wrap(JsonElement.readFrom(
				aSchema.toString()).asJsonObject());

		JsonGeneratorConfig gConf = new JsonGeneratorConfig();
		gConf.emptyJson = aIsEmpty;

		aLogger.logInfo(this, "validateSchema", "generate fake json");
		JsonObject wJob = new JsonGenerator(schema, gConf).generate()
				.asJsonObject();

		return new JSONObject(wJob.toString());

	}

	@Override
	public JSONObject generateFakeJson(final JSONObject aSchema,
			final boolean aIsEmpty) throws Exception {
		return this.generateFakeJson(CActivityLoggerBasicConsole.getInstance(),
				aSchema, aIsEmpty);
	}
}
