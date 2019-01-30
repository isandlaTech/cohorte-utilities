package org.cohorte.utilities.filter.serializer;

import java.util.ArrayList;
import java.util.List;

import org.cohorte.utilities.filter.expression.CExpression;
import org.cohorte.utilities.filter.expression.CExpressionArray;
import org.cohorte.utilities.filter.expression.CExpressionWithField;
import org.cohorte.utilities.filter.expression.CExpressionWithFieldArray;
import org.cohorte.utilities.filter.expression.EOperator;
import org.cohorte.utilities.filter.expression.IExpressionArray;
import org.cohorte.utilities.filter.expression.IExpressionObject;
import org.cohorte.utilities.filter.expression.IExpressionValue;

public class CSerializer {
	/**
	 * serialize the expression regarding the kind of serialization asked and using
	 * the translator
	 * 
	 * @param aExpression
	 * @param aTranslator
	 * @return
	 */
	public static String serializer(CExpression aExpression, ITranslator aTranslator) throws CSerializeException {
		// browser the expression
		if (aExpression == null || aTranslator == null) {
			return "";
		}
		String wCondition = "";
		EOperator wOperator = aExpression.getOperator();
		String wTranslatedOperator = null;
		String wTranslateField = null;
		String wTranslateOperande = null;
		// in that case we have field operator and subexpression

		if (aExpression instanceof CExpressionArray) {
			CExpressionArray wExp = (CExpressionArray) aExpression;
			List<String> wSubExp = serializer((IExpressionArray) wExp, aTranslator);
			wTranslateOperande = aTranslator.getStartCondition()
					+ String.join(aTranslator.translateOperator(wOperator), wSubExp) + aTranslator.getEndCondition();
		} else {
			wTranslatedOperator = aTranslator.translateOperator(wOperator);
			if (aExpression instanceof CExpressionWithFieldArray) {
				CExpressionWithFieldArray wExp = (CExpressionWithFieldArray) aExpression;
				List<String> wSubExp = serializer((IExpressionArray) wExp, aTranslator);
				wTranslateOperande = aTranslator.getStartCondition()
						+ String.join(aTranslator.getFieldSeparator(), wSubExp) + aTranslator.getEndCondition();

			}
			if (aExpression instanceof IExpressionValue) {
				IExpressionValue wExp = (IExpressionValue) aExpression;
				wTranslateOperande = aTranslator.translateOperande(wExp.getValue());
			} else if (aExpression instanceof IExpressionObject) {
				IExpressionObject wExp = (IExpressionObject) aExpression;
				wTranslateOperande = serializer(wExp.getValue(), aTranslator);

			}
		}
		if (aExpression instanceof CExpressionWithField) {
			CExpressionWithField wExprField = (CExpressionWithField) aExpression;
			wTranslateField = aTranslator.translateOperande(wExprField.getField());
		}
		for (EArgumentOrder wArg : aTranslator.getOrderArgument()) {
			if (wArg == EArgumentOrder.FIELD && wTranslateField != null) {
				wCondition += wTranslateField;
			} else if (wArg == EArgumentOrder.OPERATOR && wTranslatedOperator != null) {
				wCondition += wTranslatedOperator;

			} else if (wArg == EArgumentOrder.OPERANDE && wTranslateOperande != null) {
				wCondition += wTranslateOperande;

			}
		}
		return wCondition;
	}

	public static List<String> serializer(IExpressionArray aExpression, ITranslator aTranslator)
			throws CSerializeException {
		List<String> wListSubExp = new ArrayList<>();
		for (Object wVal : aExpression.getListValue()) {
			if (wVal instanceof CExpression) {
				wListSubExp.add(serializer((CExpression) wVal, aTranslator));
			} else {
				wListSubExp.add(aTranslator.translateOperande(wVal));
			}
		}
		return wListSubExp;
	}

}
