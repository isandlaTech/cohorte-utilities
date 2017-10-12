package org.cohorte.utilities.installer.panels;

import org.cohorte.utilities.installer.panels.simplefinish.CSimpleFinishPanel;

import com.izforge.izpack.api.data.Panel;
import com.izforge.izpack.api.resource.Resources;
import com.izforge.izpack.gui.log.Log;
import com.izforge.izpack.installer.data.GUIInstallData;
import com.izforge.izpack.installer.data.UninstallDataWriter;
import com.izforge.izpack.installer.gui.InstallerFrame;

/**
 * MOD_OG_20160715 console mode
 * 
 * @author ogattaz
 *
 */
@Deprecated
public class CPanelSimpleFinish extends CSimpleFinishPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1910839950765884592L;

	/**
	 * @param panel
	 * @param parent
	 * @param installData
	 * @param resources
	 * @param uninstallDataWriter
	 * @param log
	 */
	public CPanelSimpleFinish(Panel panel, InstallerFrame parent, GUIInstallData installData,
			Resources resources, UninstallDataWriter uninstallDataWriter, Log log) {
		
		super(panel, parent, installData, resources, uninstallDataWriter, log);
	}
}