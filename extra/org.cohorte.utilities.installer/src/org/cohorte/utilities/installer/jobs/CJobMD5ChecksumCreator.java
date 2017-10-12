package org.cohorte.utilities.installer.jobs;

import static org.cohorte.utilities.installer.CInstallerTools.getServiceLogger;

import java.util.Map;

import org.cohorte.utilities.installer.system.CXOSInstallerCommand;
import org.psem2m.utilities.files.CXFile;
import org.psem2m.utilities.files.CXFileDir;
import org.psem2m.utilities.files.CXFileUtf8;
import org.psem2m.utilities.logging.IActivityLogger;
import org.cohorte.utilities.crypto.*;

import com.izforge.izpack.panels.process.AbstractUIProcessHandler;

public class CJobMD5ChecksumCreator {

	/**
	 * Logger
	 */
	private final IActivityLogger pLogger;

	public CJobMD5ChecksumCreator() {
		// first - retreive the 'logger' service asking the service registry
		pLogger = getServiceLogger();
		// log
		pLogger.logInfo(this, "<init>", "instanciated");
	}

	public void run(AbstractUIProcessHandler handler, String[] args) {
		
		String wMsg = "";
		
		if (args == null || args.length < 1) {
			wMsg = "wrong parameters were given to the command executor!";
			handler.emitWarning("CJobMD5ChecksumCreator", wMsg);
			handler.logOutput(wMsg, false);
			pLogger.logWarn(this, "run", wMsg);
		} else {
			dumpArgs(args);
			String wFile = args[0];
			try {
				CXFile wOriginalFile = new CXFile(wFile);
				String wHash = CMD5Utils.getMD5Checksum(wOriginalFile);
				if (wHash != null) {
					CXFileUtf8 wMD5File = new CXFileUtf8(wOriginalFile.getParentDirectory().getAbsolutePath(), wOriginalFile.getName() + ".MD5");
					wMD5File.openWrite();
					wMD5File.write(wHash);
					wMD5File.close();
				} else {
					wMsg = String.format("Cannot calculate the MD5 checksum of the file [%s]", wFile);
					handler.logOutput(wMsg, false);
					pLogger.logWarn(this, "run 1", wMsg);
				}
				
			} catch (Exception e) {
				wMsg = "Cannot create the MD5 checksum file!";
				handler.logOutput(wMsg, false);
				pLogger.logWarn(this, "run 2", wMsg);
			}
			wMsg = String.format("MD5 Checksum file created for file [%s]", wFile);
			handler.logOutput(wMsg, false);
			pLogger.logInfo(this, "run 3", wMsg);
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
