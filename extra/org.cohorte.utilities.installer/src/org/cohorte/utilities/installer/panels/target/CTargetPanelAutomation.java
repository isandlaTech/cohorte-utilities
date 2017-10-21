package org.cohorte.utilities.installer.panels.target;

import static org.cohorte.utilities.installer.CInstallerTools.getServiceLogger;

import org.psem2m.utilities.logging.IActivityLogger;

import com.izforge.izpack.api.adaptator.IXMLElement;
import com.izforge.izpack.api.data.InstallData;
import com.izforge.izpack.panels.target.TargetPanelAutomation;

/**
 * MOD_OG_20160715 automation
 * 
 * To manage the data available in the element
 * "org.cohorte.utilities.installer.panels.CTargetPanel"
 * 
 * <pre>
 * <?xml version="1.0" encoding="UTF-8" standalone="no"?>
 * <AutomatedInstallation langpack="fra">
 * ...
 * <org.cohorte.utilities.installer.panels.CTargetPanel id="panel.target">
 * <installpath>/Users/ogattaz/temp/test-install/temp</installpath>
 * </org.cohorte.utilities.installer.panels.CTargetPanel>
 * ...
 * </AutomatedInstallation>
 * </pre>
 * 
 * The suffixes "Automation" or "AutomationHelper" are accepted.
 * 
 * @Lookat com.izforge.izpack.installer.util.PanelHelper:getAutomatedPanel()
 * 
 * @author ogattaz
 *
 */
public class CTargetPanelAutomation extends TargetPanelAutomation {

	/**
	 * Logger
	 */
	private final IActivityLogger pLogger;

	/**
	 * @param panel
	 * @param installData
	 * @param prompt
	 */
	public CTargetPanelAutomation() {
		super();

		// get logger service (using static class CInstallerTools)
		pLogger = getServiceLogger();
		// log
		pLogger.logInfo(this, "<init>", "instanciated AutomationClass=[%s]", getClass().getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.izforge.izpack.panels.packs.PacksPanelAutomationHelper#
	 * createInstallationRecord(com.izforge.izpack.api.data.InstallData,
	 * com.izforge.izpack.api.adaptator.IXMLElement)
	 */
	@Override
	public void createInstallationRecord(InstallData installData, IXMLElement panelRoot) {

		pLogger.logInfo(this, "createInstallationRecord", "begin panelRoot=[%s] id=[%s]",
				panelRoot.getName(), panelRoot.getAttribute("id"));

		super.createInstallationRecord(installData, panelRoot);

		pLogger.logInfo(this, "createInstallationRecord", "end");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.izforge.izpack.panels.packs.PacksPanelAutomationHelper#runAutomated
	 * (com.izforge.izpack.api.data.InstallData,
	 * com.izforge.izpack.api.adaptator.IXMLElement)
	 */
	@Override
	public void runAutomated(InstallData installData, IXMLElement panelRoot) {

		pLogger.logInfo(this, "runAutomated", "begin panelRoot=[%s] id=[%s]", panelRoot.getName(),
				panelRoot.getAttribute("id"));

		super.runAutomated(installData, panelRoot);

		pLogger.logInfo(this, "runAutomated", "end");
	}

}