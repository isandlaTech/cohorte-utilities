package org.cohorte.utilities.filter.serializer;

import java.util.HashMap;
import java.util.Map;

import org.cohorte.utilities.filter.expression.EOperator;

/**
 * handler that translate operator, field to return the correct value expected
 * for a SQL query
 */
public class CSQLTranslator implements ITranslator {

	private final EArgumentOrder[] pOrderArgument;
	private static String SPACE = " ";
	private static String SEP_FIELD = ",";

	private static String END_CONDITION = " ) ";

	private static String START_CONDITION = " ( ";

	private final Map<String, String> pMapOperator;

	private CSQLTranslator() {
		pMapOperator = new HashMap<>();
		pOrderArgument = new EArgumentOrder[] { EArgumentOrder.FIELD, EArgumentOrder.OPERATOR,
				EArgumentOrder.OPERANDE };
		initOperatorMap();

	}

	private static CSQLTranslator sTranslator;

	public static CSQLTranslator getSingleton() {
		if (sTranslator == null) {
			sTranslator = new CSQLTranslator();
		}
		return sTranslator;
	}

	protected void initOperatorMap() {
		pMapOperator.put(EOperator.EQ.toString(), "=");
		pMapOperator.put(EOperator.AND.toString(), "AND");
		pMapOperator.put(EOperator.OR.toString(), "OR");
		pMapOperator.put(EOperator.IN.toString(), "IN");
		pMapOperator.put(EOperator.NE.toString(), "!=");
		pMapOperator.put(EOperator.GT.toString(), ">");
		pMapOperator.put(EOperator.GTE.toString(), ">=");
		pMapOperator.put(EOperator.LT.toString(), "<");
		pMapOperator.put(EOperator.LTE.toString(), "<=");
		pMapOperator.put(EOperator.NIN.toString(), "NOT IN");

	}

	@Override
	public String translateField(String aExpressionField, IFunction<String, String> aTansformField) {
		String wResult = aExpressionField;
		if (aTansformField != null) {
			wResult = aTansformField.call(aExpressionField);
		}
		return SPACE + wResult + SPACE;
	}

	@Override
	public String translateField(String aExpressionField) {
		return translateField(aExpressionField, null);
	}

	@Override
	public String translateOperator(EOperator aOperator) {
		return SPACE + pMapOperator.get(aOperator.toString()) + SPACE;
	}

	@Override
	public EArgumentOrder[] getOrderArgument() {
		// TODO Auto-generated method stub
		return pOrderArgument;
	}

	@Override
	public String getStartCondition() {
		// TODO Auto-generated method stub
		return START_CONDITION;
	}

	@Override
	public String getEndCondition() {
		// TODO Auto-generated method stub
		return END_CONDITION;
	}

	@Override
	public String getFieldSeparator() {
		// TODO Auto-generated method stub
		return SEP_FIELD;
	}

	@Override
	public String translateOperande(Object aValue) {
		// TODO Auto-generated method stub
		String wResult = null;
		if (aValue instanceof String) {
			wResult = String.format("'%s'", aValue.toString());
		} else {
			wResult = aValue.toString();
		}
		return SPACE + wResult + SPACE;
	}

}
