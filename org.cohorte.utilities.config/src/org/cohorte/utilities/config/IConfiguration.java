package org.cohorte.utilities.config;


/**
 * Configuration manager service.
 * Unless a default value is provided, methods of an object implementing this
 * interface should return null in case the parameter does not exist.
 * 
 * @author Ahmad Shahwan
 * @author Bassem Debbabi
 *
 */
public interface IConfiguration {
	
	/**
	 * Default name of the current properties file that contains configuration
	 * parameters.
	 */
	String CURRENT_FILENAME = "current.properties";

	/**
	 * Default name of the default properties file that contains default
	 * configuration parameters.
	 */
	String DEFAULT_FILENAME = "default.properties";
	
	/**
	 * File extension for user-defined configuration files. 
	 */
	String USER_EXTENSION = ".user.properties";

	/**
	 * Name of the directory that contains configuration sub-directory.
	 * The sub-directory name is identical to the isolate name by default.
	 */
	String CONF_DIR = "conf";

	/**
	 * Serialize configuration.
	 *
	 * @return
	 */
	String dump();

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
	 * Get integer parameter or fall back to null.
	 * 
	 * @param aName
	 * @return
	 */
	Integer getIntergerParam(String aName);
	
	/**
	 * Get integer parameter or fall back to default value.
	 * 
	 * @param aName
	 * @param aDefault
	 * @return
	 */
	Integer getIntergerParam(String aName, Integer aDefault);
	
	/**
	 * Get integer parameter or fall back to default value.
	 * 
	 * @param aName
	 * @param aDefault
	 * @return
	 */
	int getIntergerParam(String aName, int aDefault);
	
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
	Long getLongParam(String aName, Long aDefault);
	
	/**
	 * Get long parameter or fall back to default value.
	 * 
	 * @param aName
	 * @param aDefault
	 * @return
	 */
	long getLongParam(String aName, long aDefault);
	
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
	Boolean getBooleanrParam(String aName, Boolean aDefault);
	
	/**
	 * Get boolean parameter or fall back to default value.
	 * 
	 * @param aName
	 * @param aDefault
	 * @return
	 */
	boolean getBooleanParam(String aName, boolean aDefault);
	
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
	Double getDoubleParam(String aName, Double aDefault);
	
	/**
	 * Get double parameter or fall back to default value.
	 * 
	 * @param aName
	 * @param aDefault
	 * @return
	 */
	double getDoubleParam(String aName, double aDefault);

	/**
	 * Denote whether a configuration parameter exists or not.
	 * 
	 * @param aParamName
	 * @return
	 */
	boolean hasParam(String aParamName);

}
