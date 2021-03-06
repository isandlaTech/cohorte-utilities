package org.cohorte.iot.json.validator;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(final BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		Bundle wBundle = context.getBundle();

		System.out.printf("%50s | Bundle=[%50s][%s] started\n",
				"Activator.start()", wBundle.getSymbolicName(),
				wBundle.getVersion());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(final BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		Bundle wBundle = context.getBundle();
		System.out.printf("%50s | Bundle=[%50s][%s] stopped\n",
				"Activator.stop()", wBundle.getSymbolicName(),
				wBundle.getVersion());
	}

}
