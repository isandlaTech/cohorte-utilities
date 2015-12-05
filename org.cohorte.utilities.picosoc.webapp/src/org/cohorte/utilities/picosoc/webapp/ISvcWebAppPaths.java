package org.cohorte.utilities.picosoc.webapp;

import java.io.File;

/**
 *
 * Les arguments de la jvm pris en compte.
 *
 * <pre>
 * -Dcatalina.base == OBLIGATOIRE - Dcatalina.home == OBLIGATOIRE
 * 		- Dorg.cohorte.utilities.webapp.install.toolroot
 * 		- Dorg.cohorte.utilities.webapp.install.dataroot
 * </pre>
 *
 * @author ogattaz
 *
 */
public interface ISvcWebAppPaths {

	public final static String NAME_DIR_CONFIG = "conf";
	public final static String NAME_DIR_CUSTOMERS = "_customer";
	public final static String NAME_DIR_LOGS = "logs";
	public final static String NAME_DIR_TEMP = "temp";

	public final static String NAME_DIR_TOMCAT = "tomcat";
	public final static String NAME_FILE_PID = "tomcat.pid";
	public final static String PARAM_JVM_CATALINA_BASE = "catalina.base";
	public final static String PARAM_JVM_CATALINA_HOME = "catalina.home";
	public final static String PARAM_JVM_DATAROOT = "org.cohorte.utilities.webapp.install.dataroot";

	public final static String PARAM_JVM_TOOLROOT = "org.cohorte.utilities.webapp.install.toolroot";

	/**
	 * @return ex: C:\SAGEX3\WEBV60\WebTools\SERVER_BASE
	 *
	 * @see -Dcatalina.base
	 *
	 * @throws Exception
	 *             if the path is not set and the system property doesn't exist
	 */
	File getDirCatalinaBase() throws Exception;

	/**
	 * @return ex: C:\SAGEX3\WEBV60\WebTools\SOFTS\TOMCAT
	 *
	 * @see -Dcatalina.home
	 *
	 * @throws Exception
	 *             if the path is not set and the system property doesn't exist
	 */
	File getDirCatalinaHome() throws Exception;

	/**
	 * @return the File instance of the "CONFIG" dir ex:
	 *         C:\SAGEX3\WEBV60\WebToolsTOOLS\ADXADMIN\CONFIG
	 * @throws Exception
	 */
	File getDirConfig() throws Exception;

	/**
	 * @return
	 * @throws Exception
	 */
	File getDirCustomers() throws Exception;

	/**
	 * @return the File instance of the "WebData" dir ex:
	 *         C:\SAGEX3\WEBV60\WebData
	 *
	 * @see PARAM_JVM_DATAROOT : "-Dadonix.x3web.install.approot" =
	 *      "all.host.installdatapath" => ex: C:\SAGEX3\WEBV60\WebData
	 *
	 * @throws Exception
	 */
	File getDirDataRoot() throws Exception;

	/**
	 * @return the File instance of the temp dir ex:
	 *         C:\SAGEX3\WEBV60\WebData\SERVERSLOGS
	 * @throws Exception
	 */
	File getDirLogs() throws Exception;

	/**
	 * @return the File instance of the temp dir ex:
	 *         C:\SAGEX3\WEBV60\WebData\SERVERSLOGS\WAWEBSERVER
	 * @throws Exception
	 */
	File getDirLogs(String aWebAppName) throws Exception;

	/**
	 * @return the File instance of the Logs dir of tomcat ex:
	 *         C:\SAGEX3\WEBV60\WebData\SERVERSLOGS\TOMCAT
	 * @throws Exception
	 */
	File getDirLogsTomcat() throws Exception;

	/**
	 * @return the File instance of the temp dir ex:
	 *         C:\SAGEX3\WEBV60\WebData\TEMP
	 * @throws Exception
	 */
	File getDirTemp() throws Exception;

	/**
	 * @return the File instance of the temp dir of tomcat ex:
	 *         C:\SAGEX3\WEBV60\WebData\TEMP\TOMCAT
	 * @throws Exception
	 */
	File getDirTempTomcat() throws Exception;

	/**
	 * @return the File instance of the "WebTools" dir ex:
	 *         C:\SAGEX3\WEBV60\WebTools
	 *
	 * @see PARAM_JVM_TOOLROOT : "-Dadonix.x3web.install.progroot" =
	 *      "all.host.installtoolspath" => ex: C:\SAGEX3\WEBV60\WebTools
	 *
	 * @throws Exception
	 */
	File getDirToolRoot() throws Exception;

	/**
	 * @return the path of the "CatalinaBase" dir ex:
	 *         C:\SAGEX3\WEBV60\WebTools\SERVER_BASE
	 * @throws Exception
	 *             if the path is not set and the system property doesn't exist
	 */
	String getPathCatalinaBase() throws Exception;

	/**
	 * @return the path of the "CatalinaHome" dir ex:
	 *         C:\SAGEX3\WEBV60\WebTools\SOFTS\TOMCAT
	 * @throws Exception
	 *             if the path is not set and the system property doesn't exist
	 */
	String getPathCatalinaHome() throws Exception;

	/**
	 * @return the path of the "WebData" dir ex: ex: C:\SAGEX3\WEBV60\WebData
	 * @throws Exception
	 *             if the path is not set and the system property doesn't exist
	 */
	String getPathDataRoot() throws Exception;

	/**
	 * @return the path of the "WebTools" dir ex: C:\SAGEX3\WEBV60\WebTools
	 * @throws Exception
	 *             if the path is not set and the system property doesn't exist
	 */
	String getPathToolRoot() throws Exception;

	/**
	 * @return
	 * @throws Exception
	 */
	boolean isTomcatPidAvailable() throws Exception;

}
