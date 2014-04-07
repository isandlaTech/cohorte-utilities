package org.psem2m.utilities.system.win32;

import java.util.HashMap;
import java.util.Map;

import com.sun.jna.FromNativeContext;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIFunctionMapper;
import com.sun.jna.win32.W32APITypeMapper;

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
 * 
 * Base type for most W32 API libraries. Provides standard options for
 * unicode/ASCII mappings. Set the system property w32.ascii to true to default
 * to the ASCII mappings.
 * 
 * @author Timothy Wall
 * 
 * @see http://www.golesny.de/p/code/javagetpid
 */
public interface W32API extends StdCallLibrary, W32Errors {

	/**
	 * @author Timothy Wall
	 * 
	 */
	public class HANDLE extends PointerType {
		/*
		 * (non-Javadoc)
		 * 
		 * @see com.sun.jna.PointerType#fromNative(java.lang.Object,
		 * com.sun.jna.FromNativeContext)
		 */
		@Override
		public Object fromNative(final Object nativeValue,
				final FromNativeContext context) {
			Object o = super.fromNative(nativeValue, context);
			if (INVALID_HANDLE_VALUE.equals(o)) {
				return INVALID_HANDLE_VALUE;
			}
			return o;
		}
	}

	/**
	 * Standard options to use the ASCII/MBCS version of a w32 API.
	 */
	Map<String, Object> _ASCII_OPTIONS = new HashMap<String, Object>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = -8912100385501808157L;

		{
			put(OPTION_TYPE_MAPPER, W32APITypeMapper.ASCII);
			put(OPTION_FUNCTION_MAPPER, W32APIFunctionMapper.ASCII);
		}
	};

	/**
	 * Standard options to use the unicode version of a w32 API.
	 */
	Map<String, Object> _UNICODE_OPTIONS = new HashMap<String, Object>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = -8887848937811133072L;

		{
			put(OPTION_TYPE_MAPPER, W32APITypeMapper.UNICODE);
			put(OPTION_FUNCTION_MAPPER, W32APIFunctionMapper.UNICODE);
		}
	};

	Map<String, Object> DEFAULT_OPTIONS = Boolean.getBoolean("w32.ascii") ? _ASCII_OPTIONS
			: _UNICODE_OPTIONS;

	/**
	 * Constant value representing an invalid HANDLE.
	 */
	HANDLE INVALID_HANDLE_VALUE = new HANDLE() {
		{
			super.setPointer(Pointer.createConstant(-1));
		}

		@Override
		public void setPointer(final Pointer p) {
			throw new UnsupportedOperationException("Immutable reference");
		}
	};

}
