package org.cohorte.utilities.rest;

import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.ServletException;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;
import org.cohorte.utilities.rest.api.IRestApplication;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.servlet.ServletProperties;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.service.log.LogService;

/**
 * REST API provider. This component aggregates all registered REST API modules
 * and register them into an OSGi HTTP Service whenever such a service is made
 * available.
 * 
 * @author Ahmad Shahwan
 *
 */
@Component
@Instantiate
public class CRestPublisher {
	
	/**
	 * A set of REST modules awaiting to be registered, once the component is
	 * validated.
	 */
	private Set<IRestApplication> pAwaiting = new HashSet<>();
	
	/**
	 * Whether the component is valid.
	 */
	private boolean pValid = false;
	
	/**
	 * OSGi HTTP Service.
	 */
	@Requires
	private HttpService httpService = null;
	
	/**
	 * OSGi Logger.
	 */
	@Requires(optional=true)
	LogService pLogger = null;
	
	/**
	 * No-args constructor.
	 */
	public CRestPublisher() {
	}
	
	/**
	 * Callback that binds a REST module.
	 * 
	 * @param aApplication REST module
	 */
	@Bind(aggregate=true)
	private void bind(IRestApplication aApplication) {
		this.pLogger.log(LogService.LOG_INFO, "Binding to a REST application.");
		boolean valid = false;
		synchronized (this) {
			if (this.pValid) {
				register(aApplication);
				valid = true;
			} else {
				this.pAwaiting.add(aApplication);
			}
		}
		this.pLogger.log(LogService.LOG_INFO,
				valid ? "Valid application." : "Invalid application.");
	}
	
	/**
	 * Validate callback.
	 */
	@Validate
	private void validate() {
		try {
			this.pLogger.log(LogService.LOG_INFO, "Validating REST provider.");
			this.pLogger.log(LogService.LOG_INFO, 
					String.format("Number of awaiting REST modules: %d .",
							pAwaiting.size()));
			this.pLogger.log(LogService.LOG_INFO,
					"(Re-)starting resource locator bundle.");
			/*
			 * The bundle {@code org.glassfish.hk2.osgiresourcelocator} must be
			 * started before Servlet registration.
			 * Fix for {@code java.lang.IllegalStateException} with message: 
			 * "No generator was provided and there is no default generator
			 * registered"
			 * 
			 * @see https://java.net/jira/browse/JERSEY-2788
			 */
			FrameworkUtil.getBundle(
				org.glassfish.hk2.osgiresourcelocator.Activator.class
			).start();
			synchronized (this) {
				for (Iterator<IRestApplication> i = this.pAwaiting.iterator();
						i.hasNext();) {
					if (register(i.next())) {
						i.remove();
					}
				}
			}
			if (this.pAwaiting.size() == 0) {
				this.pValid = true;
			} else {
				this.pLogger.log(LogService.LOG_WARNING,
						"Not all REST application have been registered");
			}
		} catch (Throwable e) {
			this.pLogger.log(
					LogService.LOG_ERROR, "REST Registration error.", e);
			e.printStackTrace();
		}
	}
	
	/**
	 * Invalidate callback.
	 */
	@Invalidate
	private void invalidate() {
		this.pLogger.log(LogService.LOG_DEBUG, "Invalidating REST provider");
		this.pValid = false;
	}
	
	/**
	 * Register a REST module.
	 * 
	 * @param aApplication
	 */
	private boolean register(IRestApplication aApplication) {
		boolean result = true;
		ClassLoader classLoader = aApplication.getClass().getClassLoader();
        ClassLoader originalContextClassLoader =
        		Thread.currentThread().getContextClassLoader();
		try {
			// Work around issue JERSEY-2093
	        Thread.currentThread().setContextClassLoader(classLoader);
			httpService.registerServlet(
					aApplication.getPath(),
					new ServletContainer(),
					getInitParams(aApplication), null);
			this.pLogger.log(LogService.LOG_INFO, "REST module registered.");
		} catch (IllegalStateException e) { 
			this.pLogger.log(
					LogService.LOG_ERROR, "REST Registration error.", e);
			result = false;
		} catch (ServletException | NamespaceException e) {
			this.pLogger.log(LogService.LOG_ERROR,
					"Exception occurred while registering REST module.", e);
		}
		finally {
			Thread.currentThread()
				.setContextClassLoader(originalContextClassLoader);
		}
		return result;
	}

	/**
	 * Initialize Jersey servlet init parameters.
	 * 
	 * @param module
	 * @return
	 */
	private Dictionary<String, String> getInitParams(IRestApplication module) {
		Dictionary<String, String> jerseyServletParams = new Hashtable<>();
		jerseyServletParams.put(
				ServletProperties.JAXRS_APPLICATION_CLASS,
				module.getClass().getName());
		return jerseyServletParams;
	}
}
