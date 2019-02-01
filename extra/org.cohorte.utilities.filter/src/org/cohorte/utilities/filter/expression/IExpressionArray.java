package org.cohorte.utilities.filter.expression;

import java.util.List;

/**
 * desrcibe an expression that admit only an array that admit a field and an
 * array as operator
 *
 * @author apisu
 *
 */
public interface IExpressionArray {

	public List<IExpression> getListValue();
}
