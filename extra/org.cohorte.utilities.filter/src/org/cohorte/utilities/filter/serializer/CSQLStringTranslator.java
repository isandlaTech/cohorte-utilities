package org.cohorte.utilities.filter.serializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cohorte.utilities.filter.expression.ExpressionOperator;
import org.cohorte.utilities.filter.expression.IExpressionFieldArray;
import org.cohorte.utilities.filter.expression.IExpressionValue;

/**
 * handler that translate operator, field to return the correct value expected
 * for a SQL query
 */
public class CSQLStringTranslator implements ITranslator<String> {

	private final Map<String, String> pMapOperator;
	IFunction<String, String> pTansformField = null;

	public CSQLStringTranslator() {
		this(null);

	}

	public CSQLStringTranslator(final IFunction<String, String> aTansformField) {
		pMapOperator = new HashMap<>();

		pTansformField = aTansformField;
		initOperatorMap();

	}

	protected void initOperatorMap() {
		pMapOperator.put(ExpressionOperator.EQ.toString(), "=");
		pMapOperator.put(ExpressionOperator.AND.toString(), "AND");
		pMapOperator.put(ExpressionOperator.OR.toString(), "OR");
		pMapOperator.put(ExpressionOperator.IN.toString(), "IN");
		pMapOperator.put(ExpressionOperator.NE.toString(), "!=");
		pMapOperator.put(ExpressionOperator.GT.toString(), ">");
		pMapOperator.put(ExpressionOperator.GTE.toString(), ">=");
		pMapOperator.put(ExpressionOperator.LT.toString(), "<");
		pMapOperator.put(ExpressionOperator.LTE.toString(), "<=");
		pMapOperator.put(ExpressionOperator.NIN.toString(), "NOT IN");
		pMapOperator.put(ExpressionOperator.EXISTS.toString(), "IS %s NULL");

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.cohorte.utilities.filter.serializer.ITranslator#translateExpression(org.
	 * cohorte.utilities.filter.expression.ExpressionOperator, java.util.List)
	 */
	@Override
	public String translateExpression(final ExpressionOperator aOperator, final List<String> aListOfExpression) {
		String wOperator = pMapOperator.get(aOperator.toString());

		return " (" + String.join(wOperator, aListOfExpression) + ") ";
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.cohorte.utilities.filter.serializer.ITranslator#translateExpression(org.
	 * cohorte.utilities.filter.expression.IExpressionFieldArray)
	 */
	@Override
	public String translateExpression(final IExpressionFieldArray aExpression) {
		String wField = aExpression.getField();
		String wOperator = pMapOperator.get(aExpression.getOperator().toString());
		String wValue = "(";
		int i = 0;
		List<Object> wList = aExpression.getListValue();
		for (Object wObj : wList) {
			if (i > 0 && i < wList.size()) {
				wValue += ",";
			}
			if (wObj instanceof String) {
				wValue += String.format("'%s'", wObj.toString());
			} else {
				wValue += String.format("%s", wObj.toString());

			}
			i++;
		}
		wValue += ")";
		return String.format(" %s %s %s ", wField, wOperator, wValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cohorte.utilities.filter.serializer.ITranslator#translateExpression(org.
	 * cohorte.utilities.filter.expression.IExpressionValue)
	 */
	@Override
	public String translateExpression(final IExpressionValue aExpression) {
		String wField = aExpression.getField();
		if (aExpression.getOperator() == ExpressionOperator.EXISTS) {
			if (aExpression.getValue() instanceof Boolean && ((Boolean) aExpression.getValue()).booleanValue()) {
				return String.format(" %s IS NOT NULL ", wField);

			} else {
				return String.format(" %s IS NULL ", wField);
			}
		} else {
			String wOperator = pMapOperator.get(aExpression.getOperator().toString());
			String wValue = aExpression.getValue() instanceof String ? String.format("'%s'", aExpression.getValue())
					: aExpression.getValue().toString();
			return String.format(" %s %s %s ", wField, wOperator, wValue);
		}
	}

}
