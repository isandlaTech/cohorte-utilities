package org.cohorte.utilities.installer.listeners;

import java.io.File;
import java.util.List;

import org.cohorte.utilities.installer.CInstaller;
import org.cohorte.utilities.installer.IInstaller;
import org.cohorte.utilities.picosoc.CAbstractComponentWithLogger;

import com.izforge.izpack.api.data.AutomatedInstallData;
import com.izforge.izpack.api.data.Pack;
import com.izforge.izpack.api.data.PackFile;
import com.izforge.izpack.api.event.InstallerListener;
import com.izforge.izpack.api.event.ProgressListener;
import com.izforge.izpack.api.handler.AbstractUIProgressHandler;


/**
 * Installer Listener.
 *
 */ 
@SuppressWarnings("deprecation")
public class CListenerInstaller extends CAbstractComponentWithLogger implements
		InstallerListener {

	// create the Installer singleton
	static {
		try {
			new CInstaller();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Constructor. Register this Installer listener as a service. The Services
	 * Broker is already created at this point of time. It's the static bloc
	 * above who creates and initializes the service broker.
	 * 
	 */
	public CListenerInstaller() {
		super();
		// register this Installer Listener as a service in PICOSOC broker
		registerMeAsService(InstallerListener.class);
		// log info
		getLogger().logInfo(this, "<init>", "Instanciated");
	}

	//@Override
	public void afterDir(final File dir, final PackFile packFile)
			throws Exception {
		getLogger().logInfo(this, "afterDir", "+++ TargetPath=[%s]",
				packFile.getTargetPath());

	}

	//@Override
	public void afterDir(final File dir, final PackFile packFile,
			final Pack pack) {
		getLogger().logInfo(this, "afterDir",
				"+++ TargetPath=[%s] Pack.name=[%s]", packFile.getTargetPath(),
				pack.getName());

	}

	//@Override
	public void afterFile(final File file, final PackFile packFile)
			throws Exception {
		getLogger().logInfo(this, "afterFile", "+++ TargetPath=[%s]",
				packFile.getTargetPath());

	}

	// ----- logging
	// ---------------------------------------------------------------------//

	//@Override
	public void afterFile(final File file, final PackFile packFile,
			final Pack pack) {
		getLogger().logInfo(this, "afterFile",
				"+++ TargetPath=[%s] Pack.name=[%s]", packFile.getTargetPath(),
				pack.getName());

	}

	//@Override
	public void afterInstallerInitialization(final AutomatedInstallData data)
			throws Exception {
		getLogger().logInfo(this, "afterInstallerInitialization", "+++");

	}

	//@Override
	public void afterPack(final Pack pack, final int index) {
		getLogger().logInfo(this, "afterPack", "+++ Pack.name=[%s] index=[%s]",
				pack.getName(), index);
	}

	//@Override
	public void afterPack(final Pack pack, final Integer i,
			final AbstractUIProgressHandler handler) throws Exception {

	}

	//@Override
	public void afterPacks(final AutomatedInstallData data,
			final AbstractUIProgressHandler handler) throws Exception {

	}

	//@Override
	public void afterPacks(final List<Pack> packs,
			final ProgressListener listener) {
		getLogger().logInfo(this, "afterPacks", "+++ packs.size=[%s]",
				packs.size());
	}

	//@Override
	public void beforeDir(final File dir, final PackFile packFile)
			throws Exception {
		getLogger().logInfo(this, "beforeDir", "+++ dir=[%s] TargetPath=[%s]",
				dir.getName(), packFile.getTargetPath());

	}

	//@Override
	public void beforeDir(final File dir, final PackFile packFile,
			final Pack pack) {
		getLogger().logInfo(this, "beforeDir",
				"+++ dir=[%s] TargetPath=[%s] Pack.name=[%s]", dir.getName(),
				packFile.getTargetPath(), pack.getName());

	}

	//@Override
	public void beforeFile(final File file, final PackFile packFile)
			throws Exception {
		getLogger().logInfo(this, "beforeFile",
				"+++ file=[%s] TargetPath=[%s]", file.getName(),
				packFile.getTargetPath());
	}

	//@Override
	public void beforeFile(final File file, final PackFile packFile,
			final Pack pack) {
		getLogger().logInfo(this, "beforeFile",
				"+++ file=[%s] TargetPath=[%s] Pack.name=[%s]", file.getName(),
				packFile.getTargetPath(), pack.getName());

	}

	//@Override
	public void beforePack(final Pack pack, final int index) {
		getLogger().logInfo(this, "beforePack",
				"+++ Pack.name=[%s] index=[%s]", pack.getName(), index);

	}

	//@Override
	public void beforePack(final Pack pack, final Integer i,
			final AbstractUIProgressHandler handler) throws Exception {

	}

	//@Override
	public void beforePacks(final AutomatedInstallData data,
			final Integer packs, final AbstractUIProgressHandler handler)
			throws Exception {

	}

	//@Override
	public void beforePacks(final List<Pack> packs) {
		getLogger().logInfo(this, "beforePacks", "+++ packs.size=[%s]",
				packs.size());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.izforge.izpack.util.CleanupClient#cleanUp()
	 */
	//@Override
	public void cleanUp() {
		getLogger().logInfo(this, "cleanUp", "+++ OK");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.izforge.izpack.api.event.InstallerListener#initialise()
	 */
	//@Override
	public void initialise() {

		// print log informations

		getLogger().logInfo(this, "initialise",
				"+++ OKK: registered as an 'InstallerListener'");

		try {
			String wInstallerInfos = getService(IInstaller.class).toString();
			getLogger().logInfo(this, "initialise",
					"+++ OK: got the 'IInstaller' service [%s]",
					wInstallerInfos);
		} catch (Exception e) {
			getLogger().logInfo(this, "initialise",
					"+++ ERR: can't get 'IInstaller' service: %s", e);
			// CXException.eMiniInString(e));
		}		
		
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.izforge.izpack.api.event.InstallerListener#isFileListener()
	 */
	//@Override
	public boolean isFileListener() {
		getLogger()
				.logInfo(this, "isFileListener",
						"+++ OK: this listener isn't a 'FileListener': returns 'false'");
		return false;
	}

}
