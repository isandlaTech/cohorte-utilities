package org.cohorte.utilities.installer.panels;

import com.izforge.izpack.api.data.Panel;
import com.izforge.izpack.api.factory.ObjectFactory;
import com.izforge.izpack.api.handler.Prompt;
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
public class CPanelUserInput extends CUserInputPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4620859311732010833L;
	
	/**
	 * @param panel
	 * @param parent
	 * @param installData
	 * @param resources
	 * @param rules
	 * @param factory
	 * @param matcher
	 * @param prompt
	 */
	public CPanelUserInput(Panel panel, InstallerFrame parent,
			GUIInstallData installData, Resources resources, RulesEngine rules,
			ObjectFactory factory, PlatformModelMatcher matcher, Prompt prompt) {
		super(panel, parent, installData, resources, rules, factory, matcher, prompt);

	}
}
