package org.cohorte.utilities.installer.panels.updater;

import static org.cohorte.utilities.installer.CInstallerTools.getServiceLogger;

import java.io.PrintWriter;
import java.util.Properties;

import org.psem2m.utilities.logging.IActivityLogger;

import com.izforge.izpack.api.adaptator.IXMLElement;
import com.izforge.izpack.api.data.InstallData;
import com.izforge.izpack.installer.console.ConsolePanel;
import com.izforge.izpack.util.Console;

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
public class CUpdaterConsolePanel implements ConsolePanel {

	/**
	 * Logger
	 */
	private final IActivityLogger pLogger;

	/**
	 * @param unpacker
	 * @param panel
	 */
	public CUpdaterConsolePanel() {

		super();

		// get logger service (using static class CInstallerTools)
		pLogger = getServiceLogger();
		// log
		pLogger.logInfo(this, "<init>", "instanciated panelClass=[%s]", getClass().getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.izforge.izpack.installer.console.ConsolePanel#generateProperties(
	 * com.izforge.izpack.api.data.InstallData, java.io.PrintWriter)
	 */
	public boolean generateProperties(InstallData installData, PrintWriter printWriter) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.izforge.izpack.installer.console.ConsolePanel#run(com.izforge.izpack
	 * .api.data.InstallData, java.util.Properties)
	 */
	public boolean run(InstallData installData, Properties properties) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.izforge.izpack.installer.console.ConsolePanel#run(com.izforge.izpack
	 * .api.data.InstallData, com.izforge.izpack.util.Console)
	 */
	public boolean run(InstallData installData, Console console) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.izforge.izpack.installer.console.ConsolePanel#createInstallationRecord
	 * (com.izforge.izpack.api.adaptator.IXMLElement)
	 */
	public void createInstallationRecord(IXMLElement rootElement) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.izforge.izpack.installer.console.ConsolePanel#handlePanelValidationResult
	 * (boolean)
	 */
	public boolean handlePanelValidationResult(boolean valid) {
		// TODO Auto-generated method stub
		return false;
	}

}
