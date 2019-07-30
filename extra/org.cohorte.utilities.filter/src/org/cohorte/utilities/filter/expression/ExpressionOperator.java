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
public enum ExpressionOperator implements IExpressionConstants {

	AND("$and", !HAS_FIELD, ON_ARRAY),
	//
	EQ(new String[] { "$eq", "" }, HAS_FIELD, !ON_ARRAY),
	//
	EXISTS("$exists", HAS_FIELD, !ON_ARRAY),
	//
	GT("$gt", HAS_FIELD, !ON_ARRAY),
	//
	GTE("$gte", HAS_FIELD, !ON_ARRAY),
	//
	IN("$in", HAS_FIELD, ON_ARRAY),
	//
	LIKE("$like", HAS_FIELD, !ON_ARRAY),
	//
	LT("$lt", HAS_FIELD, !ON_ARRAY),
	//
	LTE("$lte", HAS_FIELD, !ON_ARRAY),
	//
	NE("$ne", HAS_FIELD, !ON_ARRAY),
	//
	NIN("$nin", HAS_FIELD, ON_ARRAY),
	//
	NOR("$nor", !HAS_FIELD, ON_ARRAY),
	//
	NOT("$not", !HAS_FIELD, !ON_ARRAY),
	//
	OR("$or", !HAS_FIELD, ON_ARRAY),
	//
	REGEXP("$regexp", HAS_FIELD, !ON_ARRAY);

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

	public static String listAll() {
		List<String> wListOpStr = new ArrayList<>();
		for (ExpressionOperator wOp : values()) {
			wListOpStr.addAll(wOp.getValues());
		}
		return wListOpStr.toString();
	}

	private boolean pHasField;

	private boolean pIsOperandArray;

	private final List<String> pValues;

	/**
	 * 
	 */
	ExpressionOperator() {
		pValues = new ArrayList<>();
	}

	/**
	 * @param aValues
	 * @param aIsHasField
	 * @param isOperandArray
	 */
	ExpressionOperator(final String aValues, final boolean aIsHasField, final boolean isOperandArray) {
		this();
		pValues.add(aValues);
		pIsOperandArray = isOperandArray;
		pHasField = aIsHasField;
	}

	/**
	 * @param aValues
	 * @param aIsHasField
	 * @param isOperandArray
	 */
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
