package org.cohorte.utilities.installer.panels;

import org.cohorte.utilities.installer.panels.process.CProcessPanel;

import com.izforge.izpack.api.data.Panel;
import com.izforge.izpack.api.resource.Resources;
import com.izforge.izpack.api.rules.RulesEngine;
import com.izforge.izpack.installer.data.GUIInstallData;
import com.izforge.izpack.installer.gui.InstallerFrame;
import com.izforge.izpack.util.PlatformModelMatcher;

/**
 * MOD_OG_20160715 console mode
 * 
 * @author ogattaz
 *
 */
@Deprecated
public class CPanelProcess extends CProcessPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2760089734608133274L;

	/**
	 * @param panel
	 * @param parent
	 * @param installData
	 * @param resources
	 * @param rules
	 * @param matcher
	 */
	public CPanelProcess(Panel panel, InstallerFrame parent,
			GUIInstallData installData, Resources resources, RulesEngine rules,
			PlatformModelMatcher matcher) {
		super(panel, parent, installData, resources, rules, matcher);
	}
}