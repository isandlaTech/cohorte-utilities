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

/**
 * MOD_OG_20160715 console mode
 * 
 * @author ogattaz
 *
 */
@Deprecated
public class CPanelTarget extends CTargetPanel {

	/**
	 *
	 */
	private static final long serialVersionUID = 2735989402646225915L;

	/**
	 * Constructor of Welcome Panel.
	 *
	 * @param panel
	 * @param parent
	 * @param installData
	 * @param resources
	 * @param log
	 */
	public CPanelTarget(final Panel panel, final InstallerFrame parent, final GUIInstallData installData,
			final Resources resources, final Log log) {
		super(panel, parent, installData, resources, log);

	}
}