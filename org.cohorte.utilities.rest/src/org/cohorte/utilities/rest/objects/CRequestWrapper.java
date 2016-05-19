package org.cohorte.utilities.rest.objects;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * HTTP request wrapper for serialization.
 * 
 * @author Ahmad Shahwan
 *
 */
@XmlRootElement(name = "Request")
public class CRequestWrapper {
	private HttpServletRequest pRequest;

	/**
	 * Constructor.
	 * 
	 * @param aRequest
	 */
	public CRequestWrapper(HttpServletRequest aRequest) {
		this.pRequest = aRequest;
	}

	/**
	 * Request URL.
	 * 
	 * @return
	 */
	@XmlElement(name = "url")
	public String getUrlString() {
		StringBuffer wRequestURL = this.pRequest.getRequestURL();
		String wQueryString = this.pRequest.getQueryString();

		if (wQueryString == null) {
			return wRequestURL.toString();
		} else {
			return wRequestURL.append('?').append(wQueryString).toString();
		}
	}

	/**
	 * HTTP method.
	 * 
	 * @return
	 */
	@XmlElement(name = "method")
	public String getMethod() {
		return this.pRequest.getMethod();
	}

	/**
	 * HTTP headers.
	 * 
	 * @return
	 */
	@XmlElement(name = "headers")
	public Map<String, String> getHeaders() {
		Map<String, String> wMap = new HashMap<String, String>();

		Enumeration<String> headerNames = this.pRequest.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String wKey = headerNames.nextElement();
			String wValue = this.pRequest.getHeader(wKey);
			wMap.put(wKey, wValue);
		}
		return wMap;
	}

}
