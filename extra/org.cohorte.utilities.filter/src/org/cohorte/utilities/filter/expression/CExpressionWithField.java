package org.cohorte.utilities.filter.expression;

import org.psem2m.utilities.CXStringUtils;

public abstract class CExpressionWithField extends CExpression {

	private String pField;

	public String getField() {
		return pField;
	}

	public CExpressionWithField(String aField, EOperator aOperator) {
		super(aOperator);
		pField = aField;

	}

	@Override
	public Appendable addDescriptionInBuffer(Appendable aBuffer) {
		super.addDescriptionInBuffer(aBuffer);
		CXStringUtils.appendKeyValInBuff(aBuffer, "field", getField());

		return aBuffer;
	}
}
