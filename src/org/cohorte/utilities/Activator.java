package org.cohorte.utilities;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * @author ogattaz
 * 
 */
public class Activator implements BundleActivator {

	private static BundleContext sBundleContext;

	/**
	 * @return
	 */
	public static BundleContext getContext() {
		return sBundleContext;
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
		sBundleContext = bundleContext;
		Bundle wBundle = bundleContext.getBundle();
		System.out.printf("%50s | Bundle=[%50s][%s] starting\n",
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
		Bundle wBundle = bundleContext.getBundle();
		System.out.printf("%50s | Bundle=[%50s][%s] stoping\n",
				"Activator.stop()", wBundle.getSymbolicName(),
				wBundle.getVersion());

		sBundleContext = null;
	}

}
