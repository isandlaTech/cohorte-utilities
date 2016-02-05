package org.cohorte.utilities.installer.panels;

import static org.cohorte.utilities.installer.CInstallerTools.getServiceLogger;

import org.psem2m.utilities.logging.IActivityLogger;

import com.izforge.izpack.api.data.Panel;
import com.izforge.izpack.api.resource.Resources;
import com.izforge.izpack.gui.log.Log;
import com.izforge.izpack.installer.data.GUIInstallData;
import com.izforge.izpack.installer.gui.InstallerFrame;
import com.izforge.izpack.panels.install.InstallPanel;

public class CPanelInstall extends InstallPanel {

	/**
	 * Logger
	 */
	private final IActivityLogger pLogger;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8844777594823486779L;

	public CPanelInstall(Panel panel, InstallerFrame parent,
			GUIInstallData installData, Resources resources, Log log) {
		super(panel, parent, installData, resources, log);
		// get logger service (using static class CInstallerTools)
		pLogger = getServiceLogger();
		// log
		pLogger.logInfo(this, "<init>", "instanciated panelResourceName=[%s]",
				"InstallPanel");
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
	
}
