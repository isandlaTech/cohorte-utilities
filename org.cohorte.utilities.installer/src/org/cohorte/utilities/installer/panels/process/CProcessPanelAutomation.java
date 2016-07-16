package org.cohorte.utilities.installer.panels.process;

import static org.cohorte.utilities.installer.CInstallerTools.getServiceLogger;

import org.psem2m.utilities.logging.IActivityLogger;

import com.izforge.izpack.api.adaptator.IXMLElement;
import com.izforge.izpack.api.data.InstallData;
import com.izforge.izpack.api.resource.Resources;
import com.izforge.izpack.api.rules.RulesEngine;
import com.izforge.izpack.panels.process.ProcessPanelAutomation;
import com.izforge.izpack.util.PlatformModelMatcher;

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
public class CProcessPanelAutomation extends ProcessPanelAutomation {

	/**
	 * Logger
	 */
	private final IActivityLogger pLogger;

	/**
	 * @param panel
	 * @param installData
	 * @param prompt
	 */
	public CProcessPanelAutomation(InstallData installData, RulesEngine rules, Resources resources,
			PlatformModelMatcher matcher) {
		
		super(installData, rules, resources, matcher);

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
	

    /* (non-Javadoc)
     * @see com.izforge.izpack.panels.process.ProcessPanelAutomation#startProcessing(int)
     */
    @Override
    public void startProcessing(int noOfJobs)
    {
    	pNbJobs = noOfJobs;
    	pLogger.logInfo(this, "startProcessing", "[--/%2s]" ,pNbJobs );
        super.startProcessing(noOfJobs);
    }


    /* (non-Javadoc)
     * @see com.izforge.izpack.panels.process.ProcessPanelAutomation#finishProcessing(boolean, boolean)
     */
    @Override
    public void finishProcessing(boolean unlockPrev, boolean unlockNext)
    {
    	pLogger.logInfo(this, "finishProcessing", "[%2s/%2s] unlockPrev=[%s] unlockNext=[%s]" ,pJobIdx,pNbJobs,unlockPrev,unlockNext);
        super.finishProcessing(unlockPrev, unlockNext);
    }
    
    private int pJobIdx=0;
    private int pNbJobs=0;

 

    /* (non-Javadoc)
     * @see com.izforge.izpack.panels.process.ProcessPanelAutomation#startProcess(java.lang.String)
     */
    @Override
    public void startProcess(String name)
    {
    	pJobIdx++;
    	pLogger.logInfo(this, "startProcess", "[%2s/%2s][%s]" ,pJobIdx,pNbJobs, name );
        super.startProcess(name);
    }

    /* (non-Javadoc)
     * @see com.izforge.izpack.panels.process.ProcessPanelAutomation#finishProcess()
     */
    @Override
    public void finishProcess()
    {
    	pLogger.logInfo(this, "finishProcess", "[%2s/%2s]" ,pJobIdx,pNbJobs );

    	super.finishProcess();
    }

}