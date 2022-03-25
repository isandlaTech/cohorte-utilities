package org.cohorte.libs.com.networknt.schema;

import java.io.File;

import org.cohorte.iot.json.validator.api.CJsonSchema;
import org.cohorte.iot.json.validator.api.CJsonValidatorFactory;
import org.cohorte.iot.json.validator.api.CJsonValidatorFactory;
import org.junit.Test;
import org.psem2m.utilities.CXTimer;
import org.psem2m.utilities.files.CXFileText;
import org.psem2m.utilities.json.JSONObject;
import org.psem2m.utilities.logging.CActivityLoggerBasicConsole;
import org.psem2m.utilities.logging.CActivityLoggerNull;
import org.psem2m.utilities.logging.IActivityLogger;

import junit.framework.TestCase;

public class CTestSchemaValidation extends TestCase {

	@Test
	public void testValidation() {
		boolean wThrowable = false;

		try {
			double wSum = 0;
			final int max = 10000;
			for(int i=0;i<max;i++) {
				final CXTimer wTimer = new CXTimer();
				final CXFileText wFileSchema = new CXFileText(
						System.getProperty("user.dir") + File.separatorChar
						+ "files" + File.separatorChar + "schema.js");


				final CXFileText wFileData = new CXFileText(
						System.getProperty("user.dir") + File.separatorChar + "files" +
								File.separatorChar + "data.js");

				final JSONObject wData = new JSONObject(wFileData.readAll());

				final JSONObject wSchema = new JSONObject(wFileSchema.readAll());

				final IActivityLogger wLogger = CActivityLoggerNull.getInstance();
				final CJsonSchema aSchema =CJsonValidatorFactory.getSingleton().getSchema(CActivityLoggerBasicConsole.getInstance(),wSchema);
				wTimer.start();

				CJsonValidatorFactory.getSingleton().validateJson(wLogger,
						wSchema, wData);
				wTimer.stop();
				System.out.println("time "+wTimer.getDurationMs());
				wSum+=wTimer.getDurationMs();
			}
			System.out.println("mean "+(wSum/max));

		} catch (final Exception e) {
			e.printStackTrace();
			wThrowable = true;
		}

		try {

			final CXFileText wFileSchema = new CXFileText(
					System.getProperty("user.dir") + File.separatorChar
					+ "files" + File.separatorChar + "schema.js");

			final CXFileText wFileData = new CXFileText(
					System.getProperty("user.dir") + File.separatorChar
					+ "files" + File.separatorChar + "datainvalid.js");

			final JSONObject wData = new JSONObject(wFileData.readAll());
			final JSONObject wSchema = new JSONObject(wFileSchema.readAll());

			final IActivityLogger wLogger = CActivityLoggerNull.getInstance();
			CJsonValidatorFactory.getSingleton().validateJson(wLogger, wSchema,
					wData);
		} catch (final Exception e) {
			wThrowable = true;
		}

	}
}
