package org.cohorte.utilities.config.providers;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;
import org.cohorte.utilities.config.IConfiguration;
import org.osgi.framework.BundleContext;
import org.psem2m.isolates.base.IIsolateLoggerSvc;
import org.psem2m.isolates.constants.IPlatformProperties;

/**
 * Configuration service component.
 * This component is not automatically instantiated.
 * 
 * <h3>Resources and Priorities</h3>
 * The component read configuration from the following resources. Configurations
 * pile, overriding one another, in respective order. Later resources have
 * higher priority.
 * 
 * <ol>
 * <li><strong>User-defined configuration files</strong>, those are java
 * properties files with the extension {@code pUserExtension}, that are
 * located in Cohorte data directory, under conf/{@code pSubdirName}
 * directory. Order in which user-defined configuration files are considered is
 * OS-dependent. User-defined configuration files are optional.</li>
 * <li><strong>Custom configuration file</strong>, this is a file named
 * {@code pCurrentFile}, and located in Cohorte data directory under
 * conf/{@code pSubdirName}. This file is optional.</li>
 * <li><strong>Default configuration file</strong>, this is a file named
 * {@code pDefaultFile}, and located in Cohorte base directory under
 * conf/{@code pSubdirName}. This file is optional.</li> 
 * <li><strong>System properties</strong>, those are environment variable, plus
 * properties passed through the JVM arguments.</li>
 * </ol>
 *
 * @author Ahmad Shahwan
 * @author Bassem Debbabi
 *
 */
@Component(name = CCpntPropertiesConfiguration.COMPONENT_NAME)
@Provides
public class CCpntPropertiesConfiguration implements IConfiguration {
	
	/**
	 * Service property names. The component can be parameterized using
	 * properties with those keys. 
	 * 
	 * @author Ahmad Shahwan
	 *
	 */
	public interface IServiceProperties {
		/**
		 * Custom filename service property name. This service property defines
		 * {@code pCurrentFilename}.
		 */
		String CURRENT_FILENAME = "org.cohorte.config.current.filename";
		
		/**
		 * Default filename service property name. This service property defines
		 * {@code pDefaultFilename}.
		 */
		String DEFAULT_FILENAME = "org.cohorte.config.default.filename";
		
		/**
		 * User file extension service property name. This service property
		 * defines {@code pUserExtension}.
		 */
		String USER_EXTENSION = "org.cohorte.config.user.extension";
		
		/**
		 * Sub-directory service property name. This service property defines
		 * {@code pSubdirName}.
		 */
		String SUBDIR_NAME = "org.cohorte.config.subdir.name";
	}
	
	/**
	 * Component name.
	 */
	public static final String COMPONENT_NAME =
			"Cohorte-CCpntPropertiesConfiguration-Factory";
	
	private static final String[] TRUE_VALUES = { "yes", "on" };
	
	/**
	 * Default value of {@code pCurrentFilename}.
	 */
	String CURRENT_FILENAME = "current.properties";

	/**
	 * Default value of {@code pDefaultFilename}.
	 */
	String DEFAULT_FILENAME = "default.properties";
	
	/**
	 * Default value of {@code pUserExtension}.
	 */
	String USER_EXTENSION = ".user.properties";

	/**
	 * Name of the directory that contains configuration sub-directory.
	 */
	String CONF_DIR = "conf";
	
	@Property(name=IServiceProperties.CURRENT_FILENAME)
	private String pCurrentFilename = CURRENT_FILENAME;
	
	@Property(name=IServiceProperties.DEFAULT_FILENAME)
	private String pDefaultFilename = DEFAULT_FILENAME;
	
	@Property(name=IServiceProperties.USER_EXTENSION)
	private String pUserExtension = USER_EXTENSION;
	
	@Property(name=IServiceProperties.SUBDIR_NAME)
	private String pSubdirName = null;

	/**
	 * Cohorte isolate logger.
	 */
	@Requires
	private IIsolateLoggerSvc pLogger;

	/**
	 * Final (merged) properties.
	 */
	private final Properties pProperties = new Properties();
	
	/**
	 * Bundle context.
	 */
	private final BundleContext pContext;

	/**
	 * Constructor.
	 * 
	 * @param aContext 
	 */
	public CCpntPropertiesConfiguration(final BundleContext aContext) {
		this.pContext = aContext;
	}

	/**
	 * Cleanup properties
	 */
	private void clean() {
		pProperties.clear();
	}

	@Override
	public String dump() {
		StringBuilder wResult = new StringBuilder();
		for (Object wKey : pProperties.keySet()) {
			wResult.append(String.format("%25s=[%s]\n", wKey,
					pProperties.getProperty(wKey.toString())));
		}
		return wResult.toString();
	}

	@Override
	public String getParam(final String aName) {
		return pProperties.getProperty(aName);
	}

	@Override
	public String getParam(final String aName, final String aDefault) {
		return pProperties.getProperty(aName, aDefault);
	}

	@Override
	public boolean hasParam(final String aName) {
		return pProperties.containsKey(aName);
	}

	/**
	 * Called when this component is invalidated.
	 */
	@Invalidate
	public void invalidate() {
		pLogger.logDebug(this, "invalidating", "Cleaning cofig propetries.");
		clean();
		pLogger.logDebug(this, "invalidating", "Done.");
	}
	
	/**
	 * Home directory. Not null.
	 * 
	 * @return
	 */
	private File getHomeDir() {
		String wPath = this.pContext.getProperty(
				IPlatformProperties.PROP_PLATFORM_HOME);
		if (wPath == null) {
			return new File("");
		}
		File wDir = new File (wPath);
		if (!wDir.isDirectory()) {
			return new File("");
		}
		return wDir;
	}

	/**
	 * Base directory. Not null.
	 * 
	 * @return
	 */
	private File getBaseDir() {
		String wPath = this.pContext.getProperty(
				IPlatformProperties.PROP_PLATFORM_BASE);
		if (wPath == null) {
			return this.getHomeDir();
		}
		File wDir = new File (wPath);
		if (!wDir.isDirectory()) {
			return this.getHomeDir();
		}
		return wDir;
	}
	
	/**
	 * Data directory. Not null.
	 * 
	 * @return
	 */
	private File getDataDir() {
		String wPath = this.pContext.getProperty(
				IPlatformProperties.PROP_NODE_DATA_DIR);
		if (wPath == null) {
			return this.getBaseDir();
		}
		File wDir = new File (wPath);
		if (!wDir.isDirectory()) {
			return this.getBaseDir();
		}
		return wDir;
	}
	
	private String getSubdirName() {
		if (this.pSubdirName != null) {
			return this.pSubdirName;
		}
		/*
		 * Isolate name.
		 */
		return this.pContext.getProperty(IPlatformProperties.PROP_ISOLATE_NAME);
		
	}
	
	private void addFile(List<File> aList, File aFile) {
		aList.add(aFile);
		this.pLogger.logDebug(this, null,
				"Configurtation file %s added.", aFile.getAbsolutePath());
		
	}
	
	private File[] getConfigurationFiles() {
		List<File> wList = new ArrayList<>(3);
		/*
		 * Default configuration file has the following default path:
		 * <cohort-base>/conf/<isolate-name>/default.properties
		 */
		File wDefault = getBaseDir();
		wDefault = new File(wDefault, CONF_DIR);
		wDefault = new File(wDefault, getSubdirName());
		wDefault = new File(wDefault, this.pDefaultFilename);
		if (wDefault.isFile()) {
			addFile(wList, wDefault);
		} else {
			this.pLogger.logInfo(this, null,
					"Default configurtation file not found.");
		}
		File wCurrent = getDataDir();
		wCurrent = new File(wCurrent, CONF_DIR);
		wCurrent = new File(wCurrent, getSubdirName());
		if (wCurrent.isDirectory()) {
			/*
			 * List user-defined configuration files.
			 */
			File[] wUserFiles = wCurrent.listFiles(new FileFilter() {
				
				@Override
				public boolean accept(File wCandidate) {
					return wCandidate.isFile() && wCandidate.getName()
							.toLowerCase().endsWith(pUserExtension);
				}
			});
			/*
			 * Default configuration file has the following default path:
			 * <cohort-data>/conf/<isolate-name>/current.properties
			 */
			wCurrent = new File(wCurrent, this.pCurrentFilename);
			if (wCurrent.isFile()) {
				addFile(wList, wCurrent);
			}
			/*
			 * Add user-defined files, if any.
			 */
			for (File wUserFile : wUserFiles) addFile(wList, wUserFile);						
		} else {
			this.pLogger.logInfo(this, null,
					"Node configuration diretory %s not found.",
					wCurrent.getAbsolutePath());
		}
		return wList.toArray(new File[wList.size()]);
	}

	/**
	 * Called when the component is validated.
	 */
	@Validate
	public void validate() {
		this.pLogger.setLevel(IIsolateLoggerSvc.ALL);
		this.pLogger.logDebug(this, "validating",
				"Building configuration map.");
		this.pProperties.clear();
		File[] wFiles = getConfigurationFiles();
		for (File wFile : wFiles) {
			try (FileInputStream wStream = new FileInputStream(wFile)) {
				this.pProperties.load(wStream);
			} catch (IOException e) {
				this.pLogger.logSevere("validating", 
					"Error while reading configuration file %s.",
					wFile.getPath());
				// Move on to the next file.
			}
		}
		// System properties have the highest priority.
		this.pProperties.putAll(System.getProperties());
		pLogger.logDebug(this, "validating", "Configuration dump:\n%s", dump());
	}

	@Override
	public Integer getIntergerParam(String aName) {
		return this.getIntergerParam(aName, null);
	}

	@Override
	public Integer getIntergerParam(String aName, Integer aDefault) {
		String wStr = this.getParam(aName);
		if (wStr != null) {
			try {
				return Integer.parseInt(wStr);
			} catch (NumberFormatException e) {}
		}
		return aDefault;
	}

	@Override
	public int getIntergerParam(String aName, int aDefault) {
		return this.getIntergerParam(aName, new Integer(aDefault));
	}

	@Override
	public Long getLongParam(String aName) {
		return this.getLongParam(aName, null);
	}

	@Override
	public Long getLongParam(String aName, Long aDefault) {
		String wStr = this.getParam(aName);
		if (wStr != null) {
			try {
				return Long.parseLong(wStr);
			} catch (NumberFormatException e) {}
		}
		return aDefault;
	}

	@Override
	public long getLongParam(String aName, long aDefault) {
		return this.getLongParam(aName, new Long(aDefault));
	}

	@Override
	public Boolean getBooleanParam(String aName) {
		return this.getBooleanParam(aName, null);
	}

	@Override
	public Boolean getBooleanParam(String aName, Boolean aDefault) {
		String wStr = this.getParam(aName);
		if (wStr != null) {
			wStr = wStr.toLowerCase();
			if (Arrays.asList(TRUE_VALUES).contains(wStr)) {
				return true;
			}
			return Boolean.parseBoolean(wStr);
		} else {
			return aDefault;
		}
	}

	@Override
	public boolean getBooleanParam(String aName, boolean aDefault) {
		return this.getBooleanParam(aName, new Boolean(aDefault));
	}

	@Override
	public Double getDoubleParam(String aName) {
		return this.getDoubleParam(aName, null);
	}

	@Override
	public Double getDoubleParam(String aName, Double aDefault) {
		String wStr = this.getParam(aName);
		if (wStr != null) {
			try {
				return Double.parseDouble(wStr);
			} catch (NumberFormatException e) {}
		}
		return aDefault;
	}

	@Override
	public double getDoubleParam(String aName, double aDefault) {
		return this.getDoubleParam(aName, new Double(aDefault));
	}

	@Override
	public String getBasePath() {
		return this.getBaseDir().getAbsolutePath();
	}

	@Override
	public String getHomePath() {
		return this.getHomeDir().getAbsolutePath();
	}

	@Override
	public String getDataPath() {
		return this.getDataDir().getAbsolutePath();
	}
}
