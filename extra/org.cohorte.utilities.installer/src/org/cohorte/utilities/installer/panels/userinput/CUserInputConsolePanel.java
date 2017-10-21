package org.cohorte.utilities.installer.panels.userinput;

import static org.cohorte.utilities.installer.CInstallerTools.getServiceLogger;

import java.lang.reflect.Field;
import java.util.List;

import org.psem2m.utilities.logging.IActivityLogger;

import com.izforge.izpack.api.adaptator.IXMLElement;
import com.izforge.izpack.api.data.InstallData;
import com.izforge.izpack.api.factory.ObjectFactory;
import com.izforge.izpack.api.handler.Prompt;
import com.izforge.izpack.api.resource.Resources;
import com.izforge.izpack.api.rules.RulesEngine;
import com.izforge.izpack.installer.console.ConsolePanel;
import com.izforge.izpack.installer.panel.PanelView;
import com.izforge.izpack.panels.userinput.UserInputConsolePanel;
import com.izforge.izpack.panels.userinput.UserInputPanel;
import com.izforge.izpack.panels.userinput.field.AbstractFieldView;
import com.izforge.izpack.util.Console;
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
public class CUserInputConsolePanel extends UserInputConsolePanel {

	/**
	 * Logger
	 */
	private final IActivityLogger pLogger;

	private List<AbstractFieldView> pFields;

	private InstallData pInstallData;

	/**
	 * @param resources
	 * @param factory
	 * @param rules
	 * @param matcher
	 * @param console
	 * @param prompt
	 * @param panelView
	 * @param installData
	 */
	public CUserInputConsolePanel(Resources resources, ObjectFactory factory, RulesEngine rules,
			PlatformModelMatcher matcher, Console console, Prompt prompt, PanelView<ConsolePanel> panelView,
			InstallData installData) {

		super(resources, factory, rules, matcher, console, prompt, panelView, installData);

		// get logger service (using static class CInstallerTools)
		pLogger = getServiceLogger();
		// log
		pLogger.logInfo(this, "<init>", "instanciated panelClass=[%s]", getClass().getName());

		pFields = retreiveFields();
		pInstallData = retreiveInstallData();
	}

	/**
	 * @return
	 */
	protected List<AbstractFieldView> getFields() {
		return pFields;
	}

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected List<AbstractFieldView> retreiveFields() {
		List<AbstractFieldView> wFields = null;

		try {
			// private List<ConsoleField> fields = new
			// ArrayList<ConsoleField>();
			Field wFieldFields = UserInputConsolePanel.class.getDeclaredField("fields");
			wFieldFields.setAccessible(true);
			wFields = (List<AbstractFieldView>) wFieldFields.get(this);

			pLogger.logInfo(this, "retreiveFields", "Fields=[%s]", wFields);

		} catch (Exception e) {
			pLogger.logSevere(this, "retreiveFields", "ERROR: %s", e);
		}
		return wFields;
	}

	/**
	 * @return
	 */
	protected InstallData retreiveInstallData() {
		InstallData wInstallData = null;

		try {
			// private final InstallData installData;
			Field wFieldInstallData = UserInputConsolePanel.class.getDeclaredField("installData");
			wFieldInstallData.setAccessible(true);
			wInstallData = (InstallData) wFieldInstallData.get(this);

			pLogger.logInfo(this, "retreiveInstallData", "InstallData=[%s]", wInstallData);

		} catch (Exception e) {
			pLogger.logSevere(this, "retreiveInstallData", "ERROR: %s", e);
		}
		return wInstallData;
	}

	/**
	 * @return
	 */
	protected InstallData getInstallData() {
		return pInstallData;
	}

	@Override
	public void createInstallationRecord(IXMLElement rootElement) {
		new CUserInputPanelAutomation(getFields()).createInstallationRecord(getInstallData(), rootElement);
	}
}