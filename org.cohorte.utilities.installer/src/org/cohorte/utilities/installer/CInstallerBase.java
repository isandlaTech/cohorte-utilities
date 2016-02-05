package org.cohorte.utilities.installer;

import java.io.File;

import org.cohorte.utilities.picosoc.CAbstractComponentBase;
import org.cohorte.utilities.picosoc.CServicesRegistry;
import org.psem2m.utilities.files.CXFileDir;

/**
 * @author ogattaz
 * 
 */
public abstract class CInstallerBase extends CAbstractComponentBase implements
		IInstaller {

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

		//@Override
		public void run() {
			pLogger.logInfo(this, "run", "Exit hook !");

			finish();
		}
	}

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

		// third - create the 'data' service of this installer (auto
		// registration: it registers itself)
		new CInstallerData();

		// install a ShutdownHook as izPack doesn't call the listeners when the installer stops
		// http://docs.oracle.com/javase/7/docs/api/java/lang/Runtime.html#addShutdownHook(java.lang.Thread)
		Thread wShutdownHook = new Thread(new CExitListener(), "ExitListener");
		Runtime.getRuntime().addShutdownHook(wShutdownHook);

		pLogger.logInfo(this, "<init>", "instanciated.  ServicesRegistry:");

	}

	/**
	 * @return
	 */
	public abstract String dumpInstallDataInfos();

	/**
	 * When the installer stops, we move the log file created in the initial
	 * user dir of the installer into the target dir
	 * 
	 * @see org.cohorte.utilities.installer.IInstaller#finishInstallation
	 *      (com.izforge.izpack.installer.data.GUIInstallData)
	 */
	//@Override
	public void finish() {

		pSvcServicesRegistry.clear();
		pLogger.logInfo(this, "finish", "Clear The service registry");

		// calculates the path where to move the log file
		String wLogFileDestPath = getIzPackInstallPath();
		if (wLogFileDestPath == null || wLogFileDestPath.isEmpty()
				|| !(new File(wLogFileDestPath).exists())) {

			wLogFileDestPath = CXFileDir.getUserDir().getAbsolutePath();

			// if CUninstall => pInstallData isn't set => try to find the
			// "X3CryptedExchange" folder according the fact that the
			// "Uninstaller" folder is a child of the "X3CryptedExchange" one
			if (this.getClass().getSimpleName()
					.equals(getUninstallerClassName())) {

				File wDir = new File(wLogFileDestPath);
				while (wDir != null
						&& !wDir.getName().equals(getInstalledAppName())) {
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

}
