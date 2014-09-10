/*******************************************************************************
 * Copyright (c) 2011 www.isandlatech.com (www.isandlatech.com)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    ogattaz (isandlaTech) - initial API and implementation
 *******************************************************************************/
package org.psem2m.utilities;

/**
 * @author isandlatech (www.isandlatech.com) - ogattaz
 * 
 */
public class CXJavaRunContext {

	/** the "before index" of the calling method */
	public static final int METHOD_CALLING = 2;
	/** the "before index" of the current method */
	public static final int METHOD_CURRENT = 1;

	public static final boolean PREVENT_IPOJO = true;

	/**
	 * @param aSTEs
	 *            the StackTraceElement array
	 * @param aStartIdx
	 *            the start index of the search operation
	 * @return the name of the first method which have a name which not starts
	 *         by a double underscore charater
	 */
	private static String findFirstNonIPojoMethod(
			final StackTraceElement[] aSTEs, final int aStartIdx) {

		if (aSTEs == null) {
			return null;
		}

		int wMax = aSTEs.length;

		if (aStartIdx < 0 || aStartIdx > wMax - 1) {
			return null;
		}

		String wMethodName;
		for (int wI = aStartIdx; wI > -1; wI--) {
			wMethodName = aSTEs[wI].getMethodName();
			if (!wMethodName.startsWith("__")) {
				return wMethodName;
			}
		}
		return null;
	}

	/**
	 * @param aSTEs
	 *            the StackTraceElement array
	 * @param aMethodName
	 *            the name of the method to find
	 * @return the idx of the found method name or -1 if not found
	 */
	private static int findMethodIdx(final StackTraceElement[] aSTEs,
			final String aMethodName) {
		int wMax = (aSTEs != null) ? aSTEs.length : 0;

		for (int wI = 0; wI < wMax; wI++) {
			if (aSTEs[wI].getMethodName().equals(aMethodName)) {
				return wI;
			}
		}
		return -1;
	}

	/**
	 * <pre>
	 *  getStackTrace,getStackTrace,getCallingMethod,doCmdeMethods,monitorCommand,execLine,monitor,main,main
	 * </pre>
	 * 
	 * @return the current method name:
	 */

	public static String getCallingMethod() {
		// METHOD_CALLING => 2 previous "getCallingMethod"
		return getMethod("getCallingMethod", METHOD_CALLING);
	}

	/**
	 * <pre>
	 *  getStackTrace,getStackTrace,getCurrentMethod,doCmdeMethods,monitorCommand,execLine,monitor,main,main
	 * </pre>
	 * 
	 * @return the calling method name
	 */
	public static String getCurrentMethod() {

		// METHOD_CURRENT => 1 previous "getCurrentMethod"
		return getMethod("getCurrentMethod", METHOD_CURRENT);

	}

	/**
	 * @param aFromMethodName
	 * @param aBefore
	 * @return
	 */
	public static String getMethod(final String aFromMethodName,
			final int aBefore) {
		return getMethod(aFromMethodName, aBefore, false);
	}

	/**
	 * @param aFromMethodName
	 *            the name of the methode finding from
	 * @param aBefore
	 * @param aPreventIPojo
	 *            neglects the responses starting with a double underscore
	 *            charater
	 * @return
	 */
	public static String getMethod(final String aFromMethodName,
			final int aBefore, final boolean aPreventIPojo) {

		StackTraceElement[] wSTEs = getStackTrace();
		int wMax = (wSTEs != null) ? wSTEs.length : 0;

		if (wMax < 1) {
			return null;
		}
		int wIdx = findMethodIdx(wSTEs, aFromMethodName);
		if (wIdx < 0) {
			return null;
		}
		int wIdxCallingMethod = wIdx + aBefore;
		if (aPreventIPojo) {
			return findFirstNonIPojoMethod(wSTEs, wIdxCallingMethod);
		} else {
			return (wIdxCallingMethod < wMax) ? wSTEs[wIdxCallingMethod]
					.getMethodName() : null;
		}

	}

	/**
	 * <pre>
	 *  getStackTrace,getStackTrace,getStackTrace,getMethod,getPreCallingMethod,doCmdeMethods,monitorCommand,execLine,monitor,main,main
	 * </pre>
	 * 
	 * @return
	 */
	public static String getPreCallingMethod() {
		// CALLING_METHOD => 3 previous "getPreCallingMethod"
		return getMethod("getPreCallingMethod", 3);
	}

	/**
	 * <pre>
	 * getStackTrace,getStackTrace,getStackMethods,doCmdeMethods,monitorCommand,execLine,monitor,main,main
	 * </pre>
	 * 
	 * @return the list of the name of the methods currently in the stack.
	 */
	public static String[] getStackMethods() {
		StackTraceElement[] wSTEs = getStackTrace();
		int wMax = (wSTEs != null) ? wSTEs.length : 0;
		String[] wSMs = new String[wSTEs.length];
		if (wMax > 0) {

			for (int wI = 0; wI < wMax; wI++) {
				wSMs[wI] = wSTEs[wI].getMethodName();
			}
		}
		return wSMs;
	}

	/**
	 * @return
	 */
	private static java.lang.StackTraceElement[] getStackTrace() {
		return Thread.currentThread().getStackTrace();
	}
}
