package org.cohorte.utilities.installer.panels.license;

import static org.cohorte.utilities.installer.CInstallerTools.getServiceLogger;

import java.net.URL;

import org.psem2m.utilities.CXException;
import org.psem2m.utilities.logging.IActivityLogger;

import com.izforge.izpack.api.resource.Resources;
import com.izforge.izpack.installer.console.ConsolePanel;
import com.izforge.izpack.installer.panel.PanelView;
import com.izforge.izpack.panels.htmllicence.HTMLLicenceConsolePanel;

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
public class CLicenseConsolePanel extends HTMLLicenceConsolePanel {

	private static final String DEFAULT_SUFFIX = ".licence";

	/**
	 * Logger
	 */
	private final IActivityLogger pLogger;

	private final Resources pResources;

	/**
	 * @param panel
	 * @param resources
	 */
	public CLicenseConsolePanel(PanelView<ConsolePanel> panel, Resources resources) {

		super(panel, resources);
		pResources = resources;

		// get logger service (using static class CInstallerTools)
		pLogger = getServiceLogger();
		// log
		pLogger.logInfo(this, "<init>", "instanciated panelClass=[%s]", getClass().getName());
	}

	/**
	 * Try to Load "CLicenseConsolePanel.licence_xxx"
	 * 
	 * <pre>
	 * <res id="CLicenseConsolePanel.licence_eng" src="resources/license_eng.txt" />
	 * </pre>
	 * 
	 * @see com.izforge.izpack.panels.licence.AbstractLicenceConsolePanel#loadLicence()
	 */
	protected URL loadLicence() {
		final String resNamePrefix = getClass().getSimpleName();
		String resNameStr = resNamePrefix + DEFAULT_SUFFIX;

		URL url = null;

		try {
			url = pResources.getURL(resNameStr);
		} catch (Exception e) {
			pLogger.logSevere(this, "loadLicence", "CAN'T LOAD LICENCE : %s",
					CXException.eCauseMessagesInString(e));

		}

		return url;
	}
}