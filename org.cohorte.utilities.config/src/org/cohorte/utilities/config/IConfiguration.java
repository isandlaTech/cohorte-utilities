package org.cohorte.utilities.config;


public interface IConfiguration {

	/**
	 * Dump Configuration.
	 *
	 * @return
	 */
	String dumpConfig();

	/**
	 * @param aParamName
	 * @return
	 */
	String getParam(String aParamName);

	/**
	 * @param aParamName
	 * @param aDefValue
	 * @return
	 */
	String getParam(String aParamName, String aDefValue);

	/**
	 * @param aParamName
	 * @return
	 */
	boolean hasParam(String aParamName);

}
