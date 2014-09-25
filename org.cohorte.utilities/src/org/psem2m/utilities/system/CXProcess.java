package org.psem2m.utilities.system;

import java.lang.management.ManagementFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ogattaz
 * 
 */
public abstract class CXProcess {

	/**
	 * @return
	 */
	public static String getCurrentProcessName() {
		return ManagementFactory.getRuntimeMXBean().getName();
	}

	/**
	 * @return
	 */
	public static int getCurrentProcessPid() {
		return tryPattern1(getCurrentProcessName());
	}

	/**
	 * @return
	 */
	public static long getCurrentProcessUpTime() {
		return ManagementFactory.getRuntimeMXBean().getUptime();
	}

	/**
	 * @param aProcess
	 * @return an instance of CXProcess according the king of the passed
	 *         java.lang.Process
	 */
	static CXProcess newCXProcess(final Process aProcess)
			throws IllegalArgumentException {

		if (aProcess == null) {
			throw new IllegalArgumentException(
					"Unable to instanciate a CXProcess with a null java.lang.Process");
		}

		if (CXProcessUnix.isProcessUnix(aProcess)) {
			return new CXProcessUnix(aProcess);
		} else if (CXProcessWin32.isProcessWin32(aProcess)) {
			return new CXProcessWin32(aProcess);
		}
		throw new IllegalArgumentException(
				String.format(
						"Unable to instanciate a CXProcess with a unknonw kind of java.lang.Process",
						aProcess.getClass().getName()));
	}

	/**
	 * @param processName
	 * @return
	 */
	private static Integer tryPattern1(final String processName) {
		Integer result = null;

		/* tested on: */
		/* - windows xp sp 2, java 1.5.0_13 */
		/* - mac os x 10.4.10, java 1.5.0 */
		/* - debian linux, java 1.5.0_13 */
		/* all return pid@host, e.g 2204@antonius */

		Pattern pattern = Pattern.compile("^([0-9]+)@.+$",
				Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(processName);
		if (matcher.matches()) {
			result = new Integer(Integer.parseInt(matcher.group(1)));
		}
		return result;
	}

	private final Process pProcess;

	/**
	 * @param aProcess
	 */
	CXProcess(final Process aProcess) throws IllegalArgumentException {
		super();
		if (aProcess == null) {
			throw new IllegalArgumentException(
					"Unable to instanciate a CXProcess with a null java.lang.Process");
		}
		pProcess = aProcess;
	}

	/**
	 * @return the PID of the process
	 */
	public abstract int getPid();

	/**
	 * 
	 */
	Process getProcess() {
		return pProcess;
	}

	/**
	 * @return
	 */
	String getProcessKind() {
		return getProcess().getClass().getSimpleName();
	}
}
