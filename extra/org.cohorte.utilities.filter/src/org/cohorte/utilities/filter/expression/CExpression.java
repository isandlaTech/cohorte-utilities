package org.cohorte.utilities.filter.expression;

import org.psem2m.utilities.CXStringUtils;
import org.psem2m.utilities.IXDescriber;

public abstract class CExpression implements IXDescriber {
	private EOperator pOperator;

	public EOperator getOperator() {
		return pOperator;
	}

	public CExpression(EOperator aOperator) {
		pOperator = aOperator;

	}

	@Override
	public Appendable addDescriptionInBuffer(Appendable aBuffer) {
		CXStringUtils.appendKeyValInBuff(aBuffer, "operator", pOperator.toString());
		return aBuffer;
	}

	@Override
	public String toDescription() {
		StringBuilder wBuffer = new StringBuilder();
		addDescriptionInBuffer(wBuffer);
		return wBuffer.toString();
	}

	public String toString() {
		return toDescription() + "\n";
	}
}
