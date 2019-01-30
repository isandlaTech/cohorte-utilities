package org.cohorte.utilities.filter.expression;

import org.psem2m.utilities.CXStringUtils;

/**
 * desrcibe an expression that admit only an object as opérand that can be an
 * expression or
 * 
 * @author apisu
 *
 */
public class CExpressionWithFieldObject extends CExpressionWithField implements IExpressionObject {

	private CExpression pValue;
	private String pField;

	public CExpression getValue() {
		return pValue;
	}

	public void setValue(CExpression pValue) {
		this.pValue = pValue;
	}

	public CExpressionWithFieldObject(String aField, EOperator aOperator) {
		super(aField, aOperator);
	}

	@Override
	public Appendable addDescriptionInBuffer(Appendable aBuffer) {
		super.addDescriptionInBuffer(aBuffer);

		CXStringUtils.appendKeyValInBuff(aBuffer, "isExpression", pValue instanceof CExpression);
		CXStringUtils.appendKeyValInBuff(aBuffer, "value",
				pValue instanceof CExpression ? ((CExpression) pValue).toDescription() : pValue.toString());
		return aBuffer;
	}

}
