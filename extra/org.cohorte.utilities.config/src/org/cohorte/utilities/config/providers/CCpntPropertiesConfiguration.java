package org.cohorte.utilities.config.providers;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.ServiceController;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.ipojo.annotations.Validate;
import org.cohorte.utilities.config.IConfiguration;
import org.osgi.framework.BundleContext;
import org.psem2m.isolates.base.IIsolateLoggerSvc;
import org.psem2m.isolates.constants.IPlatformProperties;
import org.psem2m.utilities.files.CXFileDir;

/**
 * Configuration service component. This component is not automatically
 * instantiated.
 *
 * <h3>Resources and Priorities</h3> The component read configuration from the
 * following resources. Configurations pile, overriding one another, in
 * respective order. Later resources have higher priority.
 *
 * <ol>
 * <li><strong>User-defined configuration files</strong>, those are java
 * properties files with the extension {@code pUserExtension}, that are located
 * in Cohorte data directory, under conf/{@code pSubdirName} directory. Order in
 * which user-defined configuration files are considered is OS-dependent.
 * User-defined configuration files are optional.</li>
 * <li><strong>Custom configuration file</strong>, this is a file named
 * {@code pCurrentFile}, and located in Cohorte data directory under conf/
 * {@code pSubdirName}. This file is optional.</li>
 * <li><strong>Default configuration file</strong>, this is a file named
 * {@code pDefaultFile}, and located in Cohorte base directory under conf/
 * {@code pSubdirName}. This file is optional.</li>
 * <li><strong>System properties</strong>, those are environment variable, plus
 * properties passed through the JVM arguments.</li>
 * </ol>
 *
 * @author Ahmad Shahwan
 * @author Olivier Gattaz
 * @author Bassem Debbabi
 *
 */
@Component(name = CCpntPropertiesConfiguration.COMPONENT_NAME)
@Provides(specifications = IConfiguration.class)
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
		 * install filename service property name. This service property defines 
		 * {@code pInstallFilename}.
		 */
		String INSTALL_FILENAME = "org.cohorte.config.install.filename";

		/**
		 * Sub-directory service property name. This service property defines
		 * {@code pSubdirName}.
		 */
		String SUBDIR_NAME = "org.cohorte.config.subdir.name";

		/**
		 * User file extension service property name. This service property
		 * defines {@code pUserExtension}.
		 */
		String USER_EXTENSION = "org.cohorte.config.user.extension";
	}

	/**
	 * Component name.
	 */
	public static final String COMPONENT_NAME = "Cohorte-CCpntPropertiesConfiguration-Factory";

	/**
	 * Name of the directory that contains configuration sub-directory.
	 */
	public static final String CONF_DIR = "conf";

	/**
	 * Default value of {@code pCurrentFilename}.
	 */
	public static final String CURRENT_FILENAME = "current.properties";
	/**
	 * Default value of {@code pINstallFilename}.
	 */
	public static final String INSTALL_FILENAME = "install.properties";

	/**
	 * Default value of {@code pDefaultFilename}.
	 */
	public static final String DEFAULT_FILENAME = "default.properties";

	private static final String[] TRUE_VALUES = { "yes", "on" };

	/**
	 * Default value of {@code pUserExtension}.
	 */
	public static final String USER_EXTENSION = ".user.properties";

	/**
	 * Bundle context.
	 */
	private final BundleContext pContext;

	@ServiceController
	private boolean pController;

	@Property(name = IServiceProperties.CURRENT_FILENAME)
	private String pCurrentFilename = CURRENT_FILENAME;

	@Property(name = IServiceProperties.DEFAULT_FILENAME)
	private String pDefaultFilename = DEFAULT_FILENAME;

	@Property(name = IServiceProperties.INSTALL_FILENAME)
	private String pInstallFileName = INSTALL_FILENAME;
	/**
	 * Cohorte isolate logger.
	 */
	@Requires
	private IIsolateLoggerSvc pLogger;

	/**
	 * Service only available locally (inside the isolate).
	 */
	@ServiceProperty(name = "pelix.remote.export.reject", immutable = true)
	private final String pNoExport = IConfiguration.class.getName();

	/**
	 * Final (merged) properties.
	 */
	private final Properties pProperties = new Properties();

	@Property(name = IServiceProperties.SUBDIR_NAME)
	private String pSubdirName = null;

	@Property(name = IServiceProperties.USER_EXTENSION)
	private String pUserExtension = USER_EXTENSION;

	/**
	 * Constructor.
	 *
	 * @param aContext
	 */
	public CCpntPropertiesConfiguration(final BundleContext aContext) {
		super();
		this.pContext = aContext;
	}

	/**
	 * @param aList
	 * @param aFile
	 */
	private void addFile(List<File> aList, File aFile) {
		aList.add(aFile);
		this.pLogger.logDebug(this, "addFile", "add file=[%s]",
				aFile.getAbsolutePath());
	}

	/**
	 * Cleanup properties
	 */
	private void clean() {
		pProperties.clear();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.cohorte.utilities.config.IConfiguration#dump()
	 */
	@Override
	public String dump() {
		StringBuilder wResult = new StringBuilder();

		Map<Object, Object> wSorted = new TreeMap<Object, Object>(pProperties);

		for (Object wKey : wSorted.keySet()) {
			wResult.append(String.format("%50s=[%s]\n", wKey,
					pProperties.getProperty(wKey.toString())));
		}
		return wResult.toString();
	}

	/**
	 * Base directory. Not null.
	 *
	 * @return
	 */
	private File getBaseDir() {
		String wPath = this.pContext
				.getProperty(IPlatformProperties.PROP_PLATFORM_BASE);
		if (wPath == null) {
			return this.getHomeDir();
		}
		File wDir = new File(wPath);
		if (!wDir.isDirectory()) {
			return this.getHomeDir();
		}
		return wDir;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cohorte.utilities.config.IConfiguration#getBasePath()
	 */
	@Override
	public String getBasePath() {
		return this.getBaseDir().getAbsolutePath();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cohorte.utilities.config.IConfiguration#getBooleanParam(java.lang
	 * .String)
	 */
	@Override
	public Boolean getBooleanParam(String aName) {
		return this.getBooleanParam(aName, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cohorte.utilities.config.IConfiguration#getBooleanParam(java.lang
	 * .String, boolean)
	 */
	@Override
	public boolean getBooleanParam(String aName, boolean aDefault) {
		return this.getBooleanParam(aName, new Boolean(aDefault));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cohorte.utilities.config.IConfiguration#getBooleanParam(java.lang
	 * .String, java.lang.Boolean)
	 */
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

	/**
	 * @param aRootConfigDir
	 *            a root dir of the config dir (eg. cohorte_base or
	 *            cohorte_data)
	 * @returnt he directory where the config files are stored
	 */
	private CXFileDir getConfigDir(final File aRootConfigDir) {
		CXFileDir wConfigDir = new CXFileDir(aRootConfigDir, CONF_DIR,
				getSubdirName());
		return wConfigDir;
	}

	/**
	 * @return
	 */
	private File[] getConfigurationFiles() {

		List<File> wList = new ArrayList<>(3);
		/*
		 * Default configuration file is in the directory :
		 * <cohorte-base>/conf/<isolate-name>
		 */
		File wDefaultDir = getConfigDir(getBaseDir());

		/*
		 * Default configuration file has the following default path:
		 * <cohorte-base>/conf/<isolate-name>/default.properties
		 */
		File wDefaultFile = new File(wDefaultDir, this.pDefaultFilename);

		// WARNING if the 'default.properties' file doen't exist !
		if (!wDefaultFile.isFile()) {
			pLogger.logWarn(this, "getConfigurationFiles",
					"The 'default' config file isn't found : [%s]",
					wDefaultFile);
		}
		// add the "default.properties" in the list
		else {
			addFile(wList, wDefaultFile);
		}
		/*
		 * install configuration file has the following default path:
		 * <cohorte-base>/conf/<isolate-name>/install.properties
		 */
		File wInstallFile = new File(wDefaultDir, this.pInstallFileName);

		// WARNING if the 'default.properties' file doen't exist !
		if (!wInstallFile.isFile()) {
			pLogger.logWarn(this, "getConfigurationFiles",
					"The 'install' config file isn't found : [%s]",
					wInstallFile);
		}
		// add the "install.properties" in the list
		else {
			addFile(wList, wInstallFile);
		}

		
		/*
		 * Current configuration files are in the directory :
		 * <cohorte-data>/conf/<isolate-name>
		 * 
		 * if the framework properties "cohorte.node.data.dir" isn't defined,
		 * the wExtendDir is the same as the wDefaultDir
		 */
		File wExtendDir = getConfigDir(getDataDir());

		// WARNING if '<cohort-data>/conf/<isolate-name>' doen't exist !
		if (!wExtendDir.isDirectory()) {
			pLogger.logWarn(this, "getConfigurationFiles",
					"The 'extends' config dir isn't found : [%s]", wExtendDir);
		}
		// try to load current file and the user config files
		else {
			/*
			 * Current configuration file has the following default path:
			 * <cohort-data>/conf/<isolate-name>/current.properties
			 */
			File wCurrentFile = new File(wExtendDir, this.pCurrentFilename);

			// WARNING if current.properties doen't exist !
			if (!wCurrentFile.isFile()) {
				pLogger.logWarn(this, "getConfigurationFiles",
						"The 'current' config file isn't found : [%s]",
						wCurrentFile);
			}
			// add the "current.properties" in the list if exists
			else {
				addFile(wList, wCurrentFile);
			}

			/*
			 * List user-defined configuration files.
			 */
			File[] wUserFiles = wExtendDir.listFiles(new FileFilter() {

				@Override
				public boolean accept(File wCandidate) {
					return wCandidate.isFile()
							&& wCandidate.getName().toLowerCase()
									.endsWith(pUserExtension);
				}
			});
			/*
			 * Add user-defined files, if any.
			 */
			for (File wUserFile : wUserFiles) {
				addFile(wList, wUserFile);
			}
		}
		return wList.toArray(new File[wList.size()]);
	}

	/**
	 * Data directory. Not null.
	 *
	 * @return
	 */
	private File getDataDir() {
		String wPath = this.pContext
				.getProperty(IPlatformProperties.PROP_NODE_DATA_DIR);
		if (wPath == null) {
			return this.getBaseDir();
		}
		File wDir = new File(wPath);
		if (!wDir.isDirectory()) {
			return this.getBaseDir();
		}
		return wDir;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.cohorte.utilities.config.IConfiguration#getDataPath()
	 */
	@Override
	public String getDataPath() {
		return this.getDataDir().getAbsolutePath();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.cohorte.utilities.config.IConfiguration#getDoubleParam(java.lang.
	 * String)
	 */
	@Override
	public Double getDoubleParam(String aName) {
		return this.getDoubleParam(aName, null);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.cohorte.utilities.config.IConfiguration#getDoubleParam(java.lang.
	 * String, double)
	 */
	@Override
	public double getDoubleParam(String aName, double aDefault) {
		return this.getDoubleParam(aName, new Double(aDefault));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.cohorte.utilities.config.IConfiguration#getDoubleParam(java.lang.
	 * String, java.lang.Double)
	 */
	@Override
	public Double getDoubleParam(String aName, Double aDefault) {
		String wStr = this.getParam(aName);
		if (wStr != null) {
			try {
				return Double.parseDouble(wStr);
			} catch (NumberFormatException e) {
			}
		}
		return aDefault;
	}

	/**
	 * Home directory. Not null.
	 *
	 * @return
	 */
	private File getHomeDir() {
		String wPath = this.pContext
				.getProperty(IPlatformProperties.PROP_PLATFORM_HOME);
		if (wPath == null) {
			return new File("");
		}
		File wDir = new File(wPath);
		if (!wDir.isDirectory()) {
			return new File("");
		}
		return wDir;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.cohorte.utilities.config.IConfiguration#getHomePath()
	 */
	@Override
	public String getHomePath() {
		return this.getHomeDir().getAbsolutePath();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.cohorte.utilities.config.IConfiguration#getIntegerParam(java.lang
	 * .String)
	 */
	@Override
	public Integer getIntegerParam(String aName) {
		return this.getIntegerParam(aName, null);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.cohorte.utilities.config.IConfiguration#getIntegerParam(java.lang
	 * .String, int)
	 */
	@Override
	public int getIntegerParam(String aName, int aDefault) {
		return this.getIntegerParam(aName, new Integer(aDefault));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.cohorte.utilities.config.IConfiguration#getIntegerParam(java.lang
	 * .String, java.lang.Integer)
	 */
	@Override
	public Integer getIntegerParam(String aName, Integer aDefault) {
		String wStr = this.getParam(aName);
		if (wStr != null) {
			try {
				return Integer.parseInt(wStr);
			} catch (NumberFormatException e) {
			}
		}
		return aDefault;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.cohorte.utilities.config.IConfiguration#getLongParam(java.lang.String
	 * )
	 */
	@Override
	public Long getLongParam(String aName) {
		return this.getLongParam(aName, null);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.cohorte.utilities.config.IConfiguration#getLongParam(java.lang.String
	 * , long)
	 */
	@Override
	public long getLongParam(String aName, long aDefault) {
		return this.getLongParam(aName, new Long(aDefault));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.cohorte.utilities.config.IConfiguration#getLongParam(java.lang.String
	 * , java.lang.Long)
	 */
	@Override
	public Long getLongParam(String aName, Long aDefault) {
		String wStr = this.getParam(aName);
		if (wStr != null) {
			try {
				return Long.parseLong(wStr);
			} catch (NumberFormatException e) {
			}
		}
		return aDefault;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.cohorte.utilities.config.IConfiguration#getParam(java.lang.String)
	 */
	@Override
	public String getParam(final String aName) {
		return pProperties.getProperty(aName);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.cohorte.utilities.config.IConfiguration#getParam(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public String getParam(final String aName, final String aDefault) {
		return pProperties.getProperty(aName, aDefault);
	}

	/**
	 * @return
	 */
	private String getSubdirName() {
		if (this.pSubdirName != null) {
			return this.pSubdirName;
		}
		/*
		 * Isolate name.
		 */
		return this.pContext.getProperty(IPlatformProperties.PROP_ISOLATE_NAME);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cohorte.utilities.config.IConfiguration#getSubset(java.lang.String)
	 */
	@Override
	public Properties getSubset(final String aFilter) {
		Properties wSubSet = new Properties();

		if (aFilter == null || aFilter.isEmpty()
				|| "*".equalsIgnoreCase(aFilter)) {
			wSubSet.putAll(this.pProperties);
		}
		//
		else {
			for (Entry<Object, Object> wEntry : pProperties.entrySet()) {
				if (wEntry.getKey().toString().toLowerCase()
						.startsWith(aFilter.toLowerCase())) {
					wSubSet.put(wEntry.getKey(), wEntry.getValue());
				}
			}
		}
		return wSubSet;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.cohorte.utilities.config.IConfiguration#hasParam(java.lang.String)
	 */
	@Override
	public boolean hasParam(final String aName) {
		return pProperties.containsKey(aName);
	}

	/**
	 * Called when this component is invalidated.
	 */
	@Invalidate
	public void invalidate() {
		pLogger.logInfo(this, "invalidate",
				"invalidating... Cleaning cofig propetries.");

		// clean the map of properties
		clean();

		// unpublish the IConfiguration service
		this.pController = false;

		pLogger.logInfo(this, "invalidate", "invalidated.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cohorte.utilities.config.IConfiguration#size()
	 */
	@Override
	public int size() {
		return pProperties.size();
	}

	/**
	 * Called when the component is validated.
	 */
	@Validate
	public void validate() {
		pLogger.logInfo(this, "validate", "validating... ");

		try {
			this.pLogger.setLevel(IIsolateLoggerSvc.ALL);
			this.pLogger.logDebug(this, "validate",
					"Building configuration map.");

			// clean the map of properties
			clean();

			File[] wFiles = getConfigurationFiles();
			for (File wFile : wFiles) {
				pLogger.logInfo(this, "validate", "Load config file [%s]",
						wFile);

				try (FileInputStream wStream = new FileInputStream(wFile)) {
					this.pProperties.load(wStream);
				} catch (Exception | Error e) {
					this.pLogger.logSevere("validate",
							"ERROR while reading config file [%s] : %s", wFile,
							e);
					// Move on to the next file.
				}
			}

			// System properties have the highest priority.
			this.pProperties.putAll(System.getProperties());

			pLogger.logDebug(this, "validating",
					"Configuration dump: NbProperties=[%s]\n%s", size(), dump());

			// Only now publish IConfiguration service
			this.pController = true;

		} catch (Exception e) {
			pLogger.logSevere(this, "validate", "ERROR: %s", e);
		}
		pLogger.logInfo(this, "validate", "validated");
	}
}
