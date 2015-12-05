package org.cohorte.utilities.picosoc.webapp;

import java.io.File;
import java.util.logging.Level;

import org.cohorte.utilities.picosoc.CAbstractComponentBase;
import org.cohorte.utilities.picosoc.CComponentLoggerFile;

/**
 *
 * Gestion des dosseir de la Web App :
 *
 * Simple mode: only "catalina.home" and "catalina.base" proterties are declared
 *
 * => all the dirs are in the catalina_base dir
 *
 *
 * Embedded mode : the optionnel "org.cohorte.utilities.webapp.install.toolroot"
 * and "org.cohorte.utilities.webapp.install.dataroot" proterties are declared.
 *
 * => in this mode, tomcat is embeded : "catalina.home" and "catalina.base" are
 * subdirs of "toolroot" and the "logs" and "temp" dirs are subdirs of
 * "dataroot"
 *
 *
 * The supported jvm arguments
 *
 * <ul>
 * <li>-Dcatalina.base ==> OBLIGATOIRE
 * <li>-Dcatalina.home ==> OBLIGATOIRE
 * <li>-Dorg.cohorte.utilities.webapp.install.toolroot ==> optionnel
 * <li>-Dorg.cohorte.utilities.webapp.install.dataroot ==> optionnel
 * </ul>
 *
 *
 * @author ogattaz
 *
 */
public class CWebAppPaths extends CAbstractComponentBase implements
		ISvcWebAppPaths {

	private final String pPathCatalinaBase;
	private final String pPathCatalinaHome;
	private final String pPathDataRoot;
	private final String pPathToolRoot;

	/**
	 * @throws Exception
	 *             if "catalina.home" of "catalina.base" doesn't exist
	 */
	public CWebAppPaths() throws Exception {
		super();

		/*
		 * init the path of the ToolDir with the value of the system property
		 * "catalina.home"
		 */
		pPathCatalinaHome = getPathFromSysProperty(PARAM_JVM_CATALINA_HOME);
		/*
		 * init the path of the ToolDir with the value of the system property
		 * "catalina.base"
		 */
		pPathCatalinaBase = getPathFromSysProperty(PARAM_JVM_CATALINA_BASE);
		/*
		 * init the path of the ToolDir with the value of the system property
		 * "org.cohorte.utilities.webapp.install.toolroot". get
		 */
		pPathToolRoot = getPathFromSysProperty(PARAM_JVM_TOOLROOT,
				getPathCatalinaBase());
		/*
		 * init the path of the DataDir with the value of the system property
		 * "org.cohorte.utilities.webapp.install.dataroot"
		 */
		pPathDataRoot = getPathFromSysProperty(PARAM_JVM_DATAROOT,
				getPathCatalinaBase());

		// if OK
		registerMeAsService(ISvcWebAppPaths.class);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isandlatech.webapp.utilities.ISvcX3WebUtils#getDirCatalinaBase()
	 */
	@Override
	public File getDirCatalinaBase() throws Exception {
		return getDirFromPath(getPathCatalinaBase(), PARAM_JVM_CATALINA_BASE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isandlatech.webapp.utilities.ISvcX3WebUtils#getDirCatalinaHome()
	 */
	@Override
	public File getDirCatalinaHome() throws Exception {
		return getDirFromPath(getPathCatalinaHome(), PARAM_JVM_CATALINA_HOME);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isandlatech.webapp.utilities.ISvcX3WebUtils#getDirConfig()
	 */
	@Override
	public File getDirConfig() throws Exception {
		File wDir = new File(getDirDataRoot(), NAME_DIR_CONFIG);

		if (wDir == null || !wDir.exists()) {
			throwUnknwonDir(wDir, NAME_DIR_CONFIG);
		}
		return wDir;
	}

	/**
	 * @return
	 * @throws Exception
	 */
	@Override
	public File getDirCustomers() throws Exception {
		File wDir = new File(getDirDataRoot(), NAME_DIR_CUSTOMERS);

		if (wDir == null || !wDir.exists()) {
			throwUnknwonDir(wDir, NAME_DIR_CUSTOMERS);
		}
		return wDir;
	}

	/**
	 * @return
	 * @throws Exception
	 */
	@Override
	public File getDirDataRoot() throws Exception {
		return getDirFromPath(getPathDataRoot(), PARAM_JVM_DATAROOT);
	}

	/**
	 * @param aSysPropId
	 * @return
	 * @throws Exception
	 */
	private File getDirFromPath(String aPath, String aSyspropId)
			throws Exception {

		File wFile = new File(aPath);
		if (wFile == null || !wFile.exists() || !wFile.isDirectory())
			throwUnknwonDir(wFile, aSyspropId);
		return wFile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isandlatech.webapp.utilities.ISvcX3WebUtils#getDirLogs()
	 */
	@Override
	public File getDirLogs() throws Exception {
		File wDir = new File(getDirDataRoot(), NAME_DIR_LOGS);

		if (!wDir.exists()) {
			boolean wDirCreated = wDir.mkdirs();
			CComponentLoggerFile.logInMain(Level.INFO, CWebAppPaths.class,
					"getDirLogs", "Dir=[%s] Created=[%b]",
					wDir.getAbsolutePath(), wDirCreated);
		}
		return wDir;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.isandlatech.webapp.utilities.ISvcX3WebUtils#getDirLogs(java.lang.
	 * String)
	 */
	@Override
	public File getDirLogs(String aWebAppName) throws Exception {
		File wDir = new File(getDirLogs(), aWebAppName);

		if (!wDir.exists()) {
			boolean wDirCreated = wDir.mkdirs();
			CComponentLoggerFile.logInMain(Level.INFO, CWebAppPaths.class,
					"getDirLogs", "Dir=[%s] Created=[%b]",
					wDir.getAbsolutePath(), wDirCreated);
		}
		return wDir;
	}

	/**
	 * @return the File instance of the Logs dir of tomcat ex:
	 *         C:\SAGEX3\WEBV60\WebData\SERVERSLOGS\TOMCAT
	 * @throws Exception
	 */
	@Override
	public File getDirLogsTomcat() throws Exception {

		return getDirLogs(NAME_DIR_TOMCAT);
	}

	/**
	 * @return the File instance of the temp dir ex:
	 *         C:\SAGEX3\WEBV60\WebData\TEMP
	 * @throws Exception
	 */
	@Override
	public File getDirTemp() throws Exception {
		File wDir = new File(getDirDataRoot(), NAME_DIR_TEMP);

		if (!wDir.exists()) {
			boolean wDirCreated = wDir.mkdirs();
			CComponentLoggerFile.logInMain(Level.INFO, CWebAppPaths.class,
					"getDirTemp", "Dir=[%s] Created=[%b]",
					wDir.getAbsolutePath(), wDirCreated);
		}
		return wDir;
	}

	/**
	 * @return the File instance of the temp dir of tomcat ex:
	 *         C:\SAGEX3\WEBV60\WebData\TEMP\TOMCAT
	 * @throws Exception
	 */
	@Override
	public File getDirTempTomcat() throws Exception {

		File wDirTemp = new File(getDirTemp(), NAME_DIR_TOMCAT);
		if (!wDirTemp.exists()) {
			boolean wDirCreated = wDirTemp.mkdirs();
			CComponentLoggerFile.logInMain(Level.INFO, CWebAppPaths.class,
					"getDirTomcatTemp", "Dir=[%s] Created=[%b]",
					wDirTemp.getAbsolutePath(), wDirCreated);
		}
		return wDirTemp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isandlatech.webapp.utilities.ISvcX3WebUtils#getDirToolRoot()
	 */
	@Override
	public File getDirToolRoot() throws Exception {
		return getDirFromPath(getPathToolRoot(), PARAM_JVM_TOOLROOT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.isandlatech.webapp.utilities.ISvcX3WebUtils#getPathCatalinaBase()
	 */
	@Override
	public String getPathCatalinaBase() throws Exception {
		return pPathCatalinaBase;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.isandlatech.webapp.utilities.ISvcX3WebUtils#getPathCatalinaHome()
	 */
	@Override
	public String getPathCatalinaHome() throws Exception {
		return pPathCatalinaHome;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isandlatech.webapp.utilities.ISvcX3WebUtils#getPathDataRoot()
	 */
	@Override
	public String getPathDataRoot() throws Exception {
		return pPathDataRoot;
	}

	/**
	 * @param aSysPropId
	 * @return
	 * @throws Exception
	 */
	private String getPathFromSysProperty(String aSysPropId) throws Exception {
		return getPathFromSysProperty(aSysPropId, null);
	}

	/**
	 * @param aSysPropId
	 * @param aDefault
	 * @return
	 * @throws Exception
	 */
	private String getPathFromSysProperty(String aSysPropId, String aDefault)
			throws Exception {

		String wServerBasePath = System.getProperty(aSysPropId, aDefault);
		if (wServerBasePath == null || wServerBasePath.length() == 0)
			throwUnknownSysProp(aSysPropId);
		return wServerBasePath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isandlatech.webapp.utilities.ISvcX3WebUtils#getPathToolRoot()
	 */
	@Override
	public String getPathToolRoot() throws Exception {
		return pPathToolRoot;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.isandlatech.webapp.utilities.ISvcX3WebUtils#isTomcatPidAvailable()
	 */
	@Override
	public boolean isTomcatPidAvailable() throws Exception {

		File wFile = new File(getDirTempTomcat(), NAME_FILE_PID);

		boolean wExists = wFile.exists();
		CComponentLoggerFile.logInMain(Level.INFO, CWebAppPaths.class,
				"isTomcatPidAvailable", "File=[%s] exists=[%b]",
				wFile.getAbsolutePath(), wExists);

		return wExists;
	}

	/**
	 * @param aSysPropName
	 * @throws Exception
	 */
	private void throwUnknownSysProp(String aSysPropName) throws Exception {
		String wMess = String
				.format("The System Property [%1$s] is undefined or empty. Check the argument [-D%1$s=...] passed to the jvm ",
						aSysPropName);
		throw new Exception(wMess);
	}

	/**
	 * @param aDir
	 * @param aSysPropName
	 * @throws Exception
	 */
	private void throwUnknwonDir(File aDir, String aSysPropName)
			throws Exception {
		String wPath = (aDir != null) ? aDir.getAbsolutePath() : "null";
		String wMess = String
				.format("The path [%s] does'nt exist or is'nt a directory. Check the value of the argument [-D%s=...] passed to the jvm or set the path using the right setter.",
						wPath, aSysPropName);
		throw new Exception(wMess);
	}

}
