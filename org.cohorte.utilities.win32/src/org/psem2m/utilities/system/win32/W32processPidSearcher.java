package org.psem2m.utilities.system.win32;

import java.lang.reflect.Field;

import org.psem2m.utilities.system.IXOSProcessPidSearcher;

import com.sun.jna.Pointer;

/**
 * @author ogattaz
 * 
 */
public class W32processPidSearcher implements IXOSProcessPidSearcher {

	/**
	 * 
	 */
	public W32processPidSearcher() {

		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.system.IXOSProcessPidSearcher#getPid(long)
	 */
	@Override
	public int getPid(final Process aProcess) {

		try {
			W32API.HANDLE handle = new W32API.HANDLE();
			handle.setPointer(Pointer
					.createConstant(getProcessHandle(aProcess)));

			Kernel32 wKernel32 = Kernel32.INSTANCE;
			return wKernel32.GetProcessId(handle);

		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * java 7 & 8 :
	 * 
	 * @url 
	 *      http://hg.openjdk.java.net/jdk8/jdk8/jdk/file/tip/src/windows/classes
	 *      /java/lang/ProcessImpl.java
	 * 
	 * @param aProcess
	 * @return
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	long getProcessHandle(final Process aProcess) throws NoSuchFieldException,
			SecurityException, IllegalArgumentException, IllegalAccessException {

		// getDeclaredField to get the private field "handle"
		Field f = aProcess.getClass().getDeclaredField("handle");
		f.setAccessible(true);
		return f.getLong(aProcess);
	}

}
