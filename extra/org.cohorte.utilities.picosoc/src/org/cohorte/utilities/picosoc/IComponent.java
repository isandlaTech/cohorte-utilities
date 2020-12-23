package org.cohorte.utilities.picosoc;

import java.util.List;
import java.util.Map;

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
	 * @param aProperties
	 * @return
	 */
	<T> boolean doesServiceExist(Class<? extends T> aSpecification,
			final Map<String, String> aProperties);

	/**
	 * @param aSpecification
	 * @return
	 * @throws Exception
	 */
	<T> CServicReference<T> findServiceRef(Class<? extends T> aSpecification);

	/**
	 * @param aSpecification
	 * @param aProperties
	 * @return
	 * @throws Exception
	 */
	<T> CServicReference<T> findServiceRef(Class<? extends T> aSpecification,
			final Map<String, String> aProperties);

	/**
	 * @param aSpecification
	 * @return
	 * @throws Exception
	 */
	<T> T getOptionalService(Class<? extends T> aSpecification);

	/**
	 * @param aSpecification
	 * @param aProperties
	 * @return
	 */
	<T> T getOptionalService(Class<? extends T> aSpecification,
			final Map<String, String> aProperties);

	/**
	 * @param aSpecification
	 * @return
	 * @throws Exception
	 */
	<T> T getService(Class<? extends T> aSpecification) throws Exception;

	/**
	 * @param aSpecification
	 * @param aProperties
	 * @return
	 * @throws Exception
	 */
	<T> T getService(Class<? extends T> aSpecification,
			final Map<String, String> aProperties) throws Exception;

	/**
	 * @param aSpecification
	 * @return
	 * @throws Exception
	 */
	<T> CServicReference<T> getServiceRef(Class<? extends T> aSpecification)
			throws Exception;

	/**
	 * @param aSpecification
	 * @param aProperties
	 * @return
	 * @throws Exception
	 */
	<T> CServicReference<T> getServiceRef(Class<? extends T> aSpecification,
			final Map<String, String> aProperties) throws Exception;

	/**
	 * @param aSpecification
	 * @return
	 * @throws Exception
	 */
	<T> List<CServicReference<T>> getServiceRefs(
			Class<? extends T> aSpecification);

	/**
	 * @param aSpecification
	 * @return
	 * @throws Exception
	 */
	<T> List<CServicReference<T>> getServiceRefs(
			Class<? extends T> aSpecification,
			final Map<String, String> aProperties);
	/**
	 * @param aSpecification
	 * @param aProperties
	 * @param aStrictMode
	 * @return
	 */
	<T> List<CServicReference<T>> getServiceRefs(
			Class<? extends T> aSpecification,
			final Map<String, String> aProperties,final boolean aStrictMode);
	/**
	 * @param aSpecification
	 */
	<T> void registerMeAsService(Class<? extends T> aSpecification);

	/**
	 * @param aSpecification
	 * @param aProperties
	 */
	<T> void registerMeAsService(Class<? extends T> aSpecification,
			final Map<String, String> aProperties);

	/**
	 * @param aSpecification
	 * @param aProperties
	 * @param aService
	 * @return
	 * @throws Exception
	 */
	<T> CServicReference<T> registerService(Class<? extends T> aSpecification,
			final Map<String, String> aProperties, T aService) throws Exception;

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

	/**
	 * @return
	 */
	<T> boolean unregisterMe();

}
