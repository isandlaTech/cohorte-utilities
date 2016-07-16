package org.cohorte.utilities.installer.panels;

import org.cohorte.utilities.installer.panels.treepacks.CTreePacksPanel;

import com.izforge.izpack.api.data.Panel;
import com.izforge.izpack.api.resource.Locales;
import com.izforge.izpack.api.resource.Resources;
import com.izforge.izpack.api.rules.RulesEngine;
import com.izforge.izpack.installer.data.GUIInstallData;
import com.izforge.izpack.installer.gui.InstallerFrame;

/**
 * MOD_OG_20160715 console mode
 * 
 * @author ogattaz
 *
 */
@Deprecated
public class CPanelTreePacks extends CTreePacksPanel {

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
	public CPanelTreePacks(final Panel panel, final InstallerFrame parent,
			final GUIInstallData installData, final Resources resources,
			final Locales locals, RulesEngine rules) {
		
		super(panel, parent, installData, resources, locals, rules);
	}
}