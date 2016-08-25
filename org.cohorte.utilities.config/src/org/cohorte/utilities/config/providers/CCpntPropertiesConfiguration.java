package org.cohorte.utilities.config.providers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.ipojo.annotations.Validate;
import org.cohorte.utilities.config.IConfiguration;
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
@Component(name = "Cohorte-CCpntPropertiesConfiguration-Factory")
@Provides(specifications = { IConfiguration.class })
public class CCpntPropertiesConfiguration implements IConfiguration {

	/**
	 * Name of the current properties file that contains overriding values of
	 * default parameters.
	 */
	private static final String CONFIGURATION_CURRENT_FILENAME = "current.properties";

	/**
	 * Name of the default properties file that contains the default values for
	 * the configuration parameters.
	 */
	private static final String CONFIGURATION_DEFAULT_FILENAME = "default.properties";

	/**
	 * Name of the directory (under Cohorte Data directory) that contains
	 * configuration files.
	 */
	private static final String CONFIGURATION_DIR_NAME = "conf";

	/**
	 * Cohorte Logger.
	 */
	@Requires
	private IIsolateLoggerSvc pLogger;

	/**
	 * The "pelix.remote.export.reject" property to limit the remote export of
	 * the service
	 */
	@ServiceProperty(name = "pelix.remote.export.reject", immutable = true)
	private final String pNotRemote = IConfiguration.class.getName();

	/**
	 * Cohorte Isolate service
	 */
	@Requires
	protected IPlatformDirsSvc pPlatformDirsSrv;

	/**
	 * Loaded final (merged) properties
	 */
	private final Properties pProperties = new Properties();

	/**
	 * Cleanup properties
	 */
	private void cleanConfig() {
		pProperties.clear();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.cohorte.utilities.config.IConfiguration#dumpConfig()
	 */
	@Override
	public String dumpConfig() {
		StringBuilder wResult = new StringBuilder();
		for (Object wKey : pProperties.keySet()) {
			wResult.append(String.format("%25s=[%s]\n", wKey,
					pProperties.getProperty(wKey.toString())));
		}
		return wResult.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cohorte.utilities.config.IConfiguration#getParam(java.lang.String)
	 */
	@Override
	public String getParam(final String aParamName) {
		return pProperties.getProperty(aParamName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cohorte.utilities.config.IConfiguration#getParam(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public String getParam(final String aParamName, final String aDefValue) {
		return pProperties.getProperty(aParamName, aDefValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cohorte.utilities.config.IConfiguration#hasParam(java.lang.String)
	 */
	@Override
	public boolean hasParam(final String aParamName) {
		return pProperties.containsKey(aParamName);
	}

	/**
	 * Called when this component is invalidated
	 */
	@Invalidate
	public void invalidate() {
		pLogger.logDebug(this, "invalidating", "...");
		cleanConfig();
		pLogger.logDebug(this, "invalidating", "...DONE");
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

		File wDataDir = pPlatformDirsSrv.getPlatformBase();
		String wIsolateName = pPlatformDirsSrv.getIsolateName();
		if (wDataDir != null) {
			// load default properties
			CXFileDir wConfigDir = new CXFileDir(wDataDir,
					CONFIGURATION_DIR_NAME);
			pLogger.logDebug(this, "readConfig",
					"Configuration directory=[%s]",
					wConfigDir.getAbsolutePath());
			CXFileDir wIsolatConfDir = new CXFileDir(wConfigDir, wIsolateName);
			CXFile wDefaultConfigFile = new CXFile(wIsolatConfDir,
					CONFIGURATION_DEFAULT_FILENAME);
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
							CONFIGURATION_CURRENT_FILENAME);
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

	/**
	 * Called when the component is validated.
	 *
	 * @throws Exception
	 */
	@Validate
	public void validate() throws Exception {
		pLogger.logDebug(this, "validating", "...");
		readConfig();
		pLogger.logDebug(this, "validating", "Configuration:\n%s", dumpConfig());
		pLogger.logDebug(this, "validating", "...DONE");
	}
}
