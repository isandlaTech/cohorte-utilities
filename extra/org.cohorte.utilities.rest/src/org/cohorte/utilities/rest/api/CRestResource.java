package org.cohorte.utilities.rest.api;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;
import org.psem2m.utilities.logging.CActivityLoggerNull;
import org.psem2m.utilities.logging.IActivityLogger;

/**
 * Abstract REST API resource.
 * 
 * Since object of the subclasses of this class are meant to be instantiated by
 * Jersey framework, their life cycle is manager by Jersey as well, and not by
 * iPOJO. Consumed services are managed by means of services trackers in this
 * class and inheriting classes.
 * 
 * @author Ahmad Shahwan
 *
 */
public abstract class CRestResource implements AutoCloseable {
	
	private ServiceTracker<IActivityLogger, IActivityLogger> pLogTracker = null;
	
	private  IActivityLogger pDummyLogger = CActivityLoggerNull.getInstance();
	
	@Context
	private HttpServletRequest req;
	
	/**
	 * No-agrs constructor.
	 * 
	 * Open service trackers.
	 */
	public CRestResource() {
		this.pLogTracker = new ServiceTracker<>(
				FrameworkUtil.getBundle(this.getClass()).getBundleContext(),
				IActivityLogger.class,
				null
			);
		this.pLogTracker.open();
	}

	/**
	 * Close service trackers.
	 */
	@Override
	public void close() {
		if (this.pLogTracker != null) {
			this.pLogTracker.close();
		}
	}
	
	/**
	 * Retrieve an OSGi log service. If no such services is provided, return
	 * a dummy log service.
	 * 
	 * This method should not return {@code null}
	 * 
	 * @return a log service 
	 */
	protected IActivityLogger getLog() {
		IActivityLogger logger = this.pLogTracker.getService();
		if (logger != null) {
			return logger;
		}
		return this.pDummyLogger;
	}
	
	/**
	 * HTTP request.
	 * 
	 * @return
	 */
	protected HttpServletRequest getRequest() {
		return this.req;
	}
}
