package org.psem2m.utilities.rsrc;

import java.util.ArrayList;

import org.psem2m.utilities.CXStringUtils;
import org.psem2m.utilities.IXDescriber;

/**
 * class that modelize a list of RsrcText
 *
 * @author apisu
 *
 */
public class CXListRsrcText extends ArrayList<CXRsrcText> implements
		IXDescriber {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Appendable addDescriptionInBuffer(final Appendable aBuffer) {
		CXStringUtils.appendKeyValInBuff(aBuffer, "ListRsrcText size", size());

		for (CXRsrcText wElem : this) {
			CXStringUtils.appendKeyValInBuff(aBuffer,
					wElem.getPath().getName(), wElem.toDescription());
		}

		return aBuffer;
	}

	@Override
	public String toDescription() {
		return addDescriptionInBuffer(new StringBuilder()).toString();

	}

	@Override
	public String toString() {
		return toDescription();
	}
}
