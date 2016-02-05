package org.cohorte.utilities.installer.jobs;

import static org.cohorte.utilities.installer.CInstallerTools.getServiceLogger;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Map;

import org.psem2m.utilities.files.CXFile;
import org.psem2m.utilities.files.CXFileText;
import org.psem2m.utilities.files.CXFileUtf8;
import org.psem2m.utilities.logging.IActivityLogger;

import com.izforge.izpack.panels.process.AbstractUIProcessHandler;

/**
 * Creates a new Text File with the content provided as argument.
 * 
 * @author bdebbabi
 *
 */
public class CJobFileTextCreator {

	/**
	 * Number of arguments
	 */
	private static final int NBR_ARGUMENTS = 4;
	
	/**
	 * Logger
	 */
	private final IActivityLogger pLogger;
	
	public CJobFileTextCreator() {
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
			
			wMsg = "START - " + wTitle;
			handler.logOutput(wMsg, false);
			pLogger.logWarn(this, "run", wMsg);
			
			String wPathToFileToCreate = args[1];
			String wFileContent = args[2];
			String wSaveOldFile = args[3];
			
			// chck if saveOldFile is true
			CXFileUtf8 wFile = new CXFileUtf8(wPathToFileToCreate);
			//CXFileText wFile = new CXFileText(wPathToFileToCreate);
			if (wFile.exists() && wSaveOldFile.equalsIgnoreCase("true")) {
				CXFile wOldFile = new CXFile(wPathToFileToCreate + ".old");
				try {
					wFile.copyTo(wOldFile, true);
				} catch (IOException e) {					
					//e.printStackTrace();
					pLogger.logSevere(this, "run", "Cannot write a copy of %s file!", wPathToFileToCreate);
				} finally {
					wFile.close();
				}
			}
			// write new file 
			// HINT: CXFileXXX classes generate UTF-8-BOM encoded files.
			//       we avoid to use them to write new file as Agilium is sensible for that.
			Writer out = null;
			try {
				out = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(wPathToFileToCreate), "UTF-8"));
				out.write(wFileContent);
			} catch (UnsupportedEncodingException e) {
				pLogger.logSevere(this, "run", "Cannot write file [%s]! Unsupported Encoding Exception [%s]!", 
										wPathToFileToCreate, "UTF-8");
				e.printStackTrace();				
			} catch (FileNotFoundException e) {
				pLogger.logSevere(this, "run", "Cannot write to non existing file [%s]! Exception [%s]", 
						wPathToFileToCreate, e.getMessage());
			} catch (IOException e) {
				pLogger.logSevere(this, "run", "Cannot write the file [%s]! IOException [%s]", 
						wPathToFileToCreate, e.getMessage());
			} finally {
			    try {
			    	if (out != null) out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
						
			wMsg = "File "+wPathToFileToCreate+" created!";
			//handler.logOutput("Ok \n", false);
			pLogger.logInfo(this, "run", wMsg);	
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
}
