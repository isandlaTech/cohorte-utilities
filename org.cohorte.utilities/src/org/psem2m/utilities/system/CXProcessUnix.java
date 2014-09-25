package org.psem2m.utilities.system;

import java.lang.reflect.Field;

/**
 * @author ogattaz
 * 
 */
public class CXProcessUnix extends CXProcess {

	/**
	 * @param aProcess
	 * @return
	 */
	public static boolean isProcessUnix(final Process aProcess) {
		return aProcess.getClass().getName().equals("java.lang.UNIXProcess");
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
