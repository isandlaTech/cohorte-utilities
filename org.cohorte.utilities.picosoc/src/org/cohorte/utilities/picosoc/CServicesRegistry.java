package org.cohorte.utilities.picosoc;

import java.util.HashMap;
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
	public static CServicesRegistry getRegistry(){
		return sServicesRegistry;
	}

	/**
	 * @return
	 * @throws Exception
	 */
	public static CServicesRegistry newRegistry()  throws Exception{
		
		if (getRegistry()!=null){
			throw new   Exception(String.format("sServicesRegistry already exists. registry=[%s]", getRegistry()));
		}
		
		return new CServicesRegistry();
	}
	
	private final Map<Class<?>, CServicReference<?>> pServicesRegistry = new HashMap<Class<?>, CServicReference<?>>();
	
	/**
	 * 
	 */
	private CServicesRegistry() throws Exception {
		super();
		sServicesRegistry=this;

		registerService(ISvcServiceRegistry.class, this);
	}

	/**
	 * 
	 */
	@Override
	public void clear() {
		CComponentLogger.logInMain(Level.INFO, this, "clear", "NbService=[%s]",
				pServicesRegistry.size());
		pServicesRegistry.clear();
	}

	/**
	 * @return
	 */
	@Override
	public String dump() {
		StringBuilder wSB = new StringBuilder();
		int wIdx = 0;

		for (Map.Entry<Class<?>, CServicReference<?>> wEntry : pServicesRegistry
				.entrySet()) {
			if (wIdx > 0) {
				wSB.append('\n');
			}
			wSB.append(String.format("(%d)%30s=%s", wIdx, wEntry.getKey()
					.getSimpleName(), wEntry.getValue().getService()));
			wIdx++;
		}
		return wSB.toString();
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
			Class<? extends T> aSpecification) throws Exception {

		@SuppressWarnings("unchecked")
		CServicReference<T> wWebAppServicRef = (CServicReference<T>) pServicesRegistry
				.get(aSpecification);

		if (wWebAppServicRef == null) {
			throw new Exception(String.format(
					"Unable to find the specification [%s]", aSpecification.getSimpleName()));
		}
		CComponentLogger.logInMain(Level.FINE, this, "getServiceRef",
				"specification=[%s] Service=[%s]", aSpecification,
				wWebAppServicRef.getService());

		return wWebAppServicRef;
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
			Class<? extends T> aSpecification, T aService) throws Exception {

		if (pServicesRegistry.containsKey(aSpecification)) {
			throw new Exception(
					String.format("The specification [%s] already registered",
							aSpecification.getSimpleName()));
		}
		CServicReference<T> wWebAppServicRef = new CServicReference<T>(aService);
		pServicesRegistry.put(aSpecification, wWebAppServicRef);
		CComponentLogger.logInMain(Level.INFO, this, "registerService",
				"specification=[%s] Service=[%s]", aSpecification,
				wWebAppServicRef.getService());
		return wWebAppServicRef;
	}

}
