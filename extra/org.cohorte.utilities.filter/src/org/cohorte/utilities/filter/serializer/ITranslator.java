package org.cohorte.utilities.filter.serializer;

import java.util.List;

import org.cohorte.utilities.filter.expression.ExpressionOperator;
import org.cohorte.utilities.filter.expression.IExpressionFieldArray;
import org.cohorte.utilities.filter.expression.IExpressionValue;

public interface ITranslator<T> {

	/**
	 * 
	 * @param aExpression
	 * @return
	 */
	public T translateExpression(IExpressionValue aExpression);

	public T translateExpression(IExpressionFieldArray aExpression);

	public T translateExpression(ExpressionOperator aOperator, List<T> aListOfExpression);

}
