package org.psem2m.utilities.logging;

import java.util.logging.Level;

import org.psem2m.utilities.IXDescriber;

/**
 * @author Adonix Grenoble
 * @version 140
 */
public interface IActivityLogger extends IActivityLoggerBase, IXDescriber {

	/**
	   * 
	   */
	public void close();
	
	/**
	 * @return
	 */
	public IActivityRequester getRequester();

	/**
	 * @param aLevel
	 */
	public void setLevel(Level aLevel);

}
