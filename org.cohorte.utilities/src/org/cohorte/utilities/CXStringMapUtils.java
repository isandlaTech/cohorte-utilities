package org.cohorte.utilities;

import java.util.HashMap;
import java.util.Map;

import org.psem2m.utilities.CXStringUtils;
import org.psem2m.utilities.IXDescriber;

/**
 * MOD_OG_20210427 Sharing utilities developped in Dimensions core
 * 
 * Used for example to manage the replacements pairs in the Dimensions
 * 
 * @author ogattaz
 *
 */
public class CXStringMapUtils<D> extends HashMap<String, D> implements IXDescriber {

	private static final long serialVersionUID = 1384607384864992854L;

	/**
	 * 
	 */
	public CXStringMapUtils() {
		super();
	}

	/**
	 * @param aPairs
	 */
	@SuppressWarnings("unchecked")
	public CXStringMapUtils(final Object[]... aReplacementPairs) {
		this();

		try {
			if (aReplacementPairs.length > 0) {
				for (Object[] wPair : aReplacementPairs) {
					this.put(String.valueOf(wPair[0]), (D) wPair[1]);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(String.format("Unable to build a CLabelReplacements using the argument [%s]",
					replacementPairToString(aReplacementPairs)), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.psem2m.utilities.IXDescriber#addDescriptionInBuffer(java.lang.Appendable)
	 */
	@Override
	public Appendable addDescriptionInBuffer(Appendable aBuffer) {

		CXStringUtils.appendFormatStrInBuff(aBuffer, "NbPairs=[%d]", size());

		CXStringUtils.appendFormatStrInBuff(aBuffer, " Pairs: %s", dump(","));

		return aBuffer;
	}

	/**
	 * @param aSeparator
	 * @return
	 */
	public String dump(final String aSeparator) {

		if (isEmpty()) {
			return "{}";
		}
		final StringBuilder wSB = new StringBuilder(256);
		wSB.append('{');
		int wI = 0;
		for (final Map.Entry<String, D> wKeyValue : entrySet()) {
			if (wI > 0) {
				wSB.append(aSeparator);
			}
			wSB.append(String.format("%s=[%s]", wKeyValue.getKey(), wKeyValue.getValue()));
			wI++;
		}
		wSB.append('}');
		return wSB.toString();
	}

	/**
	 * @param aReplacementPairs
	 * @return
	 */
	private String replacementPairToString(Object[]... aReplacementPairs) {
		StringBuilder wSB = new StringBuilder();

		try {
			for (Object[] wPair : aReplacementPairs) {
				if (wSB.length() > 0) {
					wSB.append(',');
				}
				CXStringUtils.appendFormatStrInBuff(wSB, "[%s,%s]", wPair[0], wPair[1]);
			}
		} catch (Exception e) {

		}
		return wSB.toString();
	}

	/**
	 * @param aId
	 * @param aValue
	 * @return
	 */
	public CXStringMapUtils<D> setPair(final String aId, final D aValue) {
		this.put(aId, aValue);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.IXDescriber#toDescription()
	 */
	@Override
	public String toDescription() {
		return addDescriptionInBuffer(new StringBuilder()).toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.AbstractMap#toString()
	 */
	@Override
	public String toString() {
		return toDescription();
	}
}
