package org.cohorte.utilities.rest.objects;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Exception object wrapper for serialization.
 * 
 * @author Ahmad Shahwan
 *
 */
@XmlRootElement(name="UnckeckedException")
public class CGedExceptionWrapper {

	private Throwable pElement;
	private CRequestWrapper pRequest;
	
	/**
	 * Constructor.
	 * 
	 * @param aElement
	 */
	public CGedExceptionWrapper(Throwable aElement) {
		this.pElement = aElement;
	}
	
	/**
	 * Constructor with two parameters.
	 * 
	 * @param aElement
	 * @param aRequest
	 */
	public CGedExceptionWrapper(
			Throwable aElement,
			HttpServletRequest aRequest) {
		this.pElement = aElement;
		if (aRequest != null) {
			this.pRequest = new CRequestWrapper(aRequest);
		}
	}

	/**
	 * Error message.
	 * 
	 * @return
	 */
	@XmlElement(name="message")
	public String getMessage() {
		return this.pElement.getMessage();
	}
	
	/**
	 * Cause message. 
	 * @return
	 */
	@XmlElement(name="original_messages")
	public String[] getOriginalMessages() {
		List<String> wList = new ArrayList<>(10);
		Throwable e = this.pElement;
		while (e.getCause() != null) {
			String cause = String.format("%s:\t%s",
					e.getCause().getClass().getName(),
					e.getCause().getMessage());
			wList.add(cause);
			e = e.getCause();
		}
		return wList.toArray(new String[wList.size()]);
	}
	
	/**
	 * Class name.
	 * 
	 * @return
	 */
	@XmlElement(name="type")
	public String getTypeName() {
		return this.pElement.getClass().getName();
	}
	
	/**
	 * HTTP status code.
	 * 
	 * @return
	 */
	@XmlElement(name="status_code")
	public int getStatusCode() {
		if (this.pElement instanceof WebApplicationException) {
			return ((WebApplicationException) this.pElement)
					.getResponse().getStatus();
		} else {
			return 500;
		}
	}

	/**
	 * HTTP request.
	 * 
	 * @return
	 */
	@XmlElement(name = "request")
	public CRequestWrapper getRequest() {
		return this.pRequest;
	}
}
