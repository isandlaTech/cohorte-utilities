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

	private final List<IExpression> pListExpression;

	public CExpressionArray(ExpressionOperator aOperator) {
		super(aOperator);
		pListExpression = new ArrayList<>();
	}

	public List<IExpression> getListValue() {
		return pListExpression;
	}

	public void addExpression(IExpression aExpression) {
		pListExpression.add(aExpression);
	}

	/**
	 * set list of expression
	 * @param aListExpressions
	 */
	public void setValues(List<IExpression> aListExpressions) {
		pListExpression.clear();
		pListExpression.addAll(aListExpressions);
	}

	@Override
	public Appendable addDescriptionInBuffer(Appendable aBuffer) {
		super.addDescriptionInBuffer(aBuffer);
		CXStringUtils.appendKeyValInBuff(aBuffer, "expressions_size", pListExpression.size());

		for (Object wItem : pListExpression) {
			CXStringUtils.appendKeyValInBuff(aBuffer, "value",
					wItem instanceof CExpression ? ((CExpression) wItem).toDescription() : wItem.toString());

		}
		return aBuffer;
	}
}
