package org.cohorte.utilities.installer.panels.summary;

import static org.cohorte.utilities.installer.CInstallerTools.getServiceLogger;

import org.psem2m.utilities.logging.IActivityLogger;

import com.izforge.izpack.panels.summary.SummaryConsolePanel;

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
public class CSummaryConsolePanel extends SummaryConsolePanel {

	/**
	 * Logger
	 */
	private final IActivityLogger pLogger;

	/**
	 * @param unpacker
	 * @param panel
	 */
	public CSummaryConsolePanel() {
		
		super();
		
		// get logger service (using static class CInstallerTools)
		pLogger = getServiceLogger();
		// log
		pLogger.logInfo(this, "<init>", "instanciated panelClass=[%s]", getClass().getName());
	}
}
