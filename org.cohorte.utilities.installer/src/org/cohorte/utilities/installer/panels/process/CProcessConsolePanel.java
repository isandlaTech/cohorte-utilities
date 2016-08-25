package org.cohorte.utilities.installer.panels.process;

import static org.cohorte.utilities.installer.CInstallerTools.getServiceLogger;

import org.psem2m.utilities.logging.IActivityLogger;

import com.izforge.izpack.api.handler.Prompt;
import com.izforge.izpack.api.resource.Resources;
import com.izforge.izpack.api.rules.RulesEngine;
import com.izforge.izpack.installer.console.ConsolePanel;
import com.izforge.izpack.installer.panel.PanelView;
import com.izforge.izpack.panels.process.ProcessConsolePanel;
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
public class CProcessConsolePanel extends ProcessConsolePanel {

	/**
	 * Logger
	 */
	private final IActivityLogger pLogger;

	/**
	 * @param rules
	 * @param resources
	 * @param prompt
	 * @param matcher
	 * @param panel
	 */
	public CProcessConsolePanel(RulesEngine rules, Resources resources, Prompt prompt,
			PlatformModelMatcher matcher, PanelView<ConsolePanel> panel) {

		super(rules, resources, prompt, matcher, panel);

		// get logger service (using static class CInstallerTools)
		pLogger = getServiceLogger();
		// log
		pLogger.logInfo(this, "<init>", "instanciated panelClass=[%s]", getClass().getName());
	}

	private int pJobIdx = 0;
	private int pNbJob = 0;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.izforge.izpack.panels.process.ProcessConsolePanel#startProcessing
	 * (int)
	 */
	@Override
	public void startProcessing(int no_of_processes) {
		pNbJob = no_of_processes;
		pLogger.logInfo(this, "startProcessing", "Nb Jobs [%s]", pNbJob);
		super.startProcessing(no_of_processes);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.izforge.izpack.panels.process.ProcessConsolePanel#startProcess(java
	 * .lang.String)
	 */
	@Override
	public void startProcess(String name) {
		pJobIdx++;
		pLogger.logInfo(this, "startProcess", "Job [%s/%s] [%s]", pJobIdx, pNbJob, name);
		super.startProcess(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.izforge.izpack.panels.process.ProcessConsolePanel#finishProcess()
	 */
	@Override
	public void finishProcess() {
		pLogger.logInfo(this, "finishProcess", "Jobs done [%s/%s] ", pJobIdx, pNbJob);
		super.finishProcess();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.izforge.izpack.panels.process.ProcessConsolePanel#finishProcessing
	 * (boolean, boolean)
	 */
	@Override
	public void finishProcessing(boolean unlockPrev, boolean unlockNext) {
		pLogger.logInfo(this, "finishProcessing", "Job [%s/%s] unlockPrev=[%s] unlockNext=[%s]", pJobIdx,
				pNbJob, unlockPrev, unlockNext);

		super.finishProcessing(unlockPrev, unlockNext);
	}
}