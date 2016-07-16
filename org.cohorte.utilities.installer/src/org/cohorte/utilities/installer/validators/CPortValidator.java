package org.cohorte.utilities.installer.validators;

import static org.cohorte.utilities.installer.CInstallerTools.getServiceLogger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.psem2m.utilities.logging.IActivityLogger;

import com.izforge.izpack.panels.userinput.processorclient.ProcessingClient;
import com.izforge.izpack.panels.userinput.validator.Validator;

public class CPortValidator implements Validator {

	private static int TIME_OUT = 100;

	private final IActivityLogger pLogger;

	public CPortValidator() {
		pLogger = getServiceLogger();
	}

	private boolean checkPort(final String port) {
		pLogger.logWarn(this, "validate", "Checking port %s availability.",
				port);
		return isPortAvailable(Integer.parseInt(port));
	}

	/**
	 * @see "http://stackoverflow.com/a/13826145/3402449"
	 *
	 * @param aPort
	 * @return
	 */
	private boolean isPortAvailable(final int aPort) {
		Socket s = null;
		try {
			s = new Socket();
			s.connect(new InetSocketAddress("localhost", aPort), TIME_OUT);
			return false;
		} catch (IOException e) {
			return true;
		} finally {
			if (s != null) {
				try {
					s.close();
				} catch (IOException e) {
					pLogger.logSevere(this, "", "Forbidden zone.\n %s", e);
					throw new RuntimeException(
							"This code souldn't be reached.", e);
				}
			}
		}
	}

	public boolean validate(final ProcessingClient client) {

		String value1 = client.getFieldContents(0);

		if ((value1 == null) || (value1.length() == 0)) {
			pLogger.logWarn(this, "validate",
					"No provided port number! Validation returns false");
			return false;
		} else {
			pLogger.logWarn(this, "validate", "Provided port numbers : %s.",
					value1);
			return checkPort(value1);
		}
	}
}
