package org.cohorte.utilities.installer.panels;

import static org.cohorte.utilities.installer.CInstallerTools.getServiceLogger;

import java.util.List;

import org.psem2m.utilities.logging.IActivityLogger;

import com.izforge.izpack.api.adaptator.IXMLElement;
import com.izforge.izpack.api.data.InstallData;
import com.izforge.izpack.panels.userinput.UserInputPanelAutomationHelper;
import com.izforge.izpack.panels.userinput.field.AbstractFieldView;


/**
 * MOD_OG_20160715 automation
 * 
 * To manage the data available in the element
 * "org.cohorte.utilities.installer.panels.CTreePacksConsolePanel"
 * 
 * <pre>

 * </pre>
 * 
 * The suffixes "Automation" or "AutomationHelper" are accepted.
 * 
 * @Lookat com.izforge.izpack.installer.util.PanelHelper:getAutomatedPanel()
 * 
 * @author ogattaz
 *
 */

public class CUserInputPanelAutomation extends UserInputPanelAutomationHelper {

	/**
	 * Logger
	 */
	private final IActivityLogger pLogger;

	
    /**
     * Creates an {@link UserInputPanelAutomationHelper}
     *
     * @param views AbstractFieldView
     */
    public CUserInputPanelAutomation(List<? extends AbstractFieldView> aFields)
    {
		super(aFields);

		// get logger service (using static class CInstallerTools)
		pLogger = getServiceLogger();
		// log
		pLogger.logInfo(this, "<init>", "instanciated AutomationClass=[%s] Fields=[%s]", getClass().getName(),aFields);
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
