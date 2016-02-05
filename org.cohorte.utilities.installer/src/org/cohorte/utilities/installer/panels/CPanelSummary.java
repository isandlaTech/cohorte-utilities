package org.cohorte.utilities.installer.panels;

import static org.cohorte.utilities.installer.CInstallerTools.getServiceLogger;

import org.psem2m.utilities.logging.IActivityLogger;

import com.izforge.izpack.api.data.Panel;
import com.izforge.izpack.api.resource.Resources;
import com.izforge.izpack.gui.log.Log;
import com.izforge.izpack.installer.data.GUIInstallData;
import com.izforge.izpack.installer.gui.InstallerFrame;
import com.izforge.izpack.panels.summary.SummaryPanel;

public class CPanelSummary extends SummaryPanel {

	/**
	 * Logger
	 */
	private final IActivityLogger pLogger;
	
	/**
	 * Constructor.
	 * 
	 * @param panel
	 * @param parent
	 * @param installData
	 * @param resources
	 * @param log
	 */
	public CPanelSummary(Panel panel, InstallerFrame parent, GUIInstallData installData,
			Resources resources, Log log) {
		super(panel, parent, installData, resources, log);
		// get logger service (using static class CInstallerTools)
		pLogger = getServiceLogger();		
		// log
		pLogger.logInfo(this, "<init>", "instanciated panelResourceName=[%s]",
				"InstallPanel");
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
