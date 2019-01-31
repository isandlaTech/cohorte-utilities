package org.cohorte.utilities.filter.expression;

import java.util.ArrayList;
import java.util.List;

/**
 * describe an operator of a expression
 * 
 * @author apisu
 *
 */
public enum ExpressionOperator {

	EQ(new String[] { "$eq", "" }, true, false),

	IN("$in", true, true), GTE("$gte", true, false), GT("$gt", true, false), LTE("$lte", true, false), LT("$lt", true,
			false), NE("$ne", true, false), NIN("$nin", true, true), AND("$and", false,
					true), OR("$or", false, true), NOR("$nor", false, true), NOT("$not", false, false);

	private final List<String> pValues;
	private boolean pIsOperandArray;
	private boolean pHasField;

	ExpressionOperator() {
		pValues = new ArrayList<>();
	}

	ExpressionOperator(String aValues, boolean aIsHasField, boolean isOperandArray) {
		this();
		pValues.add(aValues);
		pIsOperandArray = isOperandArray;
		pHasField = aIsHasField;
	}

	ExpressionOperator(String[] aValues, boolean aIsHasField, boolean isOperandArray) {
		this();
		if (aValues != null) {
			for (String wVal : aValues) {
				pValues.add(wVal);
			}
		}
		pIsOperandArray = isOperandArray;
		pHasField = aIsHasField;
	}

	public boolean isOperandArray() {
		return pIsOperandArray;
	}

	public boolean hasField() {
		return pHasField;
	}

	public List<String> getValues() {
		return pValues;
	}

	/**
	 * return an operator is the string is a operator else null
	 * 
	 * @param aValue
	 * @return
	 */
	public static ExpressionOperator getEnum(String value) {
		for (ExpressionOperator v : values())
			if (v.getValues().contains(value))
				return v;
		return null;
	}
}
