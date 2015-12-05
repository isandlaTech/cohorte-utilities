package org.cohorte.utilities.picosoc.webapp;

import java.util.Properties;

import org.psem2m.utilities.files.CXFileDir;

/**
 * @author ogattaz
 * 
 */
public interface ISvcWebApp {

	/**
	 * @return
	 */
	String getContextPath();

	/**
	 * @return
	 */
	int getNbWebAppProperties();

	/**
	 * @return
	 */
	CXFileDir getWebAppDir();

	/**
	 * @return
	 */
	String getWebAppDirName();

	/**
	 * @return
	 */
	String getWebAppFilePath();

	/**
	 * @return
	 */
	String getWebAppName();

	/**
	 * @return
	 */
	Properties getWebAppProperties();

	/**
	 * @param aPropertyName
	 * @return
	 */
	String getWebAppProperty(final String aPropertyName);

	/**
	 * @param aPropertyName
	 * @param aDefault
	 * @return
	 */
	String getWebAppProperty(final String aPropertyName, final String aDefault);

	/**
	 * @param aPropertyName
	 * @return
	 */
	boolean getWebAppPropertyBool(final String aPropertyName);

	/**
	 * @param aPropertyName
	 * @param aDefault
	 * @return
	 */
	boolean getWebAppPropertyBool(final String aPropertyName,
			final String aDefault);

	/**
	 * @param aPropertyName
	 * @return
	 */
	int getWebAppPropertyInt(final String aPropertyName);

	/**
	 * @param aPropertyName
	 * @param aDefault
	 * @return
	 */
	int getWebAppPropertyInt(final String aPropertyName, final String aDefault);

	/**
	 * @return
	 */
	boolean hasProperties();
	
	/**
	 * @return
	 */
	boolean hasPropertiesXmlFile();

	/**
	 * @return
	 */
	boolean hasWebAppFilePath();

	/**
	 * @return true if the WebApp properties is loaded
	 */
	boolean hasWebAppProperties();

}
