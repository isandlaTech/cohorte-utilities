package org.cohorte.utilities.picosoc;

import java.util.Map;

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
	 * @param aSpecification
	 * @return
	 */
	<T> boolean contains(final Class<? extends T> aSpecification,
			final Map<String, String> aProperties);

	/**
	 * @return
	 */
	public String dump();

	/**
	 * @param aSpecification
	 * @return an instance of CServicReference<T> if the service exists
	 * @throws Exception
	 */
	<T> CServicReference<T> findServiceRef(
			final Class<? extends T> aSpecification,
			final Map<String, String> aProperties) throws Exception;

	/**
	 * @param aSpecification
	 * @return an instance of CServicReference<T>
	 * @throws Exception
	 *             if no instance of aSpecification
	 */
	<T> CServicReference<T> getServiceRef(
			final Class<? extends T> aSpecification,
			final Map<String, String> aProperties) throws Exception;

	/**
	 * @param aSpecification
	 * @param aService
	 * @return the ServiceReference
	 */
	<T> CServicReference<T> registerService(Class<? extends T> aSpecification,
			final Map<String, String> aProperties, final T aService)
			throws Exception;

	/**
	 * @param aServiceRef
	 * @throws Exception
	 */
	<T> boolean removeService(CServicReference<T> aServiceRef) throws Exception;

}
