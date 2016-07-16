package org.cohorte.utilities.installer.panels.treepacks;

import static org.cohorte.utilities.installer.CInstallerTools.getServiceLogger;

import java.util.ArrayList;
import java.util.List;

import org.psem2m.utilities.CXStringUtils;
import org.psem2m.utilities.logging.IActivityLogger;

import com.izforge.izpack.api.adaptator.IXMLElement;
import com.izforge.izpack.api.data.InstallData;
import com.izforge.izpack.panels.treepacks.TreePacksPanelAutomationHelper;

/**
 * MOD_OG_20160715 automation
 * 
 * To manage the data available in the element
 * "org.cohorte.utilities.installer.panels.CTreePacksConsolePanel"
 * 
 * <pre>
 * <?xml version="1.0" encoding="UTF-8" standalone="no"?>
 * <AutomatedInstallation langpack="fra">
 * ...
 * <org.cohorte.utilities.installer.panels.CTreePacksConsolePanel id="treePackspanel">
 * <pack index="0" name="Installer" selected="true"/>
 * <pack index="1" name="Agilium Server" selected="true"/>
 * <pack index="2" name="Agilium Sample Projects" selected="true"/>
 * <pack index="3" name="Agilium Web" selected="true"/>
 * <pack index="4" name="Agilium Factory" selected="true"/>
 * <pack index="5" name="LDAP Browser" selected="true"/>
 * </com.izforge.izpack.panels.treepacks.TreePacksPanel>
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
public class CTreePacksPanelAutomation extends TreePacksPanelAutomationHelper {

	/**
	 * Logger
	 */
	private final IActivityLogger pLogger;

	/**
	 * @param panel
	 * @param installData
	 * @param prompt
	 */
	public CTreePacksPanelAutomation() {
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
		
		   // <pack index="0" name="Installer" selected="true"/>
        List<IXMLElement> wEntries = panelRoot.getChildrenNamed("pack");
		pLogger.logInfo(this, "runAutomated", "nbEntries=[%s] %s",wEntries.size());
		for (IXMLElement wEntry : wEntries){
			pLogger.logInfo(this, "runAutomated", "pack=[%s] selected=[%s]",wEntry.getAttribute("name"),wEntry.getAttribute("selected"));
		}
		super.runAutomated(installData, panelRoot);

		pLogger.logInfo(this, "runAutomated", "end");
	}
}