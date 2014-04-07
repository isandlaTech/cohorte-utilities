package org.psem2m.utilities.system.win32;

import com.sun.jna.Native;

/**
 * Copyright (c) 2007 Timothy Wall, All Rights Reserved
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * @author Timothy Wall
 * 
 * @see http://www.golesny.de/p/code/javagetpid
 */
public interface Kernel32 extends W32API {
	/**
	 * 
	 */
	Kernel32 INSTANCE = (Kernel32) Native.loadLibrary("kernel32",
			Kernel32.class, DEFAULT_OPTIONS);

	/* http://msdn.microsoft.com/en-us/library/ms683179(VS.85).aspx */
	HANDLE GetCurrentProcess();

	/* http://msdn.microsoft.com/en-us/library/ms683215.aspx */
	int GetProcessId(HANDLE Process);
}