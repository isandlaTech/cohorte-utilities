package org.cohorte.utilities.rest.providers;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;

import org.cohorte.utilities.rest.api.CRestResource;

/**
 * Server-side request log filter.
 * 
 * @author Ahmad Shahwan
 *
 */
@PreMatching
public class CRestLogFilter extends CRestResource implements
		ContainerRequestFilter, ContainerResponseFilter {
	
	static boolean pDebug;
	
	static {
		pDebug = System.getProperty("DEBUG") != null;
	}

	/**
	 * No-args constructor.
	 */
	public CRestLogFilter() {
	}

	@Override
	public void filter(final ContainerRequestContext context)
			throws IOException {
		this.getLog().logInfo(this, "REST Request in",
				"method: [%s], uri: [%s], headers: [%s]", context.getMethod(),
				context.getUriInfo().getRequestUri(), context.getHeaders());
		if (pDebug) {
			System.out.println(String.format(
					"REST Request in\t method: [%s], uri: [%s], headers: [%s]",
					context.getMethod(),
					context.getUriInfo().getRequestUri(),
					context.getHeaders()
			));
		}
	}

	@Override
	public void filter(ContainerRequestContext arg0,
			ContainerResponseContext context) throws IOException {
		this.getLog().logInfo(this, "REST Response out",
				"status: [%d], headers: [%s]", context.getStatus(),
				context.getStringHeaders());
		if (pDebug) {
			System.out.println(String.format(
					"REST Response out in\t status: [%d], headers: [%s]",
					context.getStatus(),
					context.getStringHeaders()
			));
		}
	}

}
