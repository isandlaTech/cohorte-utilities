package org.cohorte.utilities.picosoc;

/**
 * @author ogattaz
 * 
 */
public class CServicReference<T> {

	private final T pService;
	private final Class<? extends T> pSpecification;

	/**
	 * @param aService
	 */
	public CServicReference(Class<? extends T> aSpecification, T aService)
			throws Exception {
		super();
		if (aService == null) {
			throw new Exception(
					"Unable to instanciate a Service Reference with a null service");
		}
		pService = aService;
		pSpecification = aSpecification;
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
	public Class<? extends T> getSpecification() {

		return pSpecification;
	}
}
