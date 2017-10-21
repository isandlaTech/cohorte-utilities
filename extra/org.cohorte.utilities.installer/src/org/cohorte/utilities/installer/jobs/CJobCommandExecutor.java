package org.cohorte.utilities.installer.jobs;

import static org.cohorte.utilities.installer.CInstallerTools.getServiceLogger;

import java.util.Map;

import org.cohorte.utilities.installer.system.CXOSInstallerCommand;
import org.psem2m.utilities.files.CXFileDir;
import org.psem2m.utilities.logging.IActivityLogger;

import com.izforge.izpack.panels.process.AbstractUIProcessHandler;

public class CJobCommandExecutor {

	/**
	 * Logger
	 */
	private final IActivityLogger pLogger;

	private long CMD_TIME_OUT = 300000;

	public CJobCommandExecutor() {
		// first - retreive the 'logger' service asking the service registry
		pLogger = getServiceLogger();
		// log
		pLogger.logInfo(this, "<init>", "instanciated");
	}

	public void run(AbstractUIProcessHandler handler, String[] args) {
		Map<String, String> wEnv = null;
		String wMsg = "";
		
		if (args == null || args.length < 2) {
			wMsg = "wrong parameters were given to the command executor!";
			handler.emitWarning("OSCommandExecutor", wMsg);
			handler.logOutput(wMsg, false);
			pLogger.logWarn(this, "run", wMsg);
		} else {
			dumpArgs(args);
			String wTitle = args[0];
			wMsg = "START - " + wTitle;
			handler.logOutput(wMsg, false);
			pLogger.logWarn(this, "run", wMsg);
			String wUserDirName = args[1];
			CXFileDir wUserDir = new CXFileDir(wUserDirName);
			if (args.length >= 3) {
				String[] aCmdLineArgs = new String[args.length - 2];
				System.arraycopy(args, 2, aCmdLineArgs, 0, args.length - 2);
				
				execOsCommand(handler, wTitle, CMD_TIME_OUT, wEnv, wUserDir, aCmdLineArgs);
				
			} else {
				wMsg = "No command to execute!";
				handler.logOutput(wMsg, false);
				pLogger.logWarn(this, "run", wMsg);
			}			
			wMsg = "END   - " + wTitle + "\n";
			handler.logOutput(wMsg, false);
			pLogger.logWarn(this, "run", wMsg);
		}

	}

	private void dumpArgs(String[] args) {
		StringBuilder wArgs = new StringBuilder("-- args:");
		for (int i = 0; i < args.length; i++) {
			wArgs.append("").append(args[i]).append("\n");
		}
		pLogger.logDebug(this, "dumpArgs", wArgs.toString());
	}

	private void execOsCommand(AbstractUIProcessHandler handler, String wJobTitle, final long aTimeOut,
			final Map<String, String> aEnv, final CXFileDir aUserDir,
			final String... aCmdLineArgs) {
		
		CXOSInstallerCommand wCommand = new CXOSInstallerCommand(pLogger, handler, aCmdLineArgs);
		boolean wOk = wCommand.run(aTimeOut, aUserDir);
		
		pLogger.logDebug(this, "run", wCommand.getRepport());
		if (wOk) {
			// all is ok
			// nothing to do
		} else {			
				String wMsg = "Exception while executing command of the job '" + wJobTitle 
						+ "'. \n" + wCommand.getErrMess();
				//handler.emitError("OSCommandExecutor", wMsg);
				handler.logOutput(wMsg, false);
				pLogger.logWarn(this, "run", wMsg + ".\n" + wCommand.getRepport());
				handler.finishProcess();				
		}
	}	
}
