package org.cohorte.utilities.picosoc;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.psem2m.utilities.logging.IActivityLogger;

/**
 * @author ogattaz
 * 
 */
public abstract class CAbstractComponentBase implements IComponent {

	CServicReference<?> pMyServiceRef = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cohorte.utilities.picosoc.IComponent#doesServiceExist(java.lang.Class
	 * )
	 */
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
	 * org.cohorte.utilities.picosoc.IComponent#findServiceRef(java.lang.Class)
	 */
	@Override
	public <T> boolean doesServiceExist(Class<? extends T> aSpecification) {

		return doesServiceExist(aSpecification, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cohorte.utilities.picosoc.IComponent#doesServiceExist(java.lang.Class
	 * , java.util.Map)
	 */
	@Override
	public <T> boolean doesServiceExist(Class<? extends T> aSpecification,
			final Map<String, String> aProperties) {

		return CServicesRegistry.getRegistry().contains(aSpecification,
				aProperties);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cohorte.utilities.picosoc.IComponent#getOptionalService(java.lang
	 * .Class)
	 */
	@Override
	public <T> CServicReference<T> findServiceRef(
			Class<? extends T> aSpecification) {

		return this.findServiceRef(aSpecification, null);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cohorte.utilities.picosoc.IComponent#findServiceRef(java.lang.Class,
	 * java.util.Map)
	 */
	@Override
	public <T> CServicReference<T> findServiceRef(
			Class<? extends T> aSpecification, Map<String, String> aProperties) {

		return CServicesRegistry.getRegistry().findServiceRef(aSpecification,
				aProperties);
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

		return this.getOptionalService(aSpecification, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cohorte.utilities.picosoc.IComponent#getOptionalService(java.lang
	 * .Class, java.util.Map)
	 */
	@Override
	public <T> T getOptionalService(Class<? extends T> aSpecification,
			Map<String, String> aProperties) {
		try {
			return CServicesRegistry.getRegistry()
					.findServiceRef(aSpecification, aProperties).getService();
		} catch (Exception e) {
			CComponentLogger.logInMain(Level.FINE, this, "getOptionalService",
					"unable to get the optional service [‰s]",
					new CServiceKey<T>(aSpecification, aProperties));

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
		return this.getService(aSpecification, null);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cohorte.utilities.picosoc.IComponent#getService(java.lang.Class,
	 * java.util.Map)
	 */
	@Override
	public <T> T getService(Class<? extends T> aSpecification,
			Map<String, String> aProperties) throws Exception {
		try {
			return CServicesRegistry.getRegistry()
					.getServiceRef(aSpecification, aProperties).getService();
		} catch (Exception e) {
			log(Level.SEVERE, this, "getService",
					"unable to get the service [‰s] : %s", new CServiceKey<T>(
							aSpecification, aProperties),
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

		return this.getServiceRef(aSpecification, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cohorte.utilities.picosoc.IComponent#getServiceRef(java.lang.Class,
	 * java.util.Map)
	 */
	@Override
	public <T> CServicReference<T> getServiceRef(
			Class<? extends T> aSpecification, Map<String, String> aProperties)
			throws Exception {

		try {
			return CServicesRegistry.getRegistry().getServiceRef(
					aSpecification, aProperties);
		} catch (Exception e) {
			log(Level.SEVERE, this, "getServiceRef",
					"unable to get the service ref of the service [‰s] : %s",
					new CServiceKey<T>(aSpecification, aProperties),
					e.getLocalizedMessage());
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cohorte.utilities.picosoc.IComponent#getServiceRefs(java.lang.Class)
	 */
	@Override
	public <T> List<CServicReference<T>> getServiceRefs(
			Class<? extends T> aSpecification) {

		return this.getServiceRefs(aSpecification, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cohorte.utilities.picosoc.IComponent#getServiceRefs(java.lang.Class,
	 * java.util.Map)
	 */
	@Override
	public <T> List<CServicReference<T>> getServiceRefs(
			Class<? extends T> aSpecification, Map<String, String> aProperties) {

		return CServicesRegistry.getRegistry().getServiceRefs(aSpecification,
				aProperties);
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

		this.registerMeAsService(aSpecification, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cohorte.utilities.picosoc.IComponent#registerMeAsService(java.lang
	 * .Class, java.util.Map)
	 */
	@Override
	public <T> void registerMeAsService(Class<? extends T> aSpecification,
			Map<String, String> aProperties) {

		try {
			pMyServiceRef = registerService(aSpecification, aProperties, this);
			log(Level.INFO, this, "registerMeAsService", "MyServiceRef=[%s]",
					pMyServiceRef);

		} catch (Exception e) {
			log(Level.SEVERE, this, "registerMeAsService",
					"unable to register me as service [‰s] : %s",
					new CServiceKey<T>(aSpecification, aProperties),
					e.getLocalizedMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cohorte.utilities.picosoc.IComponent#registerService(java.lang.Class,
	 * java.util.Map, java.lang.Object)
	 */
	@Override
	public <T> CServicReference<T> registerService(
			Class<? extends T> aSpecification, Map<String, String> aProperties,
			T aService) throws Exception {

		return CServicesRegistry.getRegistry().registerService(aSpecification,
				aProperties, aService);
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

		return this.registerService(aSpecification, null, aService);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cohorte.utilities.picosoc.IComponent#unregisterMe()
	 */
	@Override
	public <T> boolean unregisterMe() {

		log(Level.INFO, this, "unregisterMe", "MyServiceRef=[%s]",
				pMyServiceRef);
		if (pMyServiceRef != null) {
			try {
				boolean wRemoved = removeService(pMyServiceRef);
				pMyServiceRef = null;
				return wRemoved;
			} catch (Exception e) {
				log(Level.SEVERE, this, "<init>",
						"unable to unregister me as service [‰s] : %s", this
								.getClass().getSimpleName(),
						e.getLocalizedMessage());
			}
		}
		return false;
	}
}
