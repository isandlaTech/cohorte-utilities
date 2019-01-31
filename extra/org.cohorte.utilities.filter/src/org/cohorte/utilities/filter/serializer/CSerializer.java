package org.cohorte.utilities.filter.serializer;

import java.util.ArrayList;
import java.util.List;

import org.cohorte.utilities.filter.expression.IExpression;
import org.cohorte.utilities.filter.expression.IExpressionArray;
import org.cohorte.utilities.filter.expression.IExpressionFieldArray;
import org.cohorte.utilities.filter.expression.IExpressionValue;

public class CSerializer<T> {
	/**
	 * serialize the expression regarding the kind of serialization asked and using
	 * the translator
	 * 
	 * @param aExpression
	 * @param aTranslator
	 * @return
	 */
	public List<T> serializer(IExpression aExpression, ITranslator<T> aTranslator) throws CSerializeException {
		// browser the expression
		if (aExpression == null || aTranslator == null) {
			return null;
		}
		List<T> wListCondition = new ArrayList<>();
		return serializer(wListCondition, aExpression, aTranslator);

	}

	private List<T> serializer(List<T> aListCondition, IExpression aExpression, ITranslator<T> aTranslator)
			throws CSerializeException {

		// in that case we have field operator and subexpression

		if (aExpression instanceof IExpressionArray) {
			List<T> wArrListCondition = new ArrayList<>();
			IExpressionArray wExp = (IExpressionArray) aExpression;
			List<IExpression> wListObj = wExp.getListValue();
			for (IExpression wVal : wListObj) {
				serializer(wArrListCondition, wVal, aTranslator);
			}
			aListCondition.add(aTranslator.translateExpression(aExpression.getOperator(), wArrListCondition));

		} else if (aExpression instanceof IExpressionFieldArray) {
			aListCondition.add(aTranslator.translateExpression((IExpressionFieldArray) aExpression));
		} else if (aExpression instanceof IExpressionValue) {
			aListCondition.add(aTranslator.translateExpression((IExpressionValue) aExpression));
		}

		return aListCondition;

	}

}
