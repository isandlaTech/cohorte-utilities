package org.cohorte.utilities.installer.panels.welcome;

import static org.cohorte.utilities.installer.CInstallerTools.getServiceLogger;

import org.psem2m.utilities.logging.IActivityLogger;

import com.izforge.izpack.api.resource.Resources;
import com.izforge.izpack.installer.console.ConsolePanel;
import com.izforge.izpack.installer.panel.PanelView;
import com.izforge.izpack.panels.htmlinfo.HTMLInfoConsolePanel;

/**
 * MOD_OG_20160715 console mode
 * 
 * @author ogattaz
 *
 */
public class CWelcomeConsolePanel extends HTMLInfoConsolePanel {
	
	/** to get the string resource  "CWelcomeConsolePanel.info" 
	 * <pre>
	 * 
		<res id="TXTHelloPanel.panel.welcome_eng" src="resources/welcome_eng.txt" />
		<res id="TXTHelloPanel.panel.welcome_fra" src="resources/welcome_fra.txt" />
	 * </pre>
	 */
	private static final String RESOURCE_PREFIX = "TXTHelloPanel";

	/**
	 * Logger
	 */
	private final IActivityLogger pLogger;

	/**
	 * @param resources
	 * @param panel
	 */
	public CWelcomeConsolePanel(Resources resources, PanelView<ConsolePanel> panel) {

		super(panel, resources,RESOURCE_PREFIX );

		// get logger service (using static class CInstallerTools)
		pLogger = getServiceLogger();
		// log
		pLogger.logInfo(this, "<init>", "instanciated panelClass=[%s]", getClass().getName());
	}
	
	
	
}
