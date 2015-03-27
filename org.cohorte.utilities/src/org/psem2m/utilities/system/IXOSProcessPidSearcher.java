package org.psem2m.utilities.system;

/**
 * @author ogattaz
 * 
 */
public interface IXOSProcessPidSearcher {

	/**
	 * @param aProcess
	 *            the instance of Process
	 * @return the pid ogf the process
	 */
	int getPid(final Process aProcess);

}
