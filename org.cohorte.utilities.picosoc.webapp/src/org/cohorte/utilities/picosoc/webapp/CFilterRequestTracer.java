package org.cohorte.utilities.picosoc.webapp;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.cohorte.utilities.picosoc.CAbstractComponentWithLogger;
import org.psem2m.utilities.CXDateTime;
import org.psem2m.utilities.CXStringUtils;
import org.psem2m.utilities.CXTimer;

/**
 * <pre>
 *
 *  	<filter>
 * 		<filter-name>RequestTracer</filter-name>
 * 		<filter-class>org.cohorte.utilities.picosoc.webapp.CFilterRequestTracer</filter-class>
 * 		<init-param>
 * 			<param-name>Included_Url_Pattern</param-name>
 * 			<param-value>.*\.jsp</param-value>
 * 		</init-param>
 * 		<init-param>
 * 			<param-name>Log_Debug_Forced</param-name>
 * 			<param-value>true</param-value>
 * 		</init-param>
 * 	</filter>
 * 	<filter-mapping>
 * 		<filter-name>RequestTracer</filter-name>
 * 		<url-pattern>/*</url-pattern>
 * 	</filter-mapping>
 *
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
 * @author ogattaz
 *
 */
public class CFilterRequestTracer extends CAbstractComponentWithLogger
		implements Filter {

	/**
	 * @author ogattaz
	 *
	 */
	interface IValueGetter {

		public Object getValue(final String aName);

	}

	public final static String FMT_SECONDS = "%6.3f";
	private static final String PARAM_ID_IUP = "Included_Url_Pattern";
	private static final String PARAM_ID_LDF = "Log_Debug_Forced";
	private static final double THOUSAND = 1000;

	private String pIncludedUrlPattern = null;
	private boolean pLogDebugForced = false;
	private String pName = "unknowwn";

	/**
	 *
	 */
	public CFilterRequestTracer() {

		super();

		getLogger().logInfo(this, "<init>", "instanciated");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {

		getLogger().logInfo(this, "destroy", "destroyed");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
	 * javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest aRequest, ServletResponse aResponse,
			FilterChain aFilterChain) throws IOException, ServletException {

		try {
			HttpServletRequest wRequest = (HttpServletRequest) aRequest;
			HttpServletResponse wResponse = (HttpServletResponse) aResponse;

			CXTimer wTimer = CXTimer.newStartedTimer();
			StringBuilder wSB = new StringBuilder();

			wSB = dumpRequest(wSB, wRequest);

			aFilterChain.doFilter(aRequest, aResponse);

			wSB = dumpResponse(wSB, wResponse);

			wSB.append(String.format(" Duration=[%s ms]",
					wTimer.getDurationStrMicroSec()));

			getLogger().logInfo(this, "doFilter", wSB.toString());

		} catch (Exception e) {
			getLogger().logSevere(this, "doFilter", "ERROR:\n%s", e);
			throw e;
		}
	}

	/**
	 * @param wSB
	 * @param aInfoId
	 * @param aValue
	 * @return
	 */
	private StringBuilder dumpOneInfoInSB(StringBuilder wSB, String aInfoId,
			Object... aValues) {
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

	/**
	 * @param wRequest
	 */
	protected StringBuilder dumpRequest(final StringBuilder wSB,
			final HttpServletRequest wRequest) {

		try {
			CXTimer wTimer = CXTimer.newStartedTimer();

			if (pLogDebugForced || getLogger().isLogDebugOn()) {
				dumpRequestInfosInSB(wSB, wRequest);
				dumpRequestHeadersInSB(wSB, wRequest);
				dumpRequestgetCookiesInSB(wSB, wRequest);
				dumpRequestParametersInSB(wSB, wRequest);
				dumpRequestAttributesInSB(wSB, wRequest);
				wSB.append(String.format(
						"\n------------ Request trace duration=[%s ms] -----",
						wTimer.getDurationStrMicroSec()));
			} else {
				if (getLogger().isLogInfoOn()) {
					dumpRequestInfosMiniInSB(wSB, wRequest);
					wSB.append(String.format(" RequestTraceDuration=[%s ms]",
							wTimer.getDurationStrMicroSec()));
				}
			}

		} catch (Exception e) {
			getLogger().logSevere(this, "doFilter", "ERROR:\n%s", e);
		}
		return wSB;
	}

	/**
	 * @param aSB
	 * @param aRequest
	 * @return
	 */
	private StringBuilder dumpRequestAttributesInSB(StringBuilder aSB,final 
			HttpServletRequest aRequest) {

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
	private StringBuilder dumpRequestgetCookiesInSB(StringBuilder wSB,
			HttpServletRequest aRequest) {
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
	private StringBuilder dumpRequestHeadersInSB(StringBuilder aSB,final 
			HttpServletRequest aRequest) {

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
	private StringBuilder dumpRequestInfosInSB(StringBuilder wSB,
			HttpServletRequest aRequest) {

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
	private StringBuilder dumpRequestInfosMiniInSB(StringBuilder wSB,
			HttpServletRequest aRequest) {

		HttpSession wSession = aRequest.getSession();

		return wSB.append(String.format("%s [%s] [%s]", wSession.getId(),
				aRequest.getMethod(), aRequest.getRequestURL()));
	}

	/**
	 * @param wSB
	 * @param aRequest
	 * @return
	 */
	private StringBuilder dumpRequestParametersInSB(StringBuilder wSB,
			HttpServletRequest aRequest) {

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
	private StringBuilder dumpRequestValuesInSB(StringBuilder aSB,
			String wValueKind, Enumeration<String> aNames,
			IValueGetter aValueGetter) {
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

	/**
	 * @param wSB
	 * @param wResponse
	 * @return
	 */
	protected StringBuilder dumpResponse(final StringBuilder wSB,
			final HttpServletResponse wResponse) {

		try {
			CXTimer wTimer = CXTimer.newStartedTimer();

			if (pLogDebugForced || getLogger().isLogDebugOn()) {
				dumpResponseInfosInSB(wSB, wResponse);
				dumpResponseHeadersInSB(wSB, wResponse);
				wSB.append(String.format(
						"\n------------ Response trace duration=[%s ms] -----",
						wTimer.getDurationStrMicroSec()));
			} else {
				if (getLogger().isLogInfoOn()) {
					dumpResponseInfosMiniInSB(wSB, wResponse);
					wSB.append(String.format(" ResponseTraceDuration=[%s ms]",
							wTimer.getDurationStrMicroSec()));
				}
			}

		} catch (Exception e) {
			getLogger().logSevere(this, "doFilter", "ERROR:\n%s", e);
		}
		return wSB;
	}

	/**
	 * @param aSB
	 * @param aRequest
	 * @return
	 */
	private StringBuilder dumpResponseHeadersInSB(StringBuilder aSB,final 
			HttpServletResponse wResponse) {

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
	private StringBuilder dumpResponseInfosInSB(StringBuilder wSB,
			HttpServletResponse aResponse) {

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
	private StringBuilder dumpResponseInfosMiniInSB(StringBuilder wSB,
			HttpServletResponse aResponse) {

		return wSB.append(String.format(" [%s] [%s] [%s]",
				aResponse.getStatus(), aResponse.getLocale(),
				aResponse.getContentType()));
	}

	/**
	 * @return
	 */
	String getIncludedUrlPattern() {
		return pIncludedUrlPattern;
	}

	/**
	 * @return
	 */
	public String getName() {
		return pName;
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
	@Override
	public void init(FilterConfig aConfig) throws ServletException {

		pName = aConfig.getFilterName();

		StringBuilder wSB = new StringBuilder();
		Enumeration<String> wParameterNames = aConfig.getInitParameterNames();
		int wIdx = 0;
		while (wParameterNames.hasMoreElements()) {
			String wParameterName = wParameterNames.nextElement();
			String wParameterValue = aConfig.getInitParameter(wParameterName);
			boolean wUsed = false;

			// Included_Url_Pattern
			if (PARAM_ID_IUP.equalsIgnoreCase(wParameterName)) {
				wUsed = true;
				pIncludedUrlPattern = wParameterValue;
			} else
			// Log_Debug_Forced
			if (PARAM_ID_LDF.equalsIgnoreCase(wParameterName)) {
				wUsed = true;
				// true if the string argument is not null and is equal,
				// ignoring case, to the string "true".
				pLogDebugForced = Boolean.parseBoolean(wParameterValue);
			}

			wSB.append(String.format("\n(%2d) %30s=[%s] used=[%b]", wIdx,
					wParameterName, wParameterValue, wUsed));
			wIdx++;
		}

		getLogger().logInfo(this, "init", "initialized name=[%s] Config:%s",
				getName(), wSB.toString());

	}

	/**
	 * @param aStartTime
	 * @return
	 */
	private String nbSecondsFrom(final long aStartTime) {

		long wNbMiliSeconds = System.currentTimeMillis() - aStartTime;
		Double wDbl = new Double(wNbMiliSeconds / THOUSAND);
		return String.format(FMT_SECONDS, wDbl);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return String.format("FilterClass=[%s] FilterName=[%s]", getClass()
				.getSimpleName(), getName());
	}

}
