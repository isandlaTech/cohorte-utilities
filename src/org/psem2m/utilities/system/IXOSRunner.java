package org.psem2m.utilities.system;

/**
 * @author ogattaz
 * 
 */
public interface IXOSRunner {

	/**
	 * @param aLine
	 */
	int consumeStdOutputErrLine(final String aLine);

	/**
	 * @param aLine
	 */
	int consumeStdOutputLine(final String aLine);

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
	String getLaunchTimeStamp();

	/**
	 * @return
	 */
	StringBuilder getStdOutBuffer();

	/**
	 * @return
	 */
	StringBuilder getStdErrBuffer();

	/**
	 * @return
	 */
	long getRunElapsedTime();

	/**
	 * @return
	 */
	boolean hasRunStdOutput();

	/**
	 * @return
	 */
	boolean hasRunStdOutputErr();

	/**
	 * 
	 */
	void razRunStdOutput();

	/**
	 * 
	 */
	void razRunStdOutputErr();

}
