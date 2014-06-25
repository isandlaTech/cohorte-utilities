package org.psem2m.utilities.system;


public abstract class CXProcess {

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
	abstract int getPid();

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
