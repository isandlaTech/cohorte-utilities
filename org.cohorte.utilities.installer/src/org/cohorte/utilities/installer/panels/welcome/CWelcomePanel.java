package org.cohorte.utilities.installer.panels.welcome;

import static org.cohorte.utilities.installer.CInstallerTools.getService;
import static org.cohorte.utilities.installer.CInstallerTools.getServiceLogger;

import java.io.PrintStream;
import java.util.prefs.Preferences;

import org.cohorte.utilities.installer.IConstants;
import org.cohorte.utilities.installer.IInstaller;
import org.psem2m.utilities.logging.IActivityLogger;

import com.izforge.izpack.api.data.Panel;
import com.izforge.izpack.api.resource.Resources;
import com.izforge.izpack.gui.log.Log;
import com.izforge.izpack.installer.data.GUIInstallData;
import com.izforge.izpack.installer.gui.InstallerFrame;
import com.izforge.izpack.panels.htmlhello.HTMLHelloPanel;

/**
 * MOD_OG_20160715 console mode
 *
 * <p/>
 * Console implementations must use the naming convention:
 * <p>
 * {@code <prefix>ConsolePanel}
 * </p>
 * where <em>{@code <prefix>}</em> is the IzPanel name, minus <em>Panel</em>. <br/>
 * E.g for the panel {@code HelloPanel}, the console implementation must be
 * named {@code HelloConsolePanel}.
 * <p/>
 *
 * @author ogattaz
 *
 */
public class CWelcomePanel extends HTMLHelloPanel {

	/**
	 *
	 */
	private static final long serialVersionUID = 2735989401646225915L;

	/**
	 * @see http
	 *      ://stackoverflow.com/questions/4350356/detect-if-java-application
	 *      -was-run-as-a-windows-admin
	 * @return
	 */
	public static boolean isPrivilegedMode() {
		Preferences prefs = Preferences.systemRoot();
		PrintStream systemErr = System.err;
		synchronized (systemErr) { // better synchroize to avoid problems with
									// other threads that access System.err
			System.setErr(null);
			try {
				prefs.put("foo", "bar"); // SecurityException on Windows
				prefs.remove("foo");
				prefs.flush(); // BackingStoreException on Linux
				return true;
			} catch (Exception e) {
				return false;
			} finally {
				System.setErr(systemErr);
			}
		}
	}

	/**
	 * Logger
	 */
	private final IActivityLogger pLogger;

	/**
	 * Constructor of Welcome Panel.
	 *
	 * @param panel
	 * @param parent
	 * @param installData
	 * @param resources
	 * @param log
	 */
	public CWelcomePanel(final Panel panel, final InstallerFrame parent,
			final GUIInstallData installData, final Resources resources,
			final Log log) {
		super(panel, parent, installData, resources, log);

		// get logger service (using static class CInstallerTools)
		pLogger = getServiceLogger();

		try {
			pLogger.logInfo(this, "<init>", "Try to store installData [%s]",
					this.installData);

			// set the installData object of our installer service
			getService(IInstaller.class).setIzPackInstallData(this.installData);

			// set priviliged mode variable
			boolean wIsPrivileged = isPrivilegedMode();
			this.installData.setVariable(IConstants.INSTALLER__PRIVILEGED_MODE,
					new Boolean(wIsPrivileged).toString());
			this.installData.setVariable(IConstants.INSTALLER__64_ARCH,
					System.getenv("ProgramW6432") != null ? "true" : "false");

		} catch (Exception e) {
			pLogger.logSevere(this, "<init>", "ERROR: %s", e);
		}
		// log
		pLogger.logInfo(this, "<init>",
				"instanciated panelClass=[%s] panelResourceName=[%s]",
				getClass().getName(), panelResourceNameStr);
	}
}
