package org.psem2m.utilities.system;

import java.lang.management.ManagementFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @see Java How to get the PID from a process?
 *      http://www.golesny.de/p/code/javagetpid
 * 
 * @author ogattaz
 * 
 */
public abstract class CXProcess {

	/**
	 * @return the name of the current process using the format "pid@name"
	 */
	public static String getCurrentProcessName() {
		return ManagementFactory.getRuntimeMXBean().getName();
	}

	/**
	 * @return the pid of the current process
	 */
	public static int getCurrentProcessPid() {
		return getProcessPid(getCurrentProcessName());
	}

	/**
	 * @return the up time of the current process
	 */
	public static long getCurrentProcessUpTime() {
		return ManagementFactory.getRuntimeMXBean().getUptime();
	}

	/**
	 * 
	 * tested with the jvm :
	 * <ul>
	 * <li>windows xp sp 2, java 1.5.0_13
	 * <li>mac os x 10.4.10, java 1.5.0
	 * <li>debian linux, java 1.5.0_13
	 * </ul>
	 * 
	 * all return pid@host, e.g 2204@antonius
	 * 
	 * @url 
	 *      https://github.com/wangscu/jessica/blob/master/src/main/java/com/mogujie
	 *      /storeage/bitcask/OS.java
	 * 
	 * 
	 * @param processName
	 *            the name of the process return by the RuntimeMXBean. The
	 *            format must be "pid@host"
	 * @return the pid
	 */
	private static Integer getProcessPid(final String processName) {
		Integer wPid = null;

		Pattern wPattern = Pattern.compile("^([0-9]+)@.+$",
				Pattern.CASE_INSENSITIVE);
		Matcher matcher = wPattern.matcher(processName);
		if (matcher.matches()) {
			wPid = new Integer(Integer.parseInt(matcher.group(1)));
		}
		return wPid;
	}

	/**
	 * @param aProcess
	 * @return an instance of CXProcess according the kind of the passed
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
		}
		//
		else if (CXProcessWin32.isProcessWin32(aProcess)) {
			return new CXProcessWin32(aProcess);
		}

		throw new IllegalArgumentException(
				String.format(
						"Unable to instanciate a CXProcess with a unknonw kind of java.lang.Process. Implementation class=[%s]",
						aProcess.getClass().getName()));
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
	 * @return the PID of the instance of Process
	 */
	public abstract int getPid();

	/**
	 * @return the instance of Process
	 */
	Process getProcess() {
		return pProcess;
	}

	/**
	 * @return the simple name of the process instance as kind => "ProcessImpl"
	 *         or "UNIXProcess"
	 */
	String getProcessKind() {
		return getProcess().getClass().getSimpleName();
	}
}
