package org.cohorte.utilities.extra.junit.rest;

import org.psem2m.utilities.json.JSONException;
import org.psem2m.utilities.json.JSONObject;

import io.restassured.response.Response;

/**
 * <pre>
 * {
 *     "data": {
 *         "companyId": "",
 *         "preferenceId": "",
 *         "ihmId": "",
 *         "machineId": "",
 *         "UserId": "",
 *         "label": "",
 *         "comment": "",
 *         "data": {}
 *     },
 *     "size": 106,
 *     "success": true,
 *     "duration": " 1.220"
 * }
 * </pre>
 *
 * @author ogattaz
 *
 */
public class CResponseDimension {

	private final JSONObject pBodyJSONObject;

	private final Response pResponse;

	/**
	 * @param aResponse
	 */
	public CResponseDimension(final Response aResponse) {
		super();
		pResponse = aResponse;
		pBodyJSONObject = parseResponse();

	}

	/**
	 * @return
	 */
	public String getBody() {
		return pResponse.getBody().asString();
	}

	/**
	 * @return
	 */
	public JSONObject getBodyJSONObject() {
		return pBodyJSONObject;
	}

	/**
	 * @return
	 */
	public Integer getCount() {
		int wSize = -1;
		try {
			if (pBodyJSONObject.has("count")) {
				wSize = pBodyJSONObject.getInt("count");
			}
		} catch (Exception | Error e) {
			// nothing
		}
		return wSize;
	}

	/**
	 * @return
	 */
	public String getDuration() {
		String wDuration = pResponse.path("duration");
		return wDuration;
	}

	/**
	 * @return
	 */
	public Integer getSize() {
		Integer wSize = pResponse.path("size");
		return wSize;
	}

	/**
	 * @return
	 */
	public Boolean isSuccess() {
		Boolean wSuccess = pResponse.path("success");
		return wSuccess;
	}

	/**
	 * @return
	 */
	private JSONObject parseResponse() {
		try {
			return new JSONObject(getBody());
		} catch (JSONException e) {
			throw new RuntimeException("Unable to parse body", e);
		}
	}

	public void removeData() {
		pBodyJSONObject.put("data", "DATA EXPLICITLY REMOVED");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return toString(0);
	}

	/**
	 * @param aIndentFactor
	 * @return
	 */
	public String toString(final int aIndentFactor) {
		try {
			return String.format(
					"duration=[%s] size=[%s] %s data:%s",
					getDuration(),
					getSize(),
					(getCount() == -1) ? "" : String.format("count=[%s]",
							getCount()),
					getBodyJSONObject().toString(aIndentFactor));
		} catch (JSONException e) {
			throw new RuntimeException("Unable to format the bidy json object",
					e);
		}
	}

}
