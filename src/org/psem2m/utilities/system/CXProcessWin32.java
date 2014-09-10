package org.psem2m.utilities.system;

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
			// IXProcessWin32Service wProcessWin32Service = Activator
			// .getService(IXProcessWin32Service.class);
			//
			// wPid = wProcessWin32Service.getProcessPid(getProcess());

		} catch (Throwable e) {
			System.err.println(String.format(
					"Unable to retreive the pid of the %s :\n%s",
					getProcessKind(), CXException.eInString(e)));
		}
		return wPid;
	}
}
