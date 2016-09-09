package org.cohorte.utilities.rest;

import static org.osgi.framework.Constants.SERVICE_DESCRIPTION;

import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.ServletException;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;
import org.cohorte.utilities.rest.api.IRestApplication;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.servlet.ServletProperties;
import org.osgi.framework.BundleContext;
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
@Component(name = "Cohorte-Rest")
public class CRestPublisher {

	/**
	 * OSGi HTTP Service.
	 */
	@Requires
	private HttpService httpService = null;

	/**
	 * A set of REST modules awaiting to be registered, once the component is
	 * validated.
	 */
	private Set<IRestApplication> pAwaiting = new HashSet<>();

	private final BundleContext pBundleContext;

	/**
	 * OSGi Logger.
	 */
	@Requires(proxy = false, filter = "(" + SERVICE_DESCRIPTION + "=cohorte)")
	private LogService pLogger = null;

	/**
	 * Whether the component is valid.
	 */
	private boolean pValid = false;

	/**
	 * No-args constructor.
	 */
	public CRestPublisher(final BundleContext aBundleContext) {
		super();
		pBundleContext = aBundleContext;

	}

	/**
	 * Callback that binds a REST module.
	 *
	 * @param aApplication
	 *            REST module
	 */
	@Bind(aggregate = true)
	private void bind(IRestApplication aApplication) {
		pLogger.log(LogService.LOG_INFO, "Binding to a REST application.");
		boolean valid = false;
		synchronized (this) {
			if (this.pValid) {
				register(aApplication);
				valid = true;
			} else {
				this.pAwaiting.add(aApplication);
			}
		}
		pLogger.log(LogService.LOG_INFO, valid ? "Valid application."
				: "Invalid application.");
	}

	/**
	 * Initialize Jersey servlet init parameters.
	 *
	 * @param module
	 * @return
	 */
	private Dictionary<String, String> getInitParams(IRestApplication module) {
		Dictionary<String, String> jerseyServletParams = new Hashtable<>();
		jerseyServletParams.put(ServletProperties.JAXRS_APPLICATION_CLASS,
				module.getClass().getName());
		return jerseyServletParams;
	}

	/**
	 * Invalidate callback.
	 */
	@Invalidate
	private void invalidate() {
		pLogger.log(LogService.LOG_DEBUG, "Invalidating REST provider");
		this.pValid = false;
	}

	/**
	 * Register a REST module.
	 *
	 * @param aApplication
	 */
	private boolean register(IRestApplication aApplication) {

		String wModulePath = aApplication.getPath();
		ClassLoader classLoader = aApplication.getClass().getClassLoader();
		ClassLoader originalContextClassLoader = Thread.currentThread()
				.getContextClassLoader();
		try {
			// Work around issue JERSEY-2093
			Thread.currentThread().setContextClassLoader(classLoader);

			httpService.registerServlet(wModulePath, new ServletContainer(),
					getInitParams(aApplication), null);

			pLogger.log(LogService.LOG_INFO,
					String.format("REST module [%s] registered.", wModulePath));

			return true;

		} catch (IllegalStateException e) {
			pLogger.log(
					LogService.LOG_ERROR,
					String.format(
							"REST Registration State error while registering module [%s]",
							wModulePath), e);
		} catch (ServletException | NamespaceException e) {
			pLogger.log(
					LogService.LOG_ERROR,
					String.format(
							"ServletException of NamespaceException occurred while registering module [%s]",
							wModulePath), e);
		} catch (Exception | Error e) {
			pLogger.log(LogService.LOG_ERROR, String.format(
					"REST Registration error while registering module [%s]",
					wModulePath), e);
		} finally {

			// restore
			Thread.currentThread().setContextClassLoader(
					originalContextClassLoader);
		}
		// if pb
		return false;
	}

	/**
	 * Validate callback.
	 */
	@Validate
	private void validate() {
		try {
			System.out.println(String.format("pLogger=[%s]", pLogger));

			pLogger.log(LogService.LOG_INFO, "Validating REST provider.");

			/*
			 * The bundle {@code org.glassfish.hk2.osgiresourcelocator} must be
			 * started before Servlet registration. Fix for {@code
			 * java.lang.IllegalStateException} with message: "No generator was
			 * provided and there is no default generator registered"
			 * 
			 * @see https://java.net/jira/browse/JERSEY-2788
			 */
			pLogger.log(LogService.LOG_INFO,
					"(Re-)starting resource locator bundle.");
			FrameworkUtil.getBundle(
					org.glassfish.hk2.osgiresourcelocator.Activator.class)
					.start();

			pLogger.log(LogService.LOG_INFO,
					String.format("Number of awaiting REST modules: [%d] .",
							pAwaiting.size()));

			synchronized (this) {
				for (Iterator<IRestApplication> i = this.pAwaiting.iterator(); i
						.hasNext();) {
					if (register(i.next())) {
						i.remove();
					}
				}
			}

			int wNbRemain = pAwaiting.size();
			if (wNbRemain == 0) {
				pValid = true;
			} else {
				pLogger.log(LogService.LOG_WARNING,
						"Not all REST application have been registered. remain="
								+ wNbRemain);
			}
		} catch (Throwable e) {
			pLogger.log(LogService.LOG_ERROR, "REST Registration error.", e);
			e.printStackTrace();
		}
	}
}
