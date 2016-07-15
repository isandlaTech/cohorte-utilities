package org.cohorte.utilities.installer.panels;

import static org.cohorte.utilities.installer.CInstallerTools.getServiceLogger;

import org.psem2m.utilities.logging.IActivityLogger;

import com.izforge.izpack.api.data.InstallData;
import com.izforge.izpack.api.factory.ObjectFactory;
import com.izforge.izpack.api.handler.Prompt;
import com.izforge.izpack.api.resource.Resources;
import com.izforge.izpack.api.rules.RulesEngine;
import com.izforge.izpack.installer.console.ConsolePanel;
import com.izforge.izpack.installer.panel.PanelView;
import com.izforge.izpack.panels.userinput.UserInputConsolePanel;
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
	}
}