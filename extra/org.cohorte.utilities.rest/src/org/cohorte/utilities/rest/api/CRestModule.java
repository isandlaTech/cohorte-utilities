package org.cohorte.utilities.rest.api;

import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Abstract REST API module.
 * 
 * A module is a REST resource that has a manager component. This component
 * implements the management service.
 * 
 * @author Ahmad Shahwan
 * @param <T> Manager's type
 *
 */
public abstract class CRestModule<T>
	extends CRestResource {
	
	private ServiceTracker<T, T> pManager = null;

	
	/**
	 * Close service trackers.
	 */
	@Override
	public void close() {
		if (this.pManager instanceof AutoCloseable) {
			try {
				((AutoCloseable) this.pManager).close();
			} catch (Exception e) {}
		}
	}

	/**
	 * Constructor.
	 * 
	 * Open service trackers.
	 * @param aManagerType 
	 */
	protected CRestModule(final Class<T> aManagerType) {
		super();
		this.pManager = new ServiceTracker<>(
			FrameworkUtil.getBundle(aManagerType).getBundleContext(),
			aManagerType,
			null
		);
		this.pManager.open();
	}
	
	/**
	 * Retrieve a content manager form the OSGi framework.
	 *  
	 * @return A content manager component.
	 * @throws UnsupportedOperationException 
	 */
	protected T getManager() throws UnsupportedOperationException {
		T wManager = this.pManager.getService();
		if (wManager == null) {
			throw new UnsupportedOperationException();
		} else {
			return wManager;
		}
	}
}