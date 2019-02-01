package org.cohorte.utilities.filter.expression;

import java.util.List;

/**
 * desrcibe an expression that admit only an array that admit a field and an
 * array as operator
 *
 * operator that contains only values
 *
 * @author apisu
 *
 */
public interface IExpressionFieldArray extends IExpressionField {

	public List<Object> getListValue();
}
