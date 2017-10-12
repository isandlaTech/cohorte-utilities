package org.psem2m.utilities.system.win32;

/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.  
 */

import com.sun.jna.Native;

/**
 * 
 * @see https://jna.dev.java.net/
 * 
 * @see http://en.wikipedia.org/wiki/Java_Native_Access
 * 
 * @see http://www.golesny.de/p/code/javagetpid
 * 
 * @author Timothy Wall,
 * 
 */
public interface Kernel32 extends W32API {

	Kernel32 INSTANCE = (Kernel32) Native.loadLibrary("kernel32",
			Kernel32.class, OPTIONS_DEFAULT);

	/**
	 * @return The return value is a pseudo handle to the current process.
	 * @see http://msdn.microsoft.com/en-us/library/ms683179(VS.85).aspx
	 */
	HANDLE GetCurrentProcess();

	/**
	 * Retrieves the process identifier of the specified process.
	 * 
	 * @param Process
	 *            A handle to the process. The handle must have the
	 *            PROCESS_QUERY_INFORMATION or PROCESS_QUERY_LIMITED_INFORMATION
	 *            access right. For more information, see Process Security and
	 *            Access Rights.
	 * @return DWORD
	 * @see http://msdn.microsoft.com/en-us/library/ms683215.aspx
	 */
	int GetProcessId(HANDLE Process);

}
