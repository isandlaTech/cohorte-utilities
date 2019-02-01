package org.cohorte.utilities.filter.serializer;

import java.util.List;

import org.cohorte.utilities.filter.expression.ExpressionOperator;
import org.cohorte.utilities.filter.expression.IExpressionFieldArray;
import org.cohorte.utilities.filter.expression.IExpressionValue;

public interface ITranslator<T> {

	/**
	 * translate List of Expression and operator
	 *
	 * @param aOperator
	 * @param aListOfExpression
	 * @return
	 */
	public T translateExpression(ExpressionOperator aOperator, List<T> aListOfExpression);

	/**
	 * translate Expression Field Array that admit field, operator, array of object
	 *
	 * @param aExpression
	 * @return
	 */
	public T translateExpression(IExpressionFieldArray aExpression);

	/**
	 * translate Expression value that admit field, operator, object and not
	 * expression
	 *
	 * @param aExpression
	 * @return
	 */
	public T translateExpression(IExpressionValue aExpression);

}
