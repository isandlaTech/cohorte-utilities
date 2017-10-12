package org.cohorte.utilities.rest.providers;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;

/**
 * CORS filter. This provider allow CORS requests by sending the proper headers.
 * 
 * @see "http://www.codingpedia.org/ama/how-to-add-cors-support-on-the-server-side-in-java-with-jersey/"
 * 
 * @author Ahmad Shahwan
 *
 */
public class CRestCorsFilter implements ContainerResponseFilter {

	@Override
	public void filter(
			ContainerRequestContext requestContext,
			ContainerResponseContext responseContext)
			throws IOException {
		MultivaluedMap<String, Object> headers =
				responseContext.getHeaders();
		headers.add("Access-Control-Allow-Origin", "*");	
		headers.add("Access-Control-Allow-Methods",	"GET, POST, DELETE, PUT");
		String reuqestHeaders =
			requestContext.getHeaderString("Access-Control-Request-Headers");
		if (reuqestHeaders != null) {
			headers.add("Access-Control-Allow-Headers", reuqestHeaders);
		}
	}

}
