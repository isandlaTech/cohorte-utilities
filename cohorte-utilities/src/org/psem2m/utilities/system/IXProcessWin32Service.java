package org.psem2m.utilities.system;

/**
 * @author ogattaz
 * 
 */
public interface IXProcessWin32Service {

	/**
	 * @param aProcess
	 * @return
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	int getProcessPid(final Process aProcess) throws NoSuchFieldException,
			SecurityException, IllegalArgumentException, IllegalAccessException;

}
