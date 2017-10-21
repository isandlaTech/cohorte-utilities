package org.cohorte.utilities.installer.listeners;

import java.io.File;
import java.util.List;

import org.cohorte.utilities.installer.CUninstaller;
import org.cohorte.utilities.installer.IInstaller;
import org.cohorte.utilities.picosoc.CAbstractComponentWithLogger;
import org.psem2m.utilities.CXException;

import com.izforge.izpack.api.event.ProgressListener;
import com.izforge.izpack.api.event.UninstallerListener;
import com.izforge.izpack.api.handler.AbstractUIProgressHandler;

/**
 *
 * (constructor) : only the default constructor will be used. It is called from
 * Destroyer.run as first call.
 * <ul>
 * <li>beforeDeletion will be called after execute files was performed. The
 * given list contains all File objects which are marked for deletion.
 * <li>isFileListener determines whether the next two methods are called or not.
 * <li>beforeDelete is the method which, is called before a single file is
 * deleted. The File object is given as parameter.
 * <li>
 * afterDelete will be invoked after the delete call for a single file.
 * <li>
 * afterDeletion is the last call before the cleanup of created data is
 * performed.
 * </ul>
 *
 * @see http://izpack.org/documentation/custom-actions.html#custom-actions-at-
 *      uninstalling-time
 *
 * @author ogattaz
 *
 */
@SuppressWarnings("deprecation")
public class CListenerUninstaller extends CAbstractComponentWithLogger
		implements UninstallerListener {

	// create the Singleton Uninstaller
	static {
		try {
			new CUninstaller();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Constructor.
	 */
	public CListenerUninstaller() {
		super();
		// register this Uninstaller Listener as a service in PICOSOC broker
		registerMeAsService(UninstallerListener.class);
		// log info
		getLogger().logInfo(this, "<init>", "Instanciated");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.izforge.izpack.api.event.UninstallerListener#afterDelete(java.io.
	 * File)
	 */
	//@Override
	public void afterDelete(final File file) {
		getLogger().logInfo(this, "afterDelete", "+++ file=[%s] ",
				file.getName());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.izforge.izpack.api.event.UninstallerListener#afterDelete(java.io.
	 * File, com.izforge.izpack.api.handler.AbstractUIProgressHandler)
	 */
	//@Override
	public void afterDelete(final File file,
			final AbstractUIProgressHandler handler) throws Exception {
		getLogger().logInfo(this, "afterDelete", "+++ file=[%s] ",
				file.getName());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.izforge.izpack.api.event.UninstallerListener#afterDelete(java.util
	 * .List, com.izforge.izpack.api.event.ProgressListener)
	 */
	//@Override
	public void afterDelete(final List<File> files,
			final ProgressListener listener) {
		for (File file : files) {
			getLogger().logInfo(this, "afterDelete", "+++ file=[%s] ",
					file.getName());
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.izforge.izpack.api.event.UninstallerListener#afterDeletion(java.util
	 * .List, com.izforge.izpack.api.handler.AbstractUIProgressHandler)
	 */
	//@Override
	public void afterDeletion(@SuppressWarnings("rawtypes") final List files,
			final AbstractUIProgressHandler handler) throws Exception {
		@SuppressWarnings("unchecked")
		List<File> wFiles = files;
		for (File file : wFiles) {
			getLogger().logInfo(this, "afterDeletion", "+++ file=[%s] ",
					file.getName());
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.izforge.izpack.api.event.UninstallerListener#beforeDelete(java.io
	 * .File)
	 */
	//@Override
	public void beforeDelete(final File file) {

		getLogger().logInfo(this, "beforeDelete", "+++ file=[%s] ",
				file.getName());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.izforge.izpack.api.event.UninstallerListener#beforeDelete(java.io
	 * .File, com.izforge.izpack.api.handler.AbstractUIProgressHandler)
	 */
	//@Override
	public void beforeDelete(final File file,
			final AbstractUIProgressHandler handler) throws Exception {
		getLogger().logInfo(this, "beforeDelete", "+++ file=[%s] ",
				file.getName());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.izforge.izpack.api.event.UninstallerListener#beforeDelete(java.util
	 * .List)
	 */
	//@Override
	public void beforeDelete(final List<File> files) {
		for (File file : files) {
			getLogger().logInfo(this, "beforeDelete", "+++ file=[%s] ",
					file.getName());
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.izforge.izpack.api.event.UninstallerListener#beforeDeletion(java.
	 * util.List, com.izforge.izpack.api.handler.AbstractUIProgressHandler)
	 */
	//@Override
	public void beforeDeletion(@SuppressWarnings("rawtypes") final List files,
			final AbstractUIProgressHandler handler) throws Exception {
		@SuppressWarnings("unchecked")
		List<File> wFiles = files;
		for (File file : wFiles) {
			getLogger().logInfo(this, "beforeDeletion", "+++ file=[%s] ",
					file.getName());
		}
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
	 * @see com.izforge.izpack.api.event.UninstallerListener#initialise()
	 */
	//@Override
	public void initialise() {

		getLogger().logInfo(this, "initialise",
				"+++ OK: registered as an UninstallerListener");

		String wInstallerInfos;
		try {
			wInstallerInfos = getService(IInstaller.class).toString();
		} catch (Exception e) {
			wInstallerInfos = CXException.eMiniInString(e);
		}
		getLogger().logInfo(this, "initialise",
				"+++ the IInstaller singleton [%s]", wInstallerInfos);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.izforge.izpack.api.event.UninstallerListener#isFileListener()
	 */
	//@Override
	public boolean isFileListener() {

		getLogger().logInfo(this, "isFileListener",
				"+++ OK: this listener is a 'FileListener': returns 'true'");
		return true;
	}

}
