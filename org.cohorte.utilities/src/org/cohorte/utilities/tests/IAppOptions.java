package org.cohorte.utilities.tests;

/**
 * @author ogattaz
 * 
 */
public interface IAppOptions {

	public final static String OPT_DEBUG = "debug";
	public final static String OPT_USAGE = "usage";
	public final static String OPT_VERBOSE = "verbose";
	public final static String OPT_KIND = "kind";
	public final static String OPT_ACTION = "action";
	public final static String OPT_NAME = "name";
	public final static String OPT_VALUE = "value";

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
