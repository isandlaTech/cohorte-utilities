package org.cohorte.utilities.picosoc;

import org.psem2m.utilities.logging.EActivityLogColumn;

/**
 * @author ogattaz
 *
 */
public interface ISvcActivityLoggerConfigurator extends ISvcLoggerConfigurator{

	/**
	 * @return the size limit of each log File 
	 */
	int getFileLimit();
	
	/**
	 * @return
	 */
	EActivityLogColumn[]  getLineDef();

	/**
	 * @return the number of log File kept in the dir
	 */
	int getNbFile();
	
	/**
	 * @return true if the activity log format is multi-line
	 */
	boolean isMultiline();
}
