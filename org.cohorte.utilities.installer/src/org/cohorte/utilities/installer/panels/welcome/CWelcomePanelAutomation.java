package org.cohorte.utilities.installer.panels.welcome;

import static org.cohorte.utilities.installer.CInstallerTools.getService;
import static org.cohorte.utilities.installer.CInstallerTools.getServiceLogger;

import org.cohorte.utilities.installer.IInstaller;
import org.psem2m.utilities.logging.IActivityLogger;

import com.izforge.izpack.api.adaptator.IXMLElement;
import com.izforge.izpack.api.data.InstallData;
import com.izforge.izpack.installer.automation.PanelAutomation;

/**
 * MOD_OG_20160715 automation
 * 
 * To manage the data available in the element
 * "org.cohorte.utilities.installer.panels.CTreePacksConsolePanel"
 * 
 * <pre>
 * 
 * </pre>
 * 
 * The suffixes "Automation" or "AutomationHelper" are accepted.
 * 
 * @Lookat com.izforge.izpack.installer.util.PanelHelper:getAutomatedPanel()
 * 
 * @author ogattaz
 *
 */
public class CWelcomePanelAutomation implements PanelAutomation {

	/**
	 * Logger
	 */
	private final IActivityLogger pLogger;

	/**
	 * @param panel
	 * @param installData
	 * @param prompt
	 */
	public CWelcomePanelAutomation() {
		super();

		// get logger service (using static class CInstallerTools)
		pLogger = getServiceLogger();
		// log
		pLogger.logInfo(this, "<init>", "instanciated AutomationClass=[%s]", getClass().getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.izforge.izpack.panels.packs.PacksPanelAutomationHelper#runAutomated
	 * (com.izforge.izpack.api.data.InstallData,
	 * com.izforge.izpack.api.adaptator.IXMLElement)
	 */
	public void runAutomated(InstallData installData, IXMLElement panelRoot) {

		pLogger.logInfo(this, "runAutomated", "begin panelRoot=[%s] id=[%s]", panelRoot.getName(),
				panelRoot.getAttribute("id"));

		try {
			pLogger.logInfo(this, "runAutomated", "Try to store installData [%s]", installData);

			// set the installData object of our installer service
			getService(IInstaller.class).setIzPackInstallData(installData);

		} catch (Exception e) {
			pLogger.logSevere(this, "runAutomated", "ERROR: %s", e);
		}

		pLogger.logInfo(this, "runAutomated", "end");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.izforge.izpack.installer.automation.PanelAutomation#
	 * createInstallationRecord(com.izforge.izpack.api.data.InstallData,
	 * com.izforge.izpack.api.adaptator.IXMLElement)
	 */
	public void createInstallationRecord(InstallData installData, IXMLElement panelRoot) {
		pLogger.logInfo(this, "createInstallationRecord", "begin panelRoot=[%s] id=[%s]. Store nothing.",
				panelRoot.getName(), panelRoot.getAttribute("id"));
	}
}
