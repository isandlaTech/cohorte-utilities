package org.cohorte.utilities.extra.junit.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import java.net.URL;

import org.cohorte.utilities.junit.CAbstractJunitTest;
import org.psem2m.utilities.CXBytesUtils;
import org.psem2m.utilities.CXStringUtils;
import org.psem2m.utilities.json.JSONObject;

import io.restassured.http.ContentType;
import io.restassured.http.Cookie;
import io.restassured.response.Response;
import io.restassured.specification.ResponseSpecification;

/**
 * @author ogattaz
 *
 */
public class CAbstractEndpointTest extends CAbstractJunitTest {

	private static final String COOKIE_ID = "COOKIE_ID";

	private static final String[] NO_PATH_PARTS = new String[0];

	/**
	 *
	 */
	public CAbstractEndpointTest() {
		super();
	}

	/**
	 * @param wPath
	 * @param aPathsParts
	 * @return
	 */
	protected StringBuilder addPathInSB(final StringBuilder wPath,
			final String... aPathsParts) {

		for (String wPathPart : aPathsParts) {
			if (wPathPart != null && !wPathPart.isEmpty()) {

				if (!isParamPart(wPathPart) && wPath.length() > 0
						&& !wPath.toString().endsWith("/")) {
					wPath.append("/");
				}
				wPath.append(wPathPart);
			}
		}
		return wPath;
	}

	/**
	 * @param aPathsParts
	 * @return
	 */
	protected String buildPath(final String... aPathsParts) {

		return addPathInSB(new StringBuilder(), aPathsParts).toString();
	}

	/**
	 * @param aDimensionApiUri
	 * @param aPathSubParts
	 * @return
	 * @throws Exception
	 */
	protected String builURL(final String aDimensionApiUri,
			final String... aPathsParts) throws Exception {

		if (aDimensionApiUri == null || aDimensionApiUri.isEmpty()) {
			throw new Exception(
					"Unable to buil the Url, the argument 'aDimensionApiUri' is null or empty");
		}

		StringBuilder wUri = new StringBuilder();
		wUri.append(aDimensionApiUri);
		if (aPathsParts != null) {
			addPathInSB(wUri, aPathsParts);
		}

		// validation => MalformedURLException
		URL wURL = new URL(wUri.toString());

		getLogger().logInfo(this, "builURL", "URL=[%s]", wURL.toString());
		return wURL.toString();
	}

	/**
	 * @return
	 */
	public Cookie getCookie() {
		getLogger().logInfo(this, "getCookie", "Cookies GET");
		return (Cookie) sTestsContext.get(COOKIE_ID);
	}

	/**
	 * @param aPathPart
	 * @return
	 */
	protected boolean isParamPart(final String aPathPart) {
		return aPathPart.startsWith("?") || aPathPart.startsWith("&");
	}

	/**
	 * @param aCookie
	 */
	public void storeCookie(final Cookie aCookie) {
		getLogger().logInfo(this, "setCookie", "Cookies SET :");
		sTestsContext.put(COOKIE_ID, aCookie);

	}

	/**
	 * @param aDimensionApiUri
	 * @param aUserId
	 * @param aPassword
	 * @throws Exception
	 */
	protected void test00GenericLogin(final String aTestMethodName,
			final String aDimensionApiUri, final String aUserId,
			final String aPassword) throws Exception {
		logBegin(this, aTestMethodName, "login %s", aUserId);
		try {

			Response wResponse = given()
					.contentType(ContentType.URLENC.withCharset("UTF-8"))
					//
					.formParam("user", aUserId)
					//
					.formParam("password", aPassword).log().all()
					//
					.post(builURL(aDimensionApiUri, "login/v3"));

			//
			Cookie wCookie = wResponse.getDetailedCookie("AGL_LSSO");

			storeCookie(wCookie);

			CResponseDimension wResponseDimension = new CResponseDimension(
					wResponse);

			logEndOK(this, aTestMethodName, "login : %s",
					wResponseDimension.toString(2));

		} catch (Throwable e) {
			logEndKO(this, aTestMethodName, e);
			throw e;
		}
	}

	/**
	 * @param aTestMethodName
	 * @param aDimensionApiUri
	 * @param aPathSubParts
	 * @return
	 * @throws Exception
	 */
	protected CResponseDimension test1OGenericGetEmpty(
			final String aTestMethodName, final String aDimensionApiUri)
			throws Exception {

		try {
			Response wResponse = given()
					.contentType(ContentType.URLENC.withCharset("UTF-8"))
					.cookie(getCookie())
					//
					.expect()
					//
					.statusCode(200)
					//
					.body("success", equalTo(true))
					//
					.when()
					//
					.get(builURL(aDimensionApiUri, "empty"));

			CResponseDimension wResponseDimension = new CResponseDimension(
					wResponse);

			logEndOK(this, aTestMethodName, "GET empty : %s",
					wResponseDimension.toString(2));

			return wResponseDimension;

		} catch (Throwable e) {
			logEndKO(this, aTestMethodName, e);
			throw e;
		}
	}

	/**
	 * @param aTestMethodName
	 * @param aDimensionApiUri
	 * @return
	 * @throws Exception
	 */
	protected CResponseDimension test1OGenericGetMeta(
			final String aTestMethodName, final String aDimensionApiUri)
			throws Exception {

		try {
			Response wResponse = given()
					.contentType(ContentType.URLENC.withCharset("UTF-8"))
					.cookie(getCookie())
					//
					.expect()
					//
					.statusCode(200)
					//
					.body("success", equalTo(true))
					//
					.when()
					//
					.get(builURL(aDimensionApiUri, "meta"));

			CResponseDimension wResponseDimension = new CResponseDimension(
					wResponse);

			logEndOK(this, aTestMethodName, "GET meta : %s",
					wResponseDimension.toString(2));

			return wResponseDimension;

		} catch (Throwable e) {
			logEndKO(this, aTestMethodName, e);
			throw e;
		}
	}

	/**
	 * @param aMethodName
	 * @param aDimensionApiUri
	 *            eg. "http://localhost:8080/QSFab/api/"
	 * @param aJsonBodyObj
	 *            The json object of the entity to be created. Take care of the
	 *            id(s).
	 * @throws Exception
	 */
	protected void test30GenericPost(final String aMethodName,
			final String aDimensionApiUri, final JSONObject aJsonBodyObj,
			final CExpectedBodyEqualTo... aExpectedBodyEqualTos)
			throws Exception {

		test30GenericPost(aMethodName, aDimensionApiUri, NO_PATH_PARTS,
				aJsonBodyObj, 201, aExpectedBodyEqualTos);
	}

	/**
	 * @param aMethodName
	 * @param aDimensionApiUri
	 * @param aJsonBodyObj
	 * @param aHttpStatus
	 * @param aExpectedBodyEqualTos
	 * @throws Exception
	 */
	protected void test30GenericPost(final String aMethodName,
			final String aDimensionApiUri, final String[] aPathParts,
			final JSONObject aJsonBodyObj, final int aHttpStatus,
			final CExpectedBodyEqualTo... aExpectedBodyEqualTos)
			throws Exception {
		try {
			String wUrl = builURL(aDimensionApiUri, aPathParts);

			getLogger().logInfo(this, "test30GenericPost",
					"POST : CREATE OBJECT : test=[%s] Url=[%s] Json:\n%s",
					aMethodName, wUrl, aJsonBodyObj.toString(2));

			ResponseSpecification wResponseSpecification = given()
					.urlEncodingEnabled(false)
					.contentType(ContentType.URLENC.withCharset("UTF-8"))
					.cookie(getCookie())
					.log()
					.all(true)
					//
					// or "re-read"
					//
					.contentType(ContentType.JSON.withCharset("UTF-8"))
					//
					.body(aJsonBodyObj.toString().getBytes(
							CXBytesUtils.ENCODING_UTF_8))
					//
					.expect()
					//
					.statusCode(aHttpStatus)
					//
					.body("success", equalTo(true));

			int wIdx = 0;
			for (CExpectedBodyEqualTo wExpectedBodyEqualTo : aExpectedBodyEqualTos) {
				getLogger().logInfo(this, "test30GenericPost",
						"POST  : ExpectedBodyEqualTo(%d): %s", wIdx,
						wExpectedBodyEqualTo);
				wExpectedBodyEqualTo.appendTo(wResponseSpecification);
				wIdx++;
			}

			Response wResponse = wResponseSpecification.when().post(wUrl);

			CResponseDimension wResponseDimension = new CResponseDimension(
					wResponse);

			logEndOK(this, aMethodName, "Post : %s",
					wResponseDimension.toString(2));

		} catch (Throwable e) {
			logEndKO(this, aMethodName, e);
			throw e;
		}
	}

	/**
	 * @param aMethodName
	 * @param aDimensionApiUri
	 *            eg. "http://localhost:8080/QSFab/api/"
	 * @param aJsonBodyObj
	 *            The json object of the entity to be created. Take care of the
	 *            id(s).
	 * @throws Exception
	 */
	protected void test35GenericPut(final String aMethodName,
			final String aDimensionApiUri, final JSONObject aJsonBodyObj,
			final CExpectedBodyEqualTo... aExpectedBodyEqualTos)
			throws Exception {

		test35GenericPut(aMethodName, aDimensionApiUri, NO_PATH_PARTS,
				aJsonBodyObj, 200, aExpectedBodyEqualTos);
	}

	/**
	 * @param aMethodName
	 * @param aDimensionApiUri
	 * @param aJsonBodyObj
	 * @param aHttpStatus
	 * @param aExpectedBodyEqualTos
	 * @throws Exception
	 */
	protected void test35GenericPut(final String aMethodName,
			final String aDimensionApiUri, final String[] aPathParts,
			final JSONObject aJsonBodyObj, final int aHttpStatus,
			final CExpectedBodyEqualTo... aExpectedBodyEqualTos)
			throws Exception {
		try {
			String wUrl = builURL(aDimensionApiUri, aPathParts);

			getLogger().logInfo(this, "test30GenericPost",
					"PUT : UPDATE OBJECT : test=[%s] Url=[%s] Json:\n%s",
					aMethodName, wUrl, aJsonBodyObj.toString(2));

			ResponseSpecification wResponseSpecification = given()
					.urlEncodingEnabled(false)
					.contentType(ContentType.URLENC.withCharset("UTF-8"))
					.cookie(getCookie())
					.log()
					.all(true)
					//
					.queryParam("content", "same")
					// or "re-read"
					//
					.contentType(ContentType.JSON.withCharset("UTF-8"))
					//
					.body(aJsonBodyObj.toString().getBytes(
							CXBytesUtils.ENCODING_UTF_8))
					//
					.expect()
					//
					.statusCode(aHttpStatus)
					//
					.body("success", equalTo(true));

			int wIdx = 0;
			for (CExpectedBodyEqualTo wExpectedBodyEqualTo : aExpectedBodyEqualTos) {
				getLogger().logInfo(this, "test30GenericPost",
						"PUT  : ExpectedBodyEqualTo(%d): %s", wIdx,
						wExpectedBodyEqualTo);
				wExpectedBodyEqualTo.appendTo(wResponseSpecification);
				wIdx++;
			}

			Response wResponse = wResponseSpecification.when().put(wUrl);

			CResponseDimension wResponseDimension = new CResponseDimension(
					wResponse);

			logEndOK(this, aMethodName, "Post : %s",
					wResponseDimension.toString(2));

		} catch (Throwable e) {
			logEndKO(this, aMethodName, e);
			throw e;
		}
	}

	protected CResponseDimension test40GenericGet(final String aTestMethodName,
			final String aDimensionApiUri,
			final CExpectedBodyEqualTo... aExpectedBodyEqualTos)
			throws Exception {

		return test40GenericGet(aTestMethodName, aDimensionApiUri, null, 200,
				aExpectedBodyEqualTos);

	}

	/**
	 * @param aTestMethodName
	 * @param aDimensionApiUri
	 *            eg. "http://localhost:8080/QSFab/api/"
	 * @param aPathParts
	 *            a array of parts of URI eg. {"ihms", "02,IHM002"}
	 * @param aExpectedBodyEqualTos
	 * @throws Exception
	 */
	protected CResponseDimension test40GenericGet(final String aTestMethodName,
			final String aDimensionApiUri, final String[] aPathParts,
			final CExpectedBodyEqualTo... aExpectedBodyEqualTos)
			throws Exception {

		return test40GenericGet(aTestMethodName, aDimensionApiUri, aPathParts,
				200, aExpectedBodyEqualTos);

	}

	/**
	 * @param aTestMethodName
	 * @param aDimensionApiUri
	 * @param aPathParts
	 * @param aHttpStatus
	 * @param aExpectedBodyEqualTos
	 * @return
	 * @throws Exception
	 */
	protected CResponseDimension test40GenericGet(final String aTestMethodName,
			final String aDimensionApiUri, final String[] aPathParts,
			final int aHttpStatus,
			final CExpectedBodyEqualTo... aExpectedBodyEqualTos)
			throws Exception {

		try {
			String wUrl = builURL(aDimensionApiUri, aPathParts);
			getLogger().logInfo(this, "test40GenericGet",
					"GET : test=[%s] Url=[%s]", aTestMethodName, wUrl);

			ResponseSpecification wResponseSpecification = given()
					.contentType(ContentType.URLENC.withCharset("UTF-8"))
					.cookie(getCookie()).log().all(true)
					//
					.expect()
					//
					.statusCode(200)
					//
					.body("success", equalTo(true));

			int wIdx = 0;
			for (CExpectedBodyEqualTo wExpectedBodyEqualTo : aExpectedBodyEqualTos) {
				getLogger().logInfo(this, "test40GenericGet",
						"GET  : ExpectedBodyEqualTo(%d): %s", wIdx,
						wExpectedBodyEqualTo);
				wExpectedBodyEqualTo.appendTo(wResponseSpecification);
				wIdx++;
			}

			//
			Response wResponse = wResponseSpecification.when()
			//
					.get(wUrl);

			CResponseDimension wResponseDimension = new CResponseDimension(
					wResponse);

			logEndOK(this, aTestMethodName, "GET : %s",
					wResponseDimension.toString(2));

			return wResponseDimension;

		} catch (Throwable e) {
			logEndKO(this, aTestMethodName, e);
			throw e;
		}
	}

	/**
	 * @param aMethodName
	 * @param aDimensionApiUri
	 * @param aJsonBodyObj
	 * @param aExpectedBodyEqualTos
	 * @throws Exception
	 */
	protected void test50GenericUpdate(final String aMethodName,
			final String aDimensionApiUri, final String[] aPathParts,
			final JSONObject aJsonBodyObj,
			final CExpectedBodyEqualTo... aExpectedBodyEqualTos)
			throws Exception {
		try {
			getLogger().logInfo(this, "test50GenericUpdate",
					"PUT : UPDATE OBJECT : test=[%s] JsonBodyObj:\n%s",
					aMethodName, aJsonBodyObj.toString(2));

			ResponseSpecification wResponseSpecification = given()
					.contentType(ContentType.URLENC.withCharset("UTF-8"))
					.cookie(getCookie())
					.log()
					.all(true)
					//
					.queryParam("content", "same")
					// or "re-read"
					//
					.contentType(ContentType.JSON.withCharset("UTF-8"))
					//
					.body(aJsonBodyObj.toString().getBytes(
							CXBytesUtils.ENCODING_UTF_8))
					//
					.expect()
					//
					.statusCode(200)
					//
					.body("success", equalTo(true));

			int wIdx = 0;
			for (CExpectedBodyEqualTo wExpectedBodyEqualTo : aExpectedBodyEqualTos) {
				getLogger().logInfo(this, "test50GenericUpdate",
						"PUT  : ExpectedBodyEqualTo(%d): %s", wIdx,
						wExpectedBodyEqualTo);
				wExpectedBodyEqualTo.appendTo(wResponseSpecification);
				wIdx++;
			}

			Response wResponse = wResponseSpecification.when().put(
					builURL(aDimensionApiUri, aPathParts));

			CResponseDimension wResponseDimension = new CResponseDimension(
					wResponse);

			logEndOK(this, aMethodName, "Put : %s",
					wResponseDimension.toString(2));

		} catch (Throwable e) {
			logEndKO(this, aMethodName, e);
			throw e;
		}
	}

	/**
	 * @param aTestMethodName
	 * @param aDimensionApiUri
	 *            eg. "http://localhost:8080/QSFab/api/"
	 * @param aPathParts
	 *            a array of parts of URI eg. {"ihms", "02,IHM002"}
	 * @throws Exception
	 */
	protected void test80GenericDelete(final String aMethodName,
			final String aDimensionApiUri, final String... aPathParts)
			throws Exception {
		try {
			getLogger().logInfo(this, "test80GenericDelete",
					"DELETE : test=[%s]  SubPath=[%s]", aMethodName,
					CXStringUtils.stringTableToString(aPathParts));

			Response wResponse = given()
					.contentType(ContentType.URLENC.withCharset("UTF-8"))
					.cookie(getCookie()).log().all(true)
					//
					.expect()
					//
					.statusCode(200)
					//
					.body("success", equalTo(true))
					//
					.when()
					//
					.delete(builURL(aDimensionApiUri, aPathParts));

			CResponseDimension wResponseDimension = new CResponseDimension(
					wResponse);

			logEndOK(this, aMethodName, "DELETE : %s",
					wResponseDimension.toString(2));

		} catch (Throwable e) {
			logEndKO(this, aMethodName, e);
			throw e;
		}
	}

	/**
	 * <pre>
	 * 		{
	 * 		  "data": {
	 * 		    "SSOCookie": "removed"
	 * 		  },
	 * 		  "message": "You have successfully logged out",
	 * 		  "duration": " 0.468",
	 * 		  "size": 23,
	 * 		  "success": true
	 * 		}
	 * </pre>
	 *
	 * @param aDimensionApiUri
	 * @throws Exception
	 */
	protected void test90GenericLogout(final String aTestMethodName,
			final String aDimensionApiUri) throws Exception {
		logBegin(this, aTestMethodName, "logout");

		try {
			Response wResponse = given()
					.contentType(ContentType.URLENC.withCharset("UTF-8"))
					.cookie(getCookie()).log().all(true)
					//
					.expect()
					//
					.statusCode(200)
					//
					.body("success", equalTo(true))
					//
					.when()
					//
					.post(builURL(aDimensionApiUri, "logout"));

			CResponseDimension wResponseDimension = new CResponseDimension(
					wResponse);

			logEndOK(this, aTestMethodName, "logout : %s",
					wResponseDimension.toString(2));

		} catch (Throwable e) {
			logEndKO(this, aTestMethodName, e);
			throw e;
		}
	}
}
