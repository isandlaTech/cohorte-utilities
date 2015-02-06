package org.cohorte.utilities.picosoc;

import java.util.logging.Level;

import org.cohorte.utilities.tests.CAppConsoleBase;
import org.psem2m.utilities.logging.IActivityLogger;

/**
 * @author ogattaz
 * 
 */
public abstract class CAbstractComponentAppConsole extends CAppConsoleBase
		implements IComponent {

	private ISvcServiceRegistry pSvcServicesRegistry;

	/**
	 * @param args
	 * @throws Exception
	 */
	public CAbstractComponentAppConsole(final String[] args) throws Exception {
		super(args);

		// first
		pSvcServicesRegistry = CServicesRegistry.newRegistry();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cohorte.utilities.tests.CAppConsoleBase#destroy()
	 */
	@Override
	protected void destroy() {

		getLogger().logInfo(this, "destroy", "Destroy ServiceRegistry\n%s",
				pSvcServicesRegistry.dump());

		pSvcServicesRegistry.clear();
		pSvcServicesRegistry = null;

		// at the end !
		super.destroy();
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
			log(Level.FINE, this, "getOptionalService",
					"unable to get the optional service [‰s]",
					aSpecification.getSimpleName());
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cohorte.utilities.picosoc.IComponent#getService(java.lang.Class)
	 */
	@Override
	public <T> T getService(Class<? extends T> aSpecification) throws Exception {
		try {
			return CServicesRegistry.getRegistry()
					.getServiceRef(aSpecification).getService();
		} catch (Exception e) {
			log(Level.SEVERE, this, "getService",
					"unable to get service [‰s] : %s",
					IActivityLogger.class.getSimpleName(),
					e.getLocalizedMessage());
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cohorte.utilities.picosoc.IComponent#getServiceRef(java.lang.Class)
	 */
	@Override
	public <T> CServicReference<T> getServiceRef(
			Class<? extends T> aSpecification) throws Exception {

		return CServicesRegistry.getRegistry().getServiceRef(aSpecification);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cohorte.utilities.picosoc.IComponent#registerMeAsService(java.lang
	 * .Class)
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cohorte.utilities.picosoc.IComponent#registerService(java.lang.Class,
	 * java.lang.Object)
	 */
	@Override
	public <T> CServicReference<T> registerService(
			Class<? extends T> aSpecification, T aService) throws Exception {

		return CServicesRegistry.getRegistry().registerService(aSpecification,
				aService);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cohorte.utilities.picosoc.IComponent#removeService(org.cohorte.utilities
	 * .picosoc.CServicReference)
	 */
	@Override
	public <T> boolean removeService(CServicReference<T> aServiceRef)
			throws Exception {

		return CServicesRegistry.getRegistry().removeService(aServiceRef);
	}
}
