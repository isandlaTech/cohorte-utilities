package org.cohorte.utilities.installer.panels.simplefinish;

import static org.cohorte.utilities.installer.CInstallerTools.getServiceLogger;

import org.psem2m.utilities.logging.IActivityLogger;

import com.izforge.izpack.installer.console.ConsolePanel;
import com.izforge.izpack.installer.panel.PanelView;
import com.izforge.izpack.panels.simplefinish.SimpleFinishConsolePanel;

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
public class CSimpleFinishConsolePanel extends SimpleFinishConsolePanel {

	/**
	 * Logger
	 */
	private final IActivityLogger pLogger;

	/**
	 * @param unpacker
	 * @param panel
	 */
	public CSimpleFinishConsolePanel(PanelView<ConsolePanel> panel) {

		super(panel);

		// get logger service (using static class CInstallerTools)
		pLogger = getServiceLogger();
		// log
		pLogger.logInfo(this, "<init>", "instanciated panelClass=[%s]", getClass().getName() );
	}
}
