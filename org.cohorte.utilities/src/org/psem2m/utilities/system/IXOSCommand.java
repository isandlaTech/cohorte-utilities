package org.psem2m.utilities.system;

import java.io.File;
import java.util.Map;

/**
 * @author ogattaz
 * 
 */
public interface IXOSCommand extends IXOSRunner {

	/**
	 * @return
	 */
	EXCommandState getCommandState();

	/**
	 * @return true if all is OK
	 */
	boolean run();

	/**
	 * @param aTimeOut
	 *            no timeout if <= 0
	 * @return true if all is OK
	 */
	boolean run(final long aTimeOut);

	/**
	 * @param aTimeOut
	 *            no timeout if <= 0
	 * @param aUserDir
	 *            the "user dir" of the command
	 * @return true if all is OK
	 */
	boolean run(final long aTimeOut, final File aUserDir);

	/**
	 * @param aTimeOut
	 *            no timeout if <= 0
	 * @param aUserDir
	 *            the "user dir" of the command
	 * @param aEnv
	 *            a set of variables added in the context of the command
	 * @return true if all is OK
	 */
	boolean run(final long aTimeOut, final File aUserDir, final Map<String, String> aEnv);

	public boolean runAntWait(final long aTimeOut, final File aUserDir, String aCharacterEnd,
			final Map<String, String> aEnv);
}
