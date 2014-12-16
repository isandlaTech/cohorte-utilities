package org.cohorte.utilities.picosoc;


/**
 * @author ogattaz
 * 
 */
public interface ISvcServiceRegistry {

	/**
	 * 
	 */
	void clear();

	/**
	 * @return
	 */
	public String dump();

	/**
	 * @param aSpecification
	 * @return
	 * @throws Exception
	 */
	<T> CServicReference<T> getServiceRef(Class<? extends T> aSpecification)
			throws Exception;

	/**
	 * @param aSpecification
	 * @param aService
	 * @return the ServiceReference
	 */
	<T> CServicReference<T> registerService(Class<? extends T> aSpecification,
			T aService) throws Exception;

}
