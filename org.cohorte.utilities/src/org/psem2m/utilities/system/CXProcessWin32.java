package org.psem2m.utilities.system;

import org.psem2m.utilities.CXException;

/**
 * @url http://hg.openjdk.java.net/jdk8/jdk8/jdk/file/tip/src/windows/classes
 *      /java/lang/ProcessImpl.java
 *
 * @see Java How to get the PID from a process?
 *      http://www.golesny.de/p/code/javagetpid
 *
 * @author ogattaz
 *
 */
public class CXProcessWin32 extends CXProcess {

	private static IXOSProcessPidSearcher sOSProcessPidSearcher = null;

	/**
	 * @param aProcess
	 *            an Process instance
	 * @return true if the class name of the Process instance is known as a
	 *         Win32 process representation
	 */
	static boolean isProcessWin32(final Process aProcess) {
		// MOD_BD_20161228 correct test against java.lang.ProcessImpl for jre 8
		// (missing getName())
		return aProcess != null
				&& (aProcess.getClass().getName()
						.equals("java.lang.ProcessImpl") || aProcess.getClass()
						.getName().equals("java.lang.Win32Process"));
	}

	/**
	 * Use a synchronized static method. This synchronizes on the class object.
	 *
	 * @see http
	 *      ://stackoverflow.com/questions/2120248/how-to-synchronize-a-static
	 *      -variable-among-threads-running-different-instances-o
	 *
	 * @param aOSProcessPidSearcher
	 *            an instance of IXOSProcessPidSearcher
	 */
	public static synchronized void setOSProcessPidSearcher(
			final IXOSProcessPidSearcher aOSProcessPidSearcher) {

		sOSProcessPidSearcher = aOSProcessPidSearcher;
	}

	/**
	 * @param aProcess
	 */
	CXProcessWin32(final Process aProcess) {
		super(aProcess);
	}

	/**
	 * Explicitly synchronize on the class object to synchronize the static
	 * variable
	 *
	 * @see http
	 *      ://stackoverflow.com/questions/2120248/how-to-synchronize-a-static
	 *      -variable-among-threads-running-different-instances-o
	 * @return
	 */
	private IXOSProcessPidSearcher getOSProcessPidSearcher() {

		synchronized (CXProcessWin32.class) {
			return sOSProcessPidSearcher;
		}
	}

	/*
	 * retreive the "IXProcessWin32Service" to get the PID on Win32 systems
	 *
	 * @see http://www.golesny.de/p/code/javagetpid
	 *
	 * (non-Javadoc)
	 *
	 * @see org.psem2m.utilities.system.CXProcess#getPid()
	 */
	@Override
	public int getPid() {
		int wPid = -1;
		try {

			wPid = getProcessPid();

		} catch (Throwable e) {
			System.err.println(String.format(
					"Unable to retreive the pid of the %s :\n%s",
					getProcessKind(), CXException.eInString(e)));
		}
		return wPid;
	}

	/**
	 * @return the pid of the process associated to this
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private int getProcessPid() throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {

		// if a pid searcher is set, uses it to retreive the pid of the process
		IXOSProcessPidSearcher wOSProcessPidSearcher = getOSProcessPidSearcher();

		return (wOSProcessPidSearcher != null) ? wOSProcessPidSearcher
				.getPid(getProcess()) : -1;
	}
}
