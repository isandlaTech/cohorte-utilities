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

	public CSQLStringTranslator(IFunction<String, String> aTansformField) {
		pMapOperator = new HashMap<>();

		pTansformField = aTansformField;
		initOperatorMap();

	}

	public CSQLStringTranslator() {
		this(null);

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

	}

	/*
	 * @Override public String translateE(String aExpressionField) { String wResult
	 * = aExpressionField; if (pTansformField != null) { wResult =
	 * pTansformField.call(aExpressionField); } return SPACE + wResult + SPACE; }
	 */

	@Override
	public String translateExpression(IExpressionValue aExpression) {
		String wField = aExpression.getField();
		String wOperator = pMapOperator.get(aExpression.getOperator().toString());
		String wValue = aExpression.getValue() instanceof String ? String.format("'%s'", aExpression.getValue())
				: aExpression.getValue().toString();
		return String.format(" %s %s %s ", wField, wOperator, wValue);
	}

	@Override
	public String translateExpression(ExpressionOperator aOperator, List<String> aListOfExpression) {
		String wOperator = pMapOperator.get(aOperator.toString());

		return " (" + String.join(wOperator, aListOfExpression) + ") ";
	}

	@Override
	public String translateExpression(IExpressionFieldArray aExpression) {
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

}
