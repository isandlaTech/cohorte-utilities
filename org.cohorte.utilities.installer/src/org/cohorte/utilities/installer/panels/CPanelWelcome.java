package org.cohorte.utilities.installer.panels;

import org.cohorte.utilities.installer.panels.welcome.CWelcomePanel;

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
public class CPanelWelcome extends CWelcomePanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6289585652492275272L;

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
		
	}
}
