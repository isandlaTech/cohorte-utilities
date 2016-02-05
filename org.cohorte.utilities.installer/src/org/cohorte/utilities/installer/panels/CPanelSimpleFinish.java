package org.cohorte.utilities.installer.panels;

import static org.cohorte.utilities.installer.CInstallerTools.getServiceLogger;

import org.psem2m.utilities.logging.IActivityLogger;

import com.izforge.izpack.api.data.Panel;
import com.izforge.izpack.api.resource.Resources;
import com.izforge.izpack.gui.log.Log;
import com.izforge.izpack.installer.data.GUIInstallData;
import com.izforge.izpack.installer.data.UninstallDataWriter;
import com.izforge.izpack.installer.gui.InstallerFrame;
import com.izforge.izpack.panels.simplefinish.SimpleFinishPanel;

public class CPanelSimpleFinish extends SimpleFinishPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1910839950765884592L;
	
	/**
	 * Logger
	 */
	private final IActivityLogger pLogger;
	
	public CPanelSimpleFinish(Panel panel, InstallerFrame parent,
			GUIInstallData installData, Resources resources,
			UninstallDataWriter uninstallDataWriter, Log log) {
		super(panel, parent, installData, resources, uninstallDataWriter, log);
		// first - retreive the 'logger' service asking the service registry
		pLogger = getServiceLogger();
		// log
		pLogger.logInfo(this, "<init>", "instanciated");
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
