package org.cohorte.utilities.installer.jobs;

import static org.cohorte.utilities.installer.CInstallerTools.getServiceLogger;

import java.util.Map;

import org.psem2m.utilities.files.CXFile;
import org.psem2m.utilities.logging.IActivityLogger;

import com.izforge.izpack.panels.process.AbstractUIProcessHandler;

public class CJobEcho {

	/**
	 * Logger
	 */
	private final IActivityLogger pLogger;
	
	/**
	 * Constructor
	 */
	public CJobEcho() {
		// first - retreive the 'logger' service asking the service registry
		pLogger = getServiceLogger();
		// log
		pLogger.logInfo(this, "<init>", "instanciated");
	}
	
	public void run(AbstractUIProcessHandler handler, String[] args) {
		Map<String, String> wEnv = null;
		String wMsg = "";
		
		if (args == null || args.length < 1) {
			wMsg = "wrong parameters were given to the command executor!";
			handler.emitWarning("OSCommandExecutor", wMsg);
			handler.logOutput(wMsg, false);
			pLogger.logWarn(this, "run", wMsg);
		} else {
			dumpArgs(args);
			String wMessage = args[0];			
			handler.logOutput(wMessage, false);
			pLogger.logWarn(this, "run", wMessage);				
		}

	}

	private void dumpArgs(String[] args) {
		StringBuilder wArgs = new StringBuilder("-- args:");
		for (int i = 0; i < args.length; i++) {
			wArgs.append("").append(args[i]).append("\n");
		}
		pLogger.logDebug(this, "dumpArgs", wArgs.toString());
	}
}
