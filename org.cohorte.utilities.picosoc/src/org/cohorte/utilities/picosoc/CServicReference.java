package org.cohorte.utilities.picosoc;

import java.util.Map;

/**
 * @author ogattaz
 * 
 */
public class CServicReference<T> {

	private final T pService;
	private final Class<? extends T> pSpecification;
	private final CServiceKey<T> pServiceKey;

	/**
	 * @param aService
	 */
	public CServicReference(Class<? extends T> aSpecification,
			final Map<String, String> aProperties, T aService) throws Exception {
		super();
		if (aService == null) {
			throw new Exception(
					"Unable to instanciate a Service Reference with a null service");
		}
		pService = aService;
		pSpecification = aSpecification;
		pServiceKey = new CServiceKey<T>(aSpecification, aProperties);
	}

	/**
	 * @param aKey
	 * @return
	 */
	public String getProperty(final String aKey) {
		return pServiceKey.getProperty(aKey);
	}

	/**
	 * @return
	 */
	public T getService() {
		return pService;
	}

	/**
	 * @return
	 */
	public CServiceKey<T> getServiceKey() {
		return pServiceKey;
	}

	/**
	 * @return
	 */
	public Class<? extends T> getSpecification() {

		return pSpecification;
	}

	/**
	 * @param aKey
	 * @return
	 */
	public String removeProperty(final String aKey) {
		return pServiceKey.removeProperty(aKey);
	}

	/**
	 * @param aKey
	 * @param aValue
	 */
	public String setProperty(final String aKey, final String aValue) {
		return pServiceKey.setProperty(aKey, aValue);
	}
}
