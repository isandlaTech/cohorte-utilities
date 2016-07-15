package org.cohorte.utilities.installer.panels;

import com.izforge.izpack.api.data.Panel;
import com.izforge.izpack.api.resource.Resources;
import com.izforge.izpack.gui.log.Log;
import com.izforge.izpack.installer.data.GUIInstallData;
import com.izforge.izpack.installer.gui.InstallerFrame;

/**
 * MOD_OG_20160715 console mode
 * 
 * @author ogattaz
 *
 */
@Deprecated
public class CPanelInstall extends CInstallPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8844777594823486779L;

	/**
	 * @param panel
	 * @param parent
	 * @param installData
	 * @param resources
	 * @param log
	 */
	public CPanelInstall(Panel panel, InstallerFrame parent,
			GUIInstallData installData, Resources resources, Log log) {
		super(panel, parent, installData, resources, log);
	}
}