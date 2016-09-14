package org.cohorte.utilities.config.providers;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.ipojo.annotations.Validate;
import org.cohorte.utilities.config.IConfiguration;
import org.osgi.framework.BundleContext;
import org.psem2m.isolates.base.IIsolateLoggerSvc;
import org.psem2m.isolates.services.dirs.IPlatformDirsSvc;
import org.psem2m.utilities.files.CXFile;
import org.psem2m.utilities.files.CXFileDir;

/**
 * Configuration files are basic Java properties files.
 *
 * You should provide this files :
 * <ul>
 * <li><b>default.properties</b>: contains default configuration parameter
 * values.</li>
 * <li><b>current.properties</b>: contains overrided values for the default
 * configuraiton parameters.</li>
 * </ul>
 *
 * This two configuration file should be placed on Cohorte Data directory, under
 * sub-directories "conf/isolate_name", where "isolate_name" is the name of the
 * isolate where this component will be instantiated.
 *
 * The bundle containing this component should be added to the "repo" directory,
 * and you should instantiate this component in the isolate that will use it.
 * E.g. (from composition.js file):
 *
 * <pre>
 *           {
 *               "name": "COHORTE_PROPERTIES_CONFIGURATION_READER",
 *               "factory": "Cohorte-CCpntPropertiesConfiguration-Factory",
 *               "isolate": "agiliumproxy"
 *           }
 * </pre>
 *
 * @author bdebbabi
 *
 */
@Component(name = CCpntPropertiesConfiguration.COMPONENT_NAME)
@Provides(specifications = { IConfiguration.class })
public class CCpntPropertiesConfiguration implements IConfiguration {
	
	/**
	 * Service property names.
	 * 
	 * @author Ahmad Shahwan
	 *
	 */
	public interface IServiceProperties {
		String CURRENT_FILENAME = "org.cohorte.config.current.filename";
		String DEFAULT_FILENAME = "org.cohorte.config.default.filename";
		String SUBDIR_NAME = "org.cohorte.config.subdir.name";
	}
	
	public static final String COMPONENT_NAME =
			"Cohorte-CCpntPropertiesConfiguration-Factory";
	
	@Property(name=IServiceProperties.CURRENT_FILENAME)
	private String pCurrentFilename = CURRENT_FILENAME;
	
	@Property(name=IServiceProperties.DEFAULT_FILENAME)
	private String pDefaultFilename = DEFAULT_FILENAME;
	
	@Property(name=IServiceProperties.SUBDIR_NAME)
	private String pSubdirName = null;

	/**
	 * Cohorte Logger.
	 */
	@Requires
	private IIsolateLoggerSvc pLogger;

	/**
	 * Cohorte isolate service
	 */
	@Requires
	protected IPlatformDirsSvc pPlatform;

	/**
	 * Final (merged) properties.
	 */
	private final Properties pProperties = new Properties();

	/**
	 * Constructor.
	 */
	public CCpntPropertiesConfiguration() {}

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
	public String getParam(final String aParamName) {
		return pProperties.getProperty(aParamName);
	}

	@Override
	public String getParam(final String aParamName, final String aDefValue) {
		return pProperties.getProperty(aParamName, aDefValue);
	}

	@Override
	public boolean hasParam(final String aParamName) {
		return pProperties.containsKey(aParamName);
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
	 * Read configuration files.
	 *
	 * We start first by loading configs from "default.properties" file.
	 * Afterthat, we override the values by loading new values from
	 * "current.properties" file.
	 *
	 * @throws Exception
	 */
	private void readConfig() throws Exception {
		pLogger.logDebug(this, "readConfig", "...");

		File wDataDir = pPlatform.getPlatformBase();
		String wIsolateName = pPlatform.getIsolateName();
		if (wDataDir != null) {
			// load default properties
			CXFileDir wConfigDir = new CXFileDir(wDataDir,
					CONF_DIR);
			pLogger.logDebug(this, "readConfig",
					"Configuration directory=[%s]",
					wConfigDir.getAbsolutePath());
			CXFileDir wIsolatConfDir = new CXFileDir(wConfigDir, wIsolateName);
			CXFile wDefaultConfigFile = new CXFile(wIsolatConfDir,
					DEFAULT_FILENAME);
			pLogger.logDebug(this, "readConfig",
					"reading default configuration file [%s]",
					wDefaultConfigFile.getAbsolutePath());
			if (wDefaultConfigFile.exists()) {
				pLogger.logDebug(this, "readConfig",
						"Default configuration file=[%s]",
						wDefaultConfigFile.getAbsolutePath());
				FileInputStream wInputStreamDefault = null;
				FileInputStream wInputStreamCurrent = null;
				try {
					wInputStreamDefault = new FileInputStream(
							wDefaultConfigFile);
					pProperties.load(new FileInputStream(wDefaultConfigFile));
					CXFile wCurrentConfigFile = new CXFile(wIsolatConfDir,
							CURRENT_FILENAME);
					if (wCurrentConfigFile.exists()) {
						// load current configuration
						pLogger.logDebug(this, "readConfig",
								"Current configuration file=[%s]",
								wCurrentConfigFile.getAbsolutePath());
						wInputStreamCurrent = new FileInputStream(
								wCurrentConfigFile);
						pProperties
								.load(new FileInputStream(wCurrentConfigFile));
					} else {
						pLogger.logDebug(this, "readConfig",
								"No current configuration file was provided!");
					}
				} catch (FileNotFoundException e) {
					pLogger.logSevere(this, "readConfig",
							"Configuration File not found! [%s]",
							e.getMessage());
					throw new Exception(e.getMessage());
					// e.printStackTrace();
				} catch (IOException e) {
					pLogger.logSevere(this, "readConfig",
							"Cannot read configuration File! [%s]",
							e.getMessage());
					throw new Exception(e.getMessage());
					// e.printStackTrace();
				} finally {
					if (wInputStreamDefault != null) {
						try {
							wInputStreamDefault.close();
						} catch (IOException e) {
							pLogger.logSevere(
									this,
									"readConfig",
									"Cannot close default configuration file reader stream! [%s]",
									e.getMessage());
							// e.printStackTrace();
						}
					}
					if (wInputStreamCurrent != null) {
						try {
							wInputStreamCurrent.close();
						} catch (IOException e) {
							pLogger.logSevere(
									this,
									"readConfig",
									"Cannot close current configuration file reader stream! [%s]",
									e.getMessage());
							// e.printStackTrace();
						}
					}
				}
			} else {
				String wMsg = "No default configuration file was provided!";
				pLogger.logSevere(this, "readConfig", wMsg);
				throw new Exception(wMsg);
			}
		} else {
			pLogger.logWarn(this, "readConfig",
					"No Cohorte Data directory was found!");
		}
		pLogger.logDebug(this, "readConfig", "...DONE");
	}
	
	private String getSubdirName() {
		if (this.pSubdirName != null) {
			return this.pSubdirName;
		}
		return this.pPlatform.getIsolateName();
		
	}
	
	private void addFile(List<File> aList, File aFile) {
		aList.add(aFile);
		this.pLogger.logDebug(this, null,
				"Configurtation file %s added.", aFile.getAbsolutePath());
		
	}
	
	private File[] getConfigurationFiles() {
		List<File> wList = new ArrayList<>(3);
		File wFile = null;
		/*
		 * Default config file has the following default path:
		 * <cohort-base>/conf/<isolate-name>/default.properties
		 */
		wFile = pPlatform.getPlatformBase();
		wFile = new File(wFile, CONF_DIR);
		wFile = new File(wFile, getSubdirName());
		wFile = new File(wFile, this.pDefaultFilename);
		if (wFile.isFile()) {
			addFile(wList, wFile);
		} else {
			this.pLogger.logInfo(this, null,
					"Default configurtation file not found.");
		}
		wFile = pPlatform.getNodeDataDir();
		wFile = new File(wFile, CONF_DIR);
		wFile = new File(wFile, getSubdirName());
		if (wFile.isDirectory()) {
			/*
			 * List user-defined configuration files.
			 */
			File[] wUserFiles = wFile.listFiles(new FileFilter() {
				
				@Override
				public boolean accept(File wCandidate) {
					return wCandidate.isFile() && wCandidate.getName()
							.toLowerCase().endsWith(USER_EXTENSION);
				}
			});
			/*
			 * Default config file has the following default path:
			 * <cohort-data>/conf/<isolate-name>/current.properties
			 */
			wFile = new File(wFile, this.pCurrentFilename);
			if (wFile.isFile()) {
				addFile(wList, wFile);
			}
			/*
			 * Add user-defined files, if any.
			 */
			for (File wUserFile : wUserFiles) addFile(wList, wUserFile);						
		} else {
			this.pLogger.logInfo(this, null,
					"Node configuration diretory not found.");
		}
		return wList.toArray(new File[wList.size()]);
	}

	/**
	 * Called when the component is validated.
	 *
	 * @throws Exception
	 */
	@Validate
	public void validate() {
		this.pLogger.logDebug(this, "validating",
				"Validating configuration manager.");
		this.pProperties.clear();
		File[] wFiles = getConfigurationFiles();
		for (File wFile : wFiles) {
			try {
				this.pProperties.load(new FileInputStream(wFile));
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
}
