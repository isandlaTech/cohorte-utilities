package org.cohorte.utilities.security;

/**
 * @author ogattaz
 * 
 */
public interface IXPassphrase {

	/**
	 * @return
	 */
	String getDecoded();

	/**
	 * @return
	 */
	String getEncoded();

	/**
	 * @return
	 */
	boolean isInitialyEncoded();

	/**
	 * @return
	 */
	IXPassphrase getNested();

	/**
	 * @return
	 */
	boolean hasNested();

}
