package org.cohorte.utilities.filter.serializer;

import org.cohorte.utilities.filter.expression.EOperator;

public interface ITranslator {

	/**
	 * translate the field
	 * 
	 * @param aExpressionField
	 * @return
	 */
	public String getStartCondition();

	public String getFieldSeparator();

	/**
	 * translate the field
	 * 
	 * @param aExpressionField
	 * @return
	 */
	public String getEndCondition();

	/**
	 * translate the field
	 * 
	 * @param aExpressionField
	 * @return
	 */
	public String translateField(String aExpressionField, IFunction<String, String> aTansformField);

	/**
	 * 
	 * @param aExpressionField
	 * @return
	 */
	public String translateField(String aExpressionField);

	/**
	 * tranlate the operator
	 * 
	 * @param aOperator
	 * @return
	 */
	public String translateOperator(EOperator aOperator);

	/**
	 * translate the value
	 * 
	 * @param aValue
	 * @return
	 */
	public String translateOperande(Object aValue);

	/**
	 * return the order to serialize the argument (e.g in SQL field operator
	 * operande or expression operator expression ....)
	 * 
	 * @return
	 */
	public EArgumentOrder[] getOrderArgument();
}
