package com.cohorte.iot.json.validator;

import java.io.File;

import org.cohorte.iot.json.validator.api.CJsonGeneratorFactory;
import org.cohorte.iot.json.validator.api.CJsonValidatorFactory;
import org.junit.Test;
import org.psem2m.utilities.files.CXFileText;
import org.psem2m.utilities.json.JSONObject;
import org.psem2m.utilities.logging.CActivityLoggerNull;
import org.psem2m.utilities.logging.IActivityLogger;

import junit.framework.TestCase;

public class CTestSchemaValidation extends TestCase {

	@Test
	public void testValidation() {
		boolean wThrowable = false;

		try {

			CXFileText wFileSchema = new CXFileText(
					System.getProperty("user.dir") + File.separatorChar
							+ "files" + File.separatorChar + "schema.js");

			/*
			 * CXFileText wFileData = new CXFileText(
			 * System.getProperty("user.dir") + File.separatorChar + "files" +
			 * File.separatorChar + "data.js");
			 * 
			 * JSONObject wData = new JSONObject(wFileData.readAll());
			 */
			JSONObject wSchema = new JSONObject(wFileSchema.readAll());
			System.out.println(CJsonGeneratorFactory.getSingleton()
					.generateFakeJson(wSchema, true));
			IActivityLogger wLogger = CActivityLoggerNull.getInstance();
			/*
			 * CJsonValidatorFactory.getSingleton().validateJson(wLogger,
			 * wSchema, wData);
			 */

		} catch (Exception e) {
			e.printStackTrace();
			wThrowable = true;
		}

		try {

			CXFileText wFileSchema = new CXFileText(
					System.getProperty("user.dir") + File.separatorChar
							+ "files" + File.separatorChar + "schema.js");

			CXFileText wFileData = new CXFileText(
					System.getProperty("user.dir") + File.separatorChar
							+ "files" + File.separatorChar + "datainvalid.js");

			JSONObject wData = new JSONObject(wFileData.readAll());
			JSONObject wSchema = new JSONObject(wFileSchema.readAll());

			IActivityLogger wLogger = CActivityLoggerNull.getInstance();
			CJsonValidatorFactory.getSingleton().validateJson(wLogger, wSchema,
					wData);
		} catch (Exception e) {
			wThrowable = true;
		}

	}
}
