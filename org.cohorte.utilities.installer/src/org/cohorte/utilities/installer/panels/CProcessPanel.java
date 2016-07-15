package org.cohorte.utilities.installer.panels;

import static org.cohorte.utilities.installer.CInstallerTools.getServiceLogger;

import org.psem2m.utilities.logging.IActivityLogger;

import com.izforge.izpack.api.data.Panel;
import com.izforge.izpack.api.resource.Resources;
import com.izforge.izpack.api.rules.RulesEngine;
import com.izforge.izpack.installer.data.GUIInstallData;
import com.izforge.izpack.installer.gui.InstallerFrame;
import com.izforge.izpack.panels.process.ProcessPanel;
import com.izforge.izpack.util.PlatformModelMatcher;

/**
 * MOD_OG_20160715 console mode
 * 
 * <p/>
 * Console implementations must use the naming convention:
 * <p>
 * {@code <prefix>ConsolePanel}
 * </p>
 * where <em>{@code <prefix>}</em> is the IzPanel name, minus <em>Panel</em>. <br/>
 * E.g for the panel {@code HelloPanel}, the console implementation must be
 * named {@code HelloConsolePanel}.
 * <p/>
 * 
 * @author ogattaz
 *
 */
public class CProcessPanel extends ProcessPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3740430808098850103L;

	/**
	 * Logger
	 */
	private final IActivityLogger pLogger;

	/**
	 * @param panel
	 * @param parent
	 * @param installData
	 * @param resources
	 * @param rules
	 * @param matcher
	 */
	public CProcessPanel(Panel panel, InstallerFrame parent, GUIInstallData installData, Resources resources,
			RulesEngine rules, PlatformModelMatcher matcher) {

		super(panel, parent, installData, resources, rules, matcher);

		// first - retreive the 'logger' service asking the service registry
		pLogger = getServiceLogger();
		// log
		pLogger.logInfo(this, "<init>", "instanciated panelClass=[%s]",
				getClass().getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.izforge.izpack.installer.gui.IzPanel#panelActivate()
	 */
	@Override
	public void panelActivate() {
		pLogger.logInfo(this, "panelActivate", "Activating");
		super.panelActivate();
		pLogger.logInfo(this, "panelActivate", "Activated");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.izforge.izpack.installer.gui.IzPanel#panelDeactivate()
	 */
	@Override
	public void panelDeactivate() {
		pLogger.logInfo(this, "panelDeactivate", "Deactivating");
		super.panelDeactivate();
		pLogger.logInfo(this, "panelDeactivate", "Deactivated");
	}
}
