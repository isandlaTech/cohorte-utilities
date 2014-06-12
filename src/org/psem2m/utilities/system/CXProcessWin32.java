package org.psem2m.utilities.system;

import org.cohorte.utilities.Activator;
import org.psem2m.utilities.CXException;

/**
 * @author ogattaz
 * 
 */
public class CXProcessWin32 extends CXProcess {

	/**
	 * @param aProcess
	 * @return
	 */
	static boolean isProcessWin32(final Process aProcess) {
		return aProcess.getClass().getName().equals("java.lang.Win32Process");
	}

	/**
	 * @param aProcess
	 */
	CXProcessWin32(final Process aProcess) {
		super(aProcess);
	}

	@Override
	int getPid() {
		int wPid = -1;
		try {
			/*
			 * retreive the "IXProcessWin32Service" to get the PID on Win32
			 * systems
			 */
			IXProcessWin32Service wProcessWin32Service = Activator
					.getService(IXProcessWin32Service.class);

			wPid = wProcessWin32Service.getProcessPid(getProcess());

		} catch (Throwable e) {
			System.err.println(String.format(
					"Unable to retreive the pid of the %s :\n%s",
					getProcessKind(), CXException.eInString(e)));
		}
		return wPid;
	}
}
