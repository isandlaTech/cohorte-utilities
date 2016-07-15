package org.cohorte.utilities.installer.panels;

import static org.cohorte.utilities.installer.CInstallerTools.getServiceLogger;

import org.psem2m.utilities.logging.IActivityLogger;

import com.izforge.izpack.api.data.InstallData;
import com.izforge.izpack.api.factory.ObjectFactory;
import com.izforge.izpack.api.handler.Prompt;
import com.izforge.izpack.api.resource.Resources;
import com.izforge.izpack.api.rules.RulesEngine;
import com.izforge.izpack.installer.console.ConsolePanel;
import com.izforge.izpack.installer.panel.PanelView;
import com.izforge.izpack.panels.htmlinfo.HTMLInfoConsolePanel;
import com.izforge.izpack.util.Console;
import com.izforge.izpack.util.PlatformModelMatcher;

/**
 * MOD_OG_20160715 console mode
 * 
 * @author ogattaz
 *
 */
public class CWelcomeConsolePanel extends HTMLInfoConsolePanel {

	/**
	 * Logger
	 */
	private final IActivityLogger pLogger;

	/**
	 * @param resources
	 * @param panel
	 */
	public CWelcomeConsolePanel(Resources resources, PanelView<ConsolePanel> panel) {

		super(panel, resources, CWelcomeConsolePanel.class.getSimpleName());

		// get logger service (using static class CInstallerTools)
		pLogger = getServiceLogger();
		// log
		pLogger.logInfo(this, "<init>", "instanciated panelClass=[%s]", getClass().getName());
	}
}
