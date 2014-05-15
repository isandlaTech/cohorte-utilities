package org.psem2m.utilities.system;

import java.lang.reflect.Field;

import org.psem2m.utilities.CXOSUtils;

/**
 * @author ogattaz
 * 
 */
public class CXProcessUnix extends CXProcess {

	/**
	 * @param aProcess
	 * @return
	 */
	static boolean isProcessUnix(final Process aProcess) {
		return aProcess.getClass().getName().equals("java.lang.UNIXProcess");
	}

	/**
	 * @param aProcess
	 */
	CXProcessUnix(final Process aProcess) {
		super(aProcess);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.system.CXProcess#getPid()
	 */
	@Override
	int getPid() {

		int wPid = -1;
		/* get the PID on unix/linux systems */
		try {
			// getDeclaredField to get the private field
			Field f = getProcess().getClass().getDeclaredField("pid");
			f.setAccessible(true);
			wPid = f.getInt(getProcess());
			// décalage de 20 , sur Mac Os X ?
			if (CXOSUtils.isOsMacOsX()) {
				wPid += 20;
			}
		} catch (Throwable e) {
			System.err.println(String.format(
					"Unable to retreive the pid of the %s", getProcessKind()));
		}
		return wPid;
	}
}
