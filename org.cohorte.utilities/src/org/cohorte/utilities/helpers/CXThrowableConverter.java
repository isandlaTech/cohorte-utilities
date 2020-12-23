package org.cohorte.utilities.helpers;

import org.psem2m.utilities.json.JSONArray;
import org.psem2m.utilities.json.JSONException;
import org.psem2m.utilities.json.JSONObject;
import org.psem2m.utilities.logging.CActivityLoggerNull;
import org.psem2m.utilities.logging.IActivityLogger;

/**
 * <pre>
	{
    "class": "CManagerException",
    "message": "Cannot authenticated user manageree!",
    "details": [
      "( 0) Unable to check the account using the login [manageree]",
      "( 1) Unable to retreive the account from table [account] using [manageree]"
    ],
    "stacktrace": {
      "class": "CManagerException",
      "message": "Unable to check the account using the login [manageree]",
      "stack": [
        "  0: fr.quasar.dimensions.auth.managers.services.CLoginDimAuthManager(login:337)",
        "  1: fr.quasar.dimensions.auth.restapi.endpoints.CLoginV3(loginV3:119)",
	...
        " 52: java.lang.Thread(run:748)"
      ],
      "cause": {
        "class": "CNotFoundException",
        "message": "Unable to retreive the account from table [account] using [manageree]",
        "level": 1,
        "stack": [
          "  0: fr.quasar.dimensions.auth.managers.objects.CAccountManager(selectOneByCondition:302)",
          "  1: fr.quasar.dimensions.auth.managers.objects.CAccountManager(selectOneByLogin:318)",
	...
          " 54: java.lang.Thread(run:748)"
        ]
      }
    }
  }
 * </pre>
 * 
 * @author ogattaz
 *
 */
public class CXThrowableConverter {

	/**
	 * the cause of the Throwable
	 */
	public static final String CAUSE = "cause";
	/**
	 * simpleClassName
	 */
	public static final String CLASS = "class";

	/**
	 * an array containing the list of the messages of the causes
	 */
	public static final String DETAILS = "details";

	/**
	 * the level of the cause
	 */
	public static final String LEVEL = "level";

	/**
	 * the message of the Throwable
	 */
	public static final String MESSAGE = "message";

	/**
	 * the stack of the Throwable
	 */
	public static final String STACK = "stack";

	/**
	 * the stacktrace including the throwable and all its causes
	 */
	public static final String STACKTRACE = "stacktrace";

	/**
	 * Flag to obtain the stacktrace or not
	 */
	public static final boolean WITH_STACK_TRACE = true;

	private final IActivityLogger pLogger;

	private final Throwable pThrowable;

	/**
	 * @param aLogger
	 * @param aThrowable
	 */
	public CXThrowableConverter(final IActivityLogger aLogger, final Throwable aThrowable) {

		super();

		if (aThrowable == null) {
			throw new RuntimeException("The given Throwable must be not null");
		}

		pLogger = aLogger;
		pThrowable = aThrowable;
	}

	/**
	 * @param aThrowable
	 */
	public CXThrowableConverter(final Throwable aThrowable) {
		this(CActivityLoggerNull.getInstance(), aThrowable);
	}

	/**
	 * @param aThrowable
	 * @return
	 */
	public JSONArray getDetails() {
		return getDetails(getThrowable());
	}

	/**
	 * @param aThrowable
	 * @return
	 */
	private JSONArray getDetails(final Throwable aThrowable) {

		JSONArray wDetails = new JSONArray();
		Throwable wThrowable = aThrowable;
		String wMessage;
		int wIdx = 0;
		while (wThrowable != null) {
			wMessage = getThrowableMessage(wThrowable);
			wDetails.put(String.format("(%02d) %s", wIdx, wMessage));
			wThrowable = wThrowable.getCause();
			wIdx++;
		}
		return wDetails;
	}

	/**
	 * @return
	 */
	private IActivityLogger getLogger() {
		return pLogger;
	}

	/**
	 * @return
	 */
	public JSONObject getStackTrace() {
		return getStackTrace(getThrowable(), 0);
	}

	/**
	 * @param aThrowable
	 * @return
	 */
	private JSONObject getStackTrace(final Throwable aThrowable, final int aLevel) {

		if (isLogDebugOn()) {
			getLogger().logDebug(this, "getStackTrace", "bild level=[%d] : Throwable=[%s]",
					getThrowableClassName(aThrowable));
		}

		JSONObject wObj = new JSONObject();

		wObj.put(CLASS, getThrowableClassName(aThrowable));

		wObj.put(MESSAGE, getThrowableMessage(aThrowable));

		if (aLevel > 0) {
			try {
				wObj.put(LEVEL, aLevel);
			} catch (JSONException e) {
				wObj.put(LEVEL, "JSONException " + e.getMessage());
			}
		}

		JSONArray wStack = new JSONArray();
		int wIdx = 0;
		for (StackTraceElement wStackTraceElmt : aThrowable.getStackTrace()) {
			wStack.put(String.format("%03d: %s(%s:%s)", wIdx, wStackTraceElmt.getClassName(),
					wStackTraceElmt.getMethodName(), wStackTraceElmt.getLineNumber()));
			wIdx++;
		}
		try {
			wObj.put(STACK, wStack);
		} catch (JSONException e) {
			wObj.put(STACK, "JSONException " + e.getMessage());
		}

		Throwable wThrowable = aThrowable.getCause();
		if (wThrowable != null) {
			try {
				wObj.put(CAUSE, getStackTrace(wThrowable, aLevel + 1));
			} catch (JSONException e) {
				wObj.put(CAUSE, "JSONException " + e.getMessage());
			}
		}

		return wObj;
	}

	/**
	 * @return
	 */
	private Throwable getThrowable() {
		return pThrowable;
	}

	/**
	 * @return
	 */
	private String getThrowableClassName() {
		return getThrowableClassName(getThrowable());
	}

	/**
	 * @param aThrowable
	 * @return
	 */
	private String getThrowableClassName(final Throwable aThrowable) {
		if (aThrowable == null) {
			return "null";
		}
		return aThrowable.getClass().getSimpleName();
	}

	/**
	 * @return
	 */
	private String getThrowableMessage() {
		return getThrowableMessage(getThrowable());
	}

	/**
	 * @param aThrowable
	 * @return
	 */
	private String getThrowableMessage(final Throwable aThrowable) {
		if (aThrowable == null) {
			return "null";
		}
		String wMess = aThrowable.getMessage();
		if (wMess == null || wMess.isEmpty()) {
			wMess = String.format("No explicit message in throwable %s", getThrowableClassName());
		}
		return wMess;
	}

	/**
	 * @return true if the log level is at least FINE
	 */
	private boolean isLogDebugOn() {
		return getLogger().isLogDebugOn();
	}

	/**
	 * @return
	 */
	public JSONObject toJson() {
		return toJson(WITH_STACK_TRACE);
	}

	/**
	 * @param aWithStackTrace
	 * @return
	 */
	public JSONObject toJson(final boolean aWithStackTrace) {

		JSONObject wObj = new JSONObject();

		try {
			wObj.put(CLASS, getThrowableClassName());
		} catch (JSONException e) {
			wObj.put(CLASS, "JSONException " + e.getMessage());
		}
		try {
			wObj.put(MESSAGE, getThrowableMessage());

		} catch (JSONException e) {
			wObj.put(MESSAGE, "JSONException " + e.getMessage());
		}
		try {
			wObj.put(DETAILS, getDetails());
		} catch (JSONException e) {
			wObj.put(DETAILS, "JSONException " + e.getMessage());
		}

		if (aWithStackTrace) {
			try {
				wObj.put(STACKTRACE, getStackTrace());
			} catch (JSONException e) {
				wObj.put(STACKTRACE, "JSONException " + e.getMessage());
			}
		}
		return wObj;
	}

}
