package org.psem2m.utilities.system;

/**
 * @author ogattaz
 * 
 */
public interface IXOSCommand {

	/**
	 * @return
	 */
	String[] getCmdLineArgs();

	/**
	 * @return
	 */
	String getCommandLine();

	/**
	 * @return
	 */
	StringBuilder getOutBuffer();

	/**
	 * @return
	 */
	StringBuilder getOutErrBuffer();

}
