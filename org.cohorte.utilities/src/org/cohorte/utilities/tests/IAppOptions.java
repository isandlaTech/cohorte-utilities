package org.cohorte.utilities.tests;

/**
 * @author ogattaz
 * 
 */
public interface IAppOptions {

	/**
	 * @return
	 */
	public String getUsage();

	/**
	 * @return
	 * @throws Exception
	 */
	public boolean isDebugOn() throws Exception;

	/**
	 * @return
	 * @throws Exception
	 */
	public boolean isVerboseOn() throws Exception;

}
