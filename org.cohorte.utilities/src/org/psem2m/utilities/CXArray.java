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

import java.lang.reflect.Array;
import java.util.List;

/**
 * @author isandlatech (www.isandlatech.com) - ogattaz
 * 
 */
public final class CXArray implements IConstants {

	/**
	 * @param aObjects
	 *            an array of objects
	 * @param aObjectsToInsert
	 *            an array of objects to add at the end the arrayy
	 * 
	 * @return the new array of objects with the added object
	 * */
	public static Object[] appendObjects(final Object[] aObjects,
			final Object[] aObjectsToInsert) {

		return insertObjects(aObjects, aObjectsToInsert,
				(aObjects != null) ? aObjects.length : -1);
	}

	/**
	 * @param aObjects
	 *            an array of objects
	 * @param aObjectToInsert
	 *            an object to add at the end the array
	 * @return the new array of objects with the added objects
	 */
	public static Object[] appendOneObject(final Object[] aObjects,
			final Object aObjectToInsert) {

		return insertOneObject(aObjects, aObjectToInsert,
				(aObjects != null) ? aObjects.length : -1);
	}

	/**
	 * @param aValues
	 * @param aSeparator
	 * @return
	 */
	public static String arrayToString(final Object[] aValues,
			final String aSeparator) {
		return arrayToString(aValues, aSeparator, false);
	}

	/**
	 * @param aValues
	 * @param aSeparator
	 * @return
	 */
	public static String arrayToString(final Object[] aValues,
			final String aSeparator, final boolean aJumpIfNull) {
		if (aValues == null) {
			return String.valueOf(aValues);
		}

		StringBuilder wSB = new StringBuilder(256);
		int wLenAfterSep = 0;
		int wMax = aValues.length;
		Object wValue;
		for (int wI = 0; wI < wMax; wI++) {
			if (wSB.length() > wLenAfterSep) {
				wSB.append(aSeparator);
				wLenAfterSep = wSB.length();
			}
			wValue = aValues[wI];
			if (wValue != null || !aJumpIfNull) {
				String wStr = String.valueOf(wValue);
				if (wStr.isEmpty()) {
					wStr = LIB_EMPTY;
				}
				wSB.append(wStr);
			}
		}
		return wSB.toString();
	}

	/**
	 * @param aObjects
	 *            an array of objects
	 * @return the common class of the objects stored in the array
	 */
	public static Class<?> calcClassOfArrayElmts(final Object[] aObjects) {
		return calcClassOfArrayElmts(aObjects, null);
	}

	/**
	 * @param aObjects
	 *            an array of objects
	 * @param aObjectToAdd
	 *            an object to add in the array
	 * @return the common class of the objects stored in the array and that of
	 *         the object to be added
	 */
	public static Class<?> calcClassOfArrayElmts(final Object[] aObjects,
			final Object aObjectToAdd) {

		if (aObjects == null) {
			return Object.class;
		}
		// Returns the Class representing the component type of an array.
		// If this "aObjects" does not represent an array class this method
		// returns null.

		return aObjects.getClass().getComponentType();
	}

	/**
	 * @param aObjects
	 *            an array of objects
	 * @param aObject
	 *            an object to test
	 * @return true if the object is present in the array
	 */
	public static boolean contains(final Object[] aObjects, final Object aObject) {

		if (aObjects != null) {

			int wMax = aObjects.length;
			Object wValue;
			for (int wI = 0; wI < wMax; wI++) {
				wValue = aObjects[wI];
				if (wValue == null && aObject == null) {
					return true;
				}
				if (wValue.equals(aObject)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @param aObjects
	 *            an array of objects
	 * @param aObjectsToInsert
	 *            an array of objects to add at the end the arrayy
	 * @return the new array of objects with the inserted objects
	 */
	public static Object[] insertFirstObjects(final Object[] aObjects,
			final Object[] aObjectsToInsert) {

		return insertObjects(aObjects, aObjectsToInsert, 0);
	}

	/**
	 * @param aObjects
	 *            an array of objects
	 * @param aObjectToInsert
	 *            an object to add in the array
	 * @return the new array of objects with the inserted object
	 */
	public static Object[] insertFirstOneObject(final Object[] aObjects,
			final Object aObjectToInsert) {

		return insertOneObject(aObjects, aObjectToInsert, 0);
	}

	/**
	 * @param aObjects
	 *            an array of objects
	 * @param aObjectsToInsert
	 *            an array of objects to add at the end the arrayy
	 * @param aIdx
	 *            the index of the position of the inserted objects
	 * @return the new array of objects with the inserted objects
	 */
	public static Object[] insertObjects(final Object[] aObjects,
			final Object[] aObjectsToInsert, final int aIdx) {

		if (aObjects == null || aObjectsToInsert == null
				|| aObjectsToInsert.length == 0) {
			return aObjects;
		}
		int wPreviousLen = aObjects.length;

		validObjectsIndex(aObjects, aIdx);

		int wInsertedLen = aObjectsToInsert.length;
		int wNewLen = wPreviousLen + wInsertedLen;
		Object[] wNewArray = (Object[]) Array.newInstance(
				calcClassOfArrayElmts(aObjects, aObjectsToInsert), wNewLen);

		// if we must add the object first
		if (aIdx == 0) {
			System.arraycopy(aObjectsToInsert, 0, wNewArray, 0, wInsertedLen);
			System.arraycopy(aObjects, 0, wNewArray, wInsertedLen, wPreviousLen);

		} else
		// if we must remove the last object
		if (aIdx == wPreviousLen) {
			System.arraycopy(aObjects, 0, wNewArray, 0, wPreviousLen);
			System.arraycopy(aObjectsToInsert, 0, wNewArray, wPreviousLen,
					wInsertedLen);
		} else
		//
		{
			// wLen = 10 and aIdx = 5 and wInsertedLen 3 => wNewLen = 13
			// wSubLenA = aIdx = 5 (old index 0 to 4)
			// wSubLenb = wOldLen - aIdx = 5 (old index 5 to 9)
			System.arraycopy(aObjects, 0, wNewArray, 0, aIdx);
			System.arraycopy(aObjectsToInsert, 0, wNewArray, aIdx, wInsertedLen);
			System.arraycopy(aObjects, aIdx, wNewArray, aIdx + wInsertedLen,
					wPreviousLen - aIdx);

		}
		return wNewArray;

	}

	/**
	 * @param aObjects
	 *            an array of objects
	 * @param aObjectToInsert
	 *            an object to add in the array
	 * @param aIdx
	 *            the index of the position of the inserted object
	 * @return the new array of objects with the inserted object
	 */
	public static Object[] insertOneObject(final Object[] aObjects,
			final Object aObjectToInsert, final int aIdx) {
		if (aObjects == null) {
			return aObjects;
		}
		int wPreviousLen = aObjects.length;

		validObjectsIndex(aObjects, aIdx);

		int wNewLen = wPreviousLen + 1;
		Object[] wNewArray = (Object[]) Array.newInstance(
				calcClassOfArrayElmts(aObjects, aObjectToInsert), wNewLen);

		// if we must add the object first
		if (aIdx == 0) {
			System.arraycopy(aObjects, 0, wNewArray, 1, wPreviousLen);
			wNewArray[0] = aObjectToInsert;

		} else
		// if we must remove the last object
		if (aIdx == wPreviousLen) {
			System.arraycopy(aObjects, 0, wNewArray, 0, wPreviousLen);
			wNewArray[wNewLen - 1] = aObjectToInsert;
		} else
		//
		{
			// wLen = 10 and aIdx = 5 => wNewLen = 11
			// wSubLenA = aIdx = 5 (old index 0 to 4)
			// wSubLenb = wOldLen - aIdx = 5 (old index 5 to 9)
			System.arraycopy(aObjects, 0, wNewArray, 0, aIdx);
			System.arraycopy(aObjects, aIdx, wNewArray, aIdx + 1, wPreviousLen
					- aIdx);
			wNewArray[aIdx] = aObjectToInsert;

		}
		return wNewArray;
	}

	/**
	 * @param aValues
	 * @param aSeparator
	 * @return
	 */
	public static String listToString(final List<?> aValues,
			final String aSeparator) {
		return listToString(aValues, aSeparator, false);
	}

	/**
	 * @param aValues
	 * @param aSeparator
	 * @return
	 */
	public static String listToString(final List<?> aValues,
			final String aSeparator, final boolean aJumpIfNull) {
		if (aValues == null) {
			return String.valueOf(aValues);
		}

		StringBuilder wSB = new StringBuilder(256);
		int wLenAfterSep = 0;
		for (Object wObj : aValues) {
			if (wSB.length() > wLenAfterSep) {
				wSB.append(aSeparator);
				wLenAfterSep = wSB.length();
			}
			if (wObj != null || !aJumpIfNull) {
				String wStr = String.valueOf(wObj);
				if (wStr.isEmpty()) {
					wStr = LIB_EMPTY;
				}
				wSB.append(wStr);
			}
		}
		return wSB.toString();
	}

	/**
	 * @param aObjects
	 *            the original array of objects
	 * @param aIdx
	 *            the index of the object to remove
	 * @return the new array of objects
	 * @throws IndexOutOfBoundsException
	 */
	public static Object[] removeOneObject(final Object[] aObjects,
			final int aIdx) throws IndexOutOfBoundsException {
		if (aObjects == null) {
			return aObjects;
		}
		int wLen = aObjects.length;
		if (wLen < 1) {
			return aObjects;
		}

		validObjectsIndex(aObjects, aIdx);

		int wNewLen = wLen - 1;
		Object[] wNewArray = (Object[]) Array.newInstance(
				calcClassOfArrayElmts(aObjects), wNewLen);

		// if we must remove the first object
		if (aIdx == 0) {
			System.arraycopy(aObjects, 1, wNewArray, 0, wNewLen);
		} else if (aIdx == wLen - 1) {
			System.arraycopy(aObjects, 0, wNewArray, 0, wNewLen);
		} else {
			// wLen = 10 and aIdx = 5 => wNewLen = 9
			// wSubLenA = aIdx = 5 (old index 0 to 4)
			// wSubLenb = wNewMax- aIdx = 4 (old index 6 to 9)
			System.arraycopy(aObjects, 0, wNewArray, 0, aIdx);
			System.arraycopy(aObjects, aIdx + 1, wNewArray, aIdx, wNewLen
					- aIdx);
		}

		return wNewArray;
	}

	/**
	 * @param aObjects
	 * @param aIdx
	 * @throws IndexOutOfBoundsException
	 */
	private static void validObjectsIndex(final Object[] aObjects,
			final int aIdx) throws IndexOutOfBoundsException {
		if (aObjects == null) {
			throw new IndexOutOfBoundsException("the target array is null");
		}
		int wLen = aObjects.length;
		if (wLen == 0 && aIdx == 0) {
			return; // OK
		}
		if (aIdx < 0) {
			throw new IndexOutOfBoundsException(String.format(
					"index [%d] is less than zero", aIdx));
		}
		if (aIdx > wLen) {
			throw new IndexOutOfBoundsException(String.format(
					"index [%d] is greater than len (len=[%d] )", aIdx, wLen));
		}
	}

	/**
	 * Dummy constructor.
	 */
	private CXArray() {
	}
}
