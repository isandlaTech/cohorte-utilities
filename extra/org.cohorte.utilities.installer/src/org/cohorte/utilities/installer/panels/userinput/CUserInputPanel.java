package org.cohorte.utilities.installer.panels.userinput;

import static org.cohorte.utilities.installer.CInstallerTools.getServiceLogger;

import java.lang.reflect.Field;
import java.util.List;

import org.psem2m.utilities.logging.IActivityLogger;
import org.psem2m.utilities.logging.IActivityLoggerBase;

import com.izforge.izpack.api.adaptator.IXMLElement;
import com.izforge.izpack.api.data.Panel;
import com.izforge.izpack.api.factory.ObjectFactory;
import com.izforge.izpack.api.handler.Prompt;
import com.izforge.izpack.api.resource.Resources;
import com.izforge.izpack.api.rules.RulesEngine;
import com.izforge.izpack.installer.data.GUIInstallData;
import com.izforge.izpack.installer.gui.InstallerFrame;
import com.izforge.izpack.panels.userinput.UserInputPanel;
import com.izforge.izpack.panels.userinput.field.AbstractFieldView;
import com.izforge.izpack.util.PlatformModelMatcher;

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
public class CUserInputPanel extends UserInputPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -564347155761485517L;

	/**
	 * MOD_OG_20160715 automation
	 * 
	 * the list of the AbstractFieldView of the UserInputPanel
	 */
	private List<AbstractFieldView> pFields;

	/**
	 * Logger
	 */
	private final IActivityLogger pLogger;

	/**
	 * Constructor.
	 * 
	 * @param panel
	 * @param parent
	 * @param installData
	 * @param resources
	 * @param rules
	 * @param factory
	 * @param matcher
	 * @param prompt
	 */
	public CUserInputPanel(Panel panel, InstallerFrame parent, GUIInstallData installData,
			Resources resources, RulesEngine rules, ObjectFactory factory, PlatformModelMatcher matcher,
			Prompt prompt) {

		super(panel, parent, installData, resources, rules, factory, matcher, prompt);

		// first - retreive the 'logger' service asking the service registry
		pLogger = getServiceLogger();
		// log
		pLogger.logInfo(this, "<init>", "instanciated panelClass=[%s]", getClass().getName());

		pFields = retreiveFields();
	}
	
	/**
	 * @return
	 */
	protected List<AbstractFieldView> getFields(){
		return pFields;
	}

	/*
	 * MOD_OG_20160715 automation
	 * 
	 * @see
	 * com.izforge.izpack.panels.userinput.UserInputPanel#createInstallationRecord
	 * (com.izforge.izpack.api.adaptator.IXMLElement)
	 */
	@Override
	public void createInstallationRecord(IXMLElement rootElement) {
		new CUserInputPanelAutomation(getFields()).createInstallationRecord(installData, rootElement);
	}

	/**
	 * Gets the Logger
	 * 
	 * @return
	 */
	protected IActivityLoggerBase getLogger() {
		return pLogger;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.izforge.izpack.panels.userinput.UserInputPanel#panelActivate()
	 */
	@Override
	public void panelActivate() {
		pLogger.logDebug(this, "panelActivate", "");
		super.panelActivate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.izforge.izpack.installer.gui.IzPanel#panelDeactivate()
	 */
	@Override
	public void panelDeactivate() {
		pLogger.logDebug(this, "panelDeactivate", "");
		super.panelDeactivate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.izforge.izpack.installer.gui.IzPanel#panelValidated()
	 */
	@Override
	public boolean panelValidated() {
		boolean wValidated = super.panelValidated();
		pLogger.logDebug(this, "panelValidated", "Validated=[%s]", wValidated);
		return wValidated;
	}

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected List<AbstractFieldView> retreiveFields() {

		List<AbstractFieldView> wFields = null;

		try {
			//private List<GUIField> views = new ArrayList<GUIField>();
			Field wFieldViews = UserInputPanel.class.getDeclaredField("views");
			wFieldViews.setAccessible(true);
			wFields = (List<AbstractFieldView>) wFieldViews.get(this);

			pLogger.logInfo(this, "retreiveFields", "Fields=[%s]", wFields);

		} catch (Exception e) {
			pLogger.logSevere(this, "retreiveFields", "ERROR: %s", e);
		}
		return wFields;
	}

}
