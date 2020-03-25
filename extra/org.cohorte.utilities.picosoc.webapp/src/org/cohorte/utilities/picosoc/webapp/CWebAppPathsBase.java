package org.cohorte.utilities.picosoc.webapp;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;

import org.cohorte.utilities.picosoc.CAbstractComponentBase;
import org.cohorte.utilities.picosoc.CComponentLoggerFile;
import org.psem2m.utilities.CXException;
import org.psem2m.utilities.CXStringUtils;

/**
 * WebApp dirs managment
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
 * <li>-Dorg.cohorte.utilities.webapp.install.toolroot ==> optional
 * <li>-Dorg.cohorte.utilities.webapp.install.dataroot ==> optional
 * </ul>
 *
 *
 * @author ogattaz
 *
 */
public class CWebAppPathsBase extends CAbstractComponentBase implements ISvcWebAppPaths {

	private static final String DUMP_FORMAT = "\n%30s=[%s]";
	/**
	 * @param aSysPropId
	 * @return
	 * @throws Exception
	 */
	private static String getPathFromSysProperty(String aSysPropId) throws Exception {
		return getPathFromSysProperty(aSysPropId, null);
	}
	/**
	 * @param aSysPropId
	 * @param aDefault
	 * @return
	 * @throws Exception
	 */
	private static String getPathFromSysProperty(String aSysPropId, String aDefault) throws Exception {

		final String wServerBasePath = System.getProperty(aSysPropId, aDefault);
		if (wServerBasePath == null || wServerBasePath.length() == 0) {
			throwUnknownSysProp(aSysPropId);
		}
		return wServerBasePath;
	}
	
	/**
	 * @param aStr
	 * @return
	 */
	private static boolean isNullOrEmpty(final String aStr) {
		return aStr == null || aStr.isEmpty();
	}
	/**
	 * @return
	 * @throws Exception
	 */
	public static String retrievePathCatalinaBase() throws Exception {
		return getPathFromSysProperty(PARAM_JVM_CATALINA_BASE);
	}
	
	
	/**
	 * @return
	 * @throws Exception
	 */
	public static String retrievePathCatalinaHome() throws Exception {
		return getPathFromSysProperty(PARAM_JVM_CATALINA_HOME);
	}

	/**
	 * @param aPathLabel
	 * @throws Exception
	 */
	private static void throwPathNullOrEmpty(String aPathLabel) throws Exception {
		final String wMess = String.format(
				"The path [%s] is null or empty",
				aPathLabel);
		throw new Exception(wMess);
	}

	/**
	 * @param aSysPropName
	 * @throws Exception
	 */
	private static void throwUnknownSysProp(String aSysPropName) throws Exception {
		final String wMess = String.format(
				"The System Property [%1$s] is undefined or empty. Check the argument [-D%1$s=...] passed to the jvm ",
				aSysPropName);
		throw new Exception(wMess);
	}

	/**
	 * @param aDir
	 * @param aSysPropName
	 * @throws Exception
	 */
	private static void throwUnknwonDir(File aDir, String aSysPropName) throws Exception {
		final String wPath = (aDir != null) ? aDir.getAbsolutePath() : "null";
		final String wMess = String.format(
				"The path [%s] does'nt exist or is'nt a directory. Check the value of the argument [-D%s=...] passed to the jvm or set the path using the right setter.",
				wPath, aSysPropName);
		throw new Exception(wMess);
	}

	/**
	 * @param aPath
	 * @param aPathInfo
	 * @return 
	 * @throws Exception
	 */
	private static String validPath(final String aPath,final String aPathInfo) throws Exception{
		if ( isNullOrEmpty(aPath)) {
			throwPathNullOrEmpty(aPathInfo);
		}
		return aPath;
	}

	private final String pPathCatalinaBase;

	private final String pPathCatalinaHome;

	private final String pPathDataRoot;
	
	private final String pPathToolRoot;

	/**
	 * @throws Exception
	 *             if "catalina.home" of "catalina.base" doesn't exist
	 */
	public CWebAppPathsBase() throws Exception {
		this(

		/*
		 * init  pPathCatalinaHome with the value of the system property
		 * "catalina.home"
		 */
		retrievePathCatalinaHome(),
		/*
		 * init pPathCatalinaBase with the value of the system property
		 * "catalina.base"
		 */
		retrievePathCatalinaBase(),
		/*
		 * init the path of the Toolroot dir with the value of the system property
		 * "org.cohorte.utilities.webapp.install.toolroot".
		 * 
		 * if this system property does'nt exist use the CatalinaBase dir
		 */
		 getPathFromSysProperty(PARAM_JVM_TOOLROOT, retrievePathCatalinaBase()),
		/*
		 * init the path of the DataRoot dir with the value of the system property
		 * "org.cohorte.utilities.webapp.install.dataroot"
		 * 
		 * if this system property does'nt exist use the CatalinaBase dir
		 */
		 getPathFromSysProperty(PARAM_JVM_DATAROOT, retrievePathCatalinaBase()));
	}

	
	
	/**
	 * @param aPathCatalinaHome
	 * @param aPathCatalinaBase
	 * @throws Exception
	 */
	protected CWebAppPathsBase  (final String aPathCatalinaHome, final String aPathCatalinaBase) throws Exception{
		this(aPathCatalinaHome,aPathCatalinaBase,aPathCatalinaBase,aPathCatalinaBase);
	}

	/**
	 * @param aPathCatalinaHome
	 * @param aPathCatalinaBase
	 * @param aPathToolRootPath
	 * @param aPathDataRootPath
	 * @throws Exception
	 */
	protected CWebAppPathsBase  (final String aPathCatalinaHome, final String aPathCatalinaBase,final String aPathToolRootPath,final String aPathDataRootPath) throws Exception{
		super();

		pPathCatalinaHome = validPath(aPathCatalinaHome,"CatalinaHome");

		pPathCatalinaBase = validPath(aPathCatalinaBase,"CatalinaBase");

		pPathToolRoot = isNullOrEmpty(aPathToolRootPath)?aPathCatalinaBase : validPath(aPathToolRootPath,"ToolRoot");

		pPathDataRoot = isNullOrEmpty(aPathDataRootPath)?aPathCatalinaBase :validPath( aPathDataRootPath,"DataRoot");

		// if OK
		registerMeAsService(ISvcWebAppPaths.class);
	}

	/**
	 * @param aSB
	 * @param aLabel
	 * @param aMethodName
	 * @return
	 */
	private StringBuilder dumpOnePathInSB(final StringBuilder aSB, final String aLabel, final String aMethodName) {
		
		String wValue=null;
		try {
			Method wMethod = getClass().getMethod(aMethodName);
			wValue = String.valueOf(wMethod.invoke(this));
		} catch (Exception | Error e) {
			Throwable wThrowable = (e instanceof InvocationTargetException)? ((InvocationTargetException)e).getTargetException() : e;
			wValue= CXException.eUserMessagesInString(wThrowable);
		}
		CXStringUtils.appendFormatStrInBuff(aSB, DUMP_FORMAT, aLabel,wValue);
		return aSB;
	}
		
	/**
	 * @return the dump of the paths of the WebApp
	 */
	public String dumpPaths() {
		StringBuilder wSB = new StringBuilder();
		
		CXStringUtils.appendFormatStrInBuff(wSB, DUMP_FORMAT, "CatalinaHome",pPathCatalinaHome);
		CXStringUtils.appendFormatStrInBuff(wSB, DUMP_FORMAT, "CatalinaBase",pPathCatalinaBase);
		CXStringUtils.appendFormatStrInBuff(wSB, DUMP_FORMAT, "DataRoot",pPathDataRoot);
		CXStringUtils.appendFormatStrInBuff(wSB, DUMP_FORMAT, "ToolRoot",pPathToolRoot);
		

		dumpOnePathInSB(wSB,"DirConfig","getDirConfig");
		dumpOnePathInSB(wSB,"DirLogs","getDirLogs");
		dumpOnePathInSB(wSB,"DirLogsTomcat","getDirLogsTomcat");
		dumpOnePathInSB(wSB,"DirTemp","getDirTemp");
		dumpOnePathInSB(wSB,"DirTempTomcat","getDirTempTomcat");
		dumpOnePathInSB(wSB,"DirCustomers","getDirCustomers");

		return wSB.toString();
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
	 * MOD APISU - add subdirectory application in case of docker container
	 * (non-Javadoc)
	 *
	 * @see com.isandlatech.webapp.utilities.ISvcX3WebUtils#getDirConfig()
	 */
	@Override
	public File getDirConfig() throws Exception {

		String wConfDir = NAME_DIR_CONFIG + File.separatorChar + NAME_SUBDIR_CONFIG_DOCKER;

		File wDir = new File(getDirDataRoot(), wConfDir);
		if (wDir == null || !wDir.exists()) {
			wDir = new File(getDirDataRoot(), NAME_DIR_CONFIG);

		}
		if (wDir == null || !wDir.exists()) {
			throwUnknwonDir(wDir, wConfDir);
		}
		return wDir;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cohorte.utilities.picosoc.webapp.ISvcWebAppPaths#getDirConfig(java.lang.
	 * String)
	 */
	@Override
	public File getDirConfig(String... aSubPaths) throws Exception {

		File wDir = getSubDir(getDirConfig(), aSubPaths);

		if (!wDir.exists()) {
			final boolean wDirCreated = wDir.mkdirs();
			CComponentLoggerFile.logInMain(Level.INFO, CWebAppPathsBase.class, "getDirConfig", "Dir=[%s] Created=[%b]",
					wDir.getAbsolutePath(), wDirCreated);
		}
		return wDir;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cohorte.utilities.picosoc.webapp.ISvcWebAppPaths#getDirCustomers()
	 */
	@Override
	public File getDirCustomers() throws Exception {
		final File wDir = new File(getDirDataRoot(), NAME_DIR_CUSTOMERS);

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
	private File getDirFromPath(String aPath, String aSyspropId) throws Exception {

		final File wFile = new File(aPath);
		if (wFile == null || !wFile.exists() || !wFile.isDirectory()) {
			throwUnknwonDir(wFile, aSyspropId);
		}
		return wFile;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.isandlatech.webapp.utilities.ISvcX3WebUtils#getDirLogs()
	 */
	@Override
	public File getDirLogs() throws Exception {
		final File wDir = new File(getDirDataRoot(), NAME_DIR_LOGS);

		if (!wDir.exists()) {
			final boolean wDirCreated = wDir.mkdirs();
			CComponentLoggerFile.logInMain(Level.INFO, CWebAppPathsBase.class, "getDirLogs", "Dir=[%s] Created=[%b]",
					wDir.getAbsolutePath(), wDirCreated);
		}
		return wDir;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.isandlatech.webapp.utilities.ISvcX3WebUtils#getDirLogs(java.lang.
	 * String)
	 */
	@Override
	public File getDirLogs(String... aSubPaths) throws Exception {
		final File wDir = getSubDir(getDirLogs(), aSubPaths);

		if (!wDir.exists()) {
			final boolean wDirCreated = wDir.mkdirs();
			CComponentLoggerFile.logInMain(Level.INFO, CWebAppPathsBase.class, "getDirLogs", "Dir=[%s] Created=[%b]",
					wDir.getAbsolutePath(), wDirCreated);
		}
		return wDir;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cohorte.utilities.picosoc.webapp.ISvcWebAppPaths#getDirLogsTomcat()
	 */
	@Override
	public File getDirLogsTomcat() throws Exception {

		return new File(getDirCatalinaBase(), NAME_DIR_LOGS);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.cohorte.utilities.picosoc.webapp.ISvcWebAppPaths#getDirTemp()
	 */
	@Override
	public File getDirTemp() throws Exception {
		final File wDir = new File(getDirDataRoot(), NAME_DIR_TEMP);

		if (!wDir.exists()) {
			final boolean wDirCreated = wDir.mkdirs();
			CComponentLoggerFile.logInMain(Level.INFO, CWebAppPathsBase.class, "getDirTemp", "Dir=[%s] Created=[%b]",
					wDir.getAbsolutePath(), wDirCreated);
		}
		return wDir;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.cohorte.utilities.picosoc.webapp.ISvcWebAppPaths#getDirTempTomcat()
	 */
	@Override
	public File getDirTempTomcat() throws Exception {

		final File wDirTemp = new File(getDirTemp(), NAME_DIR_TOMCAT);
		if (!wDirTemp.exists()) {
			final boolean wDirCreated = wDirTemp.mkdirs();
			CComponentLoggerFile.logInMain(Level.INFO, CWebAppPathsBase.class, "getDirTomcatTemp", "Dir=[%s] Created=[%b]",
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
	 * @see com.isandlatech.webapp.utilities.ISvcX3WebUtils#getPathCatalinaBase()
	 */
	@Override
	public String getPathCatalinaBase() throws Exception {
		return pPathCatalinaBase;
	}
	/*
	 * (non-Javadoc)
	 *
	 * @see com.isandlatech.webapp.utilities.ISvcX3WebUtils#getPathCatalinaHome()
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

	
	/*
	 * (non-Javadoc)
	 *
	 * @see com.isandlatech.webapp.utilities.ISvcX3WebUtils#getPathToolRoot()
	 */
	@Override
	public String getPathToolRoot() throws Exception {
		return pPathToolRoot;
	}
	
	/**
	 * @param aDir
	 * @param aSubPaths
	 * @return
	 * @throws Exception
	 */
	private File getSubDir(File aDir, final String... aSubPaths) throws Exception {

		if (!aDir.isDirectory()) {
			throw new Exception(String.format("The passed dir isn't a directory", aDir));
		}

		File wFile = aDir;

		if (aSubPaths != null) {
			for (String wSubPath : aSubPaths) {
				if (wSubPath != null && !wSubPath.isEmpty()) {
					wFile = new File(wFile, wSubPath);
				}
			}
		}
		return wFile;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.isandlatech.webapp.utilities.ISvcX3WebUtils#isTomcatPidAvailable()
	 */
	@Override
	public boolean isTomcatPidAvailable() throws Exception {

		final File wFile = new File(getDirTempTomcat(), NAME_FILE_PID);

		final boolean wExists = wFile.exists();
		CComponentLoggerFile.logInMain(Level.INFO, CWebAppPathsBase.class, "isTomcatPidAvailable", "File=[%s] exists=[%b]",
				wFile.getAbsolutePath(), wExists);

		return wExists;
	}

}
