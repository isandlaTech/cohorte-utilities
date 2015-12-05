package org.cohorte.utilities.picosoc.webapp;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Level;

import org.cohorte.utilities.picosoc.CAbstractComponentBase;
import org.cohorte.utilities.picosoc.CComponentLoggerFile;
import org.psem2m.utilities.files.CXFileDir;

/**
 * @author ogattaz
 *
 */
public abstract class CWebAppBase extends CAbstractComponentBase implements
		ISvcWebApp {

	private String pContextPath = null;

	private final String pDefaultName;

	private File pPropertiesXmlFile = null;

	private String pWebAppFilePath = null;

	private Properties pWebAppProperties = null;

	/**
	 * @param aDefaultName
	 */
	protected CWebAppBase(String aDefaultName) {
		super();
		pDefaultName = aDefaultName;
	}

	/**
	 * @return
	 */
	public String dumpWebAppProperties() {

		if (!hasWebAppProperties())
			return "No WebAppProperties available (null)";

		StringBuilder wSB = new StringBuilder();
		for (Entry<Object, Object> wProperty : getWebAppProperties().entrySet()) {
			wSB.append(String.format("%25s=%s\n",
					wProperty.getKey().toString(), wProperty.getValue()
							.toString()));
		}
		return wSB.toString();
	}

	/**
	 * @return
	 * @throws Exception
	 */
	public abstract File findConfigurationFile() throws Exception;

	/**
	 * @param aFileName
	 * @return
	 * @throws Exception
	 */
	public abstract File findConfigurationFile(String aFileName)
			throws Exception;

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

	/**
	 * <pre>
	 * C:\Users\ogattaz\workspaces\c2kernel\.metadata\.plugins\org.eclipse.wst.server.core\tmp1\wtpwebapps\AgiliumWeb\configuration\properties.xml
	 * </pre>
	 * 
	 * @return
	 */
	protected File getPropertiesXmlFile() {
		return pPropertiesXmlFile;
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

		return (hasWebAppFilePath()) ? getWebAppDir().getName() : pDefaultName;
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
		return (hasWebAppFilePath()) ? getWebAppDirName() : pDefaultName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.isandlatech.x3.loadbalancer.ISvcWebAppListener#getWebAppProperties()
	 */
	@Override
	public Properties getWebAppProperties() {
		return pWebAppProperties;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.isandlatech.x3.loadbalancer.ISvcWebAppListener#getWebAppProperty(
	 * java.lang.String)
	 */
	@Override
	public String getWebAppProperty(final String aPropertyName) {
		return getWebAppProperty(aPropertyName, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.isandlatech.x3.loadbalancer.ISvcWebAppListener#getWebAppProperty(
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public String getWebAppProperty(final String aPropertyName,
			final String aDefault) {

		if (!hasProperties()) {
			CComponentLoggerFile
					.logInMain(
							Level.INFO,
							"getWebAppProperty",
							"No WebApp Properties available [null]. PropertiesXmlFile=[%s]",
							getPropertiesXmlFile());
			return aDefault;
		}

		String wValue = getWebAppProperties().getProperty(aPropertyName);
		if (wValue == null) {
			CComponentLoggerFile.logInMain(Level.INFO, "getWebAppProperty",
					"Property [%s] doesn't exist. Default value=[%s]",
					aPropertyName, aDefault);
			wValue = aDefault;
		}
		return wValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.isandlatech.webapp.utilities.ISvcWebApp#getWebAppPropertyBool(java
	 * .lang.String)
	 */
	@Override
	public boolean getWebAppPropertyBool(final String aPropertyName) {
		return getWebAppPropertyBool(aPropertyName, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.isandlatech.webapp.utilities.ISvcWebApp#getWebAppPropertyBool(java
	 * .lang.String, java.lang.String)
	 */
	@Override
	public boolean getWebAppPropertyBool(final String aPropertyName,
			final String aDefault) {
		String wValue = getWebAppProperty(aPropertyName, aDefault);

		return "true".equalsIgnoreCase(wValue)
				|| "yes".equalsIgnoreCase(wValue)
				|| "on".equalsIgnoreCase(wValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.isandlatech.x3.loadbalancer.ISvcWebAppListener#getWebAppPropertyInt
	 * (java.lang.String)
	 */
	@Override
	public int getWebAppPropertyInt(final String aPropertyName) {
		return getWebAppPropertyInt(aPropertyName, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.isandlatech.x3.loadbalancer.ISvcWebAppListener#getWebAppPropertyInt
	 * (java.lang.String, java.lang.String)
	 */
	@Override
	public int getWebAppPropertyInt(final String aPropertyName,
			final String aDefault) {

		String wValue = getWebAppProperty(aPropertyName, aDefault);
		try {
			return Integer.parseInt(wValue);
		} catch (Throwable e) {
			CComponentLoggerFile.logInMain(Level.SEVERE,
					"getWebAppPropertyInt",
					"Property [%s], unable to retrieve int value in [%s] %s",
					aPropertyName, wValue, e);
			return -1;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isandlatech.webapp.utilities.ISvcWebApp#hasProperties()
	 */
	@Override
	public boolean hasProperties() {
		return pWebAppProperties != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isandlatech.webapp.utilities.ISvcWebApp#hasPropertiesXmlFile()
	 */
	@Override
	public boolean hasPropertiesXmlFile() {
		return pPropertiesXmlFile != null;
	}

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
		return (pWebAppProperties != null);
	}

	/**
	 * @param aFileName
	 * @return
	 * @throws Exception
	 */
	public Properties loadSepcificProperties(String aFileName) throws Exception {

		return readPropertiesXmlFile(findConfigurationFile(aFileName));
	}

	/**
	 * @param aPropertiesXmlFile
	 * @return
	 */
	protected Properties loadWebAppProperties() {

		return (hasPropertiesXmlFile()) ? readPropertiesXmlFile(getPropertiesXmlFile())
				: null;
	}

	/**
	 * @param aPropertiesXmlFile
	 * @return
	 */
	protected Properties readPropertiesXmlFile(File aPropertiesXmlFile) {

		if (aPropertiesXmlFile == null) {
			return null;
		}
		if (!aPropertiesXmlFile.exists() || !aPropertiesXmlFile.isFile()) {
			CComponentLoggerFile.logInMain(Level.SEVERE, this,
					"readPropertiesXmlFile",
					"PropertiesXmlFile [%s] doesn't exist", aPropertiesXmlFile);
			return null;
		}
		if (!aPropertiesXmlFile.isFile()) {
			CComponentLoggerFile.logInMain(Level.SEVERE, this,
					"readPropertiesXmlFile",
					"PropertiesXmlFile [%s] isn't a file", aPropertiesXmlFile);
			return null;
		}
		try {
			java.io.BufferedInputStream bin = null;
			FileInputStream in = new FileInputStream(aPropertiesXmlFile);
			Properties wProperties = new java.util.Properties();
			bin = new java.io.BufferedInputStream(in);
			wProperties.loadFromXML(bin);
			in.close();
			return wProperties;
		} catch (Exception e) {
			CComponentLoggerFile.logInMain(Level.SEVERE, this,
					"readPropertiesXmlFile", "ERROR %s", e);
		}
		return null;
	}

	/**
	 * @param aContextPath
	 */
	protected void setContextPath(String aContextPath) {
		pContextPath = aContextPath;
	}

	/**
	 * @param aPropertiesXmlFile
	 */
	protected void setPropertiesXmlFile(File aPropertiesXmlFile) {
		pPropertiesXmlFile = aPropertiesXmlFile;
	}

	/**
	 * @param aWebAppFilePath
	 */
	protected void setWebAppFilePath(String aWebAppFilePath) {
		pWebAppFilePath = aWebAppFilePath;
	}

	/**
	 * @param aWebAppFilePath
	 */
	protected void setWebAppProperties(Properties aWebAppProperties) {
		pWebAppProperties = aWebAppProperties;
	}

}
