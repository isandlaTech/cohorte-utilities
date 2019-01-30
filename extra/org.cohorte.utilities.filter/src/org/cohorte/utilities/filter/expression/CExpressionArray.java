package org.cohorte.utilities.filter.expression;

import java.util.ArrayList;
import java.util.List;

import org.psem2m.utilities.CXStringUtils;

/**
 * desrcibe an expression that admit only an array that admit a field and an
 * array as operator
 * 
 * @author apisu
 *
 */
public class CExpressionArray extends CExpression implements IExpressionArray {

	private final List<Object> pListExpression;

	public CExpressionArray(EOperator aOperator) {
		super(aOperator);
		pListExpression = new ArrayList<>();
	}

	public List<Object> getListValue() {
		return pListExpression;
	}

	public void addExpression(CExpressionWithField aExpression) {
		pListExpression.add(aExpression);
	}

	public void setValues(List<Object> aListExpressions) {
		pListExpression.clear();
		pListExpression.addAll(aListExpressions);
	}

	public void addValue(String aValue) {
		pListExpression.add(aValue);
	}

	@Override
	public Appendable addDescriptionInBuffer(Appendable aBuffer) {
		super.addDescriptionInBuffer(aBuffer);
		CXStringUtils.appendKeyValInBuff(aBuffer, "expressions_size", pListExpression.size());

		for (Object wItem : pListExpression) {
			CXStringUtils.appendKeyValInBuff(aBuffer, "isExpression", wItem instanceof CExpression);
			CXStringUtils.appendKeyValInBuff(aBuffer, "value",
					wItem instanceof CExpression ? ((CExpression) wItem).toDescription() : wItem.toString());

		}
		return aBuffer;
	}
}
