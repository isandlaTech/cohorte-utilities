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
	// #48
	private final Map<Object, CServicReference<?>> pServicesMap = new HashMap<Object, CServicReference<?>>();

	
	/**
	 * @param aService
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> CServicReference<T> findServiceRef(final T aService){
		
		return (CServicReference<T> )pServicesMap.get(aService);
	}
	
	
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
		pServicesMap.clear();
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
	 * MOD_OG_20151224 Dump format enhancement
	 * 
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
			wSB.append(String.format("(%3d)%80s ==> %s", wIdx, wEntry.getKey()
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
	@Override
	public <T> CServicReference<T> findServiceRef(
			Class<? extends T> aSpecification,
			final Map<String, String> aProperties) {

		return searchServiceRef(aSpecification, aProperties,
				!SEARCH_MODE_STRICT);
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

		CServicReference<T> wServicReference = searchServiceRef(aSpecification,
				aProperties, !SEARCH_MODE_STRICT);

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

		CServicReference<T> wServicReference = searchServiceRef(aSpecification,
				aProperties, SEARCH_MODE_STRICT);
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

		CServicReference<T> wWebAppServicRef = searchServiceRef(aSpecification,
				aProperties, SEARCH_MODE_STRICT);

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
	@Override
	public <T> List<CServicReference<T>> getServiceRefs(
			Class<? extends T> aSpecification, Map<String, String> aProperties) {
		
		return getServiceRefs(aSpecification,aProperties,SEARCH_MODE_STRICT);
	}
	

	/* (non-Javadoc)
	 * @see org.cohorte.utilities.picosoc.CAbstractComponentBase#getServiceRefs(java.lang.Class, java.util.Map, boolean)
	 */
	@SuppressWarnings("unchecked")
	public <T> List<CServicReference<T>> getServiceRefs(
			Class<? extends T> aSpecification, Map<String, String> aProperties,final boolean aStrictMode ) {

		CServiceKey<T> wSearchedKey = new CServiceKey<T>(aSpecification,
				aProperties);
		List<CServicReference<T>> wServiceRefs = new ArrayList<CServicReference<T>>();

		synchronized (this) {
			for (Map.Entry<CServiceKey<?>, CServicReference<?>> wEntry : pServicesRegistry
					.entrySet()) {

				if (wEntry.getKey().match(wSearchedKey,aStrictMode)) {
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

		CServicReference<T> wServicRef = new CServicReference<T>(
				aSpecification, aProperties, aService);

		if (pServicesRegistry.containsKey(wServicRef.getServiceKey())) {
			throw new Exception(String.format(
					"The service [%s] already registered", wServicRef
							.getServiceKey().toString()));
		}

		pServicesRegistry.put(wServicRef.getServiceKey(),
				wServicRef);
		// #48
		pServicesMap.put(aService, wServicRef);

		log(Level.FINER, this, "registerService",
				"ServiceKey=[%s] Service=[%s] ServiceRef=[%s]",
				wServicRef.getServiceKey(),
				wServicRef.getService(), wServicRef);
		return wServicRef;
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
		
		if (aServiceRef==null) {
			throw new Exception("Unable to remove a nul serviceRef");
		}

		if (!pServicesRegistry.containsKey(aServiceRef.getServiceKey())) {
			throw new Exception(String.format(
					"The service [%s] isn't registered",
					aServiceRef.getServiceKey()));
		}

		CServicReference<?> wServiceRef1Removed = pServicesRegistry
				.remove(aServiceRef.getServiceKey());
		boolean wIsServiceRef1Removed = (wServiceRef1Removed != null);
		if (!wIsServiceRef1Removed) {
			throw new Exception(String.format(
					"Unable to remove the service [%s] from the map 'pServicesRegistry'",
					aServiceRef.getServiceKey()));
		}
		// #48
		CServicReference<?> wServiceRef2Removed = pServicesMap.remove(aServiceRef.getService());
		boolean wIsServiceRef2Removed = (wServiceRef2Removed != null);
		if (!wIsServiceRef2Removed) {
			throw new Exception(String.format(
					"Unable to remove the service [%s] from the map 'pServicesMap'",
					aServiceRef.getServiceKey()));
		}
		
		
		log(Level.INFO, this, "removeService",
				"IsServiceRemoved=[%s] ServiceRef=[%s]",
				(wIsServiceRef1Removed &wIsServiceRef2Removed) ,
				wServiceRef1Removed);
		
		return wIsServiceRef1Removed;
	}

	/**
	 * @param aSpecification
	 * @param aProperties
	 * @param aSearchModeStrict
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private <T> CServicReference<T> searchServiceRef(
			Class<? extends T> aSpecification,
			final Map<String, String> aProperties,
			final boolean aSearchModeStrict) {

		CServiceKey<T> wSearchedKey = new CServiceKey<T>(aSpecification,
				aProperties);

		CServicReference<T> wServiceRef = (CServicReference<T>) pServicesRegistry
				.get(wSearchedKey);

		if (wServiceRef == null && !aSearchModeStrict && aProperties != null
				&& !aProperties.isEmpty()) {

			synchronized (this) {
				for (Map.Entry<CServiceKey<?>, CServicReference<?>> wEntry : pServicesRegistry
						.entrySet()) {

					if (wEntry.getKey().match(wSearchedKey)) {
						wServiceRef = (CServicReference<T>) wEntry.getValue();
						break;
					}
				}
			}
		}
		return wServiceRef;
	}
}
