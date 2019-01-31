package org.cohorte.utilities.filter.expression;

import org.psem2m.utilities.CXStringUtils;

/**
 * desrcibe an expression that admit only a value as opérand
 * 
 * @author apisu
 *
 */
public class CExpressionValue extends CExpressionWithField implements IExpressionValue {

	private Object pValue;

	public CExpressionValue(String aField, ExpressionOperator aOperator, Object aValue) {
		super(aField, aOperator);
		pValue = aValue;
	}

	public Object getValue() {
		return pValue;
	}

	@Override
	public Appendable addDescriptionInBuffer(Appendable aBuffer) {
		super.addDescriptionInBuffer(aBuffer);

		CXStringUtils.appendKeyValInBuff(aBuffer, "isExpression", pValue instanceof CExpression);
		CXStringUtils.appendKeyValInBuff(aBuffer, "value", pValue.toString());

		return aBuffer;
	}
}
