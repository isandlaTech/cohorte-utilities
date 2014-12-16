package org.cohorte.utilities.picosoc;

import java.io.File;
import java.util.logging.Level;

/**
 * @author ogattaz
 *
 */
public interface ISvcLoggerConfigurator {

	/**
	 * @return the File of an existing directory
	 */
	File getDirLogs() throws Exception;

	/**
	 * @return
	 */
	Level getLevel();
	
	/**
	 * @return the name of the Logger
	 */
	String getLoggerName();
}
