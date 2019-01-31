package org.cohorte.utilities.filter.expression;

import java.util.ArrayList;
import java.util.List;

import org.psem2m.utilities.CXStringUtils;

/**
 * desrcibe an expression that admit only an array that admit a field and an
 * array as operator
 * 
 * operator that contains only values
 * 
 * @author apisu
 *
 */
public class CExpressionWithFieldArray extends CExpressionWithField implements IExpressionFieldArray {

	private final List<Object> pListObject;

	public CExpressionWithFieldArray(String aField, ExpressionOperator aOperator) {
		super(aField, aOperator);
		pListObject = new ArrayList<>();
	}

	public List<Object> getListValue() {
		return pListObject;
	}

	public void setValues(List<Object> aListExpressions) {
		pListObject.clear();
		pListObject.addAll(aListExpressions);
	}

	public void addValue(String aValue) {
		pListObject.add(aValue);
	}

	@Override
	public Appendable addDescriptionInBuffer(Appendable aBuffer) {
		super.addDescriptionInBuffer(aBuffer);

		CXStringUtils.appendKeyValInBuff(aBuffer, "value_size", pListObject.size());

		for (Object wItem : pListObject) {
			CXStringUtils.appendKeyValInBuff(aBuffer, "value",
					wItem instanceof CExpression ? ((CExpression) wItem).toDescription() : wItem.toString());

		}
		return aBuffer;
	}
}
