package org.cohorte.utilities.picosoc;

import java.util.logging.Level;

import org.psem2m.utilities.logging.IActivityLogger;

/**
 * @author ogattaz
 * 
 */
public class CAbstractComponentBase {
	

	/**
	 * 
	 */
	public CAbstractComponentBase() {
		super();
	}
	
	/**
	 * @param aSpecification
	 * @return
	 */
	public <T> T getService(Class<? extends T> aSpecification) throws Exception {
		try {
			return CServicesRegistry.getRegistry()
					.getServiceRef(aSpecification).getService();
		} catch (Exception e) {
			CSocLogger.logInMain(Level.SEVERE, this, "getService",
					"unable to get service [‰s] : %s",
					IActivityLogger.class.getSimpleName(), e.getLocalizedMessage());
			throw e;
		}
	}
	
	



	/**
	 * @param aSpecification
	 */
	public <T> void registerMeAsService(Class<? extends T> aSpecification) {

		try {
			registerService(aSpecification, this);
		} catch (Exception e) {
			CSocLogger.logInMain(Level.SEVERE, this, "<init>",
					"unable to register me as service [‰s] : %s",
					aSpecification.getSimpleName(), e.getLocalizedMessage());
		}
	}

	/**
	 * @param aSpecification
	 * @param aService
	 */
	public <T> CServicReference<T> registerService(
			Class<? extends T> aSpecification, T aService) throws Exception {

		return CServicesRegistry.getRegistry()
				.registerService(aSpecification, aService);
	}
}
