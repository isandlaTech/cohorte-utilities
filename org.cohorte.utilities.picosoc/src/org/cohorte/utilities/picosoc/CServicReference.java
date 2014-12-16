package org.cohorte.utilities.picosoc;

/**
 * @author ogattaz
 * 
 */
public class CServicReference<T> {

	private final T pService;

	/**
	 * @param aService
	 */
	public CServicReference(T aService) throws Exception {
		super();
		if (aService == null) {
			throw new Exception(
					"Unable to instanciate a Service Reference with a null service");
		}
		pService = aService;
	}

	/**
	 * @return
	 */
	public T getService(){
		return pService;
	}

}
