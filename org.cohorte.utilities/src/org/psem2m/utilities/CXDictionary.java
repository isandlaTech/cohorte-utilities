package org.psem2m.utilities;

import static org.psem2m.utilities.CXStringUtils.ELEMENT_SEP;

import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

/**
 * MOD_OG_1.0.14
 *
 * @author ogattaz
 *
 */
public class CXDictionary {

	/**
	 * @param aSB
	 * @param aDictionary
	 * @param aSeparator
	 * @return
	 */
	public static StringBuilder addDescriptionInSB(final StringBuilder aSB,
			final Dictionary<String, Object> aDictionary,
			final String aSeparator) {

		if (aDictionary == null) {
			aSB.append("null");
			return aSB;
		}
		if (aDictionary.size() == 0) {
			aSB.append("{}");
			return aSB;
		}
		aSB.append('{');
		int wI = 0;
		for (final String wKey : getKeys(aDictionary)) {
			if (wI > 0) {
				aSB.append(aSeparator);
			}
			aSB.append(String.format("%s=[%s]", wKey, aDictionary.get(wKey)));
			wI++;
		}
		aSB.append('}');

		return aSB;
	}

	/**
	 * @param aDictionary
	 *            The dictionnary to clone.
	 * @return the clone of the given dictionnary
	 */
	public static Dictionary<String, Object> cloneDictionary(
			final Dictionary<String, Object> aDictionary) {

		final Dictionary<String, Object> wDictionary = new Hashtable<String, Object>();

		for (final String wKey : getKeys(aDictionary)) {
			wDictionary.put(wKey, aDictionary.get(wKey));
		}
		return wDictionary;
	}

	/**
	 * @param aDictionary
	 * @return the list of the keys of the given dictionnary
	 */
	public static List<String> getKeys(
			final Dictionary<String, Object> aDictionary) {
		return Collections.list(aDictionary.keys());
	}

	/**
	 * @param aDictionary
	 *            The dictionnary to dump.
	 * @return the string representation of the given dictionnary
	 */
	public static String toString(final Dictionary<String, Object> aDictionary) {
		return addDescriptionInSB(new StringBuilder(), aDictionary, ELEMENT_SEP)
				.toString();
	}

}
