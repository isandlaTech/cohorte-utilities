package org.cohorte.utilities.picosoc.config;

import java.io.File;
import java.util.Properties;

/**
 * @author ogattaz
 *
 */
public interface ISvcWebAppProperties {

	/**
	 * @return
	 */
	String dumpProperties();

	/**
	 * @return the File representig the config base file
	 *         "catalina.base/conf/myconfig.base.propoerties.xml"
	 */
	File getConfigBaseFile();

	/**
	 * @return the name of the config base file eg.
	 *         "myconfig.base.propoerties.xml"
	 */
	String getConfigBaseFileName();

	/**
	 * @return the the File representig the config dir. eg. "catalina.base/conf"
	 */
	File getConfigDir();

	/**
	 * @return the File representig the config base file
	 *         "catalina.base/conf/myconfig.propoerties.xml"
	 */
	File getConfigFile();

	/**
	 * @return the name of the config base file eg. "myconfig.propoerties.xml"
	 */
	String getConfigFileName();

	/**
	 * @return the properties instance containing the properties defined in the
	 *         config base file (eg. "myconfig.base.propoerties.xml") and
	 *         overwritted by the definitions declared in the config file (eg.
	 *         "myconfig.propoerties.xml")
	 */
	public Properties getProperties();

	/**
	 * @param aPropertyName
	 * @return
	 */
	String getProperty(final String aPropertyName);

	/**
	 * @param aPropertyName
	 * @param aDefault
	 * @return
	 */
	String getProperty(final String aPropertyName, final String aDefault);

	/**
	 * @param aPropertyName
	 * @return
	 */
	String getPropertyB64(final String aPropertyName);

	/**
	 * @param aPropertyName
	 * @param aDefault
	 * @return
	 */
	String getPropertyB64(final String aPropertyName, final String aDefault);

	/**
	 * @param aPropertyName
	 * @return
	 */
	boolean getPropertyBool(final String aPropertyName);

	/**
	 * @param aPropertyName
	 * @param aDefault
	 * @return
	 */
	boolean getPropertyBool(final String aPropertyName, final String aDefault);

	/**
	 * @param aPropertyName
	 * @return
	 */
	int getPropertyInt(final String aPropertyName);

	/**
	 * @param aPropertyName
	 * @param aDefault
	 * @return
	 */
	int getPropertyInt(final String aPropertyName, final String aDefault);

	/**
	 * @param aPropertyName
	 * @param aSeparator
	 * @return
	 */
	String[] getPropertyArray(final String aPropertyName,
			final String aSeparator);

	/**
	 * @param aPropertyName
	 * @param aSeparator
	 * @param aDefault
	 * @return
	 */
	String[] getPropertyArray(final String aPropertyName,
			final String aSeparator, final String[] aDefault);

	/**
	 * @return
	 */
	int size();

}
