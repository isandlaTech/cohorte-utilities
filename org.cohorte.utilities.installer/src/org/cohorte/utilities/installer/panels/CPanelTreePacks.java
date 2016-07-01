package org.cohorte.utilities.installer.panels;

import static org.cohorte.utilities.installer.CInstallerTools.getServiceLogger;

import java.io.PrintWriter;
import java.util.Properties;

import org.psem2m.utilities.logging.IActivityLogger;

import com.izforge.izpack.api.data.InstallData;
import com.izforge.izpack.api.data.Panel;
import com.izforge.izpack.api.resource.Locales;
import com.izforge.izpack.api.resource.Resources;
import com.izforge.izpack.api.rules.RulesEngine;
import com.izforge.izpack.installer.console.ConsolePanel;
import com.izforge.izpack.installer.data.GUIInstallData;
import com.izforge.izpack.installer.gui.InstallerFrame;
import com.izforge.izpack.panels.treepacks.TreePacksPanel;
import com.izforge.izpack.util.Console;

public class CPanelTreePacks extends TreePacksPanel implements ConsolePanel {

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
	public CPanelTreePacks(final Panel panel, final InstallerFrame parent,
			final GUIInstallData installData, final Resources resources,
			final Locales locals, final RulesEngine rules) {

		super(panel, parent, installData, resources, locals, rules);

		// get logger service (using static class CInstallerTools)
		pLogger = getServiceLogger();

		try {
			// set the installData object of our installer service
			// getService(IInstaller.class).setIzPackInstallData(this.installData);

		} catch (Exception e) {
			pLogger.logSevere(this, "<init>", "ERROR: %s", e);
		}
		// log
		pLogger.logInfo(this, "<init>", "instanciated panelResourceName=[%s]",
				"TreePacksPanel");
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
