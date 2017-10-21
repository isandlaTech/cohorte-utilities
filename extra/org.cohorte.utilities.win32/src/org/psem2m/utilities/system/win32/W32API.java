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

import java.util.HashMap;
import java.util.Map;

import com.sun.jna.FromNativeContext;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIFunctionMapper;
import com.sun.jna.win32.W32APITypeMapper;

/**
 * Base type for most W32 API libraries.
 * 
 * Provides standard options for unicode/ASCII mappings.
 * 
 * Set the system property w32.ascii to true to default to the ASCII mappings.
 * 
 * @author Timothy Wall
 */
public interface W32API extends StdCallLibrary, W32Errors {

	/**
	 * @author Timothy Wall
	 * 
	 */
	public class HANDLE extends PointerType {

		@Override
		public Object fromNative(Object nativeValue, FromNativeContext context) {
			Object o = super.fromNative(nativeValue, context);
			if (INVALID_HANDLE_VALUE.equals(o))
				return INVALID_HANDLE_VALUE;
			return o;
		}

		/**
		 * @param aLong
		 * @author ogattaz
		 */
		public void setPointer(long aLong) {
			setPointer(Pointer.createConstant(aLong));
		}
	}

	/** Constant value representing an invalid HANDLE. */
	HANDLE INVALID_HANDLE_VALUE = new HANDLE() {
		{
			super.setPointer(Pointer.createConstant(-1));
		}

		@Override
		public void setPointer(Pointer p) {
			throw new UnsupportedOperationException("Immutable reference");
		}
	};

	/** Standard options to use the ASCII/MBCS version of a w32 API. */
	Map<String, Object> OPTIONS__ASCII = new HashMap<String, Object>() {

		private static final long serialVersionUID = -6160412666038046372L;

		{
			put(OPTION_TYPE_MAPPER, W32APITypeMapper.ASCII);
			put(OPTION_FUNCTION_MAPPER, W32APIFunctionMapper.ASCII);
		}
	};

	/** Standard options to use the unicode version of a w32 API. */
	Map<String, Object> OPTIONS__UNICODE = new HashMap<String, Object>() {

		private static final long serialVersionUID = 7050636533112313271L;

		{
			put(OPTION_TYPE_MAPPER, W32APITypeMapper.UNICODE);
			put(OPTION_FUNCTION_MAPPER, W32APIFunctionMapper.UNICODE);
		}
	};

	Map<String, Object> OPTIONS_DEFAULT = Boolean.getBoolean("w32.ascii") ? OPTIONS__ASCII
			: OPTIONS__UNICODE;
}
