package org.cohorte.utilities.installer.validators;

import static org.cohorte.utilities.installer.CInstallerTools.getServiceLogger;

import org.psem2m.utilities.logging.IActivityLogger;

import com.izforge.izpack.panels.userinput.processorclient.ProcessingClient;
import com.izforge.izpack.panels.userinput.validator.Validator;

/**
 * MOD_OG_20160727 CASE 2796
 *
 * @author ogattaz
 *
 */
public class CHttpStatusValidator implements Validator {

	// @see https://www.w3.org/Protocols/rfc2616/rfc2616-sec6.html
	private static final int[] HTTP_STATUS = { 100, // Section 10.1.1: Continue
			101, // Section 10.1.2: Switching Protocols
			200, // Section 10.2.1: OK
			201, // Section 10.2.2: Created
			202, // Section 10.2.3: Accepted
			203, // Section 10.2.4: Non-Authoritative Information
			204, // Section 10.2.5: No Content
			205, // Section 10.2.6: Reset Content
			206, // Section 10.2.7: Partial Content
			300, // Section 10.3.1: Multiple Choices
			301, // Section 10.3.2: Moved Permanently
			302, // Section 10.3.3: Found
			303, // Section 10.3.4: See Other
			304, // Section 10.3.5: Not Modified
			305, // Section 10.3.6: Use Proxy
			307, // Section 10.3.8: Temporary Redirect
			400, // Section 10.4.1: Bad Request
			401, // Section 10.4.2: Unauthorized
			402, // Section 10.4.3: Payment Required
			403, // Section 10.4.4: Forbidden
			404, // Section 10.4.5: Not Found
			405, // Section 10.4.6: Method Not Allowed
			406, // Section 10.4.7: Not Acceptable
			407, // Section 10.4.8: Proxy Authentication Required
			408, // Section 10.4.9: Request Time-out
			409, // Section 10.4.10: Conflict
			410, // Section 10.4.11: Gone
			411, // Section 10.4.12: Length Required
			412, // Section 10.4.13: Precondition Failed
			413, // Section 10.4.14: Request Entity Too Large
			414, // Section 10.4.15: Request-URI Too Large
			415, // Section 10.4.16: Unsupported Media Type
			416, // Section 10.4.17: Requested range not satisfiable
			417, // Section 10.4.18: Expectation Failed
			500, // Section 10.5.1: Internal Server Error
			501, // Section 10.5.2: Not Implemented
			502, // Section 10.5.3: Bad Gateway
			503, // Section 10.5.4: Service Unavailable
			504, // Section 10.5.5: Gateway Time-out
			505 // Section 10.5.6: HTTP Version not supported
	};

	private final IActivityLogger pLogger;

	/**
	 *
	 */
	public CHttpStatusValidator() {
		pLogger = getServiceLogger();
	}

	/**
	 * <pre>
	 *  - 1xx: Informational - Request received, continuing process
	 *  - 2xx: Success - The action was successfully received,understood, and accepted
	 *  - 3xx: Redirection - Further action must be taken in order to complete the request
	 *  - 4xx: Client Error - The request contains bad syntax or cannot be fulfilled
	 *  - 5xx: Server Error - The server failed to fulfill an apparently valid request
	 * </pre>
	 *
	 * @see https://www.w3.org/Protocols/rfc2616/rfc2616-sec6.html
	 *
	 * @return
	 */
	private boolean checkHttpStatus(final String aHttpStatus) {

		pLogger.logWarn(this, "checkHttpStatus",
				"Checking http status [%s] availability.", aHttpStatus);

		int wStatus = -1;
		try {
			wStatus = Integer.parseInt(aHttpStatus);
		} catch (NumberFormatException e) {
			pLogger.logWarn(
					this,
					"checkHttpStatus",
					"The provided http status [%s] isn't numeric! Validation returns false",
					aHttpStatus);
			return false;
		}

		for (int wKnownStatus : HTTP_STATUS) {
			if (wStatus == wKnownStatus) {
				return true;
			}
		}
		pLogger.logWarn(
				this,
				"checkHttpStatus",
				"The provided http status [%s] isn't known! See https://www.w3.org/Protocols/rfc2616/rfc2616-sec6.html Validation returns false",
				aHttpStatus);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.izforge.izpack.panels.userinput.validator.Validator#validate(com.
	 * izforge.izpack.panels.userinput.processorclient.ProcessingClient)
	 */
	public boolean validate(final ProcessingClient client) {

		String value1 = client.getFieldContents(0);

		if ((value1 == null) || (value1.length() == 0)) {
			pLogger.logWarn(this, "validate",
					"No provided http status! Validation returns false");
			return false;
		} else {
			pLogger.logInfo(this, "validate",
					"Provided http status numbers : %s.", value1);
			return checkHttpStatus(value1);
		}
	}
}
