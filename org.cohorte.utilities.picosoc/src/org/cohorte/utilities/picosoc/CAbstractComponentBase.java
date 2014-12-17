package org.cohorte.utilities.picosoc;

import java.util.logging.Level;

import org.psem2m.utilities.logging.IActivityLogger;

/**
 * @author ogattaz
 * 
 */
public class CAbstractComponentBase implements IComponent {

	/**
	 * 
	 */
	public CAbstractComponentBase() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cohorte.utilities.picosoc.IComponent#doesServiceExist(java.lang.Class
	 * )
	 */
	@Override
	public <T> boolean doesServiceExist(Class<? extends T> aSpecification) {

		return CServicesRegistry.getRegistry().contains(aSpecification);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cohorte.utilities.picosoc.IComponent#findServiceRef(java.lang.Class)
	 */
	@Override
	public <T> CServicReference<T> findServiceRef(
			Class<? extends T> aSpecification) throws Exception {

		return CServicesRegistry.getRegistry().findServiceRef(aSpecification);

	}

	/**
	 * @param aSpecification
	 * @return
	 */
	@Override
	public <T> T getService(Class<? extends T> aSpecification) throws Exception {
		try {
			return CServicesRegistry.getRegistry()
					.getServiceRef(aSpecification).getService();
		} catch (Exception e) {
			CComponentLogger.logInMain(Level.SEVERE, this, "getService",
					"unable to get the service [‰s] : %s",
					IActivityLogger.class.getSimpleName(),
					e.getLocalizedMessage());
			throw e;
		}
	}

	/**
	 * @param aSpecification
	 * @return
	 * @throws Exception
	 */
	@Override
	public <T> CServicReference<T> getServiceRef(
			Class<? extends T> aSpecification) throws Exception {
		try {
			return CServicesRegistry.getRegistry()
					.getServiceRef(aSpecification);
		} catch (Exception e) {
			CComponentLogger.logInMain(Level.SEVERE, this, "getServiceRef",
					"unable to get service ref [‰s] : %s",
					IActivityLogger.class.getSimpleName(),
					e.getLocalizedMessage());
			throw e;
		}
	}

	/**
	 * @param aSpecification
	 */
	@Override
	public <T> void registerMeAsService(Class<? extends T> aSpecification) {

		try {
			registerService(aSpecification, this);
		} catch (Exception e) {
			CComponentLogger.logInMain(Level.SEVERE, this, "<init>",
					"unable to register me as service [‰s] : %s",
					aSpecification.getSimpleName(), e.getLocalizedMessage());
		}
	}

	/**
	 * @param aSpecification
	 * @param aService
	 */
	@Override
	public <T> CServicReference<T> registerService(
			Class<? extends T> aSpecification, T aService) throws Exception {

		return CServicesRegistry.getRegistry().registerService(aSpecification,
				aService);
	}

	/**
	 * @param aServiceRef
	 * @return
	 * @throws Exception
	 */
	@Override
	public <T> boolean removeService(CServicReference<T> aServiceRef)
			throws Exception {

		return CServicesRegistry.getRegistry().removeService(aServiceRef);
	}
}
