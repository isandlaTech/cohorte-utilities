package org.cohorte.utilities.installer.panels;

import static org.cohorte.utilities.installer.CInstallerTools.getServiceLogger;

import org.psem2m.utilities.logging.IActivityLogger;

import com.izforge.izpack.api.adaptator.IXMLElement;
import com.izforge.izpack.api.adaptator.impl.XMLElementImpl;
import com.izforge.izpack.api.data.InstallData;
import com.izforge.izpack.api.exception.InstallerException;
import com.izforge.izpack.installer.automation.PanelAutomation;

/**
 * MOD_OG_20160715 automation
 * 
 * @author ogattaz
 *
 */
public abstract class CAbstractAutomation implements PanelAutomation {

	/**
	 * Logger
	 */
	private final IActivityLogger pLogger;

	/**
	 * @param panel
	 * @param installData
	 * @param prompt
	 */
	public CAbstractAutomation() {
		super();

		// get logger service (using static class CInstallerTools)
		pLogger = getServiceLogger();
		// log
		pLogger.logInfo(this, "<init>", "instanciated AutomationClass=[%s]", getClass().getName());
	}

	/**
	 * @return
	 */
	protected IActivityLogger getLogger() {
		return pLogger;
	}

	/**
	 * @param installData
	 * @param aVariable
	 * @param panelRoot
	 */
	protected void storeVariable(InstallData installData, String aVariable, IXMLElement panelRoot) {

		String wValue = installData.getVariable(aVariable);

		// Installation path markup
		IXMLElement wElmt = new XMLElementImpl(aVariable, panelRoot);
		// check this writes even if value is the default,
		// because without the constructor, default does not get set.
		wElmt.setContent(wValue);

		// Checkings to fix bug #1864
		IXMLElement wPreviousElmt = panelRoot.getFirstChildNamed(aVariable);
		if (wPreviousElmt != null) {
			panelRoot.removeChild(wPreviousElmt);
		}
		panelRoot.addChild(wElmt);

		pLogger.logInfo(this, "storeVariable", "Variable=[%s] Value=[%s]", aVariable, wValue);

	}

	/**
	 * @param installData
	 * @param aVariable
	 * @param panelRoot
	 */
	protected void readVariable(InstallData installData, String aVariable, IXMLElement panelRoot) {

		IXMLElement wElmt = panelRoot.getFirstChildNamed(aVariable);

		if (wElmt == null) {

			String wMess = installData.getMessages().get("TargetPanel.incompatibleInstallation");
			String wExceptionMess = String.format("%s The variable [%s] doesn't exist in the element [%s]",
					wMess, aVariable, panelRoot.getName());
			throw new InstallerException(wExceptionMess);

		}

		String wValue = wElmt.getContent();

		installData.getVariables().set(aVariable, wValue);

		pLogger.logInfo(this, "readVariable", "Variable=[%s] Value=[%s]", aVariable, wValue);

	}
}