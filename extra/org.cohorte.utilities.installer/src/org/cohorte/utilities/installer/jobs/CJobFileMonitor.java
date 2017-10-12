package org.cohorte.utilities.installer.jobs;

import static org.cohorte.utilities.installer.CInstallerTools.getServiceLogger;

import java.util.Map;

import org.psem2m.utilities.files.CXFile;
import org.psem2m.utilities.files.CXFileDir;
import org.psem2m.utilities.logging.IActivityLogger;

import com.izforge.izpack.panels.process.AbstractUIProcessHandler;

/**
 * File Monitor Job.
 * 
 * Monitors if a file is created.
 * 
 * @author bdebbabi
 *
 */
public class CJobFileMonitor {
	
	/**
	 * Number of arguments
	 */
	private static final int NBR_ARGUMENTS = 2;

	/**
	 * Logger
	 */
	private final IActivityLogger pLogger;
	
	/**
	 * Default timeout
	 */
	private long CMD_TIME_OUT = 300000;
	
	
	/**
	 * Constructor
	 */
	public CJobFileMonitor() {
		// first - retreive the 'logger' service asking the service registry
		pLogger = getServiceLogger();
		// log
		pLogger.logInfo(this, "<init>", "instanciated");
	}
	
	public void run(AbstractUIProcessHandler handler, String[] args) {
		Map<String, String> wEnv = null;
		String wMsg = "";
		
		if (args == null || args.length < NBR_ARGUMENTS) {
			wMsg = "wrong parameters were given to the command executor!";
			handler.emitWarning("OSCommandExecutor", wMsg);
			handler.logOutput(wMsg, false);
			pLogger.logWarn(this, "run", wMsg);
		} else {
			dumpArgs(args);
			String wTitle = args[0];
			
			wMsg = wTitle;
			handler.logOutput(wMsg, false);
			pLogger.logWarn(this, "run", wMsg);
			
			String wFilePath = args[1];
			long timeoutInSeconds = 20;
			
			long now = System.currentTimeMillis();
		    long timeoutInMillis = 1000L * timeoutInSeconds;
		    long finish = now + timeoutInMillis;
			
			CXFile wFile = new CXFile(wFilePath);
			long wTimeout = 0;
			while (wFile.exists() == false && ( System.currentTimeMillis() < finish )) {
				try {
					Thread.sleep( 10 );
				} catch (InterruptedException e) {
					wMsg = "Timeout reached!\n";			
					handler.logOutput(wMsg, false);
					pLogger.logInfo(this, "run", wMsg);
					return;
				}
			}
			wMsg = "Monitored file "+wFilePath+"found!";
			handler.logOutput("Ok \n", false);
			pLogger.logInfo(this, "run", wMsg);			
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
