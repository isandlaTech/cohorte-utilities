package org.cohorte.utilities.installer.panels;

import static org.cohorte.utilities.installer.CInstallerTools.getServiceLogger;

import java.io.PrintWriter;
import java.util.Properties;

import org.psem2m.utilities.logging.IActivityLogger;

import com.izforge.izpack.api.data.InstallData;
import com.izforge.izpack.api.data.Panel;
import com.izforge.izpack.api.resource.Resources;
import com.izforge.izpack.gui.log.Log;
import com.izforge.izpack.installer.console.ConsolePanel;
import com.izforge.izpack.installer.data.GUIInstallData;
import com.izforge.izpack.installer.gui.InstallerFrame;
import com.izforge.izpack.panels.htmllicence.HTMLLicencePanel;
import com.izforge.izpack.util.Console;

public class CPanelLicense extends HTMLLicencePanel implements ConsolePanel {

	/**
	 *
	 */
	private static final long serialVersionUID = 2735989402646225915L;

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
	public CPanelLicense(final Panel panel, final InstallerFrame parent,
			final GUIInstallData installData, final Resources resources,
			final Log log) {
		super(panel, parent, installData, resources, log);

		// get logger service (using static class CInstallerTools)
		pLogger = getServiceLogger();

		// log
		pLogger.logInfo(this, "<init>", "instanciated panelResourceName=[%s]",
				"LicencePanel");
	}

	// @Override
	public boolean generateProperties(final InstallData arg0,
			final PrintWriter arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean handlePanelValidationResult(final boolean arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.izforge.izpack.installer.gui.IzPanel#panelActivate()
	 */
	@Override
	public void panelActivate() {
		pLogger.logInfo(this, "panelActivate", "Activating");
		super.panelActivate();
		pLogger.logInfo(this, "panelActivate", "Activated");
	}

	// ----------------- ConsolePanel methods ---------------------------- //

	/*
	 * (non-Javadoc)
	 *
	 * @see com.izforge.izpack.installer.gui.IzPanel#panelDeactivate()
	 */
	@Override
	public void panelDeactivate() {
		pLogger.logInfo(this, "panelDeactivate", "Deactivating");
		super.panelDeactivate();
		pLogger.logInfo(this, "panelDeactivate", "Deactivated");
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
