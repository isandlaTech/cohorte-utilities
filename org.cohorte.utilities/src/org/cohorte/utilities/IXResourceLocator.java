package org.cohorte.utilities;

/**
 * MOD_OG_20151201
 * 
 * Modelize an agent able to give how (the loader) and from where (the package) a resource cant be loaded
 * 
 * @author ogattaz
 *
 */
public interface IXResourceLocator {
	
	/**
	 * @return
	 */
	ClassLoader getResourceLoader();
	
	/**
	 * @return
	 */
	Package getResourcePackage();

}
