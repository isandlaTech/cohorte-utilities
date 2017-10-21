package org.cohorte.utilities.webapp.filters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.psem2m.utilities.CXDateTime;
import org.psem2m.utilities.CXException;
import org.psem2m.utilities.CXStringUtils;
import org.psem2m.utilities.CXTimer;
import org.psem2m.utilities.json.JSONObject;

/**
 * <pre>
 * 	<filter>
 * 		<filter-name>RequestTracer</filter-name>
 * 		<filter-class>org.cohorte.utilities.picosoc.webapp.CFilterRequestTracer</filter-class>
 * 		<init-param>
 * 			<param-name>filter.request.tracer.included.uri.suffixes</param-name>
 * 			<param-value>jsp;srvl</param-value>
 * 		</init-param>
 * 		<init-param>
 * 			<param-name>filter.request.tracer.Log.debug.forced</param-name>
 * 			<param-value>true</param-value>
 * 		</init-param>
 * 	</filter>
 * 	<filter-mapping>
 * 		<filter-name>RequestTracer</filter-name>
 * 		<url-pattern>/*</url-pattern>
 * 	</filter-mapping>
 * </pre>
 *
 * Result
 *
 * <pre>
 * 486893C50ACED5A170C5DDC1A0420A06 [GET] [http://localhost:8080/AgiliumWeb/template/css/all_content.css] RequestTraceDuration=[ 0,049 ms]
 * </prE>
 *
 * <pre>
 *  SessionID/CreateTime/Duration=[41EF45FC87768BCEACA63F6BC40D7615] [2015-11-26T09:30:49.0000252+0100] [104,110]
 *                     Method/URL=[GET] [http://localhost:8080/AgiliumSAMLdev/]
 *                       AuthType=[null]
 *                 LocalAddr/Port=[0:0:0:0:0:0:0:1] [8080]
 *              getProtocol/Sheme=[HTTP/1.1] [http]
 *             getServerName/port=[localhost] [8080]
 *           RemoteHost/Addr/port=[0:0:0:0:0:0:0:1] [0:0:0:0:0:0:0:1] [57241]
 *             ContentLength/Type=[-1] [null]
 *                    HeadersSize=[9]
 * Header( 0)                          host=[localhost:8080]
 * Header( 1)                    connection=[keep-alive]
 * Header( 2)                 cache-control=[max-age=0]
 * Header( 3)                        accept=[text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,* /*;q=0.8]
 * Header( 4)     upgrade-insecure-requests=[1]
 * Header( 5)                    user-agent=[Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36]
 * Header( 6)               accept-encoding=[gzip, deflate, sdch]
 * Header( 7)               accept-language=[fr-FR,fr;q=0.8,en-US;q=0.6,en;q=0.4]
 * Header( 8)                        cookie=[JSESSIONID=41EF45FC87768BCEACA63F6BC40D7615]
 *                CookieArraySize=[1]
 * Cookie( 0)                    JSESSIONID={domain:[null] vers:[0] secure:[false] path:[null] val:[41EF45FC87768BCEACA63F6BC40D7615]}
 *               ParameterMapSize=[0]
 *                  AtributesSize=[0]
 * </pre>
 *
 *
 * @author ogattaz & bdebbabi
 *
 */
public class CServletRequestDump {

	interface IValueGetter {

		public Object getValue(final String aName);
	}

	public final static String FMT_SECONDS = "%6.3f";

	public static final String PARAM_LOG_DEBUG_FORCED = "filter.request.tracer.Log.debug.forced";
	public static final String PARAM_MAPPING = "filter.request.tracer.mapping";
	public static final String PARAM_TARGET_CONTEXT = "filter.request.tracer.target.context";
	public static final String PARAM_URI_SUFFIXES = "filter.request.tracer.included.uri.suffixes";

	private static final double THOUSAND = 1000;
	public final static String UNKNOWN = "unknowwn";

	/**
	 * @param wSB
	 * @param aInfoId
	 * @param aValue
	 * @return
	 */
	private static StringBuilder dumpOneInfoInSB(final StringBuilder wSB,
			final String aInfoId, final Object... aValues) {
		if (aValues.length > 0) {
			wSB.append(String.format("\n%30s=[%s]", aInfoId, aValues[0]));
			if (aValues.length > 1) {
				for (int wIdx = 1; wIdx < aValues.length; wIdx++) {
					wSB.append(String.format(" [%s]", aValues[wIdx]));
				}
			}
		}
		return wSB;
	}

	public static StringBuilder dumpRequest(final StringBuilder wSB,
			final HttpServletRequest wRequest) {
		return dumpRequest(wSB, wRequest, true, true);
	}

	/**
	 * @param aRequest
	 */
	public static StringBuilder dumpRequest(final StringBuilder aSB,
			final HttpServletRequest aRequest, final boolean aIsLogDebugOn,
			final boolean aIsLogIngoOn) {

		// try {
		CXTimer wTimer = CXTimer.newStartedTimer();
		aSB.append(String.format(
				"\n------------ dump Request [%s %s] -----------",
				aRequest.getMethod(), aRequest.getRequestURL()));
		// if (isLogDebugForced() || getLogger().isLogDebugOn()) {
		if (aIsLogDebugOn) {
			dumpRequestInfosInSB(aSB, aRequest);
			dumpRequestHeadersInSB(aSB, aRequest);
			dumpRequestgetCookiesInSB(aSB, aRequest);
			dumpRequestParametersInSB(aSB, aRequest);
			dumpRequestAttributesInSB(aSB, aRequest);
			aSB.append(String.format(
					"\n------------ Request trace duration=[%s ms] -----",
					wTimer.getDurationStrMicroSec()));
		} else {
			// if (getLogger().isLogInfoOn()) {
			if (aIsLogIngoOn) {
				dumpRequestInfosMiniInSB(aSB, aRequest);
				aSB.append(String.format(" RequestTraceDuration=[%s ms]",
						wTimer.getDurationStrMicroSec()));
			}
		}

		// } catch (Exception e) {
		// getLogger().logSevere(this, "doFilter", "ERROR:\n%s", e);
		// }
		return aSB;
	}

	/**
	 * @param aSB
	 * @param aRequest
	 * @return
	 */
	private static StringBuilder dumpRequestAttributesInSB(
			final StringBuilder aSB, final HttpServletRequest aRequest) {

		Enumeration<String> wNames = aRequest.getAttributeNames();

		dumpRequestValuesInSB(aSB, "Atribute", wNames, new IValueGetter() {
			@Override
			public Object getValue(final String aName) {
				return aRequest.getAttribute(aName);
			}
		});

		return aSB;
	}

	/**
	 * @param wSB
	 * @param aRequest
	 * @return
	 */

	private static StringBuilder dumpRequestgetCookiesInSB(
			final StringBuilder wSB, final HttpServletRequest aRequest) {
		Cookie[] wCookies = aRequest.getCookies();
		int wMax = (wCookies != null) ? wCookies.length : 0;
		dumpOneInfoInSB(wSB, "CookieArraySize", wMax);
		for (int wIdx = 0; wIdx < wMax; wIdx++) {
			Cookie wCookie = wCookies[wIdx];
			wSB.append(String
					.format("\nCookie(%2d)%30s={domain:[%s] vers:[%s] secure:[%s] path:[%s] val:[%s]}",
							wIdx, wCookie.getName(), wCookie.getDomain(),
							wCookie.getVersion(), wCookie.getSecure(),
							wCookie.getPath(), wCookie.getValue()));
		}
		return wSB;
	}

	/**
	 * @param aSB
	 * @param aRequest
	 * @return
	 */
	private static StringBuilder dumpRequestHeadersInSB(
			final StringBuilder aSB, final HttpServletRequest aRequest) {

		Enumeration<String> wNames = aRequest.getHeaderNames();

		dumpRequestValuesInSB(aSB, "RequestHeader", wNames, new IValueGetter() {
			@Override
			public Object getValue(final String aName) {
				return aRequest.getHeader(aName);
			}
		});

		return aSB;
	}

	/**
	 * @param wSB
	 * @param aRequest
	 * @return
	 */
	private static StringBuilder dumpRequestInfosInSB(final StringBuilder wSB,
			final HttpServletRequest aRequest) {

		HttpSession wSession = aRequest.getSession();
		long wCreationTime = wSession.getCreationTime();
		dumpOneInfoInSB(wSB, "SessionID/CreateTime/Duration", wSession.getId(),
				CXDateTime.getIso8601TimeStamp(wCreationTime),
				nbSecondsFrom(wCreationTime));

		dumpOneInfoInSB(wSB, "Method/URL", aRequest.getMethod(),
				aRequest.getRequestURL());

		dumpOneInfoInSB(wSB, "AuthType", aRequest.getAuthType());

		dumpOneInfoInSB(wSB, "LocalAddr/Port", aRequest.getLocalAddr(),
				aRequest.getLocalPort());

		dumpOneInfoInSB(wSB, "getProtocol/Sheme", aRequest.getProtocol(),
				aRequest.getScheme());

		dumpOneInfoInSB(wSB, "getServerName/port", aRequest.getServerName(),
				aRequest.getServerPort());

		dumpOneInfoInSB(wSB, "RemoteHost/Addr/port", aRequest.getRemoteHost(),
				aRequest.getRemoteAddr(), aRequest.getRemotePort());

		dumpOneInfoInSB(wSB, "ContentLength/Type", aRequest.getContentLength(),
				aRequest.getContentType());

		return wSB;
	}

	/**
	 * @param wSB
	 * @param aRequest
	 * @return
	 */
	private static StringBuilder dumpRequestInfosMiniInSB(
			final StringBuilder wSB, final HttpServletRequest aRequest) {

		HttpSession wSession = aRequest.getSession();

		return wSB.append(String.format("%s [%s] [%s]", wSession.getId(),
				aRequest.getMethod(), aRequest.getRequestURL()));
	}

	/**
	 * @param wSB
	 * @param aRequest
	 * @return
	 */
	private static StringBuilder dumpRequestParametersInSB(
			final StringBuilder wSB, final HttpServletRequest aRequest) {

		Map<String, String[]> wParameterMap = aRequest.getParameterMap();

		if (wParameterMap != null) {
			int wSize = wParameterMap.size();
			dumpOneInfoInSB(wSB, "ParameterMapSize", wSize);
			if (wSize > 0) {
				int wIdxP = 0;
				for (Map.Entry<String, String[]> wEntry : wParameterMap
						.entrySet()) {
					wSB.append(String.format("\nParameter(%2d)%30s=[%s]",
							wIdxP, wEntry.getKey(), CXStringUtils
									.stringTableToString(wEntry.getValue())));
					wIdxP++;
				}
			}
		}
		return wSB;
	}

	/**
	 * @param aSB
	 * @param wValueKind
	 * @param aNames
	 * @param aValueGetter
	 * @return
	 */
	private static StringBuilder dumpRequestValuesInSB(final StringBuilder aSB,
			final String wValueKind, final Enumeration<String> aNames,
			final IValueGetter aValueGetter) {
		// an other buffer to be able to trace the size before the attributes
		StringBuilder wSB = new StringBuilder();
		int wIdxA = 0;
		while (aNames.hasMoreElements()) {
			String wAtributesName = aNames.nextElement();
			Object wAtributesValue = aValueGetter.getValue(wAtributesName);
			wSB.append(String.format("\n%s(%2d)%30s=[%s]", wValueKind, wIdxA,
					wAtributesName, wAtributesValue));
			wIdxA++;
		}
		dumpOneInfoInSB(aSB, wValueKind + "sSize", wIdxA);
		aSB.append(wSB);
		return aSB;
	}

	public static StringBuilder dumpResponse(final StringBuilder wSB,
			final HttpServletResponse wResponse) {
		return dumpResponse(wSB, wResponse, true, true);
	}

	/**
	 * @param wSB
	 * @param wResponse
	 * @return
	 */
	public static StringBuilder dumpResponse(final StringBuilder wSB,
			final HttpServletResponse wResponse, final boolean aIsLogDebugOn,
			final boolean aIsLogInfoOn) {

		// try {
		CXTimer wTimer = CXTimer.newStartedTimer();
		wSB.append(String.format(
				"\n------------ dump Response [%s %s] -----------",
				wResponse.getStatus(), wResponse.getContentType()));
		// if (pLogDebugForced || getLogger().isLogDebugOn()) {
		if (aIsLogDebugOn) {
			dumpResponseInfosInSB(wSB, wResponse);
			dumpResponseHeadersInSB(wSB, wResponse);
			dumpResponseDataInSB(wSB, wResponse);
			wSB.append(String.format(
					"\n------------ Response trace duration=[%s ms] -----",
					wTimer.getDurationStrMicroSec()));
		} else {
			// if (getLogger().isLogInfoOn()) {
			if (aIsLogInfoOn) {
				dumpResponseInfosMiniInSB(wSB, wResponse);
				wSB.append(String.format(" ResponseTraceDuration=[%s ms]",
						wTimer.getDurationStrMicroSec()));
			}
		}

		// } catch (Exception e) {
		// getLogger().logSevere(this, "doFilter", "ERROR:\n%s", e);
		// }
		return wSB;
	}

	/**
	 * @param aSB
	 * @param wResponse
	 * @return
	 */
	private static StringBuilder dumpResponseDataInSB(final StringBuilder aSB,
			final HttpServletResponse wResponse) {
		return aSB;
	}

	/**
	 * @param aSB
	 * @param aRequest
	 * @return
	 */
	private static StringBuilder dumpResponseHeadersInSB(
			final StringBuilder aSB, final HttpServletResponse wResponse) {

		Enumeration<String> wNames = Collections.enumeration(wResponse
				.getHeaderNames());

		dumpRequestValuesInSB(aSB, "ResponseHeader", wNames,
				new IValueGetter() {
					@Override
					public Object getValue(final String aName) {
						return wResponse.getHeader(aName);
					}
				});

		return aSB;
	}

	/**
	 * @param wSB
	 * @param aResponse
	 * @return
	 */
	private static StringBuilder dumpResponseInfosInSB(final StringBuilder wSB,
			final HttpServletResponse aResponse) {

		dumpOneInfoInSB(wSB, "Status", aResponse.getStatus());
		dumpOneInfoInSB(wSB, "Locale", aResponse.getLocale());
		dumpOneInfoInSB(wSB, "BufferSize/Type", aResponse.getBufferSize(),
				aResponse.getContentType());

		return wSB;
	}

	/**
	 * @param wSB
	 * @param aRequest
	 * @return
	 */
	private static StringBuilder dumpResponseInfosMiniInSB(
			final StringBuilder wSB, final HttpServletResponse aResponse) {

		return wSB.append(String.format(" [%s] [%s] [%s]",
				aResponse.getStatus(), aResponse.getLocale(),
				aResponse.getContentType()));
	}

	/**
	 * @param aStartTime
	 * @return
	 */
	private static String nbSecondsFrom(final long aStartTime) {

		long wNbMiliSeconds = System.currentTimeMillis() - aStartTime;
		Double wDbl = new Double(wNbMiliSeconds / THOUSAND);
		return String.format(FMT_SECONDS, wDbl);
	}

	// context of the configuration
	private String pContextPath = UNKNOWN;

	private String[] pIncludedUriSuffixes = new String[0];

	private boolean pLogDebugForced = false;

	private String pMapping = UNKNOWN;

	private String pName = UNKNOWN;

	private String pTargetContext = UNKNOWN;

	/**
	 *
	 */
	public CServletRequestDump() {
		super();
	}

	/**
	 * @param aInfo
	 * @return
	 */
	protected String getFilterInfo() {

		// ATTENTION : parameters's ID used in other WeApps !
		JSONObject wObj = new JSONObject();
		wObj.put("class", getClass().getSimpleName());
		wObj.put("hashcode", String.valueOf(hashCode()));
		wObj.put("logdebugforced", String.valueOf(pLogDebugForced));
		wObj.put("name", String.valueOf(pName));
		wObj.put("mapping", String.valueOf(pMapping));
		wObj.put("targetcontext", String.valueOf(pTargetContext));
		wObj.put("suffixes", getUriSuffixesList());
		return wObj.toString();
	}

	/**
	 * @return
	 */
	public String getName() {
		return pName;
	}

	/**
	 * @return
	 */
	public int getNbUriSuffixes() {
		return (hasUriSuffixes()) ? pIncludedUriSuffixes.length : -1;
	}

	/**
	 * @return
	 */
	protected String[] getUriSuffixes() {
		return pIncludedUriSuffixes;
	}

	protected String getUriSuffixesList() {
		return CXStringUtils.stringTableToString(getUriSuffixes(), ";");
	}

	/**
	 * @return
	 */
	public boolean hasUriSuffixes() {
		return pIncludedUriSuffixes != null && pIncludedUriSuffixes.length > 0;
	}

	/**
	 * <pre>
	 *  	<init-param>
	 * 			<param-name>Included_Url_Pattern</param-name>
	 * 			<param-value>.*\.jsp</param-value>
	 * 		</init-param>
	 * 		<init-param>
	 * 			<param-name>Log_Debug_Forced</param-name>
	 * 			<param-value>true</param-value>
	 * 		</init-param>
	 * </pre>
	 *
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(final FilterConfig aConfig) throws ServletException {

		pName = aConfig.getFilterName();
		pContextPath = aConfig.getServletContext().getContextPath();

		StringBuilder wSB = new StringBuilder();
		try {
			Enumeration<String> wParameterNames = aConfig
					.getInitParameterNames();
			int wIdx = 0;
			while (wParameterNames.hasMoreElements()) {
				String wParameterName = wParameterNames.nextElement();
				String wParameterValue = aConfig
						.getInitParameter(wParameterName);
				boolean wUsed = false;

				// "filter.request.tracer.included.uri.suffixes";
				if (PARAM_URI_SUFFIXES.equalsIgnoreCase(wParameterName)) {
					wUsed = true;
					if (wParameterValue != null && !wParameterValue.isEmpty()) {
						setUriSuffixes(wParameterValue);
					}
				} else
				// "filter.request.tracer.Log.debug.forced";
				if (PARAM_LOG_DEBUG_FORCED.equalsIgnoreCase(wParameterName)) {
					wUsed = true;
					// true if the string argument is not null and is equal,
					// ignoring case, to the string "true".
					pLogDebugForced = Boolean.parseBoolean(wParameterValue);
				}
				// "filter.request.tracer.mapping";
				if (PARAM_MAPPING.equalsIgnoreCase(wParameterName)) {
					wUsed = true;
					if (wParameterValue != null && !wParameterValue.isEmpty()) {
						pMapping = wParameterValue;
					}
				}
				// "filter.request.tracer.context.path";
				if (PARAM_TARGET_CONTEXT.equalsIgnoreCase(wParameterName)) {
					wUsed = true;
					if (wParameterValue != null && !wParameterValue.isEmpty()) {
						pTargetContext = wParameterValue;
					}
				}

				wSB.append(String.format("\n(%2d) %30s=[%s] used=[%b]", wIdx,
						wParameterName, wParameterValue, wUsed));
				wIdx++;
			}
		} catch (Exception e) {
			// getLogger().logSevere(this, "init", "ERROR:\n%s", e);
			wSB.append(String.format("ERROR:\n%s", CXException.eMiniInString(e)));
		}

		// getLogger().logInfo(this, "init", "initialized name=[%s] Config:%s",
		// getName(), wSB.toString());

		String wTestURI = "https://myServer%s/auth.srvl?RelayState=main.jsp%%3Fpage%%3DDashboard";
		wTestURI = String.format(wTestURI, pContextPath);
		// getLogger().logInfo(this, "init",
		// "TestIfFiltering=[%s] TestURI=[%s]",
		// testIfFiltering(wTestURI), wTestURI);
	}

	/**
	 * @return
	 */
	protected boolean isLogDebugForced() {
		return pLogDebugForced;
	}

	/**
	 * @param aLogDebugForced
	 */
	protected void setLogDebugForced(final boolean aLogDebugForced) {
		pLogDebugForced = aLogDebugForced;
	}

	/**
	 * @param aUriSuffixesList
	 */
	protected void setUriSuffixes(final String aUriSuffixesList) {

		List<String> wUriSuffixesList = new ArrayList<String>();

		if (aUriSuffixesList != null && !aUriSuffixesList.isEmpty()) {
			if (aUriSuffixesList.contains(";")) {
				String[] wSuffixes = aUriSuffixesList.split(";");
				for (String wSuffix : wSuffixes) {

					if (wSuffix != null && !wSuffix.isEmpty()) {
						wUriSuffixesList.add(wSuffix);
					}
					//
					else {
						// getLogger()
						// .logWarn(this, "setUriSuffixes",
						// "Attention the suffixes array definition has empty member");
					}
				}
			}
			//
			else {
				wUriSuffixesList.add(aUriSuffixesList);
			}
		}
		// getLogger().logWarn(this, "setUriSuffixes", "Suffixes=%s",
		// CXStringUtils.stringListToString(wUriSuffixesList));
		//
		// if (wUriSuffixesList.size() == 0) {
		// getLogger().logWarn(this, "setUriSuffixes",
		// "Attention the suffixes array is empty ");
		// }

		setUriSuffixes(wUriSuffixesList.toArray(new String[wUriSuffixesList
				.size()]));
	}

	/**
	 * @param aUriSuffixes
	 */
	protected void setUriSuffixes(final String[] aUriSuffixes) {
		pIncludedUriSuffixes = aUriSuffixes;
	}

	/**
	 * @param aRequest
	 * @return
	 */
	private boolean testIfFiltering(final HttpServletRequest aRequest) {

		return testIfFiltering(aRequest.getRequestURI());
	}

	/**
	 * eg.
	 *
	 * <pre>
	 * http://localhost:8080/AgiliumWeb/auth.srvl?RelayState=main.jsp%3Fpage%3DDashboard
	 * </pre>
	 *
	 * @param aRequestURI
	 * @return
	 */
	private boolean testIfFiltering(final String aRequestURI) {

		if (!hasUriSuffixes()) {
			return false;
		}
		boolean wDoFiltering = false;

		String wRequestURI = aRequestURI;
		int wPos = wRequestURI.indexOf('?');
		if (wPos > -1) {
			wRequestURI = wRequestURI.substring(0, wPos);
		}

		String wFoundSuffix = null;
		for (String wSuffix : pIncludedUriSuffixes) {
			if (wSuffix != null && !wSuffix.isEmpty()
					&& wRequestURI.endsWith(wSuffix)) {
				wDoFiltering = true;
				wFoundSuffix = wSuffix;
				break;
			}
		}
		/*
		 * if (pLogDebugForced || getLogger().isLoggable(Level.FINER)) { Level
		 * wLevel = (pLogDebugForced) ? Level.ALL : Level.FINER; getLogger()
		 * .log(wLevel, this, "execFiltering",
		 * "RequestURI=[%s] execFiltering=[%s] FoundSuffix=[%s] Suffixes=[%s]",
		 * wRequestURI, wDoFiltering, wFoundSuffix, pIncludedUriSuffixes); }
		 */
		return wDoFiltering;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return getFilterInfo();
	}
}
