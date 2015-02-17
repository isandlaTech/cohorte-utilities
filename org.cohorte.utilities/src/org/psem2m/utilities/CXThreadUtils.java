package org.psem2m.utilities;

import java.util.Set;

/**
 * @author ogattaz
 * 
 */
public class CXThreadUtils {

	/**
	 * @param aName
	 *            the name the searched thread
	 * @return the thread having the name "aName" if it exists in the list of
	 *         active thread of the current group
	 */
	public static Thread getActiveThread(final String aName) {
		if (aName != null && !aName.isEmpty()) {

			for (Thread wThread : getActiveThreads()) {
				if (wThread != null && aName.equals(wThread.getName())) {
					return wThread;
				}

			}
		}
		return null;
	}

	/**
	 * @return
	 */
	public static Thread[] getActiveThreads() {
		// Returns an estimate of the number of active threads in the current
		// thread's thread group and its subgroups. Recursively iterates over
		// all subgroups in the current thread's thread group.

		// The value returned is only an estimate because the number of threads
		// may change dynamically while this method traverses internal data
		// structures, and might be affected by the presence of certain system
		// threads. This method is intended primarily for debugging and
		// monitoring purposes.
		int wCountActive = new Double(Thread.activeCount() * 1.5).intValue();

		Thread[] wThreadsArray = new Thread[wCountActive];
		// Copies into the specified array every active thread in the current
		// thread's thread group and its subgroups. This method simply invokes
		// the java.lang.ThreadGroup.enumerate(Thread[]) method of the current
		// thread's thread group.
		Thread.enumerate(wThreadsArray);

		return wThreadsArray;
	}

	/**
	 * @param aName
	 * @return
	 */
	public static Thread getLiveThread(final String aName) {

		if (aName != null && !aName.isEmpty()) {

			Set<Thread> wThreadSet = getLiveThreads();

			for (Thread wThread : wThreadSet) {
				if (aName.equals(wThread.getName())) {
					return wThread;
				}
			}
		}
		return null;
	}

	/**
	 * @return a map of set of all live threads.
	 */
	public static Set<Thread> getLiveThreads() {
		// Returns a map of stack traces for all live threads. The map keys
		// are threads and each map value is an array of StackTraceElement
		// that represents the stack dump of the corresponding Thread.
		return Thread.getAllStackTraces().keySet();
	}

	/**
	 * @param aDuration in milli-second
	 * @return false if interupted, true if the sleeping is complete
	 */
	public static boolean sleep(final long aDuration) {
		try {
			Thread.sleep(aDuration);
			return true;
		} catch (InterruptedException e) {
			return false;
		}
	}

	/**
	 * @param aDuration in milli-second
	 * @return false if interupted, true if the sleeping is complete
	 */
	public static boolean sleep(final String aDuration) {

		return sleep(Long.parseLong(aDuration));
	}

}
