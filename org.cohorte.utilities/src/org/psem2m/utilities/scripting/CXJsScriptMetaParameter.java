package org.psem2m.utilities.scripting;

import java.util.ArrayList;
import java.util.List;

/**
 * describe a meta parameter that is in a commnt line with a key started with #
 * and value separate with a space
 *
 * <pre>
 * e.g : // #include "myinclude path"
 * e.g : // #requires IInterface myvalue ....
 *
 * </pre>
 *
 * @author apisu
 *
 */
public class CXJsScriptMetaParameter {

	// key without the # character
	private final String pKey;
	// list of value separate by a space
	private final List<String> pValues = new ArrayList<>();

	public CXJsScriptMetaParameter(final String aKey) {
		pKey = aKey;
	}

	public void addValues(final String aValue) {
		pValues.add(aValue);
	}

	public String getKey() {
		return pKey;
	}

	public List<String> getValues() {
		return pValues;
	}
}
