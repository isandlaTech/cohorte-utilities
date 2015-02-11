package org.cohorte.utilities.picosoc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * @author ogattaz
 * 
 */
public class CServicesRegistry extends CAbstractComponentBase implements
		ISvcServiceRegistry {

	private static CServicesRegistry sServicesRegistry;

	/**
	 * @return
	 */
	public static CServicesRegistry getRegistry() {
		return sServicesRegistry;
	}

	/**
	 * @return
	 * @throws Exception
	 */
	public static CServicesRegistry newRegistry() throws Exception {

		if (getRegistry() != null) {
			throw new Exception(String.format(
					"sServicesRegistry already exists. registry=[%s]",
					getRegistry()));
		}

		return new CServicesRegistry();
	}

	private final Map<CServiceKey<?>, CServicReference<?>> pServicesRegistry = new HashMap<CServiceKey<?>, CServicReference<?>>();

	/**
	 * 
	 */
	private CServicesRegistry() throws Exception {
		super();

		sServicesRegistry = this;
		registerMeAsService(ISvcServiceRegistry.class);
	}

	/**
	 * 
	 */
	@Override
	public void clear() {
		log(Level.INFO, this, "clear", "NbService=[%s]",
				pServicesRegistry.size());

		unregisterMe();
		pServicesRegistry.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cohorte.utilities.picosoc.ISvcServiceRegistry#contains(java.lang.
	 * Class)
	 */
	@Override
	public <T> boolean contains(Class<? extends T> aSpecification,
			final Map<String, String> aProperties) {

		return pServicesRegistry.containsKey(new CServiceKey<T>(aSpecification,
				aProperties));
	}

	/**
	 * @return
	 */
	@Override
	public String dump() {
		StringBuilder wSB = new StringBuilder();
		int wIdx = 0;

		for (Map.Entry<CServiceKey<?>, CServicReference<?>> wEntry : pServicesRegistry
				.entrySet()) {
			if (wIdx > 0) {
				wSB.append('\n');
			}
			wSB.append(String.format("(%d)%80s ==> %s", wIdx, wEntry.getKey()
					.toString(), wEntry.getValue().getService()));
			wIdx++;
		}
		return wSB.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cohorte.utilities.picosoc.ISvcServiceRegistry#findServiceRef(java
	 * .lang.Class)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> CServicReference<T> findServiceRef(
			Class<? extends T> aSpecification,
			final Map<String, String> aProperties) {

		return (CServicReference<T>) pServicesRegistry.get(new CServiceKey<T>(
				aSpecification, aProperties));
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

		CServicReference<T> wServicReference = findServiceRef(aSpecification,
				aProperties);

		return (wServicReference != null) ? wServicReference.getService()
				: null;
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

		CServicReference<T> wServicReference = findServiceRef(aSpecification,
				aProperties);
		if (wServicReference == null) {
			throw new Exception(String.format("Unable to get the service [%s]",
					new CServiceKey<T>(aSpecification, aProperties).toString()));
		}

		return wServicReference.getService();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.isandlatech.x3.loadbalancer.ISvcWebAppServiceRegistry#getServiceRef
	 * (java.lang.String)
	 */
	@Override
	public <T> CServicReference<T> getServiceRef(
			Class<? extends T> aSpecification,
			final Map<String, String> aProperties) throws Exception {

		CServicReference<T> wWebAppServicRef = findServiceRef(aSpecification,
				aProperties);

		if (wWebAppServicRef == null) {
			throw new Exception(String.format("Unable to get the service [%s]",
					new CServiceKey<T>(aSpecification, aProperties)));
		}
		log(Level.FINE, this, "getServiceRef",
				"specification=[%s] Service=[%s]",
				wWebAppServicRef.getServiceKey(), wWebAppServicRef.getService());

		return wWebAppServicRef;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cohorte.utilities.picosoc.CAbstractComponentBase#getServiceRefs(java
	 * .lang.Class, java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> List<CServicReference<T>> getServiceRefs(
			Class<? extends T> aSpecification, Map<String, String> aProperties) {

		CServiceKey<T> wServiceKey = new CServiceKey<T>(aSpecification,
				aProperties);
		List<CServicReference<T>> wServiceRefs = new ArrayList<CServicReference<T>>();

		synchronized (this) {
			for (Map.Entry<CServiceKey<?>, CServicReference<?>> wEntry : pServicesRegistry
					.entrySet()) {

				if (wEntry.getKey().match(wServiceKey)) {
					wServiceRefs.add((CServicReference<T>) wEntry.getValue());
				}
			}
		}
		return wServiceRefs;
	}

	/**
	 * @param aLevel
	 * @param aWho
	 * @param aWhat
	 * @param aInfos
	 */
	private void log(Level aLevel, final Object aWho, CharSequence aWhat,
			Object... aInfos) {

		CComponentLogger.logInMain(aLevel, aWho, aWhat, aInfos);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.isandlatech.x3.loadbalancer.ISvcWebAppServiceRegistry#registerService
	 * (java.lang.Class, java.lang.Object)
	 */
	@Override
	public <T> CServicReference<T> registerService(
			Class<? extends T> aSpecification,
			final Map<String, String> aProperties, final T aService)
			throws Exception {

		CServicReference<T> wWebAppServicRef = new CServicReference<T>(
				aSpecification, aProperties, aService);

		if (pServicesRegistry.containsKey(wWebAppServicRef.getServiceKey())) {
			throw new Exception(String.format(
					"The service [%s] already registered", wWebAppServicRef
							.getServiceKey().toString()));
		}

		pServicesRegistry.put(wWebAppServicRef.getServiceKey(),
				wWebAppServicRef);

		log(Level.INFO, this, "registerService",
				"ServiceKey=[%s] Service=[%s] ServiceRef=[%s]",
				wWebAppServicRef.getServiceKey(),
				wWebAppServicRef.getService(), wWebAppServicRef);
		return wWebAppServicRef;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cohorte.utilities.picosoc.ISvcServiceRegistry#removeService(org.cohorte
	 * .utilities.picosoc.CServicReference)
	 */
	@Override
	public <T> boolean removeService(CServicReference<T> aServiceRef)
			throws Exception {

		if (!pServicesRegistry.containsKey(aServiceRef.getServiceKey())) {
			throw new Exception(String.format(
					"The service [%s] isn't registered",
					aServiceRef.getServiceKey()));
		}

		CServicReference<?> wServiceRemoved = pServicesRegistry
				.remove(aServiceRef.getServiceKey());
		boolean wIsServiceRemoved = (wServiceRemoved != null);

		log(Level.INFO, this, "removeService",
				"IsServiceRemoved=[%s] ServiceKey=[%s] ServiceRef=[%s]",
				wIsServiceRemoved, wServiceRemoved.getServiceKey(),
				wServiceRemoved);
		return wIsServiceRemoved;
	}
}
