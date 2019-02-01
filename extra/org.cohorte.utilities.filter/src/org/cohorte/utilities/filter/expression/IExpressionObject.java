package org.cohorte.utilities.filter.expression;

/**
 * desrcibe an expression that admit only an object as opérand that can be an
 * expression or
 *
 * @author apisu
 *
 */
public interface IExpressionObject extends IExpressionField {

	public CExpression getValue();
}
