package org.cohorte.utilities.picosoc;

import java.util.logging.Level;

import org.psem2m.utilities.CXException;
import org.psem2m.utilities.logging.IActivityLogger;

/**
 * @author ogattaz
 * 
 */
public abstract class CAbstractComponentBase implements IComponent {

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cohorte.utilities.picosoc.IComponent#getOptionalService(java.lang
	 * .Class)
	 */
	@Override
	public <T> T getOptionalService(Class<? extends T> aSpecification) {
		try {
			return CServicesRegistry.getRegistry()
					.getServiceRef(aSpecification).getService();
		} catch (Exception e) {
			CComponentLogger.logInMain(Level.FINE, this, "getOptionalService",
					"unable to get the optional service [‰s]",
					aSpecification.getSimpleName());

			return null;
		}
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
			log(Level.SEVERE, this, "getService",
					"unable to get the service [%s] : %s",
					IActivityLogger.class.getSimpleName(),
					CXException.eUserMessagesInString(e));
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
			log(Level.SEVERE, this, "getServiceRef",
					"unable to get service ref [‰s] : %s",
					IActivityLogger.class.getSimpleName(),
					e.getLocalizedMessage());
			throw e;
		}
	}

	/**
	 * @param aLevel
	 * @param aWho
	 * @param aWhat
	 * @param aInfos
	 */
	private void log(Level aLevel, final Object aWho, CharSequence aWhat,
			Object... aInfos) {

		IActivityLogger wLogger = getOptionalService(IActivityLogger.class);
		if (wLogger != null) {
			wLogger.log(aLevel, aWho, aWhat, aInfos);
		} else {
			CComponentLogger.logInMain(aLevel, aWho, aWhat, aInfos);
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
			log(Level.SEVERE, this, "<init>",
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
