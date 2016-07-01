package org.cohorte.utilities.installer.panels;

import static org.cohorte.utilities.installer.CInstallerTools.getService;
import static org.cohorte.utilities.installer.CInstallerTools.getServiceLogger;

import java.io.PrintWriter;
import java.util.Properties;

import org.cohorte.utilities.installer.IInstaller;
import org.psem2m.utilities.logging.IActivityLogger;

import com.izforge.izpack.api.data.InstallData;
import com.izforge.izpack.api.data.Panel;
import com.izforge.izpack.api.resource.Resources;
import com.izforge.izpack.gui.log.Log;
import com.izforge.izpack.installer.console.ConsolePanel;
import com.izforge.izpack.installer.data.GUIInstallData;
import com.izforge.izpack.installer.gui.InstallerFrame;
import com.izforge.izpack.panels.htmlhello.HTMLHelloPanel;
import com.izforge.izpack.util.Console;

public class CPanelWelcome extends HTMLHelloPanel implements ConsolePanel {

	/**
	 *
	 */
	private static final long serialVersionUID = 2735989401646225915L;

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
	public CPanelWelcome(final Panel panel, final InstallerFrame parent,
			final GUIInstallData installData, final Resources resources,
			final Log log) {
		super(panel, parent, installData, resources, log);

		// get logger service (using static class CInstallerTools)
		pLogger = getServiceLogger();

		try {

			// set the installData object of our installer service
			getService(IInstaller.class).setIzPackInstallData(this.installData);

		} catch (Exception e) {
			pLogger.logSevere(this, "<init>", "ERROR: %s", e);
		}
		// log
		pLogger.logInfo(this, "<init>", "instanciated panelResourceName=[%s]",
				panelResourceNameStr);
	}

	// @Override
	public boolean generateProperties(final InstallData arg0,
			final PrintWriter arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	// ----------------- ConsolePanel methods ---------------------------- //

	public boolean handlePanelValidationResult(final boolean arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	// @Override
	public boolean run(final InstallData arg0, final Console arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	// @Override
	public boolean run(final InstallData arg0, final Properties arg1) {
		// TODO Auto-generated method stub
		return false;
	}

}
