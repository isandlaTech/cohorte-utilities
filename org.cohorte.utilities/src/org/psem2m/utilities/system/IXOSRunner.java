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
	String getBuffEncoding();

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
	String getRepport();

	/**
	 * @param aParts
	 *            an array of repport parts
	 * @return
	 */
	String getRepport(final ERepportPart[] aParts);

	/**
	 * @return
	 */
	long getRunElapsedTime();

	/**
	 * @return
	 */
	String getRunStdErr();

	/**
	 * @return
	 */
	String getRunStdOut();

	/**
	 * @return
	 */
	StringBuilder getStdErrBuffer();

	/**
	 * @return
	 */
	StringBuilder getStdOutBuffer();

	/**
	 * @return
	 */
	boolean hasRunException();

	/**
	 * @return
	 */
	boolean hasRunStdOutput();

	/**
	 * @return
	 */
	boolean hasRunStdOutputErr();

	/**
	 * @return
	 */
	boolean hasRunTimeOut();

	/**
	 * @return
	 */
	boolean isLaunched();

	/**
	 * @return
	 */
	boolean isRunOk();

	/**
	 * @return
	 */
	boolean isRunTimeOutDetected();

	/**
	 * 
	 */
	void razRunStdOutput();

	/**
	 * 
	 */
	void razRunStdOutputErr();

}
