package org.cohorte.utilities.config;

import java.util.Properties;

/**
 * Configuration manager service. Unless a default value is provided, methods of
 * an object implementing this interface should return null in case the
 * parameter does not exist.
 *
 * @author Ahmad Shahwan
 * @author Bassem Debbabi
 *
 */
public interface IConfiguration {

	/**
	 * Serialize configuration.
	 *
	 * @return
	 */
	String dump();

	/**
	 * Path to Cohorte base directory. If none, path to Cohorte home directory.
	 * This method should not return null.
	 *
	 * @return
	 */
	String getBasePath();

	/**
	 * Get boolean parameter or fall back to null.
	 *
	 * @param aName
	 * @return
	 */
	Boolean getBooleanParam(String aName);

	/**
	 * Get boolean parameter or fall back to default value.
	 *
	 * @param aName
	 * @param aDefault
	 * @return
	 */
	boolean getBooleanParam(String aName, boolean aDefault);

	/**
	 * Get boolean parameter or fall back to default value.
	 *
	 * @param aName
	 * @param aDefault
	 * @return
	 */
	Boolean getBooleanParam(String aName, Boolean aDefault);

	/**
	 * Path to Cohorte data directory. If none, path to Cohorte base directory.
	 * This method should not return null.
	 *
	 * @return
	 */
	String getDataPath();

	/**
	 * Get double parameter or fall back to null.
	 *
	 * @param aName
	 * @return
	 */
	Double getDoubleParam(String aName);

	/**
	 * Get double parameter or fall back to default value.
	 *
	 * @param aName
	 * @param aDefault
	 * @return
	 */
	double getDoubleParam(String aName, double aDefault);

	/**
	 * Get double parameter or fall back to default value.
	 *
	 * @param aName
	 * @param aDefault
	 * @return
	 */
	Double getDoubleParam(String aName, Double aDefault);

	/**
	 * Path to Cohorte home directory. If none, path to the current directory.
	 * This method should not return null.
	 *
	 * @return
	 */
	String getHomePath();

	/**
	 * Get integer parameter or fall back to null.
	 *
	 * @param aName
	 * @return
	 */
	Integer getIntegerParam(String aName);

	/**
	 * Get integer parameter or fall back to default value.
	 *
	 * @param aName
	 * @param aDefault
	 * @return
	 */
	int getIntegerParam(String aName, int aDefault);

	/**
	 * Get integer parameter or fall back to default value.
	 *
	 * @param aName
	 * @param aDefault
	 * @return
	 */
	Integer getIntegerParam(String aName, Integer aDefault);

	/**
	 * Get long parameter or fall back to null.
	 *
	 * @param aName
	 * @return
	 */
	Long getLongParam(String aName);

	/**
	 * Get long parameter or fall back to default value.
	 *
	 * @param aName
	 * @param aDefault
	 * @return
	 */
	long getLongParam(String aName, long aDefault);

	/**
	 * Get long parameter or fall back to default value.
	 *
	 * @param aName
	 * @param aDefault
	 * @return
	 */
	Long getLongParam(String aName, Long aDefault);

	/**
	 * Get parameter as a string.
	 *
	 * @param aName
	 * @return
	 */
	String getParam(String aName);

	/**
	 * Get parameter as as string, or fall back to a default value no such a
	 * parameter is defined.
	 *
	 * @param aName
	 * @param aDefValue
	 * @return
	 */
	String getParam(String aName, String aDefValue);

	/**
	 * @param aFilter
	 *            the prefix to use to select the properties. If null or empty
	 *            => all
	 * @return
	 */
	Properties getSubset(final String aFilter);

	/**
	 * Denote whether a configuration parameter exists or not.
	 *
	 * @param aParamName
	 * @return
	 */
	boolean hasParam(String aParamName);

	/**
	 * @return the number of properties
	 */
	int size();

}
