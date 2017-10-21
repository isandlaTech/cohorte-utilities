package org.cohorte.utilities.installer;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.cohorte.utilities.picosoc.CAbstractComponentBase;
import org.cohorte.utilities.picosoc.CServicesRegistry;
import org.psem2m.utilities.CXException;
import org.psem2m.utilities.CXStringUtils;
import org.psem2m.utilities.files.CXFileDir;

/**
 * @author ogattaz
 * 
 */
public abstract class CInstallerBase extends CAbstractComponentBase implements IInstaller {

	/**
	 * @author ogattaz
	 * 
	 */
	private class CExitListener implements Runnable {

		/**
		 * 
		 */
		private CExitListener() {
			super();
			pLogger.logInfo(this, "<init>", "instanciated");
		}

		// @Override
		public void run() {
			pLogger.logInfo(this, "run", "Exit hook !");

			finish();
		}
	}

	private static final String IZPACK_MAIN_LOGGER = "com.izforge.izpack";

	protected final CInstallerLogger pLogger;

	private final CServicesRegistry pSvcServicesRegistry;

	/**
	 * @throws Exception
	 */
	CInstallerBase() throws Exception {
		this(CInstallerBase.class.getSimpleName());
	}

	/**
	 * 
	 */
	protected CInstallerBase(final String aLoggerName) throws Exception {
		super();

		// first - create the service registry !!!
		pSvcServicesRegistry = CServicesRegistry.newRegistry();

		// second - register this singleton as the 'IInstaller' service
		registerMeAsService(IInstaller.class);

		// third - create the 'IActivityLogger' service (auto registration: it
		// registers
		// itself)
		pLogger = new CInstallerLogger(aLoggerName);

		redirectIzPackLogger();

		dumpLoggers();

		// third - create the 'data' service of this installer (auto
		// registration: it registers itself)
		new CInstallerData();

		// install a ShutdownHook as izPack doesn't call the listeners when the
		// installer stops
		// http://docs.oracle.com/javase/7/docs/api/java/lang/Runtime.html#addShutdownHook(java.lang.Thread)
		Thread wShutdownHook = new Thread(new CExitListener(), "ExitListener");
		Runtime.getRuntime().addShutdownHook(wShutdownHook);

		pLogger.logInfo(this, "<init>", "instanciated.  ServicesRegistry:");
	}

	/**
	 * @param aHandler
	 */
	private void dumpHandler(final Handler aHandler) {
		try {
			String wSimpleName = aHandler.getClass().getSimpleName();
			String wFilterName = (aHandler.getFilter() != null) ? aHandler.getFilter().getClass()
					.getSimpleName() : "no filter";
			String wFormatterName = (aHandler.getFormatter() != null) ? aHandler.getFormatter().getClass()
					.getSimpleName() : "no formatter";

			pLogger.logInfo(this, "dumpHandler", "- [%s] Level=[%s] FilterName=[%s] wFormatterName=[%s] ",
					wSimpleName, aHandler.getLevel(), wFilterName, wFormatterName);
		} catch (Exception e) {
			pLogger.logSevere(this, "dumpLogger", "ERROR: %s", CXException.eMiniInString(e));
		}
	}

	/**
	 * @return
	 */
	public abstract String dumpInstallDataInfos();

	/**
	 * @param aLogger
	 */
	private void dumpLogger(final String aName, final Logger aLogger) {

		try {
			String wLoggerName = CXStringUtils.strAdjustLeft(aName, 80, ' ');
			String wLevelName = "null";
			String wUseParentHandlers = "null";
			String wParentName = "null";
			if (aLogger != null) {
				Level wLevel = aLogger.getLevel();
				wLevelName = CXStringUtils.strAdjustLeft((wLevel != null) ? wLevel.getName() : "no level", 8,
						' ');
				wUseParentHandlers = String.valueOf(aLogger.getUseParentHandlers());
				wParentName = (aLogger.getParent() != null) ? aLogger.getParent().getName() : "no parent";
			}

			pLogger.logInfo(this, "dumpLogger", "[%s][%s] UseParent=[%s] LoggerParent=[%s]", wLoggerName,
					wLevelName, wUseParentHandlers, wParentName);

			for (Handler wLogHandler : aLogger.getHandlers()) {
				dumpHandler(wLogHandler);
			}
		} catch (Exception e) {
			pLogger.logSevere(this, "dumpLogger", "ERROR: %s", CXException.eMiniInString(e));
		}
	}

	/**
	 * <pre>
	 *  dumpLoggers; nbLogger=[29]
	 *   dumpLogger; [com.izforge.izpack.installer.bootstrap.Installer                                ][no level] UseParent=[true] LoggerParent=[com.izforge.izpack]
	 *   dumpLogger; [com.izforge.izpack.api.data.LocaleDatabase                                      ][no level] UseParent=[true] LoggerParent=[com.izforge.izpack]
	 *   dumpLogger; [global                                                                          ][no level] UseParent=[true] LoggerParent=[]
	 *   dumpLogger; [com.izforge.izpack.core.resource.DefaultLocales                                 ][no level] UseParent=[true] LoggerParent=[com.izforge.izpack]
	 *   dumpLogger; [sun.awt.multiscreen.SunDisplayChanger                                           ][no level] UseParent=[true] LoggerParent=[]
	 *   dumpLogger; [com.izforge.izpack.util.Platforms                                               ][no level] UseParent=[true] LoggerParent=[com.izforge.izpack]
	 *   dumpLogger; [java.awt.mixing.Component                                                       ][no level] UseParent=[true] LoggerParent=[]
	 *   dumpLogger; [com.izforge.izpack.installer.container.provider.RulesProvider                   ][no level] UseParent=[true] LoggerParent=[com.izforge.izpack]
	 *   dumpLogger; [java.awt.event.Component                                                        ][no level] UseParent=[true] LoggerParent=[]
	 *   dumpLogger; [Installer_CInstaller+1                                                          ][INFO    ] UseParent=[false] LoggerParent=[]
	 *  dumpHandler; - [CActivityFileHandler] Level=[ALL] FilterName=[no filter] wFormatterName=[CActivityFormaterBasic] 
	 *   dumpLogger; [java.awt.focus.DefaultKeyboardFocusManager                                      ][no level] UseParent=[true] LoggerParent=[]
	 *   dumpLogger; [com.izforge.izpack.util.OsVersion                                               ][no level] UseParent=[true] LoggerParent=[com.izforge.izpack]
	 *   dumpLogger; [java.awt.event.InputEvent                                                       ][no level] UseParent=[true] LoggerParent=[]
	 *   dumpLogger; [com.izforge.izpack.installer.bootstrap.InstallerGui                             ][no level] UseParent=[true] LoggerParent=[com.izforge.izpack]
	 *   dumpLogger; [com.izforge.izpack.installer.container.provider.IconsProvider                   ][no level] UseParent=[true] LoggerParent=[com.izforge.izpack]
	 *   dumpLogger; [com.izforge.izpack.core.data.DynamicVariableImpl                                ][no level] UseParent=[true] LoggerParent=[com.izforge.izpack]
	 *   dumpLogger; [java.awt.Container                                                              ][no level] UseParent=[true] LoggerParent=[]
	 *   dumpLogger; [com.izforge.izpack.core.data.DefaultVariables                                   ][no level] UseParent=[true] LoggerParent=[com.izforge.izpack]
	 *   dumpLogger; [com.izforge.izpack.core.substitutor.VariableSubstitutorBase                     ][no level] UseParent=[true] LoggerParent=[com.izforge.izpack]
	 *   dumpLogger; [java.awt.Component                                                              ][no level] UseParent=[true] LoggerParent=[]
	 *   dumpLogger; [com.izforge.izpack.installer.container.provider.GUIInstallDataProvider          ][no level] UseParent=[true] LoggerParent=[com.izforge.izpack]
	 *   dumpLogger; [java.awt.focus.Component                                                        ][no level] UseParent=[true] LoggerParent=[]
	 *   dumpLogger; [com.izforge.izpack.installer.container.provider.AbstractInstallDataProvider     ][no level] UseParent=[true] LoggerParent=[com.izforge.izpack]
	 *   dumpLogger; [com.izforge.izpack.util.Housekeeper                                             ][no level] UseParent=[true] LoggerParent=[com.izforge.izpack]
	 *   dumpLogger; [java.awt.ContainerOrderFocusTraversalPolicy                                     ][no level] UseParent=[true] LoggerParent=[]
	 *   dumpLogger; [com.izforge.izpack.util.PlatformModelMatcher                                    ][no level] UseParent=[true] LoggerParent=[com.izforge.izpack]
	 *   dumpLogger; [com.izforge.izpack.core.container.PlatformProvider                              ][no level] UseParent=[true] LoggerParent=[com.izforge.izpack]
	 *   dumpLogger; [com.izforge.izpack                                                              ][INFO    ] UseParent=[false] LoggerParent=[]
	 *  dumpHandler; - [LogHandler] Level=[INFO] FilterName=[no filter] wFormatterName=[LogFormatter] 
	 *  dumpHandler; - [CActivityFileHandler] Level=[ALL] FilterName=[no filter] wFormatterName=[CActivityFormaterBasic] 
	 *   dumpLogger; [                                                                                ][INFO    ] UseParent=[true] LoggerParent=[no parent]
	 * </pre>
	 */
	private void dumpLoggers() {
		LogManager wLogManager = LogManager.getLogManager();

		List<String> wNames = Collections.list(wLogManager.getLoggerNames());

		pLogger.logInfo(this, "dumpLoggers", "nbLogger=[%s]", wNames.size());

		for (String wName : wNames) {
			dumpLogger(wName, wLogManager.getLogger(wName));
		}
	}

	/**
	 * When the installer stops, we move the log file created in the initial
	 * user dir of the installer into the target dir
	 * 
	 * @see org.cohorte.utilities.installer.IInstaller#finishInstallation
	 *      (com.izforge.izpack.installer.data.GUIInstallData)
	 */
	// @Override
	public void finish() {

		pSvcServicesRegistry.clear();
		pLogger.logInfo(this, "finish", "Clear The service registry");

		// calculates the path where to move the log file
		String wLogFileDestPath = getIzPackInstallPath();
		if (wLogFileDestPath == null || wLogFileDestPath.isEmpty() || !(new File(wLogFileDestPath).exists())) {

			wLogFileDestPath = CXFileDir.getUserDir().getAbsolutePath();

			// if CUninstall => pInstallData isn't set => try to find the
			// "X3CryptedExchange" folder according the fact that the
			// "Uninstaller" folder is a child of the "X3CryptedExchange" one
			if (this.getClass().getSimpleName().equals(getUninstallerClassName())) {

				File wDir = new File(wLogFileDestPath);
				while (wDir != null && !wDir.getName().equals(getInstalledAppName())) {
					wDir = wDir.getParentFile();
				}

				if (wDir != null) {
					wLogFileDestPath = wDir.getAbsolutePath();
				}
			}

		}
		// close an move the file !
		pLogger.closeAndMove(wLogFileDestPath);
	}

	/**
	 * @return the name of the installed application (eg. "X3CryptedExchange" )
	 */
	public abstract String getInstalledAppName();

	/**
	 * @return
	 */
	public abstract String getIzPackInstallPath();

	/**
	 * @return
	 */
	public abstract String getUninstallerClassName();

	/**
	 * 
	 */
	protected void logRegistery() {

		for (String wDumpLine : pSvcServicesRegistry.dump().split("\n")) {
			pLogger.logInfo(this, "<init>", wDumpLine);
		}
	}

	/**
	 * 
	 */
	private void redirectIzPackLogger() {

		LogManager wLogManager = LogManager.getLogManager();

		Logger wIzPackLogger = wLogManager.getLogger(IZPACK_MAIN_LOGGER);
		if (wIzPackLogger == null) {
			pLogger.logSevere(this, "redirectIzPackLogger", "ERROR: Can't retreive the logger [%s]",
					IZPACK_MAIN_LOGGER);
		}
		//
		else {
			Handler wHandler = pLogger.gethandler();
			wIzPackLogger.addHandler(wHandler);
			pLogger.logInfo(this, "redirectIzPackLogger", "Added handler=[%s]", wHandler);
			Level wLevel = pLogger.getLevel();
			wIzPackLogger.setLevel(wLevel);
			pLogger.logInfo(this, "redirectIzPackLogger", "Set level=[%s]", wLevel.getName());
		}
	}
}