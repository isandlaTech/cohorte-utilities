package org.cohorte.utilities.installer.panels.treepacks;

import static org.cohorte.utilities.installer.CInstallerTools.getServiceLogger;

import org.psem2m.utilities.logging.IActivityLogger;

import com.izforge.izpack.api.adaptator.IXMLElement;
import com.izforge.izpack.api.data.Panel;
import com.izforge.izpack.api.resource.Locales;
import com.izforge.izpack.api.resource.Resources;
import com.izforge.izpack.api.rules.RulesEngine;
import com.izforge.izpack.installer.data.GUIInstallData;
import com.izforge.izpack.installer.gui.InstallerFrame;
import com.izforge.izpack.panels.treepacks.TreePacksPanel;

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
public class CTreePacksPanel extends TreePacksPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2293060381372068463L;

	/**
	 * Logger
	 */
	private final IActivityLogger pLogger;

	/**
	 * Constructor of Welcome Panel.
	 *
	 * @param panel
	 * @param parent
	 * @param installData
	 * @param resources
	 * @param log
	 */
	public CTreePacksPanel(final Panel panel, final InstallerFrame parent, final GUIInstallData installData,
			final Resources resources, final Locales locals, RulesEngine rules) {

		super(panel, parent, installData, resources, locals, rules);

		// get logger service (using static class CInstallerTools)
		pLogger = getServiceLogger();

		try {
			// set the installData object of our installer service
			// getService(IInstaller.class).setIzPackInstallData(this.installData);

		} catch (Exception e) {
			pLogger.logSevere(this, "<init>", "ERROR: %s", e);
		}
		// log
		pLogger.logInfo(this, "<init>", "instanciated panelClass=[%s]", getClass().getName());
	}

	/*
	 * MOD_OG_20160715 automation
	 * 
	 * @see
	 * com.izforge.izpack.panels.treepacks.TreePacksPanel#createInstallationRecord
	 * (com.izforge.izpack.api.adaptator.IXMLElement)
	 */
	@Override
	public void createInstallationRecord(IXMLElement rootElement) {
		new CTreePacksPanelAutomation().createInstallationRecord(installData, rootElement);
	}
}
