package org.cohorte.utilities.installer.panels;

import static org.cohorte.utilities.installer.CInstallerTools.getService;
import static org.cohorte.utilities.installer.CInstallerTools.getServiceLogger;

import javax.swing.JOptionPane;

import org.cohorte.utilities.installer.IInstallerData;
import org.psem2m.utilities.logging.IActivityLogger;

import com.izforge.izpack.api.data.Panel;
import com.izforge.izpack.api.resource.Resources;
import com.izforge.izpack.gui.log.Log;
import com.izforge.izpack.installer.data.GUIInstallData;
import com.izforge.izpack.installer.gui.InstallerFrame;
import com.izforge.izpack.panels.target.TargetPanel;

public class CPanelTarget extends TargetPanel  {

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
	public CPanelTarget(final Panel panel, final InstallerFrame parent,
			final GUIInstallData installData, final Resources resources,
			final Log log) {
		super(panel, parent, installData, resources, log);
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
		// normalize paths
		String wInstallPath = this.installData.getVariable("INSTALL_PATH");
		pLogger.logInfo(this, "panelDeactivate", "normalize windows path : %s",
				wInstallPath);
		this.installData.setVariable("INSTALL_PATH",
				wInstallPath.replace("\\", "/"));
		
		try {			
			getService(IInstallerData.class).putData("INSTALL_DIR", installData.getInstallPath());			
		} catch (Exception e) {
			pLogger.logSevere(this, "panelDeactivate", "ERROR, cannot write INSTALL_DIR to IInstallerData service reference!\n%s", e.getMessage());
		}				
		pLogger.logInfo(this, "panelDeactivate", "Deactivated");
	}
	
}
