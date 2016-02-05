package org.cohorte.utilities.installer.panels;

import static org.cohorte.utilities.installer.CInstallerTools.getServiceLogger;

import org.psem2m.utilities.logging.IActivityLogger;

import com.izforge.izpack.api.data.Panel;
import com.izforge.izpack.api.factory.ObjectFactory;
import com.izforge.izpack.api.handler.Prompt;
import com.izforge.izpack.api.resource.Resources;
import com.izforge.izpack.api.rules.RulesEngine;
import com.izforge.izpack.installer.data.GUIInstallData;
import com.izforge.izpack.installer.gui.InstallerFrame;
import com.izforge.izpack.panels.userinput.UserInputPanel;
import com.izforge.izpack.util.PlatformModelMatcher;

public class CPanelUserInput extends UserInputPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4620859311732010833L;
	
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
	 * @param rules
	 * @param factory
	 * @param matcher
	 * @param prompt
	 */
	public CPanelUserInput(Panel panel, InstallerFrame parent,
			GUIInstallData installData, Resources resources, RulesEngine rules,
			ObjectFactory factory, PlatformModelMatcher matcher, Prompt prompt) {
		super(panel, parent, installData, resources, rules, factory, matcher, prompt);
		// first - retreive the 'logger' service asking the service registry
		pLogger = getServiceLogger();
		// log
		pLogger.logInfo(this, "<init>", "instanciated");
	}

	@Override
	public boolean panelValidated() {
		// TODO Auto-generated method stub
		return super.panelValidated();
	}
	
	@Override
	public void panelActivate() {
		// TODO Auto-generated method stub
		super.panelActivate();
		pLogger.logDebug(this, "panelActivate", "");
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.izforge.izpack.installer.gui.IzPanel#panelDeactivate()
	 */
	@Override
	public void panelDeactivate() {
		
	}
	
	
}
