package org.cohorte.utilities.installer.panels;

import static org.cohorte.utilities.installer.CInstallerTools.getServiceLogger;

import org.psem2m.utilities.logging.IActivityLogger;

import com.izforge.izpack.installer.console.ConsolePanel;
import com.izforge.izpack.installer.panel.PanelView;
import com.izforge.izpack.installer.unpacker.IUnpacker;
import com.izforge.izpack.panels.install.InstallConsolePanel;

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
public class CInstallConsolePanel extends InstallConsolePanel {

	/**
	 * Logger
	 */
	private final IActivityLogger pLogger;

	/**
	 * @param unpacker
	 * @param panel
	 */
	public CInstallConsolePanel(IUnpacker unpacker, PanelView<ConsolePanel> panel) {

		super(unpacker, panel);

		// get logger service (using static class CInstallerTools)
		pLogger = getServiceLogger();
		// log
		pLogger.logInfo(this, "<init>", "instanciated panelClass=[%s]", getClass().getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.izforge.izpack.installer.gui.IzPanel#panelActivate()
	 */
	@Override
	public void startAction(String name, int no_of_steps) {
		pLogger.logInfo(this, "panelActivate", "Activating [%s] [%s]", no_of_steps, name);
		super.startAction(name, no_of_steps);
		pLogger.logInfo(this, "panelActivate", "Activated  [%s] [%s]", no_of_steps, name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.izforge.izpack.installer.gui.IzPanel#panelDeactivate()
	 */
	@Override
	public void stopAction() {
		pLogger.logInfo(this, "panelDeactivate", "Deactivating");
		super.stopAction();
		pLogger.logInfo(this, "panelDeactivate", "Deactivated");
	}
}