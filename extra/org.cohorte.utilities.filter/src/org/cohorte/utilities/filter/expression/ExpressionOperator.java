package org.cohorte.utilities.filter.expression;

import java.util.ArrayList;
import java.util.List;

/**
 * describe an operator of a expression list operator supported
 * https://docs.mongodb.com/manual/reference/operator/query-comparison/ and
 * https://docs.mongodb.com/manual/reference/operator/query-logical/
 * 
 * @author apisu
 *
 */
public enum ExpressionOperator {

	AND("$and", false, true), EQ(new String[] { "$eq", "" }, true, false), EXISTS("$exists", true, false), GT("$gt",
			true, false), GTE("$gte", true, false), IN("$in", true, true), LT("$lt", true, false), LTE("$lte", true,
					false), NE("$ne", true, false), NIN("$nin", true, true), NOR("$nor", false,
							true), NOT("$not", false, false), OR("$or", false, true), REGEXP("$regexp", true, false);

	/**
	 * return an operator is the string is a operator else null
	 *
	 * @param aValue
	 * @return
	 */
	public static ExpressionOperator getEnum(final String value) {
		for (ExpressionOperator v : values()) {
			if (v.getValues().contains(value)) {
				return v;
			}
		}
		return null;
	}

	private boolean pHasField;
	private boolean pIsOperandArray;

	private final List<String> pValues;

	ExpressionOperator() {
		pValues = new ArrayList<>();
	}

	ExpressionOperator(final String aValues, final boolean aIsHasField, final boolean isOperandArray) {
		this();
		pValues.add(aValues);
		pIsOperandArray = isOperandArray;
		pHasField = aIsHasField;
	}

	ExpressionOperator(final String[] aValues, final boolean aIsHasField, final boolean isOperandArray) {
		this();
		if (aValues != null) {
			for (String wVal : aValues) {
				pValues.add(wVal);
			}
		}
		pIsOperandArray = isOperandArray;
		pHasField = aIsHasField;
	}

	public List<String> getValues() {
		return pValues;
	}

	public boolean hasField() {
		return pHasField;
	}

	public boolean isOperandArray() {
		return pIsOperandArray;
	}
}
