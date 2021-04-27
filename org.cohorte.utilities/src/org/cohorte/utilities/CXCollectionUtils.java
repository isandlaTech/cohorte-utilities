package org.cohorte.utilities;

import java.util.Collection;

/**
 * MOD_OG_20210427 Sharing utilities developped in Dimensions core
 * 
 * @author ogattaz
 *
 */
public class CXCollectionUtils {

	/**
	 * @param aCollection
	 *            a collection of objects
	 * @param aSeparator
	 * @return a string
	 */
	public static String collectiontoString(final Collection<?> aCollection, final String aSeparator) {

		return collectiontoString(aCollection, aSeparator, 0, -1);
	}

	/**
	 * @param aCollection
	 *            a collection of objects
	 * @param aSeparator
	 * @param aBeginIndex
	 * @return a string
	 */
	public static String collectiontoString(final Collection<?> aCollection, final String aSeparator,
			final int aBeginIndex) {

		return collectiontoString(aCollection, aSeparator, aBeginIndex, -1);
	}

	/**
	 * @param aCollection
	 *            a collection of objects
	 * @param aSeparator
	 * @param aBeginIndex
	 * @param aStopIndex
	 * @return a string
	 */
	public static String collectiontoString(final Collection<?> aCollection, final String aSeparator,
			final int aBeginIndex, final int aStopIndex) {

		if (aCollection == null || aCollection.size() == 0) {
			return "";
		}

		int wMax = aCollection.size();
		int wStart = 0;
		if (aStopIndex > 0 && aStopIndex < wMax) {
			wMax = aStopIndex;
		}
		if (aBeginIndex > 0) {
			wStart = aBeginIndex;
		}
		if (wStart > wMax) {
			wStart = wMax;
		}
		final StringBuilder wSB = new StringBuilder(256);
		int wI = 0;
		for (final Object wStr : aCollection) {

			if (wI >= wStart) {
				if (wI > 0) {
					wSB.append(aSeparator);
				}
				wSB.append(String.valueOf(wStr));
			}
			if (wI >= wMax) {
				break;
			}
			wI++;
		}
		return wSB.toString();
	}

	/**
	 * @param aCollection
	 *            a collection of objects
	 * @return a string
	 */
	public static String collectiontoString(final Collection<Object> aCollection) {

		return collectiontoString(aCollection, ",");
	}

}
