package org.cohorte.utilities.picosoc.webapp;

import java.util.logging.Level;

import org.apache.catalina.loader.WebappClassLoader;
import org.cohorte.utilities.picosoc.CAbstractComponentBase;
import org.cohorte.utilities.picosoc.CComponentLoggerFile;
import org.cohorte.utilities.picosoc.config.ISvcWebAppProperties;
import org.psem2m.utilities.files.CXFileDir;

/**
 * @author ogattaz
 *
 */
public abstract class CWebAppBase extends CAbstractComponentBase implements ISvcWebApp {

	/**
	 *
	 * @return the ContextPath
	 */
	private static String retreiveWebAppName(final String aName) {
		String wWebAppNane = aName;

		ClassLoader wCurrentClassLoader = CWebAppBase.class.getClassLoader();

		boolean wIsWebappClassLoader = (wCurrentClassLoader instanceof WebappClassLoader);
		if (wIsWebappClassLoader) {
			wWebAppNane = ((WebappClassLoader) wCurrentClassLoader).getContextName();
		}
		// keep only alphanumeric chararcters and underscore '_'
		// @see
		// http://stackoverflow.com/questions/1805518/replacing-all-non-alphanumeric-characters-with-empty-strings
		wWebAppNane = wWebAppNane.replaceAll("[^A-Za-z0-9_]", "");

		CComponentLoggerFile.logInMain(Level.INFO, CWebAppBase.class, "retreiveWebAppName",
				"IsWebappClassLoader=[%s] WebAppNane=[%s]", wIsWebappClassLoader, wWebAppNane);

		return wWebAppNane;
	}

	private String pContextPath = null;

	private final String pWebAppName;

	private String pWebAppFilePath = null;

	/**
	 * @param aDefaultName
	 */
	protected CWebAppBase(String aDefaultName) {
		super();
		pWebAppName = retreiveWebAppName(aDefaultName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isandlatech.webapp.utilities.ISvcWebApp#getContextPath()
	 */
	@Override
	public String getContextPath() {
		return pContextPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isandlatech.webapp.utilities.ISvcWebApp#getNbWebAppProperties()
	 */
	@Override
	public int getNbWebAppProperties() {
		return (hasWebAppProperties()) ? getWebAppProperties().size() : -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isandlatech.x3.loadbalancer.ISvcWebAppListener#getWebappDir()
	 */
	@Override
	public CXFileDir getWebAppDir() {
		return new CXFileDir(getWebAppFilePath());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.isandlatech.x3.loadbalancer.ISvcWebAppListener#getWebappDirName()
	 */
	@Override
	public String getWebAppDirName() {

		return (hasWebAppFilePath()) ? getWebAppDir().getName() : pWebAppName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isandlatech.x3.loadbalancer.ISvcWebAppListener#getWebappPath()
	 */
	@Override
	public String getWebAppFilePath() {
		return pWebAppFilePath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isandlatech.x3.loadbalancer.ISvcWebApp#getWebAppName()
	 */
	@Override
	public String getWebAppName() {
		return  pWebAppName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cohorte.utilities.picosoc.webapp.ISvcWebApp#getWebAppProperties()
	 */
	@Override
	public abstract ISvcWebAppProperties getWebAppProperties();

	/**
	 * @return
	 */
	@Override
	public boolean hasWebAppFilePath() {
		return getWebAppFilePath() != null;
	}

	/**
	 * @return true if the WebApp properties is loaded
	 */
	@Override
	public boolean hasWebAppProperties() {
		return getWebAppProperties() != null;
	}

	/**
	 * @param aContextPath
	 */
	protected void setContextPath(String aContextPath) {
		pContextPath = aContextPath;
	}

	/**
	 * @param aWebAppFilePath
	 */
	protected void setWebAppFilePath(String aWebAppFilePath) {
		pWebAppFilePath = aWebAppFilePath;
	}

}
