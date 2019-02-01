package org.cohorte.utilities.filter.expression;

import org.psem2m.utilities.CXStringUtils;
import org.psem2m.utilities.IXDescriber;

/**
 * abtract class that modelize an Expression
 * @author apisu
 *
 */
public abstract class CExpression implements IXDescriber, IExpression {
	private ExpressionOperator pOperator;

	public ExpressionOperator getOperator() {
		return pOperator;
	}

	public CExpression(ExpressionOperator aOperator) {
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
