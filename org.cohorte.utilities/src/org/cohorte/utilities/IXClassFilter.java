package org.cohorte.utilities;

import java.util.List;

/**
 * MOD_OG_20210427 Sharing utilities developped in Dimensions core
 * 
 * a bean describing the filtering applied during the searching of classes
 * 
 * @author ogattaz
 *
 */
public interface IXClassFilter {

	/**
	 * @param aClassNameRegex
	 * @return
	 */
	IXClassFilter addClassNameRegex(final String... aClassNameRegex);

	/**
	 * @param aInterface
	 * @return the instance of IClassesFiller
	 */
	IXClassFilter addImplementedInterface(final Class<?>... aInterface);

	/**
	 * @param aPackageName
	 * @return the instance of IClassesFiller
	 */
	IXClassFilter addPackageName(final String... aPackageName);

	/**
	 * @return the list of ClassNameRegex
	 */
	List<String> getClassNameRegex();

	/**
	 * @return the mother class the class has to extend
	 */
	Class<?> getExtendedClass();

	/**
	 * @return the list of interfaces the class has to implement
	 */
	List<Class<?>> getImplementedInterfaces();

	/**
	 * @return the number of ClassName regex
	 */
	int getNbClassNameRegex();

	/**
	 * @return the number of implemented interface
	 */
	int getNbImplementedInterfaces();

	/**
	 * @return the number of package names
	 */
	int getNbPackageNames();

	/**
	 * @return the list of the packages's path the classes founder has to explore.
	 *         One path at least.
	 */
	List<String> getPackageNames();

	/**
	 * @return true if the filter contains at least one ClassName regex
	 */
	boolean hasClassNameRegex();

	/**
	 * @return true if the boolean an extended class is set
	 */
	boolean hasExtendedClass();

	/**
	 * @return true if the boolean an extended class is set
	 */
	boolean hasImplementedInterface();

	/**
	 * @param aClassName
	 * @return
	 */
	boolean isSimpleClassNameMatches(final String aClassName);

	/**
	 * @return true if the classes founder has to explore recursively the packages.
	 *         Default true.
	 */
	boolean isWithSubPackages();

	/**
	 * @param aExtendedClass
	 * @return the instance of IClassesFiller
	 */
	IXClassFilter setExtendedClass(final Class<?> aExtendedClass);

	/**
	 * set the flag isWithSubPackages to false
	 * 
	 * @return the instance of IClassesFiller
	 */
	IXClassFilter setWithoutSubPackage();

}
