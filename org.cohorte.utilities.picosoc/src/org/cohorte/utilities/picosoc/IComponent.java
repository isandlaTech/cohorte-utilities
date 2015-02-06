package org.cohorte.utilities.picosoc;

/**
 * @author ogattaz
 * 
 */
public interface IComponent {

	/**
	 * @param aSpecification
	 * @return
	 */
	<T> boolean doesServiceExist(Class<? extends T> aSpecification);

	/**
	 * @param aSpecification
	 * @return
	 * @throws Exception
	 */
	<T> CServicReference<T> findServiceRef(Class<? extends T> aSpecification)
			throws Exception;

	/**
	 * @param aSpecification
	 * @return
	 * @throws Exception
	 */
	<T> T getOptionalService(Class<? extends T> aSpecification);

	
	/**
	 * @param aSpecification
	 * @return
	 * @throws Exception
	 */
	<T> T getService(Class<? extends T> aSpecification) throws Exception;
	
	/**
	 * @param aSpecification
	 * @return
	 * @throws Exception
	 */
	<T> CServicReference<T> getServiceRef(Class<? extends T> aSpecification)
			throws Exception;

	/**
	 * @param aSpecification
	 */
	<T> void registerMeAsService(Class<? extends T> aSpecification);

	/**
	 * @param aSpecification
	 * @param aService
	 * @return
	 * @throws Exception
	 */
	<T> CServicReference<T> registerService(Class<? extends T> aSpecification,
			T aService) throws Exception;

	/**
	 * @param aServiceRef
	 * @return
	 * @throws Exception
	 */
	<T> boolean removeService(CServicReference<T> aServiceRef) throws Exception;

}
