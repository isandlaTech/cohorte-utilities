package org.cohorte.utilities.picosoc.webapp;

import org.cohorte.utilities.picosoc.config.ISvcWebAppProperties;
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
	ISvcWebAppProperties getWebAppProperties();

	/**
	 * @return
	 */
	boolean hasWebAppFilePath();

	/**
	 * @return true if the WebApp properties is loaded
	 */
	boolean hasWebAppProperties();

}
