package org.psem2m.utilities.system;

import java.lang.reflect.Field;

/**
 * @see http://hg.openjdk.java.net/jdk8/jdk8/jdk/file/687fd7c7986d/src/solaris/
 *      classes/java/lang/UNIXProcess.java.linux
 * 
 * @see Java How to get the PID from a process?
 *      http://www.golesny.de/p/code/javagetpid
 * 
 * @author ogattaz
 * 
 */
public class CXProcessUnix extends CXProcess {

	/**
	 * @param aProcess
	 * @return
	 */
	public static boolean isProcessUnix(final Process aProcess) {

		return aProcess != null
				&& aProcess.getClass().getName()
						.equals("java.lang.UNIXProcess");
	}

	/**
	 * @param aProcess
	 */
	CXProcessUnix(final Process aProcess) {
		super(aProcess);
	}

	/*
	 * get the PID on unix/linux systems
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
			// getDeclaredField to get the private field
			Field wFieldPid = getProcess().getClass().getDeclaredField("pid");
			wFieldPid.setAccessible(true);
			// get the int value of the field for the current instance oProcess
			wPid = wFieldPid.getInt(getProcess());
		} catch (Throwable e) {
			System.err.println(String.format(
					"Unable to retreive the pid of the %s", getProcessKind()));
		}
		return wPid;
	}

}
